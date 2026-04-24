# CI/CD Pipeline Documentation

This document describes the CI/CD pipeline setup for the Flavour-Based Build Test project using GitHub Actions.

## Overview

The project uses GitHub Actions for continuous integration and continuous deployment with two main workflows:

- **CI (ci.yml)**: Runs on every push and pull request to main/develop branches
- **CD (cd.yml)**: Runs on push to main branch, tags, or manual trigger

## CI Workflow (ci.yml)

### Triggers
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

### Jobs

#### 1. Test Job
Runs unit tests and code quality checks for all flavours:
- Unit tests for dev, staging, and prod flavours
- Android Lint checks
- Detekt static code analysis
- Uploads test results, lint reports, and detekt reports as artifacts

#### 2. Build Job
Builds debug APKs for all flavours (depends on test job):
- Builds dev debug APK
- Builds staging debug APK
- Builds prod debug APK
- Uploads APKs as artifacts

### Artifacts
The CI workflow generates the following artifacts:
- `test-results`: Unit test results
- `lint-reports`: Android Lint reports
- `detekt-reports`: Detekt code analysis reports
- `dev-debug-apk`: Debug APK for dev flavour
- `staging-debug-apk`: Debug APK for staging flavour
- `prod-debug-apk`: Debug APK for prod flavour

## CD Workflow (cd.yml)

### Triggers
- Push to `main` branch
- Tags starting with `v*` (e.g., v1.0.0)
- Manual workflow dispatch (with flavour selection)

### Jobs

#### 1. Build Release Job
Builds signed release APKs:
- Decodes keystore from base64 secret
- Creates keystore properties file
- Builds release APKs for each flavour
- Uploads APKs as artifacts

#### 2. Deploy GitHub Releases Job
Creates GitHub releases (only on tags):
- Downloads all APK artifacts
- Creates a new GitHub release with APKs attached
- Requires tag format: `v*`

### Manual Trigger
To manually trigger the CD workflow:
1. Go to Actions tab in GitHub
2. Select "CD" workflow
3. Click "Run workflow"
4. Select branch and flavour (dev, staging, or prod)

## Required Setup

### 1. GitHub Secrets

Configure the following secrets in your repository (Settings → Secrets and variables → Actions):

- `KEYSTORE_BASE64`: Base64-encoded keystore file
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias in keystore
- `KEY_PASSWORD`: Key password

See [SECRETS_SETUP.md](./SECRETS_SETUP.md) for detailed instructions.

### 2. Local Development

To test the workflows locally before pushing:

```bash
# Run tests locally
./gradlew testDevDebugUnitTest
./gradlew testStagingDebugUnitTest
./gradlew testProdDebugUnitTest

# Run lint
./gradlew lint

# Run detekt
./gradlew detekt

# Build debug APKs
./gradlew assembleDevDebug
./gradlew assembleStagingDebug
./gradlew assembleProdDebug
```

## Common Workflows

### Running CI Locally

```bash
# Run all tests
./gradlew test

# Run code quality checks
./gradlew lint detekt

# Build all debug APKs
./gradlew assembleDebug
```

### Building Release APKs Locally

```bash
# Build release APK for specific flavour
./gradlew assembleDevRelease
./gradlew assembleStagingRelease
./gradlew assembleProdRelease

# Build all release APKs
./gradlew assembleRelease
```

### Creating a Release

1. Update version codes in `app/build.gradle.kts`
2. Commit and push changes
3. Create and push a tag:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
4. The CD workflow will automatically:
   - Build release APKs
   - Create a GitHub release
   - Attach APKs to the release

## Workflow Status

Check workflow status at:
- GitHub Actions tab: `https://github.com/YOUR_USERNAME/YOUR_REPO/actions`

## Troubleshooting

### Build Fails on Signing

**Symptom**: "Keystore tampered with" or "Invalid keystore format"

**Solution**:
1. Verify base64 encoding of keystore
2. Check all four secrets are set correctly
3. Ensure keystore file wasn't corrupted

### Detekt Fails

**Symptom**: Detekt reports code style issues

**Solution**:
1. Download detekt reports from artifacts
2. Review issues in `app/build/reports/detekt/`
3. Fix issues or suppress with `@Suppress` annotation
4. Update baseline if needed: `./gradlew detektBaseline`

### Lint Fails

**Symptom**: Lint reports code quality issues

**Solution**:
1. Download lint reports from artifacts
2. Review issues in `app/build/reports/lint-`
3. Fix issues or suppress with `@SuppressLint` annotation

### Tests Fail

**Symptom**: Unit tests fail in CI

**Solution**:
1. Download test results from artifacts
2. Review test reports in `app/build/test-results/`
3. Run tests locally to reproduce: `./gradlew test`
4. Fix failing tests

### Workflow Doesn't Trigger

**Symptom**: Workflow doesn't run on push

**Solution**:
1. Check branch name matches trigger conditions
2. Verify workflow file is in `.github/workflows/`
3. Check Actions tab for workflow errors
4. Ensure you have Actions enabled in repository settings

## Best Practices

1. **Always test locally** before pushing
2. **Keep secrets secure** - never commit them
3. **Review artifacts** after each build
4. **Use meaningful commit messages**
5. **Tag releases properly** with semantic versioning
6. **Monitor workflow runs** for failures
7. **Keep dependencies updated**
8. **Run detekt baseline** after major refactoring

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Build Configuration](https://developer.android.com/build)
- [Detekt Documentation](https://detekt.dev/)
- [Gradle Documentation](https://docs.gradle.org/)

## Support

For issues or questions:
1. Check this documentation
2. Review workflow logs in GitHub Actions
3. Check [SECRETS_SETUP.md](./SECRETS_SETUP.md) for secret configuration
