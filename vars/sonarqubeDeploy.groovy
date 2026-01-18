def call(Map config) {

    stage('Clone Repo') {
        git 'https://github.com/purusharma635/sonarqube-ansible-Assignment-6.git'
    }

    if (config.KEEP_APPROVAL_STAGE) {
        stage('User Approval') {
            input message: "Deploy SonarQube to ${config.ENVIRONMENT}?"
        }
    }

    stage('Playbook Execution') {
        sh """
        ansible-playbook install-sonarqube.yml \
        -i inventory/${config.ENVIRONMENT}
        """
    }

    stage('Notification') {
        slackSend(
          channel: config.#ninja-devops-course-,
          message: config.ACTION_MESSAGE
        )
    }
}
