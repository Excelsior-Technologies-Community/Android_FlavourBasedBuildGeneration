# CI/CD Implementation Report - Android Flavour-Based Build System

**Project:** FlavourBasedBuildTest  
**Package Name:** com.ext.flavourbasedbuildtest  
**Report Date:** April 24, 2026  
**Current Version Code:** 22

---

## Executive Summary

This report documents the current CI/CD implementation for the Android flavour-based build system, including build configurations, GitHub Actions workflows, Fastlane automation, and Play Store deployment capabilities. The report identifies implementation gaps and provides actionable recommendations for improvements.

---

## Table of Contents

1. [Current Implementation Status](#current-implementation-status)
2. [Build Configuration](#build-configuration)
3. [CI/CD Workflows](#cicd-workflows)
4. [Required Implementations](#required-implementations)
5. [Recommended Enhancements](#recommended-enhancements)
6. [Change Scope and Impact](#change-scope-and-impact)
7. [Implementation Roadmap](#implementation-roadmap)

---

## Current Implementation Status

### Project Structure

The project implements a flavour-based Android build system with three environments:

- **Dev Flavour**
  - Package: `com.ext.flavourbasedbuildtest.dev`
  - Min SDK: 26
  - Debug features: Enabled
  - Analytics: Disabled
  - Crash reporting: Disabled

- **Staging Flavour**
  - Package: `com.ext.flavourbasedbuildtest.staging`
  - Min SDK: 28
  - Debug features: Enabled
  - Analytics: Enabled
  - Crash reporting: Enabled

- **Prod Flavour**
  - Package: `com.ext.flavourbasedbuildtest`
  - Min SDK: 30
  - Debug features: Disabled
  - Analytics: Enabled
  - Crash reporting: Enabled

### Technology Stack

- **Build System:** Gradle with Kotlin DSL
- **Language:** Kotlin
- **CI/CD Platform:** GitHub Actions
- **Deployment Tool:** Fastlane
- **Target SDK:** 36
- **Java Version:** 17 (Temurin distribution)
- **Ruby Version:** 3.2 (for Fastlane)

### Version Management

- **CI Environment:** Version code and name passed as environment variables from GitHub Actions
- **Local Environment:** Version read from `.version` and `.versioncode` files
- **Current Version Code:** 22
- **Version Format:** Semantic versioning (MAJOR.MINOR.PATCH)

---

## Build Configuration

### Signing Configuration

**File:** `app/build.gradle.kts`

```kotlin
signingConfigs {
    create("release") {
        storeFile = file(System.getenv("KEYSTORE_PATH") ?: "../your_keystore.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "123456"
        keyAlias = System.getenv("KEY_ALIAS") ?: "your_key_alias"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "123456"
    }
}
```

**Status:** Supports environment variables for CI/CD, but has hardcoded fallback values that should be removed.

### Version Management

**File:** `app/build.gradle.kts`

```kotlin
val ciVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull()
val ciVersionName = System.getenv("VERSION_NAME")

if (ciVersionCode != null && ciVersionName != null) {
    versionCode = ciVersionCode
    versionName = ciVersionName
} else {
    val versionFile = File(projectDir, ".version")
    val versionCodeFile = File(projectDir, ".versioncode")
    
    versionName = if (versionFile.exists()) {
        versionFile.readText().trim()
    } else {
        "1.0.0"
    }
    
    versionCode = if (versionCodeFile.exists()) {
        versionCodeFile.readText().trim().toIntOrNull() ?: 1
    } else {
        1
    }
}
```

**Status:** Flexible version management supporting both CI and local builds.

### Flavour BuildConfig Fields

**Dev Flavour:**
- BASE_URL: https://api.example.com/
- AUTH_KEY: dev_auth_key_xyz123
- ENABLE_DEBUG_FEATURES: true
- ENABLE_ANALYTICS: false
- ENABLE_CRASH_REPORTING: false
- ANALYTICS_ID: dev_analytics_id
- FIREBASE_PROJECT_ID: dev-project-id

**Staging Flavour:**
- BASE_URL: https://api.example.com/
- AUTH_KEY: staging_auth_key_abc456
- ENABLE_DEBUG_FEATURES: true
- ENABLE_ANALYTICS: true
- ENABLE_CRASH_REPORTING: true
- ANALYTICS_ID: staging_analytics_id
- FIREBASE_PROJECT_ID: staging-project-id

**Prod Flavour:**
- BASE_URL: https://api.example.com/
- AUTH_KEY: prod_auth_key_def789
- ENABLE_DEBUG_FEATURES: false
- ENABLE_ANALYTICS: true
- ENABLE_CRASH_REPORTING: true
- ANALYTICS_ID: prod_analytics_id
- FIREBASE_PROJECT_ID: prod-project-id

**Status:** Clear environment separation with feature flags. API keys are hardcoded and should be moved to secrets.

---

## CI/CD Workflows

### Workflow 1: Test Build (test-build.yml)

**Purpose:** Build AABs for all flavours without deploying to Play Store

**Triggers:**
- Push to `main` or `test` branches
- Manual workflow dispatch

**Process:**
1. Checkout code
2. Setup JDK 17 (Temurin)
3. Grant gradlew permissions
4. Decode keystore from base64 secret
5. Read current version (no increment)
6. Cache Gradle packages
7. Build AAB for Dev, Staging, and Production flavours
8. Verify AABs exist
9. Upload AABs as artifacts (30-day retention)
10. Generate build summary

**Status:** Functional for building all flavours. No deployment included.

### Workflow 2: Play Store Release (release.yml)

**Purpose:** Build AABs and deploy to Google Play Store

**Triggers:**
- Push to `main` or `test` branches
- Manual workflow dispatch with options

**Workflow Inputs:**
- Track: internal, alpha, beta, production (default: internal)
- Rollout: 0.01 to 1.0 (default: 1.0)
- Changelog: Release notes (default: "Automated release from CI/CD pipeline")

**Process:**
1. Checkout code
2. Setup JDK 17 (Temurin)
3. Setup Ruby 3.2 with bundler cache
4. Install Fastlane
5. Grant gradlew permissions
6. Decode keystore from base64 secret
7. Create Play Store JSON Key (currently uses dummy key)
8. Generate unique version code (increment patch)
9. Commit and push version files
10. Cache Gradle packages
11. Build AAB for Dev, Staging, and Production flavours
12. Verify AABs exist
13. Upload AABs as artifacts (30-day retention)
14. Deploy Dev to Play Store (internal track)
15. Deploy Staging to Play Store (internal track)
16. Deploy Production to Play Store (selected track)
17. Notify deployment status

**Status:** Play Console upload not functional due to dummy JSON key usage (line 76).

---

## Required Implementations

### Priority 1: Fix Play Console Upload (CRITICAL)

#### 1.1 Replace Dummy JSON Key with Secret

**File:** `.github/workflows/release.yml`  
**Line:** 76-77

**Current Code:**
```yaml
echo 'eyJ0eXBlIjoic2VydmljZV9hY2NvdW50IiwicHJvamVjdF9pZCI6ImR1bW15LXByb2plY3Qi...' | base64 -d > fastlane/play-store-service-account.json
echo "Created dummy JSON key file in fastlane directory for testing"
```

**Required Code:**
```yaml
echo "${{ secrets.PLAY_JSON_KEY_DATA }}" > fastlane/play-store-service-account.json
echo "Created Play Store service account JSON key file"
```

#### 1.2 Add Secret Validation

**Add after JSON key creation in release.yml:**
```yaml
- name: Validate Play Store JSON Key
  run: |
    if [ ! -s "fastlane/play-store-service-account.json" ]; then
      echo "Error: Play Store JSON key is empty or not set"
      echo "Please set PLAY_JSON_KEY_DATA secret in GitHub repository settings"
      exit 1
    fi
    echo "Play Store JSON key validated successfully"
```

#### 1.3 Update Fastlane to Use Environment Variable

**File:** `fastlane/Fastfile`  
**Update all lanes to use:**

```ruby
json_key_data = ENV["PLAY_JSON_KEY_DATA"]
if json_key_data && !json_key_data.empty?
  json_key = JSON.parse(json_key_data)
else
  json_key_path = ENV["PLAY_JSON_KEY_PATH"] || "play-store-service-account.json"
  json_key_content = File.read(json_key_path) if File.exist?(json_key_path)
  json_key = JSON.parse(json_key_content) if json_key_content
end

if json_key.nil?
  UI.error("No valid Play Store JSON key found")
  UI.error("Set PLAY_JSON_KEY_DATA environment variable or ensure JSON file exists")
  raise "Play Store authentication failed"
end
```

### Priority 2: Configure GitHub Secrets

#### Required Secrets

1. **PLAY_JSON_KEY_DATA** (CRITICAL - Currently Missing)
   - Description: Service account JSON key for Play Console API
   - Source: Google Cloud Console → Service Accounts → Create Key
   - Format: JSON string (entire content of JSON key file)

2. **KEYSTORE_BASE64** (Required)
   - Description: Base64-encoded keystore file
   - Source: `base64 -i your_keystore.jks`
   - Format: Base64 string

3. **KEYSTORE_PASSWORD** (Required)
   - Description: Keystore password
   - Source: Your keystore creation
   - Format: Plain text

4. **KEY_ALIAS** (Required)
   - Description: Key alias in keystore
   - Source: Your keystore creation
   - Format: Plain text

5. **KEY_PASSWORD** (Required)
   - Description: Key password
   - Source: Your keystore creation
   - Format: Plain text

#### Configuration Steps

1. Navigate to GitHub repository
2. Go to Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Add each secret with the appropriate value
5. Verify no typos in secret names

### Priority 3: Play Console App Setup

#### Register Package Names

**Action Required:** Create separate apps in Play Console for each flavour

1. **Main App (Prod):** `com.ext.flavourbasedbuildtest`
   - Enable Play App Signing
   - Complete store listing
   - Upload first AAB manually

2. **Dev App:** `com.ext.flavourbasedbuildtest.dev`
   - Create new app in Play Console
   - Enable Play App Signing
   - Upload first AAB manually
   - Grant service account permissions

3. **Staging App:** `com.ext.flavourbasedbuildtest.staging`
   - Create new app in Play Console
   - Enable Play App Signing
   - Upload first AAB manually
   - Grant service account permissions

#### Service Account Permissions

For each app in Play Console:
1. Go to Setup → API access
2. Select the service account
3. Grant **Release Manager** permissions
4. Verify API access is enabled

### Priority 4: Workflow Improvements

#### Add Pre-Deployment Validation

**Add to release.yml before deployment steps:**

```yaml
- name: Validate Deployment Readiness
  run: |
    echo "Validating deployment readiness..."
    
    for flavor in dev staging prod; do
      AAB_PATH="app/build/outputs/bundle/${flavor}Release/app-${flavor}-release.aab"
      if [ ! -f "$AAB_PATH" ]; then
        echo "Error: AAB not found for $flavor"
        exit 1
      fi
    done
    
    if [ ! -s "fastlane/play-store-service-account.json" ]; then
      echo "Error: Play Store JSON key not found or empty"
      exit 1
    fi
    
    echo "✅ All validations passed"
```

#### Add Conditional Deployment

**Modify deployment to be optional per flavour:**

```yaml
- name: Deploy Dev to Play Store
  if: github.event.inputs.deploy_dev != 'false'
  env:
    PLAY_JSON_KEY_PATH: "fastlane/play-store-service-account.json"
  run: |
    cd fastlane
    fastlane deploy_dev
```

#### Add Rollback Capability

**Add new lane to Fastfile:**

```ruby
desc "Rollback last release"
lane :rollback do
  track = ENV["TRACK"] || "internal"
  upload_to_play_store(
    track: track,
    rollback: true,
    json_key: json_key_content,
    package_name: "com.ext.flavourbasedbuildtest"
  )
end
```

### Priority 5: Security Hardening

#### Remove Hardcoded Credentials

**File:** `app/build.gradle.kts`

**Current Code:**
```kotlin
storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "123456"
keyAlias = System.getenv("KEY_ALIAS") ?: "your_key_alias"
keyPassword = System.getenv("KEY_PASSWORD") ?: "123456"
```

**Required Code:**
```kotlin
storePassword = System.getenv("KEYSTORE_PASSWORD") ?: error("KEYSTORE_PASSWORD not set")
keyAlias = System.getenv("KEY_ALIAS") ?: error("KEY_ALIAS not set")
keyPassword = System.getenv("KEY_PASSWORD") ?: error("KEY_PASSWORD not set")
```

#### Move API Keys to Secrets

**File:** `app/build.gradle.kts`

**Current Code:**
```kotlin
buildConfigField("String", "AUTH_KEY", "\"dev_auth_key_xyz123\"")
```

**Required Code:**
```kotlin
buildConfigField("String", "AUTH_KEY", "\"${System.getenv("DEV_AUTH_KEY")}\"")
```

**Additional Secrets Required:**
- DEV_AUTH_KEY
- STAGING_AUTH_KEY
- PROD_AUTH_KEY
- DEV_BASE_URL
- STAGING_BASE_URL
- PROD_BASE_URL

---

## Recommended Enhancements

### Enhancement 1: Separate Workflows per Flavour

**Implementation:** Create separate workflows:
- `build-dev.yml` - Build and deploy dev flavour only
- `build-staging.yml` - Build and deploy staging flavour only
- `build-prod.yml` - Build and deploy production flavour only
- `build-all.yml` - Build all flavours (current test-build.yml)
- `release.yml` - Production release with track selection

**Benefits:** Faster builds for single flavour, independent deployment per environment, better control over release process, reduced resource usage

### Enhancement 2: Implement Semantic Versioning

**Implementation:** Add workflow input for version bump type (major, minor, patch) and update version generation logic accordingly.

**Benefits:** Flexible version control, support for different release types, better version management

### Enhancement 3: Add Automated Testing

**Implementation:** Add comprehensive testing to workflows:

```yaml
- name: Run Unit Tests
  run: ./gradlew test

- name: Run Instrumented Tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 30
    script: ./gradlew connectedAndroidTest

- name: Run UI Tests
  run: ./gradlew connectedDebugAndroidTest

- name: Upload Test Results
  uses: actions/upload-artifact@v4
  if: always()
  with:
    name: test-results
    path: app/build/reports/tests/
```

**Add to test-build.yml before build steps:**
```yaml
- name: Run All Tests
  run: |
    ./gradlew test
    ./gradlew connectedAndroidTest
    ./gradlew connectedDebugAndroidTest
```

**Benefits:** Automated quality assurance, catch bugs early, ensure code stability

**Additional Testing Tools:**
- **Espresso** for UI testing
- **Robolectric** for fast unit tests
- **Mockito** for mocking dependencies
- **JUnit 5** for test framework

### Enhancement 4: Add Code Quality Checks

**Implementation:** Add Detekt checks to workflows:

```yaml
- name: Run Detekt
  run: ./gradlew detekt

- name: Upload Detekt Results
  uses: actions/upload-artifact@v4
  if: always()
  with:
    name: detekt-results
    path: app/build/reports/detekt/
```

**Benefits:** Code quality enforcement, consistent code style, automated code review

### Enhancement 5: Add Notification System

**Implementation:** Add Slack/Discord/Email notifications for build and deployment status

**Benefits:** Team awareness, faster response to failures, improved communication

### Enhancement 6: Implement Canary Deployment

**Implementation:** Add canary lane to Fastfile for gradual production rollout

**Benefits:** Safe production releases, gradual user exposure, ability to monitor and rollback

### Enhancement 7: Add Build Analytics

**Implementation:** Add build time tracking and performance metrics

**Benefits:** Visibility into build performance, identify bottlenecks, track trends over time

### Enhancement 8: Add Automated Crash Reporting

**Implementation:** Integrate crash reporting service (Firebase Crashlytics, Sentry, or Bugsnag):

```yaml
- name: Configure Crash Reporting
  run: |
    # Add Firebase Crashlytics SDK to app
    # Configure crash reporting in build.gradle.kts
    # Set up crash report upload on build failure

- name: Upload Crash Reports
  if: failure()
  run: |
    # Automatically collect crash logs
    # Upload to crash reporting service
    # Trigger alert to team
```

**Add to app/build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

**Benefits:**
- Automatic crash detection in production
- Stack trace collection and categorization
- Severity-based prioritization
- Developer alerts on critical crashes

### Enhancement 9: Add Automated Rollback

**Implementation:** Add rollback capability in Fastlane and GitHub Actions:

```yaml
- name: Monitor for Crashes
  run: |
    # Check crash reporting service for spike in crashes
    # If crash rate exceeds threshold, trigger rollback
    # Notify team of automatic rollback
```

**Add to Fastfile:**
```ruby
desc "Emergency rollback to previous version"
lane :emergency_rollback do
  track = ENV["TRACK"] || "production"
  upload_to_play_store(
    track: track,
    rollback: true,
    json_key: json_key_content,
    package_name: "com.ext.flavourbasedbuildtest"
  )
end
```

**Benefits:**
- Automatic rollback on critical crashes
- Minimizes user impact
- Fast recovery from deployment issues
- Team notification on rollback events

### Enhancement 10: Add Automated Crash Triage

**Implementation:** Integrate with crash reporting service for automatic crash grouping:

```yaml
- name: Crash Triage
  run: |
    # Group similar crashes automatically
    # Prioritize by frequency and severity
    # Assign to relevant developers
    # Create GitHub issues for new crashes
```

**Benefits:**
- Automatic crash categorization
- Prioritized developer assignment
- Reduced manual triage effort
- Faster crash resolution

---

## Change Scope and Impact

### High Impact Changes (Required for Play Console Upload)

| Change | File | Risk | Priority |
|--------|------|------|----------|
| Replace dummy JSON key with secret | release.yml | Low | CRITICAL |
| Add secret validation | release.yml | Low | CRITICAL |
| Update Fastlane to use env var | Fastfile | Medium | CRITICAL |
| Configure PLAY_JSON_KEY_DATA secret | GitHub Settings | Low | CRITICAL |
| Register package names in Play Console | Play Console | Low | CRITICAL |

**Impact:** Enables Play Console upload functionality

### Medium Impact Changes (Recommended Improvements)

| Change | File | Risk | Priority |
|--------|------|------|----------|
| Remove hardcoded credentials | build.gradle.kts | Medium | HIGH |
| Move API keys to secrets | build.gradle.kts | Medium | HIGH |
| Add pre-deployment validation | release.yml | Low | HIGH |
| Conditional deployment | release.yml | Low | MEDIUM |
| Separate workflows per flavour | New files | Low | MEDIUM |
| Semantic versioning | release.yml | Low | MEDIUM |

**Impact:** Improved security, flexibility, and control

### Low Impact Changes (Nice to Have)

| Change | File | Risk | Priority |
|--------|------|------|----------|
| Add automated testing | release.yml, test-build.yml | Low | LOW |
| Add code quality checks | release.yml, test-build.yml | Low | LOW |
| Add notification system | release.yml | Low | LOW |
| Implement canary deployment | Fastfile | Low | LOW |
| Add build analytics | release.yml | Low | LOW |
| Add automated crash reporting | app/build.gradle.kts, release.yml | Low | LOW |
| Add automated rollback | Fastfile, release.yml | Low | LOW |
| Add automated crash triage | release.yml | Low | LOW |

**Impact:** Better visibility and quality assurance

---

## Implementation Roadmap

### Phase 1: Critical Fixes
- Replace dummy JSON key, add validation
- Configure GitHub secrets
- Update Fastlane, test deployment

### Phase 2: Security Hardening
- Remove hardcoded credentials
- Move API keys to secrets
- Test local builds with env vars

### Phase 3: Workflow Improvements
- Add validation, conditional deployment
- Separate workflows, semantic versioning

### Phase 4: Quality & Monitoring
- Add testing, code quality checks
- Add notifications, build analytics

---

## Next Steps

### Immediate Actions

1. **Configure PLAY_JSON_KEY_DATA Secret**
   - Generate service account key in Google Cloud Console
   - Add to GitHub repository secrets
   - Test secret access in workflow

2. **Fix release.yml Workflow**
   - Replace dummy JSON key with secret
   - Add validation steps
   - Test authentication

3. **Register Package Names in Play Console**
   - Create apps for dev and staging flavours
   - Upload first AAB manually for each
   - Grant service account permissions

4. **Test Play Console Upload**
   - Run release.yml with track=internal
   - Verify upload succeeds
   - Check Play Console for uploaded AAB

### Short-Term Actions

1. **Security Hardening**
   - Remove hardcoded credentials
   - Move API keys to secrets
   - Update local build scripts

2. **Add Validation**
   - Pre-deployment checks
   - Secret validation
   - Build verification

3. **Improve Workflows**
   - Add conditional deployment
   - Implement semantic versioning
   - Add build summaries

### Medium-Term Actions

1. **Separate Workflows**
   - Create flavour-specific workflows
   - Implement independent deployment
   - Add workflow selection

2. **Add Testing**
   - Unit tests in CI
   - Instrumented tests
   - Code quality checks

3. **Monitoring**
   - Build analytics
   - Deployment notifications
   - Error tracking

### Long-Term Actions

1. **Advanced Features**
   - Canary deployments
   - Blue-green deployments
   - A/B testing support

2. **Automation**
   - Automated changelog generation
   - Release notes from commits
   - Dependency updates

3. **Documentation**
   - Update all documentation
   - Create runbooks
   - Training materials

---

## Conclusion

The current CI/CD implementation provides a solid foundation for Android flavour-based builds with GitHub Actions and Fastlane. Play Console upload functionality requires configuration of the PLAY_JSON_KEY_DATA secret and registration of package names in Play Console.

**Critical Path to Success:**
1. Configure PLAY_JSON_KEY_DATA secret in GitHub
2. Update release.yml to use the secret instead of dummy key
3. Register all package names in Play Console
4. Test deployment to internal track

By following the implementation roadmap outlined in this report, the project can achieve a robust, secure, and automated CI/CD pipeline capable of deploying all three flavours to Google Play Store with full control over release tracks and rollout percentages.

---

## Appendix

### A. File Structure

```
FlavourBasedBuildTest/
├── .github/
│   └── workflows/
│       ├── release.yml
│       ├── test-build.yml
│       ├── WORKFLOW_DIAGRAM.md
│       ├── SECRETS_SETUP.md
│       └── QUICK_START.md
├── app/
│   ├── build.gradle.kts
│   ├── proguard-dev-rules.pro
│   ├── proguard-prod-rules.pro
│   └── src/
│       ├── dev/
│       ├── staging/
│       └── main/
├── fastlane/
│   ├── Fastfile
│   └── Appfile
├── .version (version name)
├── .versioncode (current: 22)
├── build.gradle.kts
├── gradle.properties
└── gradlew
```

### B. Required Secrets Summary

| Secret Name | Description | Status | Priority |
|-------------|-------------|--------|----------|
| PLAY_JSON_KEY_DATA | Play Console service account JSON | MISSING | CRITICAL |
| KEYSTORE_BASE64 | Base64-encoded keystore | REQUIRED | HIGH |
| KEYSTORE_PASSWORD | Keystore password | REQUIRED | HIGH |
| KEY_ALIAS | Key alias | REQUIRED | HIGH |
| KEY_PASSWORD | Key password | REQUIRED | HIGH |
| DEV_AUTH_KEY | Dev API key | NOT SET | MEDIUM |
| STAGING_AUTH_KEY | Staging API key | NOT SET | MEDIUM |
| PROD_AUTH_KEY | Production API key | NOT SET | MEDIUM |

### C. Play Console Package Names

| Flavour | Package Name | Play Console Status |
|---------|--------------|---------------------|
| Dev | com.ext.flavourbasedbuildtest.dev | NOT REGISTERED |
| Staging | com.ext.flavourbasedbuildtest.staging | NOT REGISTERED |
| Prod | com.ext.flavourbasedbuildtest | SHOULD EXIST |

### D. Workflow Trigger Summary

| Workflow | Triggers | Deploys to Play Store |
|----------|----------|----------------------|
| test-build.yml | Push to main/test, manual | NO |
| release.yml | Push to main/test, manual | YES |

### E. Fastlane Lanes Summary

| Lane | Purpose | Track | Package Name |
|------|---------|-------|--------------|
| deploy_dev | Deploy dev flavour | internal | com.ext.flavourbasedbuildtest.dev |
| deploy_staging | Deploy staging flavour | internal | com.ext.flavourbasedbuildtest.staging |
| deploy_internal | Deploy prod flavour | internal | com.ext.flavourbasedbuildtest |
| deploy_alpha | Deploy prod flavour | alpha | com.ext.flavourbasedbuildtest |
| deploy_beta | Deploy prod flavour | beta | com.ext.flavourbasedbuildtest |
| deploy_production | Deploy prod flavour | production | com.ext.flavourbasedbuildtest |
| build_dev | Build dev AAB | N/A | N/A |
| build_staging | Build staging AAB | N/A | N/A |
| build_prod | Build prod AAB | N/A | N/A |

---

**Report Generated:** April 24, 2026  
**Version:** 1.0  
**Author:** CI/CD Analysis System
