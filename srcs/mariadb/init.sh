#!/bin/sh
set -e

# Read environment variables
DB_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root}
DB_NAME=${MYSQL_DATABASE:-student_db}

# Initialize database if not exists
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "Initializing MariaDB database..."
    
    # Initialize the database
    mysql_install_db --user=mysql --datadir=/var/lib/mysql
    
    # Start temporary server
    mysqld --user=mysql --datadir=/var/lib/mysql --skip-networking &
    pid="$!"
    
    # Wait for MySQL to start
    for i in {30..0}; do
        if mysqladmin ping &>/dev/null; then
            break
        fi
        echo "Waiting for MariaDB to start..."
        sleep 1
    done
    
    if [ "$i" = 0 ]; then
        echo >&2 "MariaDB init process failed."
        exit 1
    fi
    
    echo "Setting root password and creating database..."
    
    # Set root password and create database
    mysql -u root <<-EOSQL
        SET @@SESSION.SQL_LOG_BIN=0;
        DELETE FROM mysql.user WHERE user NOT IN ('mysql.sys', 'mysqlxsys', 'root') OR host NOT IN ('localhost');
        ALTER USER 'root'@'localhost' IDENTIFIED BY '${DB_ROOT_PASSWORD}';
        CREATE USER 'root'@'%' IDENTIFIED BY '${DB_ROOT_PASSWORD}';
        GRANT ALL ON *.* TO 'root'@'%' WITH GRANT OPTION;
        CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\`;
        FLUSH PRIVILEGES;
EOSQL
    
    # Run init scripts if they exist
    if [ -d "/init-initdb.d" ]; then
        for f in /init-initdb.d/*.sql; do
            if [ -f "$f" ]; then
                echo "Running $f..."
                mysql -u root -p"${DB_ROOT_PASSWORD}" "${DB_NAME}" < "$f"
            fi
        done
    fi
    
    # Stop temporary server
    if ! kill -s TERM "$pid" || ! wait "$pid"; then
        echo >&2 "MariaDB init process failed."
        exit 1
    fi
    
    echo "MariaDB initialization complete!"
fi

# Start MariaDB normally
echo "Starting MariaDB..."
exec mysqld --user=mysql --datadir=/var/lib/mysql --bind-address=0.0.0.0