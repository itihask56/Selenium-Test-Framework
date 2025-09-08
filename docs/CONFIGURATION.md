# Configuration Guide

This guide explains how to configure the Selenium Test Automation Framework for different environments, browsers, and testing scenarios.

## Configuration Overview

The framework uses a hierarchical configuration system:

1. **Default values** (hardcoded in ConfigManager)
2. **Properties files** (config.properties, test-config.properties)
3. **System properties** (command line -D parameters)
4. **Environment variables** (OS environment variables)

Higher priority configurations override lower priority ones.

## Configuration Files

### Main Configuration File

**Location:** `src/test/resources/config.properties`

```properties
# Browser Configuration
browser=chrome
headless=false
browser.window.width=1920
browser.window.height=1080
browser.timeout.implicit=10
browser.timeout.explicit=20
browser.timeout.page.load=30

# Environment Configuration
environment=dev
base.url.dev=https://dev.example.com
base.url.staging=https://staging.example.com
base.url.prod=https://prod.example.com

# Test Execution Configuration
parallel.execution=true
thread.count=3
retry.count=2
retry.enabled=true

# Reporting Configuration
reports.directory=reports
screenshots.directory=screenshots
screenshots.on.failure=true
screenshots.on.pass=false

# Logging Configuration
log.level=INFO
log.file.enabled=true
log.console.enabled=true

# Database Configuration (optional)
db.url=jdbc:mysql://localhost:3306/testdb
db.username=testuser
db.password=testpass
db.driver=com.mysql.cj.jdbc.Driver

# API Configuration (optional)
api.base.url=https://api.example.com
api.timeout=30
api.retry.count=3
```

### Test-Specific Configuration

**Location:** `src/test/resources/test-config.properties`

```properties
# Test Data Configuration
testdata.directory=src/test/resources/testdata
testdata.excel.file=sample-test-data.xlsx
testdata.csv.file=login-data.csv

# Test Categories
smoke.tests.enabled=true
regression.tests.enabled=true
api.tests.enabled=false

# Performance Configuration
performance.tests.enabled=false
performance.threshold.response.time=5000
performance.threshold.page.load=10000
```

## Environment-Specific Configuration

### Development Environment

Create `config-dev.properties`:

```properties
environment=dev
base.url.dev=http://localhost:3000
browser=chrome
headless=false
log.level=DEBUG
parallel.execution=false
thread.count=1
```

### Staging Environment

Create `config-staging.properties`:

```properties
environment=staging
base.url.staging=https://staging.example.com
browser=chrome
headless=true
log.level=INFO
parallel.execution=true
thread.count=3
retry.count=3
```

### Production Environment

Create `config-prod.properties`:

```properties
environment=prod
base.url.prod=https://example.com
browser=chrome
headless=true
log.level=WARN
parallel.execution=true
thread.count=5
retry.count=2
screenshots.on.pass=false
```

### Loading Environment-Specific Configuration

```java
// In ConfigManager.java
private void loadEnvironmentSpecificConfig() {
    String environment = getProperty("environment", "dev");
    String envConfigFile = "config-" + environment + ".properties";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(envConfigFile)) {
        if (input != null) {
            Properties envProps = new Properties();
            envProps.load(input);
            properties.putAll(envProps);
        }
    } catch (IOException e) {
        logger.warn("Environment-specific config file not found: " + envConfigFile);
    }
}
```

## Browser Configuration

### Chrome Configuration

```properties
# Basic Chrome settings
browser=chrome
browser.chrome.headless=false
browser.chrome.incognito=true
browser.chrome.disable.extensions=true
browser.chrome.disable.images=false
browser.chrome.disable.javascript=false

# Chrome arguments (comma-separated)
browser.chrome.arguments=--no-sandbox,--disable-dev-shm-usage,--disable-gpu
```

**Advanced Chrome Options:**

```java
public ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();

    // Basic options
    if (ConfigManager.getBoolean("browser.chrome.headless")) {
        options.addArguments("--headless");
    }

    if (ConfigManager.getBoolean("browser.chrome.incognito")) {
        options.addArguments("--incognito");
    }

    // Performance options
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--disable-extensions");

    // Window size
    String width = ConfigManager.getProperty("browser.window.width", "1920");
    String height = ConfigManager.getProperty("browser.window.height", "1080");
    options.addArguments("--window-size=" + width + "," + height);

    // Download directory
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("download.default_directory", System.getProperty("user.dir") + "/downloads");
    options.setExperimentalOption("prefs", prefs);

    return options;
}
```

### Firefox Configuration

```properties
# Basic Firefox settings
browser=firefox
browser.firefox.headless=false
browser.firefox.private=true

# Firefox profile preferences
browser.firefox.pref.dom.webnotifications.enabled=false
browser.firefox.pref.media.navigator.permission.disabled=true
```

**Advanced Firefox Options:**

```java
public FirefoxOptions getFirefoxOptions() {
    FirefoxOptions options = new FirefoxOptions();

    if (ConfigManager.getBoolean("browser.firefox.headless")) {
        options.addArguments("--headless");
    }

    if (ConfigManager.getBoolean("browser.firefox.private")) {
        options.addArguments("--private");
    }

    // Firefox profile
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("dom.webnotifications.enabled", false);
    profile.setPreference("media.navigator.permission.disabled", true);
    options.setProfile(profile);

    return options;
}
```

### Edge Configuration

```properties
# Basic Edge settings
browser=edge
browser.edge.headless=false
browser.edge.inprivate=true
```

### Safari Configuration

```properties
# Basic Safari settings (macOS only)
browser=safari
browser.safari.clean.session=true
```

## Parallel Execution Configuration

### TestNG Configuration

**File:** `src/test/resources/testng.xml`

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="ParallelSuite" parallel="methods" thread-count="3">
    <parameter name="browser" value="chrome"/>
    <parameter name="environment" value="dev"/>

    <test name="SmokeTests">
        <classes>
            <class name="com.framework.tests.LoginTest"/>
            <class name="com.framework.tests.SearchTest"/>
        </classes>
    </test>
</suite>
```

### Maven Surefire Configuration

**File:** `pom.xml`

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M7</version>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>${thread.count}</threadCount>
        <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/${suite.file}</suiteXmlFile>
        </suiteXmlFiles>
        <systemPropertyVariables>
            <browser>${browser}</browser>
            <environment>${environment}</environment>
            <headless>${headless}</headless>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

## Command Line Configuration

### Basic Command Line Usage

```bash
# Run with specific browser
mvn test -Dbrowser=firefox

# Run with specific environment
mvn test -Denvironment=staging

# Run in headless mode
mvn test -Dheadless=true

# Run with custom thread count
mvn test -Dthread.count=5

# Run specific test suite
mvn test -DsuiteXmlFile=smoke-suite.xml

# Combine multiple parameters
mvn test -Dbrowser=chrome -Denvironment=prod -Dheadless=true -Dthread.count=3
```

### Advanced Command Line Usage

```bash
# Run with custom timeout values
mvn test -Dbrowser.timeout.implicit=15 -Dbrowser.timeout.explicit=30

# Run with custom base URL
mvn test -Dbase.url=https://custom.example.com

# Run with specific log level
mvn test -Dlog.level=DEBUG

# Run with custom reports directory
mvn test -Dreports.directory=custom-reports

# Disable retry mechanism
mvn test -Dretry.enabled=false

# Run with custom test data file
mvn test -Dtestdata.excel.file=custom-data.xlsx
```

## CI/CD Configuration

### Jenkins Configuration

**Jenkinsfile:**

```groovy
pipeline {
    agent any

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run tests'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environment to test'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run in headless mode'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '3',
            description: 'Number of parallel threads'
        )
    }

    stages {
        stage('Test') {
            steps {
                sh """
                    mvn clean test \
                    -Dbrowser=${params.BROWSER} \
                    -Denvironment=${params.ENVIRONMENT} \
                    -Dheadless=${params.HEADLESS} \
                    -Dthread.count=${params.THREAD_COUNT}
                """
            }
        }
    }
}
```

### GitHub Actions Configuration

**.github/workflows/test.yml:**

```yaml
name: Test Automation

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest

    strategy:
      matrix:
        browser: [chrome, firefox]
        environment: [dev, staging]

    steps:
      - uses: actions/checkout@v2

      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: "11"
          distribution: "adopt"

      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

      - name: Run tests
        run: |
          mvn clean test \
          -Dbrowser=${{ matrix.browser }} \
          -Denvironment=${{ matrix.environment }} \
          -Dheadless=true \
          -Dthread.count=2

      - name: Upload test reports
        uses: actions/upload-artifact@v2
        if: always()
        with:
          name: test-reports-${{ matrix.browser }}-${{ matrix.environment }}
          path: reports/
```

## Docker Configuration

### Dockerfile

```dockerfile
FROM openjdk:11-jdk-slim

# Install browsers
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl

# Install Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable

# Install Firefox
RUN apt-get install -y firefox-esr

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Run tests
CMD ["mvn", "clean", "test", "-Dheadless=true"]
```

### Docker Compose

**docker-compose.yml:**

```yaml
version: "3.8"

services:
  selenium-tests:
    build: .
    environment:
      - BROWSER=chrome
      - ENVIRONMENT=dev
      - HEADLESS=true
      - THREAD_COUNT=2
    volumes:
      - ./reports:/app/reports
      - ./logs:/app/logs
    command: >
      mvn clean test
      -Dbrowser=${BROWSER}
      -Denvironment=${ENVIRONMENT}
      -Dheadless=${HEADLESS}
      -Dthread.count=${THREAD_COUNT}

  selenium-grid:
    image: selenium/standalone-chrome:latest
    ports:
      - "4444:4444"
    environment:
      - SE_OPTS="--max-sessions 4"
```

## Database Configuration

### MySQL Configuration

```properties
# MySQL Database
db.url=jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC
db.username=testuser
db.password=testpass
db.driver=com.mysql.cj.jdbc.Driver
db.pool.size=10
db.connection.timeout=30000
```

### PostgreSQL Configuration

```properties
# PostgreSQL Database
db.url=jdbc:postgresql://localhost:5432/testdb
db.username=testuser
db.password=testpass
db.driver=org.postgresql.Driver
db.pool.size=10
db.connection.timeout=30000
```

### Oracle Configuration

```properties
# Oracle Database
db.url=jdbc:oracle:thin:@localhost:1521:xe
db.username=testuser
db.password=testpass
db.driver=oracle.jdbc.driver.OracleDriver
db.pool.size=5
db.connection.timeout=30000
```

## API Configuration

### REST API Configuration

```properties
# API Configuration
api.base.url=https://api.example.com
api.version=v1
api.timeout=30000
api.retry.count=3
api.retry.delay=1000

# Authentication
api.auth.type=bearer
api.auth.token=${API_TOKEN}
api.auth.username=${API_USERNAME}
api.auth.password=${API_PASSWORD}

# SSL Configuration
api.ssl.verify=true
api.ssl.keystore.path=
api.ssl.keystore.password=
```

## Logging Configuration

### Log4j2 Configuration

**File:** `src/test/resources/log4j2.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Properties>
        <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>
        <Property name="LOG_DIR">logs</Property>
    </Properties>

    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>

        <RollingFile name="FileAppender" fileName="${LOG_DIR}/test-automation.log"
                     filePattern="${LOG_DIR}/test-automation-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>

    <Loggers>
        <Logger name="com.framework" level="${sys:log.level:-INFO}" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Logger>

        <Root level="WARN">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

## Configuration Validation

### ConfigManager Validation

```java
public class ConfigManager {

    public static void validateConfiguration() {
        List<String> errors = new ArrayList<>();

        // Validate required properties
        if (getProperty("browser") == null) {
            errors.add("Browser configuration is required");
        }

        if (getProperty("base.url." + getProperty("environment")) == null) {
            errors.add("Base URL for environment '" + getProperty("environment") + "' is not configured");
        }

        // Validate numeric properties
        try {
            Integer.parseInt(getProperty("thread.count", "1"));
        } catch (NumberFormatException e) {
            errors.add("Invalid thread count: " + getProperty("thread.count"));
        }

        // Validate timeout values
        int implicitTimeout = getInt("browser.timeout.implicit", 10);
        int explicitTimeout = getInt("browser.timeout.explicit", 20);

        if (implicitTimeout <= 0 || explicitTimeout <= 0) {
            errors.add("Timeout values must be positive integers");
        }

        if (!errors.isEmpty()) {
            throw new ConfigurationException("Configuration validation failed: " + String.join(", ", errors));
        }
    }
}
```

## Best Practices

1. **Environment Separation**: Use separate configuration files for each environment
2. **Sensitive Data**: Store sensitive data (passwords, tokens) in environment variables
3. **Default Values**: Always provide sensible default values
4. **Validation**: Validate configuration at startup
5. **Documentation**: Document all configuration options
6. **Version Control**: Don't commit sensitive configuration files
7. **Override Hierarchy**: Understand the configuration override hierarchy
8. **Testing**: Test configuration changes in isolated environments

## Configuration Examples

### Cross-Browser Testing

```bash
# Chrome
mvn test -Dbrowser=chrome -Dheadless=false

# Firefox
mvn test -Dbrowser=firefox -Dheadless=false

# Edge
mvn test -Dbrowser=edge -Dheadless=false

# Safari (macOS only)
mvn test -Dbrowser=safari
```

### Performance Testing Configuration

```properties
# Performance test configuration
performance.tests.enabled=true
performance.load.users=50
performance.load.duration=300
performance.threshold.response.time=2000
performance.threshold.error.rate=5

# Browser configuration for performance
browser.chrome.arguments=--disable-images,--disable-javascript,--disable-plugins
browser.timeout.page.load=60
```

### Mobile Testing Configuration

```properties
# Mobile emulation
browser.mobile.enabled=true
browser.mobile.device=iPhone 12
browser.mobile.width=390
browser.mobile.height=844
browser.mobile.user.agent=Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)
```

This configuration guide should help you set up the framework for various testing scenarios and environments. Remember to test your configuration changes thoroughly before deploying to production environments.
