# GitHub Secrets Configuration for CI/CD - Play Store Release

To use the CD workflow for building and deploying AABs to Google Play Store, you need to configure the following secrets in your GitHub repository.

## Prerequisites (One-Time Setup)

### Step 1: Google Play Console API Setup

1. **Go to Play Console**: https://play.google.com/console
2. **Navigate to**: Setup → API access
3. **Link a Google Cloud project** to your Play Console app
4. **Create a Service Account** in your Google Cloud project
5. **Download the JSON key** for the service account
6. **Grant permissions** to the service account in Play Console:
   - Go to Setup → API access → Service accounts
   - Select your service account
   - Grant **Release Manager** permissions (required for uploads)
7. **Important**: Your app must be manually published at least once before CI/CD can work
8. **Ensure Play App Signing is enabled** for your app

### Step 2: Prepare Your Keystore

If you don't have a keystore yet, create one:

```bash
keytool -genkey -v -keystore your_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias your_key_alias
```

## Required Secrets

Navigate to your GitHub repository: Settings → Secrets and variables → Actions → New repository secret

### 1. PLAY_JSON_KEY_DATA (CRITICAL for Play Store upload)

The JSON key file content from your Google Cloud Service Account.

**How to set:**
- Open the downloaded JSON key file in a text editor
- Copy the entire JSON content
- Paste it as the value for `PLAY_JSON_KEY_DATA` secret

**Example format:**
```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "...",
  "private_key": "...",
  "client_email": "...",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token"
}
```

### 2. KEYSTORE_BASE64
Your keystore file encoded in base64.

**How to generate:**
```bash
base64 -i your_keystore.jks | pbcopy  # macOS
base64 -w 0 your_keystore.jks           # Linux
```

**Example:**
Copy the output and paste it as the value for `KEYSTORE_BASE64` secret.

### 2. KEYSTORE_PASSWORD
The password for your keystore file.

**Example:**
If your keystore password is `123456`, set this secret to `123456`.

### 3. KEY_ALIAS
The alias of the key in your keystore.

**Example:**
If your key alias is `your_key_alias`, set this secret to `your_key_alias`.

### 4. KEY_PASSWORD
The password for the key in your keystore.

**Example:**
If your key password is `123456`, set this secret to `123456`.

## Current Configuration Reference

Based on your current `app/build.gradle.kts`:
- Store file: `your_keystore.jks` (or `app/release.keystore` in CI)
- Store password: `123456` (or from KEYSTORE_PASSWORD secret)
- Key alias: `your_key_alias` (or from KEY_ALIAS secret)
- Key password: `123456` (or from KEY_PASSWORD secret)
- Package name: `com.ext.flavourbasedbuildtest`

## Complete Secrets Checklist

- [ ] `PLAY_JSON_KEY_DATA` - Service account JSON for Play Console API
- [ ] `KEYSTORE_BASE64` - Base64-encoded keystore file
- [ ] `KEYSTORE_PASSWORD` - Keystore password
- [ ] `KEY_ALIAS` - Key alias in keystore
- [ ] `KEY_PASSWORD` - Key password

## Security Best Practices

1. **Never commit keystore files** to your repository
2. **Never commit JSON key files** to your repository
3. **Use different passwords** for keystore and key
4. **Rotate secrets periodically**
5. **Use environment-specific keystores** if possible
6. **Limit secret access** to necessary team members only
7. **Enable GitHub secret scanning** for your repository

## Testing Secrets Locally

To test your secrets configuration locally before pushing:

```bash
# Test that your keystore and passwords work
keytool -list -v -keystore your_keystore.jks -storepass 123456 -alias your_key_alias -keypass 123456

# Test base64 encoding/decoding
base64 -i your_keystore.jks > keystore.b64
base64 -d keystore.b64 > keystore_decoded.jks
diff your_keystore.jks keystore_decoded.jks
# Should output nothing if encoding is correct
```

## Workflow Usage

### Automatic Deployment (on push to main)
Simply push to the `main` branch:
```bash
git push origin main
```
This will:
- Build AAB for production flavor
- Deploy to **internal** testing track
- Use auto-generated version code (timestamp-based)

### Manual Deployment with Options
Go to GitHub Actions → Select "Android CI/CD - Play Store Release" → Click "Run workflow"

Options:
- **Track**: internal, alpha, beta, or production
- **Rollout**: For production only (0.01 to 1.0, e.g., 0.1 for 10% rollout)
- **Changelog**: Release notes for the Play Store

## Version Management

The workflow uses **auto versioning** to prevent Play Store rejection:
- Version code: Auto-generated using Unix timestamp (always unique)
- Version name: Format `1.0.{version_code}`

This ensures every build has a unique version code, preventing upload failures.

## Troubleshooting

### Play Console API Errors

**"Permission denied" or "403 Forbidden"**
- Verify service account has **Release Manager** permissions
- Check that the service account is linked to the correct app
- Ensure the JSON key is valid and not expired

**"APK/AAB already exists"**
- This shouldn't happen with auto versioning
- If it occurs, check if version code generation is working
- Verify the workflow is using the generated version code

**"App not found"**
- Verify package name matches your Play Console app
- Ensure the app is created in Play Console
- Check that the service account has access to the correct app

### Keystore Errors

**"Keystore tampered with" error**
- Ensure the base64 encoding is correct
- Verify the keystore file wasn't corrupted during encoding
- Test encoding/decoding locally first

**"Invalid keystore format" error**
- Check that you're using the correct keystore file
- Verify the base64 decoding is working properly
- Ensure the keystore is a valid JKS file

**"Wrong password" error**
- Verify KEYSTORE_PASSWORD matches your keystore
- Verify KEY_PASSWORD matches your key alias
- Check for extra spaces or special characters in secrets

### Build Errors

**"Build failed during signing"**
- Verify all four keystore secrets are set correctly
- Check that the key alias matches your keystore
- Ensure passwords match your keystore configuration
- Check Gradle logs for specific error details

**"AAB file not found"**
- Verify the build completed successfully
- Check the AAB path in the workflow matches your build output
- Ensure the production flavor is building correctly

### Fastlane Errors

**"Fastlane not found"**
- Verify Ruby is installed correctly
- Check that Fastlane gem installed successfully
- Review the Fastlane installation step in workflow logs

**"Upload to Play Store failed"**
- Verify PLAY_JSON_KEY_DATA is set correctly
- Check network connectivity from GitHub Actions runner
- Review Fastlane logs for specific error details
- Ensure the track (internal/alpha/beta/production) exists in Play Console

## Important Notes

### First Release Requirement
**You MUST upload your app manually to Play Console at least once before CI/CD will work.**
- Create your app in Play Console
- Upload the first AAB manually
- Complete the initial setup questionnaire
- After this, CI/CD can handle subsequent releases

### Track Progression
Recommended progression for new apps:
1. **internal** - For your own testing (default)
2. **alpha** - For trusted testers
3. **beta** - For open testing
4. **production** - For public release (with staged rollout)

### Play App Signing
Ensure **Play App Signing** is enabled:
- Go to Play Console → Setup → App signing
- If not enabled, enable it before using CI/CD
- This is required for automatic updates

## Additional Resources

- [Fastlane Documentation](https://docs.fastlane.tools/)
- [Google Play Console API](https://developers.google.com/android-publisher)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android App Bundle Guide](https://developer.android.com/guide/app-bundle)
