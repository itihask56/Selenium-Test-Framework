# Selenium Test Framework - CI/CD Test Execution Script (PowerShell)
# Usage: .\run-tests.ps1 [OPTIONS]

param(
    [string]$Browser = "chrome",
    [string]$Environment = "dev", 
    [string]$TestSuite = "smoke",
    [string]$Headless = "true",
    [int]$ThreadCount = 3,
    [string]$ParallelExecution = "true",
    [switch]$GenerateReport = $true,
    [switch]$DockerMode = $false,
    [switch]$CleanBefore = $false,
    [switch]$Help = $false
)

# Function to show usage
function Show-Usage {
    Write-Host @"
Selenium Test Framework - CI/CD Test Execution Script (PowerShell)

Usage: .\run-tests.ps1 [OPTIONS]

OPTIONS:
    -Browser BROWSER               Browser to use (chrome, firefox, edge) [default: chrome]
    -Environment ENV               Environment to test (dev, staging, prod) [default: dev]
    -TestSuite SUITE              Test suite to run (smoke, regression, cross-browser, data-driven, api, performance) [default: smoke]
    -Headless                     Run in headless mode [default: true]
    -ThreadCount COUNT            Number of parallel threads [default: 3]
    -ParallelExecution            Enable parallel execution [default: true]
    -GenerateReport               Generate Allure report [default: true]
    -DockerMode                   Run tests in Docker container [default: false]
    -CleanBefore                  Clean before running tests [default: false]
    -Help                         Show this help message

EXAMPLES:
    # Run smoke tests on Chrome in dev environment
    .\run-tests.ps1 -Browser chrome -Environment dev -TestSuite smoke

    # Run regression tests on Firefox in staging with 5 threads
    .\run-tests.ps1 -Browser firefox -Environment staging -TestSuite regression -ThreadCount 5

    # Run cross-browser tests in headless mode
    .\run-tests.ps1 -TestSuite cross-browser -Headless true

    # Run API tests (no browser needed)
    .\run-tests.ps1 -TestSuite api

    # Clean and run regression tests
    .\run-tests.ps1 -CleanBefore -TestSuite regression -Environment staging

ENVIRONMENT VARIABLES:
    You can also set these environment variables:
    - TEST_BROWSER
    - TEST_ENVIRONMENT  
    - TEST_SUITE
    - TEST_HEADLESS
    - TEST_THREAD_COUNT
    - TEST_PARALLEL_EXECUTION
"@
}

# Function to write colored output
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Function to validate parameters
function Test-Parameters {
    # Validate browser
    if ($Browser -notin @("chrome", "firefox", "edge")) {
        Write-Error "Invalid browser: $Browser. Valid options: chrome, firefox, edge"
        exit 1
    }

    # Validate environment
    if ($Environment -notin @("dev", "staging", "prod")) {
        Write-Error "Invalid environment: $Environment. Valid options: dev, staging, prod"
        exit 1
    }

    # Validate test suite
    if ($TestSuite -notin @("smoke", "regression", "cross-browser", "data-driven", "api", "performance")) {
        Write-Error "Invalid test suite: $TestSuite. Valid options: smoke, regression, cross-browser, data-driven, api, performance"
        exit 1
    }

    # Validate thread count
    if ($ThreadCount -lt 1 -or $ThreadCount -gt 10) {
        Write-Error "Invalid thread count: $ThreadCount. Must be between 1 and 10"
        exit 1
    }
}

# Function to set up environment
function Initialize-Environment {
    Write-Info "Setting up test environment..."
    
    # Create directories if they don't exist
    $directories = @("reports", "screenshots", "logs", "target\allure-results")
    foreach ($dir in $directories) {
        if (!(Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }
    }
    
    # Check Java version
    try {
        $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
        Write-Info "Java version: $javaVersion"
        
        $majorVersion = [int]($javaVersion.Split('.')[0])
        if ($majorVersion -lt 11) {
            Write-Warning "Java 11 or higher is recommended"
        }
    }
    catch {
        Write-Error "Java is not installed or not in PATH"
        exit 1
    }
    
    # Check Maven
    try {
        $mvnVersion = mvn -version | Select-String "Apache Maven" | ForEach-Object { $_.ToString().Split(' ')[2] }
        Write-Info "Maven version: $mvnVersion"
    }
    catch {
        Write-Error "Maven is not installed or not in PATH"
        exit 1
    }
    
    # Check Docker if needed
    if ($DockerMode) {
        try {
            docker --version | Out-Null
            Write-Info "Docker is available"
        }
        catch {
            Write-Error "Docker is not installed or not in PATH"
            exit 1
        }
    }
}

# Function to clean workspace
function Clear-Workspace {
    if ($CleanBefore) {
        Write-Info "Cleaning workspace..."
        mvn clean
        
        $cleanDirs = @("reports\*", "screenshots\*", "logs\*", "target\allure-results\*")
        foreach ($dir in $cleanDirs) {
            if (Test-Path $dir) {
                Remove-Item $dir -Recurse -Force
            }
        }
        Write-Success "Workspace cleaned"
    }
}

# Function to run tests with Maven
function Invoke-MavenTests {
    Write-Info "Running tests with Maven..."
    
    # Build Maven command
    $mvnCmd = "mvn test"
    
    # Add profiles
    $profiles = "$TestSuite,$Environment"
    if ($TestSuite -ne "api") {
        $profiles += ",$Browser"
    }
    $mvnCmd += " -P$profiles"
    
    # Add system properties
    $mvnCmd += " -Dbrowser=$Browser"
    $mvnCmd += " -Denvironment=$Environment"
    $mvnCmd += " -Dheadless=$Headless"
    $mvnCmd += " -Dthread.count=$ThreadCount"
    $mvnCmd += " -Dparallel.execution=$ParallelExecution"
    
    Write-Info "Executing: $mvnCmd"
    
    # Execute Maven command
    $process = Start-Process -FilePath "cmd" -ArgumentList "/c", $mvnCmd -Wait -PassThru -NoNewWindow
    
    if ($process.ExitCode -eq 0) {
        Write-Success "Tests completed successfully"
        return $true
    }
    else {
        Write-Error "Tests failed"
        return $false
    }
}

# Function to run tests with Docker
function Invoke-DockerTests {
    Write-Info "Running tests with Docker..."
    
    # Build Docker image
    Write-Info "Building Docker image..."
    docker build -t selenium-test-framework .
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to build Docker image"
        return $false
    }
    
    # Run Docker container
    $dockerCmd = "docker run --rm"
    $dockerCmd += " -v ${PWD}\reports:/app/reports"
    $dockerCmd += " -v ${PWD}\screenshots:/app/screenshots"
    $dockerCmd += " -v ${PWD}\logs:/app/logs"
    $dockerCmd += " -v ${PWD}\target:/app/target"
    $dockerCmd += " -e BROWSER=$Browser"
    $dockerCmd += " -e ENVIRONMENT=$Environment"
    $dockerCmd += " -e HEADLESS=$Headless"
    $dockerCmd += " -e THREAD_COUNT=$ThreadCount"
    $dockerCmd += " selenium-test-framework"
    
    # Add Maven command
    $mvnCmd = "mvn test -P$TestSuite,$Environment"
    if ($TestSuite -ne "api") {
        $mvnCmd += ",$Browser"
    }
    $mvnCmd += " -Dheadless=$Headless -Dthread.count=$ThreadCount"
    
    $dockerCmd += " $mvnCmd"
    
    Write-Info "Executing: $dockerCmd"
    
    # Execute Docker command
    Invoke-Expression $dockerCmd
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Docker tests completed successfully"
        return $true
    }
    else {
        Write-Error "Docker tests failed"
        return $false
    }
}

# Function to generate reports
function New-Reports {
    if ($GenerateReport) {
        Write-Info "Generating Allure report..."
        
        mvn allure:report
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Allure report generated successfully"
            Write-Info "Report location: target\site\allure-maven-plugin\index.html"
            
            # Try to open report in browser (if running locally)
            if (!$env:CI -and (Test-Path "target\site\allure-maven-plugin\index.html")) {
                Write-Info "Opening report in browser..."
                Start-Process "target\site\allure-maven-plugin\index.html"
            }
        }
        else {
            Write-Warning "Failed to generate Allure report"
        }
    }
}

# Function to show test summary
function Show-Summary {
    Write-Info "Test Execution Summary:"
    Write-Host "  Browser: $Browser"
    Write-Host "  Environment: $Environment"
    Write-Host "  Test Suite: $TestSuite"
    Write-Host "  Headless: $Headless"
    Write-Host "  Thread Count: $ThreadCount"
    Write-Host "  Parallel Execution: $ParallelExecution"
    Write-Host "  Docker Mode: $DockerMode"
    
    # Show report locations
    $extentReport = Get-ChildItem -Path "reports" -Filter "*.html" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($extentReport) {
        Write-Host "  ExtentReports: $($extentReport.FullName)"
    }
    
    if (Test-Path "target\site\allure-maven-plugin\index.html") {
        Write-Host "  Allure Report: target\site\allure-maven-plugin\index.html"
    }
    
    if ((Get-ChildItem -Path "screenshots" -ErrorAction SilentlyContinue).Count -gt 0) {
        Write-Host "  Screenshots: screenshots\"
    }
    
    if ((Get-ChildItem -Path "logs" -ErrorAction SilentlyContinue).Count -gt 0) {
        Write-Host "  Logs: logs\"
    }
}

# Main execution
function Main {
    # Show help if requested
    if ($Help) {
        Show-Usage
        exit 0
    }
    
    # Override with environment variables if set
    if ($env:TEST_BROWSER) { $Browser = $env:TEST_BROWSER }
    if ($env:TEST_ENVIRONMENT) { $Environment = $env:TEST_ENVIRONMENT }
    if ($env:TEST_SUITE) { $TestSuite = $env:TEST_SUITE }
    if ($env:TEST_HEADLESS) { $Headless = $env:TEST_HEADLESS }
    if ($env:TEST_THREAD_COUNT) { $ThreadCount = [int]$env:TEST_THREAD_COUNT }
    if ($env:TEST_PARALLEL_EXECUTION) { $ParallelExecution = $env:TEST_PARALLEL_EXECUTION }
    
    Write-Info "Starting Selenium Test Framework execution..."
    
    # Validate parameters
    Test-Parameters
    
    # Setup environment
    Initialize-Environment
    
    # Clean workspace if requested
    Clear-Workspace
    
    # Run tests
    $testResult = $false
    if ($DockerMode) {
        $testResult = Invoke-DockerTests
    }
    else {
        $testResult = Invoke-MavenTests
    }
    
    # Generate reports
    New-Reports
    
    # Show summary
    Show-Summary
    
    # Exit with test result
    if ($testResult) {
        Write-Success "All tests completed successfully!"
        exit 0
    }
    else {
        Write-Error "Some tests failed. Check the reports for details."
        exit 1
    }
}

# Run main function
Main