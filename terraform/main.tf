terraform {
  required_version = ">= 1.3.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# ---------------------------------------------------------
# Latest Ubuntu 22.04 LTS AMI (Canonical)
# ---------------------------------------------------------
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# ---------------------------------------------------------
# Default VPC / Subnet (used so this works out-of-the-box)
# ---------------------------------------------------------
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# ---------------------------------------------------------
# Security Group - SSH (optional, no key needed to use it) + all TCP open
# ---------------------------------------------------------
resource "aws_security_group" "jenkins_sg" {
  name        = "jenkins-sg"
  description = "Security group for Jenkins EC2 instance"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # All TCP ports open (as requested) - covers Jenkins UI on 8080 as well
  ingress {
    description = "All TCP"
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "jenkins-sg"
  }
}

# ---------------------------------------------------------
# IAM Role + Instance Profile so AWS Systems Manager (SSM)
# can run commands on the instance WITHOUT any SSH key pair.
# ---------------------------------------------------------
resource "aws_iam_role" "ssm_role" {
  name = "jenkins-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ssm_profile" {
  name = "jenkins-ssm-profile"
  role = aws_iam_role.ssm_role.name
}

# ---------------------------------------------------------
# EC2 Instance (no key_name - not needed at all)
# ---------------------------------------------------------
resource "aws_instance" "jenkins_server" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = "t2.medium"
  subnet_id                   = data.aws_subnets.default.ids[0]
  vpc_security_group_ids      = [aws_security_group.jenkins_sg.id]
  associate_public_ip_address = true
  iam_instance_profile        = aws_iam_instance_profile.ssm_profile.name

  root_block_device {
    volume_size            = 20
    volume_type             = "gp3"
    delete_on_termination  = true
  }

  user_data = file("${path.module}/userdata.sh")

  tags = {
    Name = "jenkins-server"
  }
}

# ---------------------------------------------------------
# Wait for Jenkins to finish installing, then use SSM
# (no SSH, no key pair) to fetch the initial admin password.
# ---------------------------------------------------------
resource "null_resource" "fetch_jenkins_password" {
  depends_on = [aws_instance.jenkins_server, aws_iam_role_policy_attachment.ssm_policy]

  triggers = {
    instance_id = aws_instance.jenkins_server.id
  }

  provisioner "local-exec" {
    command = <<-EOT
      set -e
      INSTANCE_ID=${aws_instance.jenkins_server.id}

      # Wait for the SSM agent to register (takes a minute or two after boot)
      for i in $(seq 1 20); do
        STATUS=$(aws ssm describe-instance-information \
          --filters "Key=InstanceIds,Values=$INSTANCE_ID" \
          --query "InstanceInformationList[0].PingStatus" --output text 2>/dev/null || echo "None")
        if [ "$STATUS" = "Online" ]; then
          break
        fi
        echo "Waiting for SSM agent to come online... ($i/20)"
        sleep 15
      done

      # Wait for Jenkins to finish installing, then fetch the password
      for i in $(seq 1 20); do
        CMD_ID=$(aws ssm send-command \
          --instance-ids "$INSTANCE_ID" \
          --document-name "AWS-RunShellScript" \
          --parameters 'commands=["cat /var/lib/jenkins/secrets/initialAdminPassword"]' \
          --query "Command.CommandId" --output text)

        sleep 5

        OUTPUT=$(aws ssm get-command-invocation \
          --command-id "$CMD_ID" \
          --instance-id "$INSTANCE_ID" \
          --query "StandardOutputContent" --output text 2>/dev/null || echo "")

        if [ -n "$OUTPUT" ]; then
          echo "$OUTPUT" > ${path.module}/jenkins_admin_password.txt
          exit 0
        fi

        echo "Waiting for Jenkins to finish installing... ($i/20)"
        sleep 20
      done

      echo "Timed out waiting for the Jenkins password" >&2
      exit 1
    EOT
  }
}

data "local_file" "jenkins_password" {
  depends_on = [null_resource.fetch_jenkins_password]
  filename   = "${path.module}/jenkins_admin_password.txt"
}
