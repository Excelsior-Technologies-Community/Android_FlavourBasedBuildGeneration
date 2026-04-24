# Play Store Service Account Setup Guide

This guide explains how to create and configure the Play Store service account JSON key for automated deployments to Google Play Store via Fastlane and GitHub Actions.

## Table of Contents
- [What is the Service Account JSON Key?](#what-is-the-service-account-json-key)
- [Why is it Needed?](#why-is-it-needed)
- [Prerequisites](#prerequisites)
- [Step-by-Step Setup](#step-by-step-setup)
- [Adding to GitHub Secrets](#adding-to-github-secrets)
- [How It Works in CI/CD](#how-it-works-in-cicd)
- [Troubleshooting](#troubleshooting)

---

## What is the Service Account JSON Key?

The `play-store-service-account.json` is a Google Cloud service account key file that allows automated tools (like Fastlane) to interact with the Google Play Console API on your behalf. It contains:

- Service account email address
- Private key for authentication
- Project ID
- Other authentication details

**⚠️ IMPORTANT**: This file contains sensitive credentials. Never commit it to your repository.

---

## Why is it Needed?

- **Automated Deployments**: Enables CI/CD pipelines to upload AAB files to Play Store without manual intervention
- **Fastlane Integration**: Fastlane uses this key to authenticate with Google Play API
- **Multi-track Deployment**: Allows deploying to internal, alpha, beta, and production tracks automatically

---

## Prerequisites

Before starting, ensure you have:

1. ✅ A Google Play Console account with at least one app created
2. ✅ Access to Google Cloud Console (console.cloud.google.com)
3. ✅ Owner or Admin permissions in Google Cloud project
4. ✅ Developer permissions in Google Play Console

---

## Step-by-Step Setup

### Step 1: Access Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Sign in with your Google account (the same one used for Play Console)
3. If prompted, select an existing project or create a new one:
   - Click the project dropdown at the top
   - Click "NEW PROJECT"
   - Enter a project name (e.g., "play-store-deployment")
   - Click "CREATE"

### Step 2: Create a Service Account

1. In the Google Cloud Console, navigate to:
   - **IAM & Admin** → **Service Accounts**
   - Or search for "Service Accounts" in the search bar

2. Click **"Create Service Account"** at the top

3. Fill in the service account details:
   - **Service account name**: `play-store-publisher` (or any descriptive name)
   - **Service account ID**: Auto-generated based on name
   - **Service account description**: `Service account for Play Store deployments via CI/CD`

4. Click **"Create and Continue"**

5. **Skip adding roles** for now (we'll add permissions later)
   - Click **"Done"** or **"Continue"** to skip role assignment

6. Note the service account email (format: `play-store-publisher@project-id.iam.gserviceaccount.com`)
   - Copy this email - you'll need it for Play Console setup

### Step 3: Generate JSON Key

1. Click on the service account you just created from the list

2. Go to the **"Keys"** tab

3. Click **"Add Key"** → **"Create New Key"**

4. Select **"JSON"** as the key type

5. Click **"Create"**

6. The JSON file will automatically download to your computer
   - Rename it to `play-store-service-account.json` for consistency
   - **Store this file securely** - it contains sensitive credentials

### Step 4: Grant Permissions in Google Play Console

Now you need to grant this service account access to your app in Play Console.

#### Option A: Via Play Console API Access (Recommended)

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your app
3. Navigate to one of these locations (UI varies):
   - **Setup** → **API access**
   - **Testing & Release** → **Setup** → **API access**
   - **Settings** → **API access**
   - **Developer account** → **API access**

4. Click **"Link service account"** or **"Create service account"**

5. Enter the service account email you copied earlier
   - Format: `play-store-publisher@project-id.iam.gserviceaccount.com`

6. Grant the following permissions:
   - **Release Management** → Edit
   - **Store Listing** → Edit (optional, for metadata uploads)
   - **Finance** → View (optional, for revenue data)

7. Click **"Grant access"** or **"Link"**

#### Option B: Via Users & Permissions

If API access option is not available:

1. In Play Console, look for:
   - **Settings** → **Users & permissions**
   - **Developer account** → **Users & permissions**
   - Or search for "Users" or "Permissions"

2. Click **"Invite users"** or **"Add user"**

3. Enter the service account email

4. Assign appropriate permissions (similar to Option A)

5. Send invitation

### Step 5: Verify Service Account Access

1. In Play Console, check that the service account appears in:
   - API access section
   - Users & permissions list

2. Ensure it has the correct permissions for your app

---

## Adding to GitHub Secrets

Once you have the JSON key file, add it to your GitHub repository secrets.

### Step 1: Navigate to Repository Secrets

1. Go to your GitHub repository
2. Click **Settings** tab
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. Click **"New repository secret"**

### Step 2: Add the Secret

1. **Name**: `PLAY_JSON_KEY_DATA`
2. **Secret**: Paste the entire content of the `play-store-service-account.json` file
   - Open the JSON file in a text editor
   - Copy everything from `{` to `}`
   - Paste it into the secret value field
3. Click **"Add secret"**

### Step 3: Verify the Secret

1. The secret should now appear in the list with name `PLAY_JSON_KEY_DATA`
2. The value is masked (shown as `****`) for security

### Example JSON Content

Your secret should contain something like this (do not use these values):

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "play-store-publisher@your-project-id.iam.gserviceaccount.com",
  "client_id": "client-id",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/play-store-publisher%40your-project-id.iam.gserviceaccount.com"
}
```

---

## How It Works in CI/CD

### Workflow Integration

The GitHub Actions workflow uses this secret in two steps:

#### 1. Decode the JSON Key

```yaml
- name: Decode Play Store JSON Key
  run: |
    echo "${{ secrets.PLAY_JSON_KEY_DATA }}" > play-store-service-account.json
```

This step:
- Reads the `PLAY_JSON_KEY_DATA` secret
- Writes it to a file named `play-store-service-account.json` in the workflow runner

#### 2. Use with Fastlane

```yaml
- name: Deploy to Play Store
  env:
    PLAY_JSON_KEY_PATH: "play-store-service-account.json"
  run: |
    fastlane deploy_internal
```

Fastlane then:
- Reads the JSON file from `PLAY_JSON_KEY_PATH`
- Uses it to authenticate with Google Play API
- Uploads the AAB to the specified track

### Fastfile Configuration

The Fastfile reads the JSON key:

```ruby
lane :deploy_internal do
  json_key_path = ENV["PLAY_JSON_KEY_PATH"] || "play-store-service-account.json"
  json_key_content = File.read(json_key_path) if File.exist?(json_key_path)
  
  upload_to_play_store(
    aab: "../app/build/outputs/bundle/prodRelease/app-prod-release.aab",
    track: "internal",
    json_key: json_key_content,
    # ... other parameters
  )
end
```

---

## Troubleshooting

### Error: "Unauthorized (401)"

**Cause**: Invalid or missing credentials

**Solution**:
1. Verify the `PLAY_JSON_KEY_DATA` secret contains the complete JSON
2. Check that the service account has proper permissions in Play Console
3. Ensure the service account email is correct

### Error: "Permission denied"

**Cause**: Service account lacks required permissions

**Solution**:
1. In Play Console, verify the service account has:
   - Release Management → Edit permission
2. Re-grant permissions if needed
3. Wait a few minutes for permissions to propagate

### Error: "Service account not found"

**Cause**: Service account email is incorrect or not linked

**Solution**:
1. Verify the service account email in the JSON matches what's in Play Console
2. Re-link the service account in Play Console API access section
3. Check that the correct project is selected in Google Cloud Console

### Error: "File not found"

**Cause**: JSON key file not created in workflow

**Solution**:
1. Verify the decode step is present in workflow
2. Check that the secret name is exactly `PLAY_JSON_KEY_DATA`
3. Ensure the secret value is not empty

### Error: "Invalid JSON format"

**Cause**: JSON content is malformed or incomplete

**Solution**:
1. Open the JSON file in a text editor
2. Verify it's valid JSON (use a JSON validator)
3. Ensure you copied the entire file content, not just part of it
4. Re-add the secret with the complete JSON content

### Error: "API access not enabled"

**Cause**: Google Play API is not enabled for the project

**Solution**:
1. Go to Google Cloud Console
2. Navigate to **APIs & Services** → **Library**
3. Search for "Google Play Android Developer API"
4. Click **"Enable"**

---

## Security Best Practices

1. **Never commit the JSON file** to your repository
2. **Add to .gitignore**: Ensure `*.json` or specifically `play-store-service-account.json` is in your .gitignore
3. **Rotate keys regularly**: Consider regenerating the key periodically for security
4. **Limit permissions**: Grant only the minimum required permissions
5. **Monitor access**: Regularly review who has access to your service account
6. **Use separate keys**: Use different service accounts for different environments if possible

---

## Summary Checklist

- [ ] Created service account in Google Cloud Console
- [ ] Generated JSON key file
- [ ] Linked service account in Play Console with proper permissions
- [ ] Added `PLAY_JSON_KEY_DATA` secret to GitHub repository
- [ ] Verified workflow can decode and use the JSON key
- [ ] Tested deployment to internal track
- [ ] Documented the setup for team reference

---

## Additional Resources

- [Google Play Console API Documentation](https://developers.google.com/android-publisher)
- [Fastlane Google Play Integration](https://docs.fastlane.tools/actions/upload_to_play_store/)
- [Google Cloud Service Accounts](https://cloud.google.com/iam/docs/service-accounts)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

## Support

If you encounter issues not covered in this guide:

1. Check the GitHub Actions workflow logs for detailed error messages
2. Verify all steps in this guide were completed correctly
3. Review the Google Play Console and Cloud Console documentation
4. Consult your team's DevOps or security team for permission-related issues
