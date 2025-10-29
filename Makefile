# JavaFX + MariaDB Docker Makefile
# Alpine Linux-based

# Colors
GREEN = \033[0;32m
YELLOW = \033[0;33m
RED = \033[0;31m
NC = \033[0m

# Project
PROJECT_NAME = coe418-javafx-sql

.PHONY: all
all: ## Build and start all services
	@echo "$(GREEN)🚀 Building and starting all services...$(NC)"
	@xhost +local:docker 2>/dev/null || true
	@chmod +x srcs/mariadb/init.sh
	@docker-compose up --build -d
	@echo "$(GREEN)✅ All services started!$(NC)"
	@echo "$(YELLOW)View logs: docker-compose logs -f$(NC)"

.PHONY: up
up: ## Start services (without rebuilding)
	@echo "$(GREEN)🚀 Starting services...$(NC)"
	@xhost +local:docker 2>/dev/null || true
	@docker-compose up -d
	@echo "$(GREEN)✅ Services started!$(NC)"

.PHONY: down
down: ## Stop and remove containers
	@echo "$(YELLOW)⏬ Stopping services...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✅ Services stopped$(NC)"

.PHONY: clean
clean: ## Stop containers and remove volumes
	@echo "$(YELLOW)🧹 Cleaning up...$(NC)"
	@docker-compose down -v
	@echo "$(GREEN)✅ Cleanup complete$(NC)"

.PHONY: fclean
fclean: clean ## Full cleanup: remove everything including images
	@echo "$(RED)🗑️  Full cleanup...$(NC)"
	@docker-compose down -v --rmi all 2>/dev/null || true
	@docker images | grep $(PROJECT_NAME) | awk '{print $$3}' | xargs -r docker rmi -f 2>/dev/null || true
	@echo "$(GREEN)✅ Full cleanup complete$(NC)"

.PHONY: re
re: fclean all ## Rebuild everything from scratch

.PHONY: help
help: ## Show available commands
	@echo "$(GREEN)═══════════════════════════════════════════════════$(NC)"
	@echo "$(GREEN)  JavaFX + MariaDB Docker Project$(NC)"
	@echo "$(GREEN)═══════════════════════════════════════════════════$(NC)"
	@echo ""
	@echo "$(YELLOW)Available commands:$(NC)"
	@echo "  $(GREEN)make all$(NC)      - Build and start everything"
	@echo "  $(GREEN)make up$(NC)       - Start services (no rebuild)"
	@echo "  $(GREEN)make down$(NC)     - Stop services"
	@echo "  $(GREEN)make clean$(NC)    - Remove containers + volumes"
	@echo "  $(GREEN)make fclean$(NC)   - Full cleanup (+ images)"
	@echo "  $(GREEN)make re$(NC)       - Rebuild from scratch"
	@echo ""