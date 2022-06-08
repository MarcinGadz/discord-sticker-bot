import pulumi
import ec2

# export the ip of ec2 instances
pulumi.export('bot_ip', ec2.bot_instance.public_ip)
pulumi.export('api_ip', ec2.api_instance.public_ip)
pulumi.export('bot_dns', ec2.bot_instance.public_dns)
pulumi.export('api_dns', ec2.api_instance.public_dns)

