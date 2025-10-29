FROM alpine:3.19

# Install MariaDB
RUN apk add --no-cache \
    mariadb \
    mariadb-client

# Create necessary directories
RUN mkdir -p /run/mysqld && \
    chown -R mysql:mysql /run/mysqld

# Copy initialization script
COPY init.sql /init-initdb.d/

# Copy custom entrypoint script
COPY init.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init.sh

# Expose MySQL port
EXPOSE 3306

# Set the entrypoint
ENTRYPOINT ["init.sh"]
CMD ["mysqld"]