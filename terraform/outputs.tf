output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.jenkins_server.id
}

output "public_ip" {
  description = "Public IP address of the Jenkins server"
  value       = aws_instance.jenkins_server.public_ip
}

output "jenkins_url" {
  description = "URL to access the Jenkins UI (default port 8080)"
  value       = "http://${aws_instance.jenkins_server.public_ip}:8080"
}

output "ssh_command" {
  description = "Command to SSH into the instance"
  value       = "ssh -i <your-key>.pem ubuntu@${aws_instance.jenkins_server.public_ip}"
}
