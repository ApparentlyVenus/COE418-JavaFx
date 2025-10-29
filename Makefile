# JavaFX + MariaDB Project Makefile
# Database in Docker, Application runs locally

# Colors
GREEN = \033[0;32m
YELLOW = \033[0;33m
RED = \033[0;31m
NC = \033[0m

PROJECT_NAME = coe418-javafx-sql

.PHONY: all
all: db app ## Start database and run application

.PHONY: db
db: ## Start MariaDB in Docker
	@echo "$(GREEN)🚀 Starting MariaDB...$(NC)"
	@docker-compose up -d mariadb
	@echo "$(GREEN)✅ MariaDB started on localhost:3306$(NC)"
	@echo "$(YELLOW)   Database: student_db$(NC)"
	@echo "$(YELLOW)   User: root$(NC)"
	@echo "$(YELLOW)   Password: rootpassword$(NC)"

.PHONY: app
app: ## Run JavaFX application locally
	@echo "$(GREEN)🎮 Running JavaFX application...$(NC)"
	@cd application && chmod +x run.sh && ./run.sh

.PHONY: run
run: db ## Start database and run app (alias for 'all')
	@sleep 3
	@$(MAKE) app

.PHONY: stop
stop: ## Stop database
	@echo "$(YELLOW)⏬ Stopping MariaDB...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✅ MariaDB stopped$(NC)"

.PHONY: clean
clean: ## Stop database and remove volumes
	@echo "$(YELLOW)🧹 Cleaning up...$(NC)"
	@docker-compose down -v
	@echo "$(GREEN)✅ Cleanup complete$(NC)"

.PHONY: fclean
fclean: clean ## Full cleanup (remove images too)
	@echo "$(RED)🗑️  Full cleanup...$(NC)"
	@docker-compose down -v --rmi all 2>/dev/null || true
	@docker images | grep mariadb | awk '{print $$3}' | xargs -r docker rmi -f 2>/dev/null || true
	@cd application && rm -rf bin 2>/dev/null || true
	@echo "$(GREEN)✅ Full cleanup complete$(NC)"

.PHONY: re
re: fclean all ## Rebuild everything from scratch

.PHONY: logs
logs: ## Show database logs
	@docker-compose logs -f mariadb

.PHONY: mysql
mysql: ## Connect to MySQL shell
	@echo "$(GREEN)Connecting to MySQL...$(NC)"
	@docker exec -it javafx-mariadb mysql -u root -prootpassword student_db

.PHONY: compile
compile: ## Compile Java application only
	@echo "$(GREEN)🔨 Compiling...$(NC)"
	@cd application && mkdir -p bin && \
	javac --module-path /usr/share/openjfx/lib \
	    --add-modules javafx.controls,javafx.fxml \
	    -cp "lib/*" -d bin src/*.java
	@echo "$(GREEN)✅ Compilation complete$(NC)"

.PHONY: help
help: ## Show available commands
	@echo "$(GREEN)═══════════════════════════════════════════════════$(NC)"
	@echo "$(GREEN)  JavaFX + MariaDB Project (Local Development)$(NC)"
	@echo "$(GREEN)═══════════════════════════════════════════════════$(NC)"
	@echo ""
	@echo "$(YELLOW)Available commands:$(NC)"
	@echo "  $(GREEN)make all$(NC)      - Start database and run app"
	@echo "  $(GREEN)make db$(NC)       - Start MariaDB only"
	@echo "  $(GREEN)make app$(NC)      - Run JavaFX app only"
	@echo "  $(GREEN)make run$(NC)      - Start everything"
	@echo "  $(GREEN)make stop$(NC)     - Stop database"
	@echo "  $(GREEN)make clean$(NC)    - Remove containers + volumes"
	@echo "  $(GREEN)make fclean$(NC)   - Full cleanup"
	@echo "  $(GREEN)make re$(NC)       - Rebuild from scratch"
	@echo "  $(GREEN)make logs$(NC)     - Show database logs"
	@echo "  $(GREEN)make mysql$(NC)    - Connect to MySQL shell"
	@echo "  $(GREEN)make compile$(NC)  - Compile Java code only"
	@echo ""