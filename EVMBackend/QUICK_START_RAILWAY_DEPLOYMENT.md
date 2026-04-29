# Complete Railway Deployment for Spring Boot + MySQL

## 🚀 Quick Start (5 Steps)

### Step 1: Prepare Code (5 minutes)
```bash
cd BACKEND/EVMBackend
mvn clean package -DskipTests
# Should complete without errors
```

### Step 2: Generate Secrets
```bash
# JWT Secret
openssl rand -hex 32
# Save output - you'll need it

# Gmail App Password
# Go to: myaccount.google.com → Security → App passwords
# Generate for Mail/Windows
# Save output
```

### Step 3: Create Railway Service
- Go to Railway Dashboard → "+ New"
- Select "GitHub Repo"
- Choose your repository
- **Set root: `BACKEND/EVMBackend`**
- Name: `evm-backend`
- Deploy

### Step 4: Add Environment Variables
In Railway Dashboard → Your Service → Variables:
```
# Database (from MySQL service)
SPRING_DATASOURCE_URL=jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=[from-mysql-service]
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# Server
SERVER_PORT=8080

# JWT (use generated secret from Step 2)
APP_JWT_SECRET=[your-generated-secret]
APP_JWT_EXPIRATION_MS=86400000

# Google OAuth
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=[your-client-id]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=[your-secret]
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI=https://[service-name].up.railway.app/login/oauth2/code/google
APP_OAUTH2_FRONTEND_REDIRECT_URL=https://[your-frontend-domain]/oauth2/callback

# Email (use Gmail App Password from Step 2)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=[your-gmail]
SPRING_MAIL_PASSWORD=[gmail-app-password]
APP_MAIL_FROM=[your-gmail]

# CORS
APP_CORS_ALLOWED_ORIGINS=https://[your-frontend-domain]

# Logging
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_ELECTION_EVM=INFO
```

### Step 5: Test Endpoints
```bash
DOMAIN=https://[service-name].up.railway.app

# Health check
curl "$DOMAIN/swagger-ui.html"

# Test login (should fail with user not found - that's OK)
curl -X POST "$DOMAIN/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}'
```

✅ Done! Your backend is deployed.

---

## 📋 Files Created

| File | Purpose |
|------|---------|
| `RAILWAY_DEPLOYMENT_GUIDE.md` | Complete step-by-step deployment guide |
| `RAILWAY_DEPLOYMENT_CHECKLIST.md` | Detailed checklist for all steps |
| `RAILWAY_ENVIRONMENT_VARIABLES_TEMPLATE.md` | Environment variables with explanations |
| `application-prod.properties` | Production properties using env vars |
| `Procfile` | Railroad deployment configuration |
| `CorsConfigFromEnv.java` | CORS from environment variables |
| `SecurityConfig-Updated.java` | Updated security config |
| `AUTHENTICATION_PRODUCTION_REVIEW.md` | Review of auth modules |

---

## 📌 Important: Before You Deploy

1. **DO NOT commit secrets to Git**
   - Remove any hardcoded passwords from code
   - Add secrets to `.gitignore`

2. **Generate new JWT secret**
   ```bash
   openssl rand -hex 32
   ```

3. **Get Gmail App Password**
   - Enable 2FA on Gmail
   - Generate App Password
   - Use that password, NOT your regular Gmail password

4. **Update Google OAuth**
   - Add redirect URI in Google Cloud Console
   - Use your Railway service domain

5. **Update CORS origins**
   - Set to your frontend domain
   - NOT localhost for production

---

## 🔍 Troubleshooting

### Build Fails in Railway
**Solution**: 
- Check `mvn clean package -DskipTests` works locally
- Verify Java 21 in pom.xml
- Check for compilation errors

### Database Connection Failed
**Solution**:
- Verify credentials from Railway MySQL service
- Check MySQL service is Running
- Try: `jdbc:mysql://mysql:3306/railway` (internal network)

### OAuth Redirect Mismatch
**Solution**:
- Get exact domain from Railway dashboard
- Update Google Console with: `https://[service].up.railway.app/login/oauth2/code/google`
- Update environment variable

### Emails Not Sending
**Solution**:
- Use Gmail App Password, NOT regular password
- Enable 2FA on Gmail account
- Verify email credentials are correct

---

## 📚 Full Documentation

See these files for complete details:
- **Deployment**: `RAILWAY_DEPLOYMENT_GUIDE.md`
- **Checklist**: `RAILWAY_DEPLOYMENT_CHECKLIST.md`
- **Variables**: `RAILWAY_ENVIRONMENT_VARIABLES_TEMPLATE.md`
- **Auth Review**: `AUTHENTICATION_PRODUCTION_REVIEW.md`

---

## ✅ What's Ready for Production

✅ Spring Boot 3.3.6 with Java 21  
✅ MySQL connection with environment variables  
✅ JWT authentication (24-hour expiration recommended)  
✅ Google OAuth2 with PKCE  
✅ Email/OTP verification  
✅ CORS configuration from environment  
✅ Spring Security with role-based access  
✅ Procfile for Railway  
✅ Application properties for production  

---

## 🔒 Security Notes

1. **JWT Secret**: Generate new, don't reuse
2. **Gmail Password**: Use App Password, enable 2FA
3. **OAuth Credentials**: Keep separate for dev/prod
4. **CORS Origins**: Only list necessary domains
5. **Logging**: Set to WARN for production
6. **HTTPS**: Railway enforces automatically ✅
7. **Database**: Password-protected ✅
8. **Secrets**: Use environment variables ✅

---

## 📞 Support & Next Steps

1. **Follow the Deployment Guide step-by-step**
2. **Use the Checklist to verify each step**
3. **Reference Environment Variables Template for copy-paste**
4. **Check Authentication Review for any questions**
5. **Monitor Logs in Railway dashboard after deployment**

Deploy with confidence! Your backend is production-ready. 🚀
