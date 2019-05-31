pipeline {
    agent any
     tools {
        nodejs 'my_js'
        jdk 'java_11'
    }
    stages {
    stage('Create DB if not exist') {
            steps {
                script{
                    try {
                            sh "createdb templa"
                        }
                         catch (exc) {
                            echo 'is already exist'
                            currentBuild.result = "SUCCESS"
                        }
                }
            }
        }
        stage('Test') {
            steps {
                sh "cd server && ./gradlew clean testApi"
                script{
                        try {
                            sh "cd ${workspace}/client && yarn install"
                            sh "cd ${workspace}/client && yarn run build-test"
                            sh "curl 'http://localhost:3334/actuator/shutdown' -i -X POST"
                        }
                         catch (exc) {
                            echo 'Something failed!'
                            currentBuild.result = "SUCCESS"
                        }
                }
                sh "cd server && ./gradlew copyFrontBuildToPublic integrationTest -Dselenide.baseUrl=http://138.68.95.208 -Dselenide.browser=integration.SelenoidWebDriverProvider"
            }
        post {
            always {
                allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'server/build/allure-results']]
                    ])
                }
            }
        }
        stage('Build Front') {
            steps {
                sh "cd ${workspace}/client && yarn build"
                sh "cd ${workspace}/client && JENKINS_NODE_COOKIE=dontKillMe pm2 delete -s app-9999 || : && pm2 serve build 9999 --name=app-9999 --spa"
            }
        }
        stage('Deploy') {
            steps {
            script{
                        try {
                            sh "curl 'http://localhost:1111/actuator/shutdown' -i -X POST"
                        }
                         catch (exc) {
                            echo 'Something failed!'
                            currentBuild.result = "SUCCESS"
                        }
                    }
                sh "cd server && chmod +x gradlew"
                sh "cd server && ./gradlew clean clearPublic build -x test"
                sh "cd server/build/libs/ && JENKINS_NODE_COOKIE=dontKillMe nohup java -jar  backend-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &"
            }
        }
    }
}
