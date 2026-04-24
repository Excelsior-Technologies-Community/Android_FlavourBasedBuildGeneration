# Quick Start Guide - Android CI/CD to Play Store

This guide will help you set up automated AAB builds and deployments to Google Play Store in under 15 minutes.

## Prerequisites Checklist

- [ ] GitHub repository with this Android project
- [ ] Google Play Console account with app created
- [ ] App manually uploaded to Play Console at least once
- [ ] Play App Signing enabled in Play Console

## Step 1: Google Play Console API Setup (5 minutes)

1. Go to [Play Console](https://play.google.com/console)
2. Navigate to **Setup → API access**
3. Click **Link a Google Cloud project**
4. Create a new Google Cloud project or select existing
5. In Google Cloud Console, create a **Service Account**
   - Go to IAM & Admin → Service Accounts
   - Click "Create Service Account"
   - Give it a name (e.g., "play-store-uploader")
6. Download the **JSON key** for this service account
7. Return to Play Console → Setup → API access
8. Grant **Release Manager** permissions to the service account

## Step 2: Prepare Keystore (2 minutes)

If you already have a keystore, skip to Step 3.

```bash
# Generate keystore
keytool -genkey -v -keystore your_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias your_key_alias

# Note down these values:
# - Keystore password
# - Key alias
# - Key password
```

## Step 3: Configure GitHub Secrets (5 minutes)

Go to your GitHub repository: **Settings → Secrets and variables → Actions → New repository secret**

Add these 5 secrets:

### 1. PLAY_JSON_KEY_DATA
- Open the JSON key file downloaded in Step 1
- Copy the entire JSON content
- Paste as the secret value

### 2. KEYSTORE_BASE64
```bash
# On macOS
base64 -i your_keystore.jks | pbcopy

# On Linux
base64 -w 0 your_keystore.jks
```
- Paste the output as the secret value

### 3. KEYSTORE_PASSWORD
- Your keystore password (e.g., `123456`)

### 4. KEY_ALIAS
- Your key alias (e.g., `your_key_alias`)

### 5. KEY_PASSWORD
- Your key password (e.g., `123456`)

## Step 4: Test the Setup (3 minutes)

### Option A: Automatic Deployment (Recommended for testing)
```bash
git add .
git commit -m "Setup CI/CD pipeline"
git push origin main
```

This will automatically:
- Build AAB for production flavor
- Deploy to **internal** testing track
- Use auto-generated version code

### Option B: Manual Deployment
1. Go to GitHub Actions tab
2. Select "Android CI/CD - Play Store Release"
3. Click "Run workflow"
4. Choose track: `internal` (for testing)
5. Click "Run workflow"

## Step 5: Verify Deployment

1. Go to [Play Console](https://play.google.com/console)
2. Navigate to **Testing → Internal testing**
3. You should see your new AAB uploaded
4. Check the version code and version name

## Troubleshooting Quick Fixes

### "Permission denied" from Play Console
- Service account needs **Release Manager** permissions
- Re-check Play Console → Setup → API access

### "Keystore tampered with" error
- Re-encode keystore to base64
- Test locally: `base64 -d keystore.b64 > test.jks && keytool -list -v -keystore test.jks`

### "App not found" error
- Package name must match Play Console app exactly
- Current package: `com.ext.flavourbasedbuildtest`

### Build fails
- Check all 5 secrets are set correctly
- Verify keystore passwords match secrets
- Review GitHub Actions logs for specific errors

## Next Steps

After successful internal testing:

1. **Alpha Testing**: Run workflow with track `alpha`
2. **Beta Testing**: Run workflow with track `beta`
3. **Production**: Run workflow with track `production` and rollout `0.1` (10%)

## Advanced Options

### Custom Changelog
When running workflow manually, add changelog:
```
Bug fixes and performance improvements
```

### Staged Rollout
For production, set rollout percentage:
- `0.1` = 10% of users
- `0.5` = 50% of users
- `1.0` = 100% of users

### Local Testing with Fastlane
```bash
# Install Fastlane
brew install fastlane
gem install fastlane

# Test locally (requires PLAY_JSON_KEY_DATA env var)
export PLAY_JSON_KEY_DATA="$(cat your-service-account.json)"
fastlane deploy_internal
```

## Support

For detailed troubleshooting, see [SECRETS_SETUP.md](./SECRETS_SETUP.md)

## Key Features of This Implementation

✅ **Auto Versioning**: Never fails due to duplicate version codes
✅ **Multiple Tracks**: internal, alpha, beta, production
✅ **Staged Rollout**: Gradual production releases
✅ **Manual Control**: Override defaults via workflow inputs
✅ **Artifact Retention**: AABs saved for 30 days
✅ **Error Handling**: Clear error messages and validation
✅ **Security**: No secrets in code, all in GitHub Secrets
