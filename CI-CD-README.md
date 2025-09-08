# CI/CD Integration Guide

This document provides comprehensive instructions for running the Selenium Test Framework in CI/CD environments.

## Table of Contents

- [Maven Command Line Usage](#maven-command-line-usage)
- [Jenkins Integration](#jenkins-integration)
- [GitHub Actions Integration](#github-actions-integration)
- [Test Suite Configuration](#test-suite-configuration)
- [Reporting](#reporting)
- [Troubleshooting](#troubleshooting)

## Maven Command Line Usage

### Basic Test Execution

```bash
# Run default test suite (smoke tests)
mvn test

# Run specific test suite
mvn test -Psmoke
mvn test -Pregression
mvn test -Pcross-browser
mvn test -Pdata-driven
mvn test -Papi
mvn test -Pperformance
```

### Environment and Browser Selection

```bash
# Run tests on specific environment
mvn test -Pdev
mvn test -Pstaging
mvn test -Pprod

# Run tests on specific browser
mvn test -Pchrome
mvn test -Pfirefox
mvn test -Pedge
mvn test -Psafari

# Combine profiles
mvn test -Psmoke,dev,chrome
mvn test -Pregression,staging,firefox
```

### Command Line Parameters

```bash
# Override browser via system property
mvn test -Dbrowser=firefox

# Override environment
mvn test -Denvironment=staging

# Run in headless mode
mvn test -Dheadless=true

# Set thread count for parallel execution
mvn test -Dthread.count=5

# Disable parallel execution
mvn test -Dparallel.execution=false

# Specify custom suite XML file
mvn test -Dsuite.xml.file=src/test/resources/custom-suite.xml
```

### Complete Examples

```bash
# Smoke tests on Chrome in dev environment (headless)
mvn test -Psmoke,dev,chrome -Dheadless=true -Dthread.count=2

# Regression tests on Firefox in staging environment
mvn test -Pregression,staging,firefox -Dthread.count=3

# Cross-browser tests in production
mvn test -Pcross-browser,prod -Dheadless=true

# API tests (no browser needed)
mvn test -Papi,staging

# Performance tests with single thread
mvn test -Pperformance,dev,chrome -Dthread.count=1
```

## Jenkins Integration

### Pipeline Setup

1. Create a new Pipeline job in Jenkins
2. Use the provided `Jenkinsfile` in the repository root
3. Configure the following parameters:
   - `BROWSER`: Browser selection (chrome, firefox, edge, safari)
   - `ENVIRONMENT`: Environment selection (dev, staging, prod)
   - `TEST_SUITE`: Test suite selection (smoke, regression, cross-browser, etc.)
   - `HEADLESS`: Boolean for headless execution
   - `THREAD_COUNT`: Number of parallel threads

### Required Jenkins Plugins

```
- Pipeline Plugin
- Allure Plugin
- Email Extension Plugin
- Workspace Cleanup Plugin
- Timestamper Plugin
```

### Jenkins Configuration

```groovy
// Example Jenkins job configuration
pipeline {
    agent any
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Environment')
        choice(name: 'TEST_SUITE', choices: ['smoke', 'regression'], description: 'Test Suite')
    }
    // ... rest of pipeline configuration
}
```

### Manual Execution in Jenkins

```bash
# Build with parameters
curl -X POST "http://jenkins-url/job/test-automation/buildWithParameters" \
  --data "BROWSER=chrome&ENVIRONMENT=dev&TEST_SUITE=smoke&HEADLESS=true"
```

## GitHub Actions Integration

### Workflow Triggers

The GitHub Actions workflow (`.github/workflows/test-automation.yml`) is triggered by:

1. **Push to main/develop branches**: Runs smoke tests
2. **Pull requests**: Runs smoke tests
3. **Scheduled runs**: Daily regression tests at 2 AM UTC
4. **Manual dispatch**: Allows custom test execution

### Manual Workflow Execution

1. Go to Actions tab in GitHub repository
2. Select "Test Automation CI/CD" workflow
3. Click "Run workflow"
4. Select parameters:
   - Browser (chrome, firefox, edge)
   - Environment (dev, staging, prod)
   - Test Suite (smoke, regression, cross-browser, etc.)
   - Headless mode (true/false)
   - Thread count (1-10)

### GitHub Actions API

```bash
# Trigger workflow via API
curl -X POST \
  -H "Authorization: token YOUR_GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/USERNAME/REPO/actions/workflows/test-automation.yml/dispatches \
  -d '{
    "ref": "main",
    "inputs": {
      "browser": "chrome",
      "environment": "dev",
      "test_suite": "smoke",
      "headless": "true",
      "thread_count": "3"
    }
  }'
```

## Test Suite Configuration

### Available Test Suites

| Suite           | Description                | Parallel Execution | Typical Duration |
| --------------- | -------------------------- | ------------------ | ---------------- |
| `smoke`         | Critical path tests        | 2 threads          | 5-10 minutes     |
| `regression`    | Full test coverage         | 3 threads          | 30-60 minutes    |
| `cross-browser` | Multi-browser validation   | 3 threads          | 20-40 minutes    |
| `data-driven`   | Data-driven test scenarios | 2 threads          | 15-30 minutes    |
| `api`           | API testing only           | 2 threads          | 10-20 minutes    |
| `performance`   | Performance validation     | 1 thread           | 10-15 minutes    |

### Custom Suite Creation

Create custom TestNG suite XML files in `src/test/resources/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="CustomSuite" parallel="methods" thread-count="2">
    <parameter name="browser" value="${browser}"/>
    <parameter name="environment" value="${environment}"/>

    <listeners>
        <listener class-name="com.framework.reporting.TestListener"/>
        <listener class-name="io.qameta.allure.testng.AllureTestNg"/>
    </listeners>

    <test name="CustomTests">
        <classes>
            <class name="com.framework.tests.YourTestClass"/>
        </classes>
    </test>
</suite>
```

## Reporting

### Allure Reports

Generate and view Allure reports:

```bash
# Generate Allure report
mvn allure:report

# Serve Allure report locally
mvn allure:serve

# Report location
target/site/allure-maven-plugin/index.html
```

### ExtentReports

ExtentReports are automatically generated in the `reports/` directory:

- HTML reports with screenshots
- Test execution timeline
- Pass/fail statistics

### CI/CD Report Integration

#### Jenkins

- Allure reports are automatically published
- Test results are archived as artifacts
- Email notifications on success/failure

#### GitHub Actions

- Reports are uploaded as artifacts
- Allure reports are deployed to GitHub Pages
- Test results are available in the Actions summary

## Environment Variables

Set these environment variables for CI/CD execution:

```bash
# Required
export JAVA_HOME=/path/to/java11
export MAVEN_HOME=/path/to/maven

# Optional
export BROWSER=chrome
export ENVIRONMENT=dev
export HEADLESS=true
export THREAD_COUNT=3
export PARALLEL_EXECUTION=true
```

## Docker Integration

### Dockerfile Example

```dockerfile
FROM maven:3.8.6-openjdk-11-slim

# Install browsers
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl

# Install Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable

# Install Firefox
RUN apt-get install -y firefox-esr

WORKDIR /app
COPY . .

# Run tests
CMD ["mvn", "test", "-Psmoke,dev,chrome", "-Dheadless=true"]
```

### Docker Compose

```yaml
version: "3.8"
services:
  test-automation:
    build: .
    environment:
      - BROWSER=chrome
      - ENVIRONMENT=dev
      - HEADLESS=true
      - THREAD_COUNT=2
    volumes:
      - ./reports:/app/reports
      - ./screenshots:/app/screenshots
      - ./logs:/app/logs
```

## Troubleshooting

### Common Issues

1. **Browser Driver Issues**

   ```bash
   # Clear WebDriverManager cache
   rm -rf ~/.cache/selenium

   # Force driver download
   mvn test -Dwdm.forceDownload=true
   ```

2. **Memory Issues**

   ```bash
   # Increase JVM memory
   export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
   ```

3. **Parallel Execution Issues**

   ```bash
   # Reduce thread count
   mvn test -Dthread.count=1

   # Disable parallel execution
   mvn test -Dparallel.execution=false
   ```

4. **Headless Mode Issues**
   ```bash
   # Disable headless mode for debugging
   mvn test -Dheadless=false
   ```

### Debug Mode

Enable debug logging:

```bash
# Maven debug mode
mvn test -X

# Framework debug logging
mvn test -Dlog4j.configurationFile=src/test/resources/log4j2-debug.xml
```

### CI/CD Specific Issues

#### Jenkins

- Ensure proper Java and Maven tool configuration
- Check workspace permissions
- Verify plugin installations

#### GitHub Actions

- Check runner OS compatibility
- Verify browser installation steps
- Review artifact upload/download paths

## Performance Optimization

### CI/CD Performance Tips

1. **Use Maven Dependency Caching**

   ```yaml
   # GitHub Actions
   - uses: actions/cache@v3
     with:
       path: ~/.m2
       key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
   ```

2. **Optimize Thread Count**

   - Start with 2-3 threads
   - Monitor resource usage
   - Adjust based on CI/CD runner capacity

3. **Use Headless Mode**

   ```bash
   mvn test -Dheadless=true
   ```

4. **Selective Test Execution**

   ```bash
   # Run only changed tests
   mvn test -Dtest=ChangedTestClass

   # Run specific groups
   mvn test -Dgroups=smoke
   ```

## Best Practices

1. **Test Organization**

   - Use appropriate test groups (@Test(groups = "smoke"))
   - Organize tests by functionality
   - Keep test suites focused and fast

2. **CI/CD Pipeline Design**

   - Run smoke tests on every commit
   - Schedule regression tests nightly
   - Use cross-browser tests for releases

3. **Resource Management**

   - Clean up WebDriver instances
   - Manage test data lifecycle
   - Monitor memory usage

4. **Reporting and Notifications**
   - Configure meaningful notifications
   - Archive important artifacts
   - Provide clear failure information

## Support

For issues and questions:

1. Check the troubleshooting section
2. Review CI/CD logs and reports
3. Consult the main framework documentation
4. Contact the test automation team
