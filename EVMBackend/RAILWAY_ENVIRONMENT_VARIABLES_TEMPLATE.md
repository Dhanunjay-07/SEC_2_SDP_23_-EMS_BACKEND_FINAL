# ============================================
# RAILWAY ENVIRONMENT VARIABLES TEMPLATE
# ============================================
# Copy this entire section into your Railway Dashboard
# Dashboard Path: Your Service → Variables tab → Copy & Paste
# 
# Instructions:
# 1. Replace all [PLACEHOLDER] values with actual values
# 2. Each variable is separated by a new line in Railway
# 3. Save after adding all variables
# 4. Service will auto-restart with new variables
# ============================================

# ============================================
# STEP 1: Database Configuration from Railway
# ============================================
# Get these from your MySQL service → Connect tab
SPRING_DATASOURCE_URL=jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=[YOUR_MYSQL_PASSWORD]
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# ============================================
# STEP 2: Application Configuration
# ============================================
SPRING_APPLICATION_NAME=EVMBackend
SERVER_PORT=8080
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=false

# ============================================
# STEP 3: JWT Secret Configuration
# ============================================
# Generate a new secure secret before deploying
# Command: openssl rand -hex 32
# OR use this tool: https://randomkeygen.com/
APP_JWT_SECRET=[GENERATE_NEW_32_BYTE_HEX_STRING]
APP_JWT_EXPIRATION_MS=120000000

# ============================================
# STEP 4: Google OAuth Configuration
# ============================================
# Get from: Google Cloud Console → Credentials → OAuth 2.0 Client IDs
# NOTE: Update redirect URI in Google Console with Railway domain
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=[YOUR_GOOGLE_CLIENT_ID]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=[YOUR_GOOGLE_CLIENT_SECRET]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_SCOPE=openid,profile,email
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI=https://[YOUR_RAILWAY_SERVICE_NAME].up.railway.app/login/oauth2/code/google

# Frontend OAuth callback (where Google redirects after login)
APP_OAUTH2_FRONTEND_REDIRECT_URL=https://[YOUR_FRONTEND_DOMAIN]/oauth2/callback

# ============================================
# STEP 5: Email/SMTP Configuration (Gmail)
# ============================================
# IMPORTANT: Use App Password, NOT your Gmail password
# How to get App Password:
#   1. Go to myaccount.google.com
#   2. Security (left sidebar)
#   3. Enable 2FA if not already
#   4. Search "App passwords"
#   5. Select "Mail" and "Windows Computer"
#   6. Copy the 16-character password shown
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=[YOUR_GMAIL_ADDRESS]
SPRING_MAIL_PASSWORD=[YOUR_GMAIL_APP_PASSWORD]
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED=true
APP_MAIL_FROM=[YOUR_EMAIL_ADDRESS]

# ============================================
# STEP 6: OTP Configuration
# ============================================
APP_OTP_TTL_MS=300000
APP_OTP_RESEND_COOLDOWN_MS=60000

# ============================================
# STEP 7: CORS Configuration
# ============================================
# Add all domains that should access your API
# Format: https://domain1.com,https://domain2.com
APP_CORS_ALLOWED_ORIGINS=https://[YOUR_FRONTEND_DOMAIN],https://www.[YOUR_FRONTEND_DOMAIN]

# ============================================
# STEP 8: Logging Configuration
# ============================================
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_ELECTION_EVM=INFO

# ============================================
# REFERENCE GUIDE
# ============================================
# [YOUR_RAILWAY_SERVICE_NAME]
#   → After deploying on Railway, your service gets a name like "evm-backend-xyz"
#   → Full domain: https://evm-backend-xyz.up.railway.app
#   → Find it in Railway Dashboard → Your Service → Networking tab
#
# [YOUR_FRONTEND_DOMAIN]
#   → Your deployed frontend domain (e.g., election-evm.com or evm-frontend.up.railway.app)
#
# [YOUR_GOOGLE_CLIENT_ID]
#   → From Google Cloud Console
#   → Update Google Console Authorized Redirect URIs:
#      https://[YOUR_RAILWAY_SERVICE_NAME].up.railway.app/login/oauth2/code/google
#
# [YOUR_GMAIL_APP_PASSWORD]
#   → NOT your regular Gmail password
#   → Generated in Google Account settings
#   → Format: "xxxx xxxx xxxx xxxx" (16 characters)
#   → Only works if you have 2FA enabled
#
# [GENERATE_NEW_32_BYTE_HEX_STRING]
#   → Run: openssl rand -hex 32
#   → OR: Use https://randomkeygen.com/ (256-bit WEP key)
#   → DO NOT use: "secret123" or simple strings
#   → Example: 7d3c9e1b2f4a8c6d5e9a2b1c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d
