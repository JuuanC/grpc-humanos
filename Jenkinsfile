pipeline{
    environment{
        registry = "10.30.3.239:5000/juancarlos/demo-grpc-humanos:latest"
        dockerImage=''
        containerName='demo-grpc-humanos'
    }
    agent any
    stages{
        stage('Building'){
            steps{
                sh 'mvn package -Dquarkus.package.type=legacy-jar -Dquarkus.profile=dev -DskipTests'
            }
        }

        stage('Docker image building'){
            steps{
                script{
                     dockerImage = docker.build(registry, " -f src/main/docker/Dockerfile.legacy-jar .")
                     sh 'docker rmi -f $(docker images -f "dangling=true" -q)'
                }
            }           
        }

        stage('Docker pushing image'){
            steps{
                script{
                    docker.withRegistry('http://10.30.3.239:5000', 'docker-cred') {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Deploying'){
            steps{
                sshagent(credentials :['humanos-dev']) {
                    sh 'ssh devhumanos@10.30.3.252  docker rmi $registry || true'
                    sh 'ssh devhumanos@10.30.3.252 docker pull $registry'
                    sh 'ssh devhumanos@10.30.3.252 docker rm --force $containerName || true'
                    sh 'ssh devhumanos@10.30.3.252 docker run --name $containerName -v "/etc/timezone:/etc/timezone:ro" -v "/etc/localtime:/etc/localtime:ro" --network host -v /home/nextcloud-api/images:/home/nextcloud-api/images --restart unless-stopped -d $registry'
                }
            }
        }
    }
}