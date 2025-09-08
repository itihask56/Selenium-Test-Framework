#!/bin/bash

# Selenium Test Framework - CI/CD Test Execution Script
# Usage: ./run-tests.sh [OPTIONS]

set -e

# Default values
BROWSER="chrome"
ENVIRONMENT="dev"
TEST_SUITE="smoke"
HEADLESS="true"
THREAD_COUNT="3"
PARALLEL_EXECUTION="true"
GENERATE_REPORT="true"
DOCKER_MODE="false"
CLEAN_BEFORE="false"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    cat << EOF
Selenium Test Framework - CI/CD Test Execution Script

Usage: $0 [OPTIONS]

OPTIONS:
    -b, --browser BROWSER           Browser to use (chrome, firefox, edge, safari) [default: chrome]
    -e, --environment ENV           Environment to test (dev, staging, prod) [default: dev]
    -s, --suite SUITE              Test suite to run (smoke, regression, cross-browser, data-driven, api, performance) [default: smoke]
    -h, --headless                 Run in headless mode [default: true]
    -t, --threads COUNT            Number of parallel threads [default: 3]
    -p, --parallel                 Enable parallel execution [default: true]
    -r, --report                   Generate Allure report [default: true]
    -d, --docker                   Run tests in Docker container [default: false]
    -c, --clean                    Clean before running tests [default: false]
    --help                         Show this help message

EXAMPLES:
    # Run smoke tests on Chrome in dev environment
    $0 -b chrome -e dev -s smoke

    # Run regression tests on Firefox in staging with 5 threads
    $0 -b firefox -e staging -s regression -t 5

    # Run cross-browser tests in headless mode
    $0 -s cross-browser -h true

    # Run API tests (no browser needed)
    $0 -s api

    # Run tests in Docker
    $0 -d -s smoke -b chrome

    # Clean and run regression tests
    $0 -c -s regression -e staging

ENVIRONMENT VARIABLES:
    You can also set these environment variables instead of using command line options:
    - TEST_BROWSER
    - TEST_ENVIRONMENT  
    - TEST_SUITE
    - TEST_HEADLESS
    - TEST_THREAD_COUNT
    - TEST_PARALLEL_EXECUTION

EOF
}

# Function to validate parameters
validate_parameters() {
    # Validate browser
    case $BROWSER in
        chrome|firefox|edge|safari) ;;
        *) print_error "Invalid browser: $BROWSER. Valid options: chrome, firefox, edge, safari"; exit 1 ;;
    esac

    # Validate environment
    case $ENVIRONMENT in
        dev|staging|prod) ;;
        *) print_error "Invalid environment: $ENVIRONMENT. Valid options: dev, staging, prod"; exit 1 ;;
    esac

    # Validate test suite
    case $TEST_SUITE in
        smoke|regression|cross-browser|data-driven|api|performance) ;;
        *) print_error "Invalid test suite: $TEST_SUITE. Valid options: smoke, regression, cross-browser, data-driven, api, performance"; exit 1 ;;
    esac

    # Validate thread count
    if ! [[ "$THREAD_COUNT" =~ ^[0-9]+$ ]] || [ "$THREAD_COUNT" -lt 1 ] || [ "$THREAD_COUNT" -gt 10 ]; then
        print_error "Invalid thread count: $THREAD_COUNT. Must be a number between 1 and 10"
        exit 1
    fi
}

# Function to set up environment
setup_environment() {
    print_info "Setting up test environment..."
    
    # Create directories if they don't exist
    mkdir -p reports screenshots logs target/allure-results
    
    # Check Java version
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1-2)
        print_info "Java version: $JAVA_VERSION"
        if [[ "$JAVA_VERSION" < "11" ]]; then
            print_warning "Java 11 or higher is recommended"
        fi
    else
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        print_info "Maven version: $MVN_VERSION"
    else
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check Docker if needed
    if [ "$DOCKER_MODE" = "true" ]; then
        if command -v docker &> /dev/null; then
            print_info "Docker is available"
        else
            print_error "Docker is not installed or not in PATH"
            exit 1
        fi
    fi
}

# Function to clean workspace
clean_workspace() {
    if [ "$CLEAN_BEFORE" = "true" ]; then
        print_info "Cleaning workspace..."
        mvn clean
        rm -rf reports/* screenshots/* logs/* target/allure-results/*
        print_success "Workspace cleaned"
    fi
}

# Function to run tests with Maven
run_maven_tests() {
    print_info "Running tests with Maven..."
    
    # Build Maven command
    MVN_CMD="mvn test"
    
    # Add profiles
    PROFILES="$TEST_SUITE,$ENVIRONMENT"
    if [ "$TEST_SUITE" != "api" ]; then
        PROFILES="$PROFILES,$BROWSER"
    fi
    MVN_CMD="$MVN_CMD -P$PROFILES"
    
    # Add system properties
    MVN_CMD="$MVN_CMD -Dbrowser=$BROWSER"
    MVN_CMD="$MVN_CMD -Denvironment=$ENVIRONMENT"
    MVN_CMD="$MVN_CMD -Dheadless=$HEADLESS"
    MVN_CMD="$MVN_CMD -Dthread.count=$THREAD_COUNT"
    MVN_CMD="$MVN_CMD -Dparallel.execution=$PARALLEL_EXECUTION"
    
    print_info "Executing: $MVN_CMD"
    
    # Execute Maven command
    if eval $MVN_CMD; then
        print_success "Tests completed successfully"
        return 0
    else
        print_error "Tests failed"
        return 1
    fi
}

# Function to run tests with Docker
run_docker_tests() {
    print_info "Running tests with Docker..."
    
    # Build Docker image
    print_info "Building Docker image..."
    docker build -t selenium-test-framework .
    
    # Run Docker container
    DOCKER_CMD="docker run --rm"
    DOCKER_CMD="$DOCKER_CMD -v $(pwd)/reports:/app/reports"
    DOCKER_CMD="$DOCKER_CMD -v $(pwd)/screenshots:/app/screenshots"
    DOCKER_CMD="$DOCKER_CMD -v $(pwd)/logs:/app/logs"
    DOCKER_CMD="$DOCKER_CMD -v $(pwd)/target:/app/target"
    DOCKER_CMD="$DOCKER_CMD -e BROWSER=$BROWSER"
    DOCKER_CMD="$DOCKER_CMD -e ENVIRONMENT=$ENVIRONMENT"
    DOCKER_CMD="$DOCKER_CMD -e HEADLESS=$HEADLESS"
    DOCKER_CMD="$DOCKER_CMD -e THREAD_COUNT=$THREAD_COUNT"
    DOCKER_CMD="$DOCKER_CMD selenium-test-framework"
    
    # Add Maven command
    MVN_CMD="mvn test -P$TEST_SUITE,$ENVIRONMENT"
    if [ "$TEST_SUITE" != "api" ]; then
        MVN_CMD="$MVN_CMD,$BROWSER"
    fi
    MVN_CMD="$MVN_CMD -Dheadless=$HEADLESS -Dthread.count=$THREAD_COUNT"
    
    DOCKER_CMD="$DOCKER_CMD $MVN_CMD"
    
    print_info "Executing: $DOCKER_CMD"
    
    # Execute Docker command
    if eval $DOCKER_CMD; then
        print_success "Docker tests completed successfully"
        return 0
    else
        print_error "Docker tests failed"
        return 1
    fi
}

# Function to generate reports
generate_reports() {
    if [ "$GENERATE_REPORT" = "true" ]; then
        print_info "Generating Allure report..."
        
        if mvn allure:report; then
            print_success "Allure report generated successfully"
            print_info "Report location: target/site/allure-maven-plugin/index.html"
            
            # Try to open report in browser (if running locally)
            if [ -z "$CI" ] && command -v open &> /dev/null; then
                print_info "Opening report in browser..."
                open target/site/allure-maven-plugin/index.html
            elif [ -z "$CI" ] && command -v xdg-open &> /dev/null; then
                print_info "Opening report in browser..."
                xdg-open target/site/allure-maven-plugin/index.html
            fi
        else
            print_warning "Failed to generate Allure report"
        fi
    fi
}

# Function to show test summary
show_summary() {
    print_info "Test Execution Summary:"
    echo "  Browser: $BROWSER"
    echo "  Environment: $ENVIRONMENT"
    echo "  Test Suite: $TEST_SUITE"
    echo "  Headless: $HEADLESS"
    echo "  Thread Count: $THREAD_COUNT"
    echo "  Parallel Execution: $PARALLEL_EXECUTION"
    echo "  Docker Mode: $DOCKER_MODE"
    
    # Show report locations
    if [ -d "reports" ] && [ "$(ls -A reports)" ]; then
        echo "  ExtentReports: $(ls reports/*.html 2>/dev/null | head -1)"
    fi
    
    if [ -d "target/site/allure-maven-plugin" ]; then
        echo "  Allure Report: target/site/allure-maven-plugin/index.html"
    fi
    
    if [ -d "screenshots" ] && [ "$(ls -A screenshots)" ]; then
        echo "  Screenshots: screenshots/"
    fi
    
    if [ -d "logs" ] && [ "$(ls -A logs)" ]; then
        echo "  Logs: logs/"
    fi
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -s|--suite)
            TEST_SUITE="$2"
            shift 2
            ;;
        -h|--headless)
            HEADLESS="$2"
            shift 2
            ;;
        -t|--threads)
            THREAD_COUNT="$2"
            shift 2
            ;;
        -p|--parallel)
            PARALLEL_EXECUTION="$2"
            shift 2
            ;;
        -r|--report)
            GENERATE_REPORT="$2"
            shift 2
            ;;
        -d|--docker)
            DOCKER_MODE="true"
            shift
            ;;
        -c|--clean)
            CLEAN_BEFORE="true"
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Override with environment variables if set
BROWSER=${TEST_BROWSER:-$BROWSER}
ENVIRONMENT=${TEST_ENVIRONMENT:-$ENVIRONMENT}
TEST_SUITE=${TEST_SUITE:-$TEST_SUITE}
HEADLESS=${TEST_HEADLESS:-$HEADLESS}
THREAD_COUNT=${TEST_THREAD_COUNT:-$THREAD_COUNT}
PARALLEL_EXECUTION=${TEST_PARALLEL_EXECUTION:-$PARALLEL_EXECUTION}

# Main execution
main() {
    print_info "Starting Selenium Test Framework execution..."
    
    # Validate parameters
    validate_parameters
    
    # Setup environment
    setup_environment
    
    # Clean workspace if requested
    clean_workspace
    
    # Run tests
    if [ "$DOCKER_MODE" = "true" ]; then
        if run_docker_tests; then
            TEST_RESULT=0
        else
            TEST_RESULT=1
        fi
    else
        if run_maven_tests; then
            TEST_RESULT=0
        else
            TEST_RESULT=1
        fi
    fi
    
    # Generate reports
    generate_reports
    
    # Show summary
    show_summary
    
    # Exit with test result
    if [ $TEST_RESULT -eq 0 ]; then
        print_success "All tests completed successfully!"
        exit 0
    else
        print_error "Some tests failed. Check the reports for details."
        exit 1
    fi
}

# Run main function
main