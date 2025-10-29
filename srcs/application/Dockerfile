FROM eclipse-temurin:17-jdk-jammy

# Install JavaFX from Ubuntu repos (fast!)
RUN apt-get update && apt-get install -y \
    openjfx \
    libopenjfx-java \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy files
COPY lib /app/lib
COPY src /app/src

# Compile
RUN mkdir -p /app/bin && \
    javac --module-path /usr/share/openjfx/lib \
    --add-modules javafx.controls,javafx.fxml \
    -cp "/app/lib/*" \
    -d /app/bin \
    /src/*.java

# Run
CMD ["java", \
     "--module-path", "/usr/share/openjfx/lib", \
     --add-modules", "javafx.controls,javafx.fxml", \
     "-cp", "/app/bin:/app/lib/*", \
     "application.Main"]