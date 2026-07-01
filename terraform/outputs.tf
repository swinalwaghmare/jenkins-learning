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

output "jenkins_initial_admin_password" {
  description = "Initial admin password to unlock Jenkins (paste into http://<public_ip>:8080)"
  value       = trimspace(data.local_file.jenkins_password.content)
  sensitive   = true
}

output "ssm_connect_command" {
  description = "Command to open a shell on the instance without any SSH key (uses AWS Session Manager)"
  value       = "aws ssm start-session --target ${aws_instance.jenkins_server.id}"
}
