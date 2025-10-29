#!/bin/bash

# Colors
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 JavaFX MySQL Database Manager${NC}"
echo ""

# Check if database is running
echo -e "${YELLOW}📊 Checking database...${NC}"
if ! docker ps | grep -q javafx-mariadb; then
    echo -e "${RED}❌ Database is not running!${NC}"
    echo -e "${YELLOW}Starting database...${NC}"
    cd .. && docker-compose up -d mariadb && cd application
    echo -e "${GREEN}✅ Database started!${NC}"
    sleep 5
else
    echo -e "${GREEN}✅ Database is running${NC}"
fi

# Check for JavaFX
if [ ! -d "/usr/share/openjfx/lib" ]; then
    echo -e "${RED}❌ JavaFX not found at /usr/share/openjfx/lib${NC}"
    echo -e "${YELLOW}Please install: sudo apt install openjfx libopenjfx-java${NC}"
    exit 1
fi

# Check for MySQL connector
if [ ! -f "lib/mysql-connector-j-8.2.0.jar" ]; then
    echo -e "${YELLOW}📦 MySQL Connector not found. Downloading...${NC}"
    mkdir -p lib
    wget -q https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar -O lib/mysql-connector-j-8.2.0.jar
    echo -e "${GREEN}✅ MySQL Connector downloaded${NC}"
fi

# Compile
echo -e "${YELLOW}🔨 Compiling Java files...${NC}"
mkdir -p bin

javac --module-path /usr/share/openjfx/lib \
    --add-modules javafx.controls,javafx.fxml \
    -cp "lib/*" \
    -d bin \
    src/*.java

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Compilation failed!${NC}"
    exit 1
fi

# Copy FXML file to bin directory
echo -e "${YELLOW}📄 Copying FXML file...${NC}"
mkdir -p bin/application/src
cp src/manual.fxml bin/application/src/

echo -e "${GREEN}✅ Compilation successful${NC}"

# Run
echo -e "${YELLOW}🎮 Starting application...${NC}"
echo ""

java --module-path /usr/share/openjfx/lib \
    --add-modules javafx.controls,javafx.fxml \
    -cp "bin:lib/*" \
    application.src.Main