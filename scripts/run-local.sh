#!/usr/bin/env bash
set -euo pipefail

# Simple helper to run the CarRentalSystem with Jetty via Maven

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

if ! command -v java >/dev/null 2>&1; then
  echo "[ERROR] Java not found. Please install a JDK (e.g., brew install temurin)" >&2
  exit 1
fi
if ! command -v mvn >/dev/null 2>&1; then
  echo "[ERROR] Maven not found. Install with: brew install maven" >&2
  exit 2
fi

# Ensure pom exists
if [ ! -f pom.xml ]; then
  echo "[ERROR] pom.xml not found. Cannot run Jetty."
  exit 3
fi

echo "[INFO] Starting Jetty (Ctrl+C to stop)..."
mvn -q jetty:run

