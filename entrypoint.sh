#!/bin/sh

# Generate a self-signed certificate if it doesn't already exist
if [ ! -f /data/keystore.p12 ]; then
    echo "Generating self-signed certificate..."
    keytool -genkeypair -alias default-certificate -keyalg RSA -keysize 2048 -validity 3650 -storetype PKCS12 -keystore /data/keystore.p12 -storepass changeit -dname "CN=DEFAULT_CERTIFICATE, OU=Czompicloud, O=Czompicloud, L=City, S=State, C=HU"
fi

# Start the Spring Boot application
java -Dspring.profiles.active=dev -jar /app/retronetmc-server.jar "$@"
