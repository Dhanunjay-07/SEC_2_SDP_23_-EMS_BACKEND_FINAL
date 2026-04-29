# Railway Deployment Checklist

## Pre-Deployment (Before touching Railway)

### Code Cleanup
- [ ] Verify no hardcoded secrets in `application.properties`
- [ ] Remove `application.properties` from git if it contains secrets
- [ ] Verify `pom.xml` has Spring Boot 3.3.6 and Java 21
- [ ] Build locally: `mvn clean package -DskipTests` ✅
- [ ] No compilation errors
- [ ] No test failures (or skip with -DskipTests)
- [ ] Check git logs for any committed secrets: `git log -p | grep -i password`

### Configuration Files Created
- [ ] `application-prod.properties` created ✅
- [ ] `Procfile` created ✅
- [ ] `CorsConfigFromEnv.java` created ✅
- [ ] `SecurityConfig-Updated.java` created (ready to replace original) ✅
- [ ] All files committed to git

### Secrets Prepared
- [ ] Generated new JWT secret: `openssl rand -hex 32`
  - JWT Secret: `_____________________` (save this)
- [ ] Have Gmail App Password (not regular password)
  - Gmail App Password: `_____________________` (save this)
- [ ] Updated Google OAuth credentials with Railway domain
  - Google Client ID: `_____________________`
  - Google Client Secret: `_____________________`
- [ ] Have MySQL details from Railway
  - DB Host: `tramway.proxy.rlwy.net`
  - DB Port: `11201`
  - DB User: `root`
  - DB Password: `_____________________` (save this)

---

## Step 1: Create Railway Service

- [ ] Go to Railway dashboard
- [ ] Click "+ New"
- [ ] Select "GitHub Repo"
- [ ] Authorize GitHub with Railway
- [ ] Select your repository
- [ ] Select branch (main/master)
- [ ] **Set root directory**: `BACKEND/EVMBackend`
- [ ] Name service: `evm-backend`
- [ ] Click Deploy

**Expected Time**: 3-5 minutes for initial deployment

- [ ] Wait for build to complete (Green checkmark)
- [ ] Service shows "Running" status

---

## Step 2: Configure Database Connection

- [ ] Go to Railway MySQL service
- [ ] Click "Connect" tab
- [ ] Note down all connection details
- [ ] Go back to `evm-backend` service
- [ ] Click "Variables" tab

---

## Step 3: Add Environment Variables

### Database Variables (COPY-PASTE from MySQL Connect tab)
- [ ] `SPRING_DATASOURCE_URL` = `jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`
- [ ] `SPRING_DATASOURCE_USERNAME` = `root`
- [ ] `SPRING_DATASOURCE_PASSWORD` = (from MySQL password)
- [ ] `SPRING_DATASOURCE_DRIVER_CLASS_NAME` = `com.mysql.cj.jdbc.Driver`

### Server Configuration
- [ ] `SERVER_PORT` = `8080`
- [ ] `SPRING_APPLICATION_NAME` = `EVMBackend`

### JWT Configuration
- [ ] `APP_JWT_SECRET` = (your generated secret)
- [ ] `APP_JWT_EXPIRATION_MS` = `120000000`

### JPA Configuration
- [ ] `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
- [ ] `SPRING_JPA_DATABASE_PLATFORM` = `org.hibernate.dialect.MySQLDialect`
- [ ] `SPRING_JPA_SHOW_SQL` = `false`
- [ ] `SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL` = `false`

### Google OAuth
- [ ] `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` = (your Google Client ID)
- [ ] `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` = (your Google Client Secret)
- [ ] `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_SCOPE` = `openid,profile,email`
- [ ] `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI` = `https://[SERVICE_NAME].up.railway.app/login/oauth2/code/google`
- [ ] `APP_OAUTH2_FRONTEND_REDIRECT_URL` = `https://[YOUR_FRONTEND_DOMAIN]/oauth2/callback`

### Email Configuration
- [ ] `SPRING_MAIL_HOST` = `smtp.gmail.com`
- [ ] `SPRING_MAIL_PORT` = `587`
- [ ] `SPRING_MAIL_USERNAME` = (your Gmail address)
- [ ] `SPRING_MAIL_PASSWORD` = (your Gmail App Password)
- [ ] `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH` = `true`
- [ ] `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE` = `true`
- [ ] `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED` = `true`
- [ ] `APP_MAIL_FROM` = (your Gmail address)

### OTP Configuration
- [ ] `APP_OTP_TTL_MS` = `300000`
- [ ] `APP_OTP_RESEND_COOLDOWN_MS` = `60000`

### CORS Configuration
- [ ] `APP_CORS_ALLOWED_ORIGINS` = `https://your-frontend.com,https://www.your-frontend.com`

### Logging
- [ ] `LOGGING_LEVEL_ROOT` = `WARN`
- [ ] `LOGGING_LEVEL_COM_ELECTION_EVM` = `INFO`

---

## Step 4: Update Code for Production

### Option A: Update SecurityConfig.java (Recommended)
- [ ] Replace original `SecurityConfig.java` with `SecurityConfig-Updated.java`
- [ ] OR manually add CORS from environment variable configuration
- [ ] Rebuild locally: `mvn clean package -DskipTests`
- [ ] Commit to git

### Option B: Manual Update
If you prefer to manually update the existing SecurityConfig.java:
- [ ] Open `src/main/java/com/election/evm/config/SecurityConfig.java`
- [ ] Replace the hardcoded CORS configuration with the CorsConfigFromEnv bean
- [ ] See `SecurityConfig-Updated.java` for reference

---

## Step 5: Verify Deployment

### Check Service Status
- [ ] Go to Railway dashboard
- [ ] Click `evm-backend` service
- [ ] Status shows "Running" ✅
- [ ] No red errors in Logs tab

### Check Logs for Errors
- [ ] Click "Logs" tab
- [ ] Scroll to bottom
- [ ] Look for "Started EVMBackendApplication" message ✅
- [ ] NO "Connection refused" errors
- [ ] NO "Port already in use" errors
- [ ] NO "404" errors (unless expected)

### Get Service Domain
- [ ] Click "Settings" tab
- [ ] Under "Networking", find your domain
- [ ] Format: `https://evm-backend-xyz.up.railway.app`
- [ ] Service Domain: `_____________________` (save this)

---

## Step 6: Test API Endpoints

### Test 1: Health Check
```bash
DOMAIN=https://evm-backend-xyz.up.railway.app

curl -X GET "$DOMAIN/swagger-ui.html"
# Expected: 200 OK with HTML content
```
- [ ] Returns HTTP 200

### Test 2: Test Login Endpoint
```bash
curl -X POST "$DOMAIN/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "wrongpassword"
  }'
# Expected: 401 Unauthorized or "User not found"
```
- [ ] Returns a response (not 500)

### Test 3: Verify JWT Secret
- [ ] If previous test returned "Invalid JWT" error, JWT secret is working ✅

### Test 4: Database Connection
- [ ] Check logs in Railway dashboard
- [ ] NO "Cannot get a connection" errors
- [ ] NO "connection refused" errors

### Test 5: CORS Configuration
- [ ] Test from your frontend domain:
```bash
curl -X OPTIONS "https://your-domain.com" \
  -H "Origin: https://your-frontend.com" \
  -H "Access-Control-Request-Method: GET"
# Should return CORS headers
```
- [ ] CORS headers present (or test from frontend)

---

## Step 7: Test Authentication Flows

### Test JWT Login Flow
- [ ] Register a test user through frontend
- [ ] Login with credentials
- [ ] Receive JWT token ✅
- [ ] Token is valid and not expired

### Test Google OAuth Flow
- [ ] Update Google Console OAuth redirect URI:
  - [ ] Google Cloud Console
  - [ ] Credentials → OAuth 2.0 Client IDs → Edit
  - [ ] Add: `https://evm-backend-xyz.up.railway.app/login/oauth2/code/google`
  - [ ] Save
- [ ] Test OAuth login from frontend
- [ ] Redirects to Google login ✅
- [ ] After Google auth, redirects to frontend with token ✅

### Test OTP Email
- [ ] Request OTP through frontend
- [ ] Check email (Gmail inbox)
- [ ] OTP received successfully ✅
- [ ] OTP is valid for 5 minutes

---

## Step 8: Connect GitHub for Auto-Deployment

### Enable Auto-Deploy from GitHub
- [ ] Go to `evm-backend` service
- [ ] Click "Deployments" tab
- [ ] Click "Connect Repository" (if not already connected)
- [ ] Select repository and branch
- [ ] Enable "Auto Deploy" toggle
- [ ] Every push to this branch will auto-deploy

**Options:**
- [ ] Deploy from `main` branch
- [ ] Deploy from `development` branch
- [ ] Create a deploy workflow in GitHub

---

## Step 9: Production Verification

### Performance
- [ ] Login response time < 1 second
- [ ] Database queries are fast
- [ ] Check logs for slow queries

### Security
- [ ] All traffic is HTTPS ✅ (Railway enforces)
- [ ] JWT secret is strong and unique ✅
- [ ] OAuth credentials are not in logs
- [ ] Gmail password is App Password, not main password ✅
- [ ] CORS only allows your frontend domain ✅

### Reliability
- [ ] Service auto-restarts on crash ✅ (Railway feature)
- [ ] Database backups are enabled (Railway) ✅
- [ ] Logs are accessible for debugging ✅

---

## Step 10: Monitoring & Maintenance

### Daily Checks
- [ ] Service status is "Running"
- [ ] No error logs in Railway dashboard
- [ ] Response times are normal

### Weekly Checks
- [ ] Review error logs for patterns
- [ ] Check database size growth
- [ ] Verify OTP emails are sending

### Monthly Checks
- [ ] Update dependencies: `mvn versions:display-updates`
- [ ] Review security patches
- [ ] Audit environment variables (no exposed secrets)
- [ ] Verify backup and recovery procedures

---

## Common Issues & Quick Fixes

### Issue: Build fails in Railway but succeeds locally
**Solution:**
1. Check Java version: pom.xml should have Java 21
2. Check Maven: `mvn clean package -DskipTests` locally
3. Look at Railway build logs for specific error
4. Rebuild locally and push to GitHub

### Issue: MySQL Connection Failed
**Solution:**
1. Verify `SPRING_DATASOURCE_URL` format
2. Check MySQL service is "Running" in Railway
3. Try internal URL: `jdbc:mysql://mysql:3306/railway`
4. Verify username/password

### Issue: OAuth Redirect URI Mismatch
**Solution:**
1. Get correct Railway domain from dashboard
2. Update Google Console with exact URI
3. Update `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI`
4. Restart service or redeploy

### Issue: Emails not sending
**Solution:**
1. Use Gmail App Password, not regular password
2. Verify 2FA is enabled on Gmail account
3. Check SMTP credentials in variables
4. Restart service

---

## Rollback Procedure

If something goes wrong:

1. **Check Logs First**
   - Go to Railway dashboard → Logs
   - Look for error messages

2. **Rollback to Previous Deployment**
   - Go to Deployments tab
   - Find previous successful deployment
   - Click "Redeploy"

3. **Revert Code Changes**
   ```bash
   git revert [commit-hash]
   git push
   # Service will auto-redeploy
   ```

4. **Quick Fix Variables**
   - Go to Variables tab
   - Fix any incorrect environment variables
   - Service auto-restarts

---

## Final Checklist

- [ ] All environment variables set correctly
- [ ] Service is Running
- [ ] API endpoints respond
- [ ] Database connection works
- [ ] JWT authentication works
- [ ] Google OAuth configured
- [ ] CORS allows frontend domain
- [ ] Emails send successfully
- [ ] Logs show no critical errors
- [ ] GitHub auto-deploy is enabled
- [ ] Backup procedure documented
- [ ] Team members notified of deployment

---

## Support & Next Steps

1. **Monitor Logs Daily**: Railway Dashboard → Logs
2. **Set Up Alerts** (optional): Railroad project integrations
3. **Document Secrets**: Keep JWT secret, API keys in secure location
4. **Plan Scaling**: Monitor resource usage, upgrade if needed
5. **Enable HTTPS** (done automatically by Railway ✅)

Deployment Complete! 🚀
