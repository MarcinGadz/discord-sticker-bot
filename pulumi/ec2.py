import json
import security as sec
from os import environ

import pulumi_aws as aws

s3_role = aws.iam.Role("put-and-delete-s3-role",
    assume_role_policy= json.dumps({
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "AssumeRoleAsZZPJApi",
                "Action": ["sts:AssumeRole"],
                "Effect": "Allow",
                "Principal": {
                    "Service": [ "ec2.amazonaws.com" ]
                }
            },
        ]
    }),
    inline_policies=[aws.iam.RoleInlinePolicyArgs(
        name="create_and_delete_from_sticqr_bucket",
        policy=json.dumps({
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:PutObject",
                        "s3:DeleteObject",
                        "s3:GetObject",
                    ],
                    "Resource": "arn:aws:s3:::stickqr/*"
                },
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:ListBucket",
                    ],
                    "Resource": "arn:aws:s3:::stickqr"
                },
            ]
        })
    )],
    tags={
        "Name": "ZZPJ Create And Delete"
    }
)

api_instance_profile = aws.iam.InstanceProfile("zzpj-api-instance-profile",
    role=s3_role.name)

api_instance = aws.ec2.Instance("zzpj-api-instance",
    ami="ami-09439f09c55136ecf",
    instance_type="t3.micro",
    user_data=f"#!/bin/bash\nsudo yum install java -y\n",
    vpc_security_group_ids=[sec.api_sg.id],
    key_name="zzpj-key",
    iam_instance_profile=api_instance_profile.name,
    tags = {
        "Name": "zzpj-bot"
    },
)

def create_bot_instance(api_private_ip: str):
    return aws.ec2.Instance("zzpj-bot-instance",
        ami="ami-09439f09c55136ecf",
        instance_type="t3.micro",
        user_data=f"#!/bin/bash\n\
sudo yum install java -y\n\
echo -e 'BOT_TOKEN={environ['DISCORD_BOT_TOKEN']}\\n\
API_KEY=13cf17e6-0929-475b-bad0-1b7ab1bdca80\\n\
API_URL=http://{api_private_ip}:80/image/' > /home/ec2-user/app.properties\n",
        vpc_security_group_ids=[sec.bot_sg.id],
        key_name="zzpj-key",
        tags = {
            "Name": "zzpj-api"
        },
    )

bot_instance = api_instance.private_ip.apply(lambda ip: create_bot_instance(ip))