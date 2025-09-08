# 🚀 Selenium Test Automation Framework

[![CI/CD Pipeline](https://github.com/itihask56/selenium-test-framework/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/itihask56/selenium-test-framework/actions)
[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15.0-green.svg)](https://selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A comprehensive, enterprise-grade test automation framework built with Java, Selenium WebDriver, TestNG, and Maven. This framework provides a robust foundation for testing any web application across different browsers and environments with advanced features like parallel execution, detailed reporting, and CI/CD integration.

## ✨ Key Features

🌐 **Cross-Browser Support** - Chrome, Firefox, Edge, Safari  
⚡ **Parallel Execution** - Thread-safe WebDriver management  
📄 **Page Object Model** - Maintainable and reusable page objects  
📊 **Data-Driven Testing** - Excel, CSV, and JSON data support  
📈 **Comprehensive Reporting** - ExtentReports with screenshots  
🔗 **API Testing** - REST API utilities with RestAssured  
🗄️ **Database Integration** - JDBC utilities for database validation  
🚀 **CI/CD Ready** - Jenkins and GitHub Actions integration  
⚙️ **Configurable** - Properties-based configuration management  
📝 **Advanced Logging** - Detailed logging with Log4j2

## Features

- **Cross-Browser Support**: Chrome, Firefox, Edge, Safari
- **Parallel Execution**: Thread-safe WebDriver management
- **Page Object Model**: Maintainable and reusable page objects
- **Data-Driven Testing**: Excel, CSV, and JSON data support
- **Comprehensive Reporting**: ExtentReports with screenshots
- **API Testing**: REST API utilities with RestAssured
- **Database Integration**: JDBC utilities for database validation
- **CI/CD Ready**: Jenkins and GitHub Actions integration
- **Configurable**: Properties-based configuration management
- **Logging**: Detailed logging with Log4j2

## 🎯 Live Demo

The framework includes working examples that you can run immediately:

```bash
# Run demo website tests (tests real website functionality)
mvn test -Dtest=DemoWebsiteTest

# Run framework validation tests (validates all framework components)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/framework-validation-suite.xml
```

## 🚀 Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Chrome, Firefox, or Edge browser installed

### Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd selenium-test-framework
```

2. Install dependencies:

```bash
mvn clean install
```

3. Run sample tests:

```bash
mvn test
```

## Project Structure

```
selenium-test-framework/
├── src/
│   ├── main/java/com/framework/
│   │   ├── config/          # Configuration management
│   │   ├── driver/          # WebDriver management
│   │   ├── pages/           # Page Object Model classes
│   │   ├── utils/           # Utility classes
│   │   ├── exceptions/      # Custom exceptions
│   │   └── reporting/       # Reporting and listeners
│   └── test/
│       ├── java/com/framework/tests/  # Test classes
│       └── resources/       # Test configuration and data
├── reports/                 # Generated test reports
├── screenshots/             # Test failure screenshots
├── logs/                    # Application logs
└── pom.xml                  # Maven configuration
```

## Configuration

### Basic Configuration

Edit `src/test/resources/config.properties`:

```properties
# Browser Configuration
browser=chrome
headless=false
browser.timeout.implicit=10
browser.timeout.explicit=20

# Environment Configuration
environment=dev
base.url.dev=https://dev.example.com
base.url.staging=https://staging.example.com
base.url.prod=https://prod.example.com

# Test Configuration
parallel.execution=true
thread.count=3
retry.count=2
```

### Environment-Specific Configuration

You can override configuration via system properties:

```bash
mvn test -Dbrowser=firefox -Denvironment=staging -Dheadless=true
```

## Writing Tests

### 1. Create a Page Object

```java
public class LoginPage extends BasePage {
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        typeText(usernameField, username);
        typeText(passwordField, password);
        clickElement(loginButton);
    }
}
```

### 2. Create a Test Class

```java
public class LoginTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(getDriver());
    }

    @Test
    public void testValidLogin() {
        loginPage.login("testuser", "password123");
        // Add assertions
    }
}
```

### 3. Data-Driven Testing

```java
@Test(dataProvider = "loginData")
public void testLoginWithMultipleUsers(String username, String password, String expectedResult) {
    loginPage.login(username, password);
    // Add assertions based on expectedResult
}

@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return ExcelUtils.getTestData("testdata/login-data.xlsx", "LoginTests");
}
```

## Running Tests

### Command Line Execution

```bash
# Run all tests
mvn test

# Run specific test suite
mvn test -DsuiteXmlFile=src/test/resources/smoke-suite.xml

# Run with specific browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dheadless=true

# Run with custom thread count
mvn test -Dthread.count=5
```

### TestNG Suite Files

The framework includes pre-configured TestNG suites:

- `smoke-suite.xml` - Critical functionality tests
- `regression-suite.xml` - Full regression suite
- `cross-browser-suite.xml` - Cross-browser testing
- `api-suite.xml` - API testing suite
- `data-driven-suite.xml` - Data-driven tests

## Reporting

### ExtentReports

HTML reports are generated in the `reports/` directory after test execution. Reports include:

- Test execution summary
- Screenshots on failures
- Browser and environment information
- Execution timeline

### Allure Reports

Generate Allure reports:

```bash
mvn allure:serve
```

## Utilities

### Excel Data Management

```java
// Read test data from Excel
Object[][] data = ExcelUtils.getTestData("testdata/users.xlsx", "UserData");

// Write results back to Excel
ExcelUtils.writeTestResult("testdata/results.xlsx", "Results", rowNum, "PASS");
```

### API Testing

```java
// GET request
Response response = APIUtils.get("/api/users/1");
APIUtils.validateStatusCode(response, 200);

// POST request with JSON body
String jsonBody = "{'name': 'John', 'email': 'john@example.com'}";
Response response = APIUtils.post("/api/users", jsonBody);
```

### Database Validation

```java
// Execute query and get results
List<Map<String, Object>> results = DatabaseUtils.executeQuery(
    "SELECT * FROM users WHERE email = ?", "test@example.com"
);

// Validate data
Assert.assertEquals(results.get(0).get("name"), "Test User");
```

## CI/CD Integration

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test -Dbrowser=chrome -Dheadless=true'
            }
        }
        stage('Reports') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: '*.html',
                    reportName: 'Test Report'
                ])
            }
        }
    }
}
```

### GitHub Actions

```yaml
name: Test Automation
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: "11"
          distribution: "adopt"
      - name: Run tests
        run: mvn clean test -Dbrowser=chrome -Dheadless=true
      - name: Upload reports
        uses: actions/upload-artifact@v2
        with:
          name: test-reports
          path: reports/
```

## Best Practices

1. **Page Objects**: Keep page objects focused on a single page or component
2. **Test Data**: Use external data sources (Excel, JSON) for test data
3. **Assertions**: Use meaningful assertion messages
4. **Waits**: Prefer explicit waits over implicit waits
5. **Screenshots**: Capture screenshots on failures for debugging
6. **Logging**: Add appropriate logging for test steps
7. **Parallel Execution**: Design tests to be independent and thread-safe

## Troubleshooting

### Common Issues

**WebDriver not found**

- Ensure WebDriverManager is properly configured
- Check browser version compatibility

**Element not found**

- Verify locators are correct
- Add appropriate waits
- Check if element is in iframe

**Tests failing in parallel**

- Ensure ThreadLocal WebDriver usage
- Avoid shared test data
- Use unique test data for each thread

**Configuration not loading**

- Check config.properties file path
- Verify property names match ConfigManager

For more detailed troubleshooting, see [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)

## Documentation

### Complete Documentation Suite

- **[Configuration Guide](docs/CONFIGURATION.md)** - Comprehensive configuration options for different environments
- **[Best Practices Guide](docs/BEST_PRACTICES.md)** - Best practices for test development and maintenance
- **[Troubleshooting Guide](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **[API Documentation](docs/API_DOCUMENTATION.md)** - Complete API reference for all framework classes

### Quick Links

- [Framework Setup](README.md#installation) - Getting started with the framework
- [Writing Tests](README.md#writing-tests) - How to create test classes and page objects
- [Running Tests](README.md#running-tests) - Command line execution options
- [CI/CD Integration](README.md#cicd-integration) - Jenkins and GitHub Actions setup
- [Configuration Options](docs/CONFIGURATION.md) - Environment and browser configuration
- [Utility Classes](docs/API_DOCUMENTATION.md#utility-classes) - Available utility methods
- [Common Issues](docs/TROUBLESHOOTING.md) - Troubleshooting guide

## 👨‍💻 Author

**Itihas Verma**

- GitHub: [@itihasverma](https://github.com/itihask56)
- LinkedIn: [Itihas Verma](https://www.linkedin.com/in/itihasverma/)

## 🤝 Contributing

We welcome contributions from the community! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Follow the [Best Practices Guide](docs/BEST_PRACTICES.md)
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Created by Itihas Verma © 2024**
