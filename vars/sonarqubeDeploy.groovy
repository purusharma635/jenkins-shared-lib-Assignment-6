def call(Map config = [:]) {

    pipeline {
        agent any

        stages {

           stage('Clone Repository') {
               steps {
                   echo "Cloning SonarQube code..."
                   git branch: 'main', url: config.REPO_URL
              }
         }

            stage('User Approval') {
                when {
                    expression { config.KEEP_APPROVAL_STAGE == true }
                }
                steps {
                    input message: "Do you want to run SonarQube playbook?"
                }
            }

            stage('Playbook Execution') {
                steps {
                    echo "Running Ansible playbook for SonarQube..."
                    sh """
                    ansible-playbook ${config.CODE_BASE_PATH}/sonarqube.yml \
                    -i ${config.CODE_BASE_PATH}/inventory
                    """
                }
            }

            stage('Notification') {
                steps {
                    echo "Sending Slack Notification..."
                    slackSend(
                        channel: config.SLACK_CHANNEL_NAME,
                        message: config.ACTION_MESSAGE
                    )
                }
            }
        }
    }
}
