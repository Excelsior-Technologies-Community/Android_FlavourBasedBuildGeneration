    # CI/CD Workflow Diagram

## Complete Flow: Code → Play Store

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DEVELOPER PUSHES CODE                              │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      GITHUB ACTIONS TRIGGERED                                │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ Trigger: Push to main OR Manual workflow dispatch                     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 1: SETUP ENVIRONMENT                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Checkout code                                                       │  │
│  │ • Setup JDK 17 (Temurin)                                              │  │
│  │ • Setup Ruby 3.2                                                      │  │
│  │ • Install Fastlane                                                    │  │
│  │ • Grant gradlew permissions                                           │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 2: DECODE KEYSTORE                                │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Decode KEYSTORE_BASE64 secret → app/release.keystore                 │  │
│  │ • Uses: KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD secrets            │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 3: GENERATE VERSION CODE                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Generate unique version code using Unix timestamp                    │  │
│  │ • VERSION_CODE = date +%s (e.g., 1713938400)                          │  │
│  │ • VERSION_NAME = "1.0.{VERSION_CODE}"                                 │  │
│  │ • This ensures NO duplicate version codes                             │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 4: BUILD AAB                                      │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Run: ./gradlew bundleProdRelease                                    │  │
│  │ • Environment variables:                                              │  │
│  │   - KEYSTORE_PATH = app/release.keystore                              │  │
│  │   - KEYSTORE_PASSWORD (from secret)                                    │  │
│  │   - KEY_ALIAS (from secret)                                           │  │
│  │   - KEY_PASSWORD (from secret)                                        │  │
│  │   - VERSION_CODE (auto-generated)                                      │  │
│  │   - VERSION_NAME (auto-generated)                                     │  │
│  │ • Output: app/build/outputs/bundle/prodRelease/app-prod-release.aab   │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 5: VALIDATE AAB                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Check if AAB file exists                                            │  │
│  │ • Verify file path and size                                          │  │
│  │ • If missing → FAIL workflow                                          │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 6: UPLOAD ARTIFACT                                │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Upload AAB to GitHub Actions artifacts                              │  │
│  │ • Name: app-prod-release-{VERSION_CODE}                              │  │
│  │ • Retention: 30 days                                                  │  │
│  │ • Allows manual download if needed                                    │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 7: DEPLOY TO PLAY STORE                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Run Fastlane with appropriate track                                 │  │
│  │ • Environment variables:                                              │  │
│  │   - PLAY_JSON_KEY_DATA (from secret)                                  │  │
│  │   - CHANGELOG (from workflow input or default)                        │  │
│  │   - ROLLOUT_PERCENT (for production track)                            │  │
│  │ • Fastlane actions:                                                   │  │
│  │   - internal → deploy_internal lane                                   │  │
│  │   - alpha → deploy_alpha lane                                          │  │
│  │   - beta → deploy_beta lane                                            │  │
│  │   - production → deploy_production lane                                │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      STEP 8: PLAY STORE UPLOAD                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • Fastlane uses Play Console API                                      │  │
│  │ • Authenticates with service account JSON key                          │  │
│  │ • Uploads AAB to specified track                                       │  │
│  │ • Sets changelog and rollout percentage                               │  │
│  │ • Marks release as "completed"                                        │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      ✅ SUCCESS - APP IN PLAY STORE                         │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • AAB uploaded to Play Console                                        │  │
│  │ • Available in selected track (internal/alpha/beta/production)       │  │
│  │ • Users can update via Play Store                                     │  │
│  │ • Workflow completes with success status                             │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Secrets Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        GITHUB SECRETS STORAGE                               │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ PLAY_JSON_KEY_DATA   → Service account JSON for Play Console API    │  │
│  │ KEYSTORE_BASE64      → Base64-encoded keystore file                  │  │
│  │ KEYSTORE_PASSWORD    → Keystore password                             │  │
│  │ KEY_ALIAS            → Key alias in keystore                          │  │
│  │ KEY_PASSWORD         → Key password                                  │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    GITHUB ACTIONS WORKFLOW                                  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ Secrets injected as environment variables                             │  │
│  │ • ${{ secrets.PLAY_JSON_KEY_DATA }}                                   │  │
│  │ • ${{ secrets.KEYSTORE_BASE64 }}                                      │  │
│  │ • ${{ secrets.KEYSTORE_PASSWORD }}                                    │  │
│  │ • ${{ secrets.KEY_ALIAS }}                                            │  │
│  │ • ${{ secrets.KEY_PASSWORD }}                                         │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    GRADLE BUILD PROCESS                                     │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • KEYSTORE_BASE64 → decoded to app/release.keystore                    │  │
│  │ • KEYSTORE_PASSWORD → System.getenv("KEYSTORE_PASSWORD")              │  │
│  │ • KEY_ALIAS → System.getenv("KEY_ALIAS")                              │  │
│  │ • KEY_PASSWORD → System.getenv("KEY_PASSWORD")                        │  │
│  │ • Used for signing the AAB                                            │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    FASTLANE DEPLOYMENT                                      │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ • PLAY_JSON_KEY_DATA → ENV["PLAY_JSON_KEY_DATA"]                      │  │
│  │ • Used for Play Console API authentication                            │  │
│  │ • Uploads signed AAB to Play Store                                     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Version Code Auto-Generation Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    VERSION CODE GENERATION                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ GitHub Actions step: "Generate unique version code"                   │  │
│  │                                                                       │  │
│  │ Command: VERSION_CODE=$(date +%s)                                    │  │
│  │                                                                       │  │
│  │ Example output:                                                      │  │
│  │ • 1713938400 (Unix timestamp for April 24, 2026)                    │  │
│  │ • 1713938460 (Unix timestamp for April 24, 2026 + 1 minute)         │  │
│  │                                                                       │  │
│  │ VERSION_NAME = "1.0.${VERSION_CODE}"                                 │  │
│  │ • Example: "1.0.1713938400"                                          │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    GRADLE BUILD CONFIGURATION                               │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ In app/build.gradle.kts:                                             │  │
│  │                                                                       │  │
│  │ val ciVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull()     │  │
│  │ versionCode = ciVersionCode ?: (System.currentTimeMillis() / 1000)  │  │
│  │ versionName = System.getenv("VERSION_NAME") ?: "1.0.${versionCode}"  │  │
│  │                                                                       │  │
│  │ • CI: Uses VERSION_CODE from GitHub Actions                          │  │
│  │ • Local: Uses timestamp-based fallback                               │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PLAY STORE REQUIREMENT                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ ✅ Every upload MUST have a unique version code                      │  │
│  │ ✅ Unix timestamp guarantees uniqueness (always increasing)            │  │
│  │ ✅ Prevents "APK/AAB already exists" errors                            │  │
│  │ ✅ No manual version management needed                                 │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Track Progression Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PLAY STORE TRACK PROGRESSION                             │
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐         │
│  │   INTERNAL      │───▶│     ALPHA       │───▶│      BETA       │         │
│  │   (Your team)   │    │  (Trusted test) │    │  (Open testing) │         │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘         │
│         │                      │                      │                    │
│         │                      │                      ▼                    │
│         │                      │            ┌─────────────────┐             │
│         │                      │            │   PRODUCTION    │             │
│         │                      │            │  (Public users) │             │
│         │                      │            └─────────────────┘             │
│         │                      │                      │                    │
│         │                      │                      ▼                    │
│         │                      │            ┌─────────────────┐             │
│         │                      └────────────│ STAGED ROLLOUT  │             │
│         │                                   │  (10% → 100%)   │             │
│         └───────────────────────────────────└─────────────────┘             │
│                                                                             │
│  Workflow supports all tracks via:                                         │
│  • Automatic: internal (default on push to main)                           │
│  • Manual: Choose track in workflow dispatch                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Error Handling Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ERROR HANDLING & VALIDATION                              │
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ STEP: Decode Keystore                                                 │  │
│  │   IF base64 decode fails → ERROR: "Keystore tampered with"            │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                  │                                          │
│                                  ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ STEP: Build AAB                                                       │  │
│  │   IF signing fails → ERROR: "Wrong password" or "Invalid keystore"   │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                  │                                          │
│                                  ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ STEP: Validate AAB                                                    │  │
│  │   IF file not found → ERROR: "AAB file not found"                     │  │
│  │   Workflow FAILS, no upload attempted                                 │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                  │                                          │
│                                  ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ STEP: Deploy to Play Store                                           │  │
│  │   IF API auth fails → ERROR: "Permission denied"                     │  │
│  │   IF version duplicate → ERROR: "APK/AAB already exists"             │  │
│  │   IF app not found → ERROR: "App not found"                          │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                  │                                          │
│                                  ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ STEP: Notify Status                                                   │  │
│  │   IF success → "✅ Deployment successful"                             │  │
│  │   IF failure → "❌ Deployment failed" + error details                 │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Manual vs Automatic Deployment

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AUTOMATIC DEPLOYMENT                                      │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ Trigger: git push origin main                                         │  │
│  │                                                                       │  │
│  │ Behavior:                                                             │  │
│  │ • Track: internal (fixed)                                            │  │
│  │ • Changelog: "Automated release from CI/CD pipeline" (default)       │  │
│  │ • Rollout: N/A (internal track)                                      │  │
│  │ • Version: Auto-generated                                            │  │
│  │                                                                       │  │
│  │ Use case: Every push to main gets deployed to internal testing      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    MANUAL DEPLOYMENT                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ Trigger: GitHub Actions → Run workflow                                │  │
│  │                                                                       │  │
│  │ Options:                                                              │  │
│  │ • Track: internal, alpha, beta, production (selectable)              │  │
│  │ • Rollout: 0.01 to 1.0 (for production only)                          │  │
│  │ • Changelog: Custom release notes (optional)                           │  │
│  │ • Version: Auto-generated (always)                                    │  │
│  │                                                                       │  │
│  │ Use case: Controlled releases to specific tracks                     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## File Changes Summary

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    FILES CREATED/MODIFIED                                   │
│                                                                             │
│  Modified:                                                                  │
│  ├── app/build.gradle.kts                                                   │
│  │   ├── Added environment variable support for signing                    │
│  │   └── Added auto versioning logic                                        │
│  │                                                                          │
│  ├── .gitignore                                                             │
│  │   ├── Added keystore file patterns (*.jks, *.keystore)                  │
│  │   ├── Added service account JSON patterns                               │
│  │   └── Added Fastlane temporary files                                     │
│  │                                                                          │
│  ├── .github/workflows/SECRETS_SETUP.md                                     │
│  │   ├── Added Play Console API setup instructions                         │
│  │   ├── Added PLAY_JSON_KEY_DATA secret                                   │
│  │   ├── Added comprehensive troubleshooting                              │
│  │   └── Added workflow usage instructions                                 │
│  │                                                                          │
│  Created:                                                                   │
│  ├── .github/workflows/release.yml                                          │
│  │   ├── Complete CI/CD workflow                                           │
│  │   ├── Auto versioning                                                   │
│  │   ├── AAB validation                                                    │
│  │   └── Multi-track deployment                                             │
│  │                                                                          │
│  ├── .github/workflows/QUICK_START.md                                       │
│  │   ├── 15-minute setup guide                                             │
│  │   ├── Step-by-step instructions                                         │
│  │   └── Quick troubleshooting                                             │
│  │                                                                          │
│  ├── .github/workflows/WORKFLOW_DIAGRAM.md                                 │
│  │   └── This file - visual workflow diagrams                               │
│  │                                                                          │
│  ├── fastlane/Fastfile                                                      │
│  │   ├── Lanes for all tracks (internal, alpha, beta, production)          │
│  │   ├── Build lanes for all flavors                                       │
│  │   └── Error handling                                                    │
│  │                                                                          │
│  ├── fastlane/Appfile                                                       │
│  │   └── Package name configuration                                         │
│  │                                                                          │
│  └── Gemfile                                                                │
│      └── Fastlane dependency                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## One-Time Setup Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ONE-TIME SETUP (DO THIS ONCE)                            │
│                                                                             │
│  1. Play Console API Setup                                                  │
│     ┌──────────────────────────────────────────────────────────────────┐   │
│     │ • Go to Play Console → Setup → API access                        │   │
│     │ • Link Google Cloud project                                      │   │
│     │ • Create Service Account                                          │   │
│     │ • Download JSON key                                               │   │
│     │ • Grant Release Manager permissions                               │   │
│     └──────────────────────────────────────────────────────────────────┘   │
│                              │                                                │
│                              ▼                                                │
│  2. Prepare Keystore                                                       │
│     ┌──────────────────────────────────────────────────────────────────┐   │
│     │ • Generate keystore (if not exists)                              │   │
│     │ • Note: keystore password, key alias, key password               │   │
│     └──────────────────────────────────────────────────────────────────┘   │
│                              │                                                │
│                              ▼                                                │
│  3. Configure GitHub Secrets                                               │
│     ┌──────────────────────────────────────────────────────────────────┐   │
│     │ • PLAY_JSON_KEY_DATA (paste JSON content)                        │   │
│     │ • KEYSTORE_BASE64 (base64 encode keystore)                       │   │
│     │ • KEYSTORE_PASSWORD                                              │   │
│     │ • KEY_ALIAS                                                      │   │
│     │ • KEY_PASSWORD                                                   │   │
│     └──────────────────────────────────────────────────────────────────┘   │
│                              │                                                │
│                              ▼                                                │
│  4. First Manual Upload (Required)                                          │
│     ┌──────────────────────────────────────────────────────────────────┐   │
│     │ • Manually upload first AAB to Play Console                      │   │
│     │ • Complete initial setup questionnaire                           │   │
│     │ • Enable Play App Signing                                        │   │
│     └──────────────────────────────────────────────────────────────────┘   │
│                              │                                                │
│                              ▼                                                │
│  ✅ SETUP COMPLETE - CI/CD READY TO USE                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Success Criteria Checklist

```
✅ Every push to main builds and uploads AAB to Play Store
✅ Version codes are always unique (timestamp-based)
✅ No manual version management required
✅ Supports multiple tracks (internal, alpha, beta, production)
✅ Staged rollout support for production
✅ All secrets stored securely in GitHub Secrets
✅ Keystore and API keys never committed to repo
✅ Comprehensive error handling and validation
✅ AAB artifacts retained for 30 days
✅ Clear documentation for setup and troubleshooting
```
