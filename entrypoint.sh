#!/bin/sh

has_pem=false

if [ -n "${TLS_CERTIFICATE_PATH:-}" ] || [ -n "${TLS_PRIVATE_KEY_PATH:-}" ]; then
    if [ -z "${TLS_CERTIFICATE_PATH:-}" ] || [ -z "${TLS_PRIVATE_KEY_PATH:-}" ]; then
        echo "TLS_CERTIFICATE_PATH and TLS_PRIVATE_KEY_PATH must be provided together" >&2
        exit 1
    fi
    has_pem=true
fi

has_keystore=false

if [ -n "${TLS_KEYSTORE_PATH:-}" ]; then
    has_keystore=true
fi

if [ "$has_keystore" = true ] && [ "$has_pem" = true ]; then
    echo "Configure either TLS_KEYSTORE_PATH or the PEM certificate/private-key pair, not both" >&2
    exit 1
fi
ENVIRONMENT="${ENVIRONMENT:-production}"
GENERATE_KEYSTORE="$GENERATE_KEYSTORE"

MSA_KEYSTORE_NAME="${MSA_KEYSTORE_NAME:-msa-token-keystore.p12}"
MSA_KEYSTORE_PATH="${MSA_KEYSTORE_PATH:-/data/$MSA_KEYSTORE_NAME}"
MSA_KEYSTORE_PASS="${MSA_KEYSTORE_PASS:?This environment variable must be set to the password for the MSA token keystore.}"

TLS_KEYSTORE_NAME="${TLS_KEYSTORE_NAME:-generated-tls-keystore.p12}"
TLS_KEYSTORE_PATH="${TLS_KEYSTORE_PATH:-/data/$TLS_KEYSTORE_NAME}"
TLS_KEYSTORE_PASS="${TLS_KEYSTORE_PASS:?This environment variable must be set to the password for the TLS keystore.}"

if [ "$ENVIRONMENT" = "production" ]; then
    GENERATE_KEYSTORE="${GENERATE_KEYSTORE:-false}"
    echo "Running in production mode..."
    export SPRING_PROFILES_ACTIVE=prod
elif [ "$ENVIRONMENT" = "development" ]; then
    GENERATE_KEYSTORE="${GENERATE_KEYSTORE:-true}"
    echo "Running in development mode..."
    export SPRING_PROFILES_ACTIVE=dev
    if [ "$GENERATE_KEYSTORE" = "true" ] && [ ! -f "$TLS_KEYSTORE_PATH" ]; then
        echo "Generating self-signed TLS certificate..."
        keytool -genkeypair -alias generated-self-signed -keyalg RSA \
          -keysize 2048 -validity 3650 -storetype PKCS12 \
          -keystore "$TLS_KEYSTORE_PATH" -storepass "$TLS_KEYSTORE_PASS" \
          -dname "CN=DEFAULT_CERTIFICATE, OU=Yggdrasil Proxy"
    fi
else
    echo "Unknown environment: $ENVIRONMENT. Please set ENVIRONMENT to 'production' or 'development'."
    exit 1
fi

if [ "$GENERATE_KEYSTORE" = "true" ] && [ ! -f "$MSA_KEYSTORE_PATH" ]; then
    echo "Generating MSA token encryption key..."
    keytool -genseckey -alias msa-token-encryption-v1 -keyalg AES \
      -keysize 256 -storetype PKCS12 \
      -keystore "$MSA_KEYSTORE_PATH" -storepass "$MSA_KEYSTORE_PASS"
fi

# Start the Spring Boot application
java -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" -jar /app/retronetmc-server.jar "$@"
