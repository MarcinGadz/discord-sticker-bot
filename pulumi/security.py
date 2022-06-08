import pulumi_aws as aws

bot_sg = aws.ec2.SecurityGroup('zzpj-bot-sg',
    description='Allow http and https traffic from the world to the bot',
    ingress=[
        aws.ec2.SecurityGroupIngressArgs(
            description='HTTP into VPC',
            from_port=80,
            to_port=80,
            protocol='tcp',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        ),
        aws.ec2.SecurityGroupIngressArgs(
            description='HTTPS into VPC',
            from_port=443,
            to_port=443,
            protocol='tcp',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        ),
        aws.ec2.SecurityGroupIngressArgs(
            description='SSH into VPC',
            from_port=22,
            to_port=22,
            protocol='tcp',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        ),
    ],
    egress=[
        aws.ec2.SecurityGroupEgressArgs(
            description='Anything out of VPC',
            from_port=0,
            to_port=0,
            protocol='-1',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        ),
    ],
    tags={
        'Name': 'ZZPJ Bot Security Group'
    },
)

api_sg = aws.ec2.SecurityGroup('zzpj-api-sg',
    description='Allow http and https traffic from the bot to api',
    ingress=[
        aws.ec2.SecurityGroupIngressArgs(
            description='HTTP into VPC',
            from_port=80,
            to_port=80,
            protocol='tcp',
            security_groups=[bot_sg.id]
        ),
        aws.ec2.SecurityGroupIngressArgs(
            description='HTTPS into VPC',
            from_port=443,
            to_port=443,
            protocol='tcp',
            security_groups=[bot_sg.id]
        ),
        aws.ec2.SecurityGroupIngressArgs(
            description='SSH into VPC',
            from_port=22,
            to_port=22,
            protocol='tcp',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        ),
    ],
    egress=[
        aws.ec2.SecurityGroupEgressArgs(
            description='Anything to bot out of VPC',
            from_port=0,
            to_port=0,
            protocol='-1',
            cidr_blocks=['0.0.0.0/0'],
            ipv6_cidr_blocks=['::/0'],
        )
    ],
    tags={
        'Name': 'ZZPJ Api Security Group'
    },
)