pipeline {
    agent any
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge', 'safari'],
            description: 'Browser to run tests on'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environment to run tests against'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'cross-browser', 'data-driven', 'api', 'performance'],
            description: 'Test suite to execute'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '3',
            description: 'Number of parallel threads'
        )
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=256m'
        JAVA_HOME = tool('JDK11')
        MAVEN_HOME = tool('Maven3')
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${env.PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Checked out code from repository"
            }
        }
        
        stage('Setup') {
            steps {
                script {
                    echo "Setting up test environment"
                    echo "Browser: ${params.BROWSER}"
                    echo "Environment: ${params.ENVIRONMENT}"
                    echo "Test Suite: ${params.TEST_SUITE}"
                    echo "Headless: ${params.HEADLESS}"
                    echo "Thread Count: ${params.THREAD_COUNT}"
                }
            }
        }
        
        stage('Compile') {
            steps {
                sh 'mvn clean compile test-compile'
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    def mavenCommand = "mvn test -P${params.TEST_SUITE},${params.ENVIRONMENT},${params.BROWSER}"
                    mavenCommand += " -Dbrowser=${params.BROWSER}"
                    mavenCommand += " -Denvironment=${params.ENVIRONMENT}"
                    mavenCommand += " -Dheadless=${params.HEADLESS}"
                    mavenCommand += " -Dthread.count=${params.THREAD_COUNT}"
                    mavenCommand += " -Dparallel.execution=true"
                    
                    sh mavenCommand
                }
            }
            post {
                always {
                    // Archive test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    // Archive screenshots on failure
                    archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true
                    
                    // Archive logs
                    archiveArtifacts artifacts: 'logs/**/*.log', allowEmptyArchive: true
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    // Generate Allure report
                    sh 'mvn allure:report'
                }
            }
            post {
                always {
                    // Publish Allure report
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                    
                    // Archive ExtentReports
                    archiveArtifacts artifacts: 'reports/**/*.html', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            // Clean workspace
            cleanWs()
        }
        success {
            echo 'Tests completed successfully!'
            // Send success notification
            emailext (
                subject: "✅ Test Execution Successful - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                Test execution completed successfully!
                
                Build: ${env.BUILD_URL}
                Browser: ${params.BROWSER}
                Environment: ${params.ENVIRONMENT}
                Test Suite: ${params.TEST_SUITE}
                
                Check the Allure report for detailed results.
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL}"
            )
        }
        failure {
            echo 'Tests failed!'
            // Send failure notification
            emailext (
                subject: "❌ Test Execution Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                Test execution failed!
                
                Build: ${env.BUILD_URL}
                Browser: ${params.BROWSER}
                Environment: ${params.ENVIRONMENT}
                Test Suite: ${params.TEST_SUITE}
                
                Please check the console output and reports for details.
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL}"
            )
        }
    }
}