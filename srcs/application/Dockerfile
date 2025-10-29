FROM alpine:3.19

# Install OpenJDK 17 and JavaFX on Alpine
RUN apk add --no-cache \
    openjdk17 \
    openjfx \
    mesa-gl \
    gtk+3.0 \
    ttf-dejavu \
    fontconfig \
    bash

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Set working directory
WORKDIR /app

# Copy library files (MySQL connector)
COPY lib /app/lib

# Copy source code
COPY src /app/src

# Create output directory for compiled classes
RUN mkdir -p /app/bin

# Compile the JavaFX application
RUN javac --module-path /usr/share/java/javafx \
    --add-modules javafx.controls,javafx.fxml \
    -cp "/app/lib/*" \
    -d /app/bin \
    /app/src/application/*.java

# Set the entrypoint to run the application
CMD ["java", \
     "--module-path", "/usr/share/java/javafx", \
     "--add-modules", "javafx.controls,javafx.fxml", \
     "-cp", "/app/bin:/app/lib/*", \
     "application.Main"]