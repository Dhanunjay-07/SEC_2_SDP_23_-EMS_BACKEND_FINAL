# Spring Boot Backend Deployment on Railway - Complete Guide

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Step 1: Prepare Your Repository](#step-1-prepare-your-repository)
3. [Step 2: Create Railway Service](#step-2-create-railway-service)
4. [Step 3: Configure Environment Variables](#step-3-configure-environment-variables)
5. [Step 4: Deploy from GitHub](#step-4-deploy-from-github)
6. [Step 5: Verify Deployment](#step-5-verify-deployment)
7. [Troubleshooting](#troubleshooting)
8. [Security Best Practices](#security-best-practices)

---

## Prerequisites

### ✅ Before You Start:
1. **Railway Account**: Already created and MySQL database deployed
2. **GitHub Repository**: Your Spring Boot project pushed to GitHub
3. **Google OAuth Credentials**: Updated with Railway domain
4. **SMTP Credentials**: Gmail (App Password, not regular password)
5. **Frontend Domain**: Your deployed frontend URL
6. **Java 21 & Maven**: Verified locally (`mvn --version`, `java --version`)

---

## Step 1: Prepare Your Repository

### 1.1 Update Your pom.xml (Already OK ✅)
Your pom.xml is production-ready with:
- ✅ Spring Boot 3.3.6 (latest stable)
- ✅ Java 21 (latest LTS)
- ✅ MySQL Connector
- ✅ Spring Security + OAuth2
- ✅ JWT + Mail support

### 1.2 Verify Build Before Deployment
```bash
cd BACKEND/EVMBackend
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXs
```

### 1.3 Ensure No Hardcoded Secrets in Code
```bash
# Search for hardcoded values (should be NONE)
grep -r "mysql://" src/
grep -r "smtp.gmail.com" src/
grep -r "client-secret" src/
```

---

## Step 2: Create Railway Service

### 2.1 Add New Spring Boot Service to Railway

**In Railway Dashboard:**
1. Go to your existing Railway project
2. Click **+ New** (button in top-right)
3. Select **GitHub Repo**
4. Choose your repository containing the Spring Boot backend
5. **Make sure to select the `BACKEND/EVMBackend` directory** as the root (if using monorepo)
6. Name the service: `evm-backend` or similar
7. Click **Deploy**

### 2.2 Configure Service Settings
After service creation:
1. Go to **Settings** tab
2. Set **Root Directory**: `BACKEND/EVMBackend` (if using monorepo)
3. Set **Start Command**: `java -jar target/EVMBackend-0.0.1-SNAPSHOT.jar`
   - *Railway will auto-detect this, but ensure it's set*

---

## Step 3: Configure Environment Variables

### 3.1 Get MySQL Connection Details from Railway

In Railway Dashboard:
1. Click your **MySQL** service
2. Go to **Connect** tab
3. Note down:
   - **MYSQL_HOST**: `tramway.proxy.rlwy.net` or similar
   - **MYSQL_PORT**: Usually `11201`
   - **MYSQL_USER**: `root` or your username
   - **MYSQL_PASSWORD**: Your database password
   - **MYSQL_DATABASE**: `railway`

### 3.2 Set Environment Variables in Railway

**In Railway Dashboard for your Spring Boot service:**
1. Go to **Variables** tab
2. Add the following variables:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# Server Configuration
SERVER_PORT=8080
SPRING_APPLICATION_NAME=EVMBackend

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=false

# JWT Configuration
APP_JWT_SECRET=[GENERATE A NEW SECURE SECRET - see below]
APP_JWT_EXPIRATION_MS=120000000

# Google OAuth Configuration
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=[YOUR_GOOGLE_CLIENT_ID]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=[YOUR_GOOGLE_CLIENT_SECRET]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_SCOPE=openid,profile,email
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI=https://your-railway-domain/login/oauth2/code/google
APP_OAUTH2_FRONTEND_REDIRECT_URL=https://your-frontend-domain/oauth2/callback

# Email/SMTP Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
APP_MAIL_FROM=your-email@gmail.com

# OTP Configuration
APP_OTP_TTL_MS=300000
APP_OTP_RESEND_COOLDOWN_MS=60000

# CORS Configuration
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://www.your-frontend-domain.com

# Logging (Production should be WARN or ERROR)
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_ELECTION_EVM=INFO
```

### 3.3 Important Notes on Variables

**DATABASE URL Format:**
- If using Railway's MySQL proxy: `jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?...`
- Alternative: Use Railway's internal network (faster): `jdbc:mysql://mysql:3306/railway?...`

**JWT Secret Generation:**
```bash
# Generate a secure random secret (use one of these):
openssl rand -hex 32
# Output example: 7d3c9e1b2f4a8c6d5e9a2b1c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d

# Or use Java:
java -c "System.out.println(java.util.UUID.randomUUID().toString().replace(\"-\", \"\") + java.util.UUID.randomUUID().toString().replace(\"-\", \"\"))"
```

**Redirect URIs:**
- Replace `your-railway-domain` with Railway's assigned domain (visible in Railway dashboard)
- Format: `https://your-service-name.up.railway.app`

---

## Step 4: Deploy from GitHub

### 4.1 Connect GitHub Repository

**In Railway for your Spring Boot service:**
1. Go to **Deployments** tab
2. Click **Connect Repository**
3. Authorize Railway with GitHub
4. Select your repository and branch (`main` or `master`)
5. Click **Deploy**

### 4.2 What Happens Automatically

Railway will:
1. Clone your repository
2. Detect it's a Maven project
3. Run `mvn clean package`
4. Create a JAR file
5. Deploy the JAR to a Docker container
6. Start the application using the start command

### 4.3 Monitor Deployment

**In Railway Dashboard:**
1. Go to **Deployments** tab
2. Watch the build progress (should take 3-5 minutes)
3. Once successful, you'll see a green checkmark
4. Service will auto-start

---

## Step 5: Verify Deployment

### 5.1 Check Service Status
In Railway Dashboard:
- ✅ Service shows **"Running"** status
- ✅ No error logs in **Logs** tab
- ✅ Network shows **Public Domain** assigned

### 5.2 Test API Endpoints

```bash
# Replace with your Railway domain
RAILWAY_URL="https://your-service-name.up.railway.app"

# Test health endpoint (no auth required)
curl -X GET "$RAILWAY_URL/swagger-ui.html"
# Should return 200 and HTML content

# Test login endpoint
curl -X POST "$RAILWAY_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password"
  }'
# Should return 200 with token or error message

# Test protected endpoint
curl -X GET "$RAILWAY_URL/api/auth/me" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
# Should return user info (200) or 401 if no token
```

### 5.3 Check Database Connection

In Railway Dashboard:
1. Click your **MySQL** service
2. Go to **Logs** tab
3. Verify no "connection refused" errors

---

## Troubleshooting

### ❌ Issue 1: Port Binding Error
```
ERROR: Address already in use: 0.0.0.0:8080
```

**Solution:**
- Railway automatically manages ports
- Remove `server.port=8080` from application.properties
- Use `SERVER_PORT` environment variable instead

---

### ❌ Issue 2: MySQL Connection Failed
```
ERROR: java.sql.SQLException: Cannot get a connection
```

**Solutions:**
1. Verify `SPRING_DATASOURCE_URL` includes Railway's MySQL host
2. Check `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`
3. Ensure Railway MySQL service is **Running**
4. Try internal URL: `jdbc:mysql://mysql:3306/railway` (Railway's private network)

**Test Connection:**
```bash
# From Railway CLI
railway exec mysql -h tramway.proxy.rlwy.net -u root -p
```

---

### ❌ Issue 3: OAuth Redirect URI Mismatch
```
ERROR: redirect_uri_mismatch
```

**Solutions:**
1. Get Railway's assigned domain: `https://your-service-name.up.railway.app`
2. Update `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI`:
   ```
   https://your-service-name.up.railway.app/login/oauth2/code/google
   ```
3. Update Google OAuth console with this exact URI
4. Restart deployment with new environment variable

---

### ❌ Issue 4: Frontend OAuth Callback Error
```
ERROR: App not found or error redirecting to frontend
```

**Solutions:**
1. Update `APP_OAUTH2_FRONTEND_REDIRECT_URL` to your frontend domain
2. Frontend must have OAuth callback handler at `/oauth2/callback`
3. Ensure frontend domain is accessible and uses HTTPS

---

### ❌ Issue 5: Email/OTP Not Sending
```
ERROR: Failed to send email
```

**Solutions:**
1. Gmail requires **App Password**, not regular password
   - Go to Google Account → Security → 2FA
   - Generate an App Password
   - Use that in `SPRING_MAIL_PASSWORD`
2. Verify `SPRING_MAIL_USERNAME` is correct email
3. Ensure `APP_MAIL_FROM` matches sending email
4. Check SMTP port: 587 for TLS (not 465)

---

### ❌ Issue 6: Build Failure
```
ERROR: Build failed
```

**Solutions:**
1. Check build logs in Railway dashboard
2. Verify Maven can build locally:
   ```bash
   cd BACKEND/EVMBackend
   mvn clean package
   ```
3. Ensure no test failures:
   ```bash
   mvn test
   ```
4. Check for missing dependencies or compilation errors
5. Verify Java version in pom.xml matches Railway environment

---

### ❌ Issue 7: JWT Secret Issues
```
ERROR: JWT signature validation failed
```

**Solutions:**
1. Ensure `APP_JWT_SECRET` is set and consistent
2. Generate new secret if uncertain:
   ```bash
   openssl rand -hex 32
   ```
3. Update in Railway variables
4. Invalidate old tokens (users need to re-login)

---

## Security Best Practices

### 🔒 1. Never Hardcode Secrets
- ✅ Use Railway environment variables
- ✅ Never commit `.env` files or secrets to GitHub
- ❌ Don't hardcode API keys in application.properties

### 🔒 2. Use HTTPS Everywhere
- ✅ Railway provides free HTTPS with assigned domains
- ✅ All OAuth redirects must use HTTPS
- ✅ Update CORS origins to HTTPS

### 🔒 3. Secure JWT Secret
- ✅ Generate strong 32-byte (256-bit) secrets
- ✅ Use different secrets for different environments
- ✅ Rotate secrets periodically (requires user re-login)
- ❌ Don't use simple strings like "secret123"

### 🔒 4. Gmail App Passwords
- ✅ Use App Passwords (generated in Google Account settings)
- ✅ Never use your main Gmail password
- ✅ Revoke unused app passwords regularly

### 🔒 5. OAuth Credentials Rotation
- ✅ Generate new Google OAuth credentials periodically
- ✅ Keep production credentials separate from development
- ✅ Revoke old credentials after migration

### 🔒 6. CORS Configuration
```
# ALLOWED (Production)
APP_CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# NOT ALLOWED (Development)
APP_CORS_ALLOWED_ORIGINS=*
APP_CORS_ALLOWED_ORIGINS=http://localhost:*
```

### 🔒 7. Database Security
- ✅ Change default MySQL password
- ✅ Use strong passwords (32+ characters)
- ✅ Restrict database user permissions if possible
- ✅ Enable SSL for database connections

### 🔒 8. Logging Configuration
```
# Production (minimal logging)
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_ELECTION_EVM=INFO

# Never log sensitive data
# ❌ Don't log passwords, tokens, or personal info
```

---

## Authentication Module Review (Production Ready)

### ✅ JWT Authentication
- Strong configuration with 120 million ms expiration
- JJWT library (industry standard)
- Proper signature validation
- Token refresh endpoint available

**Recommendation:** Consider adding refresh token rotation for enhanced security.

### ✅ Google OAuth2
- Proper user creation/update flow
- Email-based user lookup
- Password encoded with BCrypt
- Success handler redirects to frontend with token

**Recommendation:** Add rate limiting to prevent brute force during token refresh.

### ✅ OTP Email Verification
- 5-minute TTL (standard)
- 60-second resend cooldown
- SMTP configuration with TLS
- Email templating ready

**Recommendation:** Implement email templating with HTML for better UX.

### ✅ Spring Security
- CORS properly configured (hardcoded, needs update)
- Role-based access control (ADMIN, ANALYST, OBSERVER, CITIZEN)
- JWT filter in place
- OAuth2 integration complete

**Recommendation:** Update CORS to use environment variables (see SecurityConfig-Updated).

### ⚠️ Issues to Fix Before Deployment

1. **CORS is hardcoded** to `localhost:5173`
   - Need to update to environment variable
   - See `SecurityConfig-Updated.java`

2. **Frontend redirect URL is hardcoded**
   - Fixed with environment variable `APP_OAUTH2_FRONTEND_REDIRECT_URL`

3. **No rate limiting on endpoints**
   - Consider adding Spring Security rate limiting

4. **No HTTPS enforcement**
   - Railway enforces HTTPS automatically ✅

---

## Summary of Environment Variables Needed

| Variable | Example Value | Purpose |
|----------|---|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?...` | MySQL Connection |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database User |
| `SPRING_DATASOURCE_PASSWORD` | `YourSecurePassword` | Database Password |
| `APP_JWT_SECRET` | `7d3c9e1b2f4a8c6d5e9a2b1c3d4e5f6a7b8c9d0...` | JWT Signing |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` | `373628908161-...` | Google OAuth |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | `GOCSPX-...` | Google Secret |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI` | `https://your-service.up.railway.app/login/oauth2/code/google` | OAuth Callback |
| `APP_OAUTH2_FRONTEND_REDIRECT_URL` | `https://your-frontend.com/oauth2/callback` | Frontend OAuth Callback |
| `SPRING_MAIL_USERNAME` | `your-email@gmail.com` | Gmail Account |
| `SPRING_MAIL_PASSWORD` | `xxxx xxxx xxxx xxxx` | Gmail App Password |
| `APP_MAIL_FROM` | `your-email@gmail.com` | Email From Address |
| `APP_CORS_ALLOWED_ORIGINS` | `https://your-frontend.com` | CORS Origins |

---

## Quick Deploy Checklist

- [ ] pom.xml verified (Java 21, Spring Boot 3.3.6)
- [ ] All secrets removed from code
- [ ] Created Procfile or verified start command
- [ ] Generated new JWT secret
- [ ] Updated Google OAuth redirect URIs
- [ ] Generated Gmail App Password
- [ ] Pushed code to GitHub
- [ ] Created Railway Spring Boot service
- [ ] Linked MySQL service from same project
- [ ] Set all environment variables in Railway
- [ ] Verified build succeeds
- [ ] Tested API endpoints
- [ ] Verified OAuth login flow
- [ ] Verified OTP email sending
- [ ] Updated CORS origins for production
- [ ] Documented all changes in git

---

## Next Steps

1. **Update your Spring Boot application properties** (see application-prod.properties)
2. **Update SecurityConfig** to support environment variables for CORS (see SecurityConfig-Updated.java)
3. **Create Procfile** (if Railway doesn't auto-detect)
4. **Update Google OAuth Console** with Railway domain
5. **Follow Step-by-Step deployment above**
6. **Monitor logs in Railway dashboard**

---

## Support & Resources

- **Railway Docs**: https://docs.railway.app
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **JWT Library**: https://github.com/jwtk/jjwt
- **Spring Security OAuth2**: https://spring.io/guides/tutorials/spring-security-and-oauth2/
