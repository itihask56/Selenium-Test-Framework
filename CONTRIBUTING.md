# Contributing to Selenium Test Automation Framework

Thank you for your interest in contributing to this project! We welcome contributions from the community.

## How to Contribute

### 1. Fork the Repository

- Fork this repository to your GitHub account
- Clone your fork locally

### 2. Set Up Development Environment

```bash
git clone https://github.com/your-username/selenium-test-framework.git
cd selenium-test-framework
mvn clean install
```

### 3. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### 4. Make Your Changes

- Follow the existing code style and patterns
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass: `mvn test`

### 5. Commit Your Changes

```bash
git add .
git commit -m "Add: Description of your changes"
```

### 6. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a pull request on GitHub.

## Code Style Guidelines

### Java Code Style

- Use 4 spaces for indentation
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Keep methods focused and small
- Use meaningful variable and method names

### Test Code Guidelines

- Test methods should be descriptive: `testLoginWithValidCredentials()`
- Use Page Object Model pattern
- Add proper assertions with meaningful messages
- Include test descriptions in `@Test` annotations

### Documentation

- Update README.md for new features
- Add JavaDoc comments for public APIs
- Update configuration guides for new properties
- Include examples in documentation

## Types of Contributions

### Bug Fixes

- Report bugs using GitHub issues
- Include steps to reproduce
- Provide expected vs actual behavior
- Include environment details

### New Features

- Discuss new features in GitHub issues first
- Ensure features align with framework goals
- Add comprehensive tests
- Update documentation

### Documentation Improvements

- Fix typos and grammar
- Add examples and clarifications
- Improve existing guides
- Add new guides for complex topics

## Testing Guidelines

### Before Submitting

- Run all tests: `mvn test`
- Run framework validation: `mvn test -Dsurefire.suiteXmlFiles=src/test/resources/framework-validation-suite.xml`
- Test with different browsers: `mvn test -Dbrowser=firefox`
- Ensure no new warnings or errors

### Test Categories

- Unit tests for utility classes
- Integration tests for framework components
- End-to-end tests for complete workflows
- Cross-browser validation tests

## Pull Request Process

1. **Description**: Provide clear description of changes
2. **Testing**: Include test results and screenshots if applicable
3. **Documentation**: Update relevant documentation
4. **Review**: Address feedback from code review
5. **Merge**: Maintainers will merge after approval

## Code Review Criteria

- **Functionality**: Does the code work as intended?
- **Tests**: Are there adequate tests for the changes?
- **Documentation**: Is documentation updated?
- **Style**: Does code follow project conventions?
- **Performance**: Are there any performance implications?

## Getting Help

- Create GitHub issues for questions
- Check existing documentation first
- Provide detailed context for questions
- Be respectful and constructive

## Recognition

Contributors will be recognized in:

- README.md contributors section
- Release notes for significant contributions
- GitHub contributors page

Thank you for contributing to make this framework better!
