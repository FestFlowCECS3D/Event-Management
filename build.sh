#!/usr/bin/env bash
set -e

# 1. Build the frontend (replace 'eventisma-frontend' with your actual folder name)
cd eventisma-frontend 
npm install
npm run build

# 2. Return to the root directory
cd ..

# 3. Create the static folder in the backend
mkdir -p eventisma-backend/src/main/resources/static

# 4. Copy the compiled frontend files into the backend's static folder
# Note: If your frontend uses Create React App, change 'dist/*' to 'build/*'
cp -r eventisma-frontend/dist/* eventisma-backend/src/main/resources/static/

# 5. Build the Java backend
cd eventisma-backend
mvn clean package -DskipTests
