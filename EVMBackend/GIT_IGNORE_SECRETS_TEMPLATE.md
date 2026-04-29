# ============================================
# Git Ignore Configuration for Production
# ============================================
# Add to root .gitignore to prevent secrets from being committed
# ============================================

# IDE Files
.idea/
.vscode/
*.swp
*.swo
*~
*.DS_Store

# Build Files
/target/
/build/
*.class
*.jar
*.war
*.rar
*.zip

# Maven
.m2/
*.pom.xml.tag
*.pom.xml.releaseBackup
*.pom.xml.versionsBackup
*.pom.xml.next
release.properties
dependency-reduced-pom.xml

# Environment & Secrets (CRITICAL - DO NOT REMOVE)
.env
.env.local
.env.*.local
application-local.properties
application-dev.properties
local.properties
secrets.properties

# Node / Frontend (if in same repo)
node_modules/
npm-debug.log
yarn-error.log
.npm

# Logs
logs/
*.log

# Database
*.sqlite
*.db
*.sql

# Temporary Files
temp/
tmp/
cache/

# OS Files
Thumbs.db
.DS_Store

# Auto-generated
src/main/resources/static/
src/main/resources/templates/

# ============================================
# IMPORTANT: Never commit these patterns
# ============================================
# Passwords, API Keys, Secrets, Credentials

# Gmail credentials
smtp.password
mail.password

# Database
db.password
database.password

# OAuth Secrets
oauth.secret
client.secret

# JWT Secrets
jwt.secret
token.secret

# Any file containing "secret", "password", "credential", "key"
*secret*
*password*
*credential*
*apikey*
*api_key*

# Environment variable files
.env*
*.env
*.pem
*.p12
*.jks
*.key

# ============================================
# Exceptions (if needed, but use carefully)
# ============================================
# If you must track a sample file, use:
# !.env.example
# !.env.sample
# But NEVER commit actual secrets

# ============================================
# Verification Commands
# ============================================
# Before committing, run:
# git log -p | grep -i "password\|secret\|token\|api"
# git diff --cached | grep -i "password\|secret\|token\|api"
