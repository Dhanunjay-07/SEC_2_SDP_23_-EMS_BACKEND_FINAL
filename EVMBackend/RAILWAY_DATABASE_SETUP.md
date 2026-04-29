# Railway Database Environment Variables Setup

## 🚂 Copy-Paste These Variables Into Railway Dashboard

### Step 1: Go to Railway Dashboard
1. Open: https://railway.app
2. Click your **EVM Project**
3. Click **evm-backend** service
4. Go to **Variables** tab

### Step 2: Add Database Variables

**Copy and paste EXACTLY these values:**

```
SPRING_DATASOURCE_URL=jdbc:mysql://root:bpfmRrVuKtDGvYqVqbDqLuPBRECcWobz@tramway.proxy.rlwy.net:11201/railway?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
```

```
SPRING_DATASOURCE_USERNAME=root
```

```
SPRING_DATASOURCE_PASSWORD=bpfmRrVuKtDGvYqVqbDqLuPBRECcWobz
```

### Step 3: Save
Click **Save** button in Railway

---

## ✅ Verification

The application will now:
1. Read `SPRING_DATASOURCE_URL` from Railway environment
2. Connect to your Railway MySQL database
3. Keep credentials **hidden from git** ✅
4. Keep credentials **encrypted in Railway** ✅

---

## 🔍 How It Works

**application.properties** (in git):
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/evm?...}
```

**Railway Environment** (NOT in git):
```
SPRING_DATASOURCE_URL=jdbc:mysql://root:bpfmRrVuKtDGvYqVqbDqLuPBRECcWobz@tramway.proxy.rlwy.net:11201/railway?...
```

**Application behavior**:
- ✅ On Railway: Uses environment variable (Railway database)
- ✅ On Local: Uses default (localhost database)
- ✅ Credentials never in git history
- ✅ Safe to commit to GitHub

---

## 🔐 Security Summary

| Location | Contains Credentials | Safe? |
|----------|----------------------|-------|
| application.properties (git) | NO | ✅ YES |
| Railway Variables | YES | ✅ YES (encrypted) |
| Your Local Database | NO (uses localhost) | ✅ YES |

---

## 📍 After Setup

1. Commit and push code to GitHub (no secrets!)
2. Railway automatically picks up environment variables
3. Application connects to Railway MySQL database
4. You're done! 🚀

---

## ⚠️ Important Notes

- **Never commit** the actual credentials in code
- **Always use** environment variables for production
- **Change password** in Railway if compromised
- **Different passwords** for different environments (dev/prod)

---

Done! Your database is now using environment variables securely. 🔒
