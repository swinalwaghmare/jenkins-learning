#!/bin/bash
set -e

exec > >(tee /var/log/user-data.log) 2>&1

sudo apt update -y
sudo apt install -y fontconfig openjdk-21-jre openjdk-21-jdk-headless

# "openjdk-21-jdk-headless" (above) provides javac, in addition to the JRE
java -version

# Add Jenkins repo key
sudo mkdir -p /etc/apt/keyrings
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

# Add Jenkins apt repo
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

sudo apt update -y
sudo apt install -y jenkins

sudo systemctl enable jenkins
sudo systemctl start jenkins
