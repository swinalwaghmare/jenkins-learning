variable "aws_region" {
  description = "AWS region to deploy resources in"
  type        = string
  default     = "us-east-1"
}

variable "key_name" {
  description = "Name of an existing EC2 KeyPair to enable SSH access to the instance"
  type        = string
  default     = null
}
