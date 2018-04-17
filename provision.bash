gcloud compute networks create ulubeynet  \
	--subnet-mode custom

gcloud compute networks subnets create subnet-ulubey \
	--network ulubeynet \
	--range 10.11.12.0/24 \
	--region europe-west4

gcloud compute firewall-rules create ulubey-allow-all-10-11-12-0-24 \
	--network ulubeynet \
	--allow tcp,udp,icmp \
	--source-ranges 10.11.12.0/24 

gcloud compute instances create ulubey-web-01 \
	--image centos-7-v20180401 \
	--image-project centos-cloud \
	--machine-type n1-standard-2 \
	--network-interface subnet=subnet-ulubey,private-network-ip=10.11.12.11 \
	--tags ulubey-web \
	--zone europe-west4-c \
	--metadata startup-script='#!/bin/bash
yum makecache fast -y
yum install varnish -y
systemctl stop varnish
curl -s -o /etc/varnish/default.vcl https://raw.githubusercontent.com/ulubeyn/config/master/default.vcl
curl -s -o /etc/varnish/varnish.params https://raw.githubusercontent.com/ulubeyn/config/master/varnish.params
systemctl start varnish
systemctl enable varnish'

gcloud compute instances create ulubey-web-02 \
	--image centos-7-v20180401 \
	--image-project centos-cloud \
	--machine-type n1-standard-2 \
	--network-interface subnet=subnet-ulubey,private-network-ip=10.11.12.12 \
	--tags ulubey-web \
	--zone europe-west4-c \
	--metadata startup-script='#!/bin/bash
yum makecache fast -y
yum install varnish -y
systemctl stop varnish
curl -s -o /etc/varnish/default.vcl https://raw.githubusercontent.com/ulubeyn/config/master/default.vcl
curl -s -o /etc/varnish/varnish.params https://raw.githubusercontent.com/ulubeyn/config/master/varnish.params
systemctl start varnish
systemctl enable varnish'

gcloud compute instances create ulubey-app-01 \
	--image centos-7-v20180401 \
	--image-project centos-cloud \
	--machine-type n1-standard-2 \
	--network-interface subnet=subnet-ulubey,private-network-ip=10.11.12.21 \
	--tags ulubey-app \
	--zone europe-west4-c \
	--metadata startup-script='#!/bin/bash
yum makecache fast -y
yum install java-1.8.0-openjdk -y'

gcloud compute instances create ulubey-app-02 \
	--image centos-7-v20180401 \
	--image-project centos-cloud \
	--machine-type n1-standard-2 \
	--network-interface subnet=subnet-ulubey,private-network-ip=10.11.12.22 \
	--tags ulubey-app \
	--zone europe-west4-c \
	--metadata startup-script='#!/bin/bash
yum makecache fast -y
yum install java-1.8.0-openjdk -y'


# EXTERNAL LB
gcloud compute firewall-rules create ulubey-web-firewall-lb \
	--target-tags ulubey-web \
	--allow tcp:80

gcloud compute addresses create ulubey-network-lb-ip-1 \
	--region europe-west4 

gcloud compute http-health-checks create ulubey-http-health-check-ext \
	--request-path=/check

gcloud compute target-pools create ulubey-web-pool \
	--region europe-west4 \
	--http-health-check ulubey-http-health-check-ext

gcloud compute target-pools add-instances ulubey-web-pool \
	--instances ulubey-web-01,ulubey-web-02 \
	--instances-zone europe-west4-c

gcloud compute forwarding-rules create ulubey-www-rule \
	--region europe-west4 \
	--ports 80 \
	--address ulubey-network-lb-ip-1 \
	--target-pool ulubey-web-pool

gcloud compute forwarding-rules describe ulubey-www-rule \
	--region europe-west4


# INTERNAL LB
gcloud compute firewall-rules create ulubey-internal-fw \
	--network ulubeynet \
	--allow tcp:22,tcp:80

gcloud compute instance-groups unmanaged create ulubey-app-group-1 \
	--zone europe-west4-c

gcloud compute instance-groups unmanaged add-instances ulubey-app-group-1 \
	--instances ulubey-app-01,ulubey-app-02 \
	--zone europe-west4-c

gcloud compute health-checks create tcp ulubey-tcp-health-check \
	--port 80

gcloud compute backend-services create ulubey-app-int-lb \
	--load-balancing-scheme internal \
	--region europe-west4 \
	--health-checks ulubey-tcp-health-check \
	--protocol tcp

gcloud compute backend-services add-backend ulubey-app-int-lb \
	--instance-group ulubey-app-group-1 \
	--instance-group-zone europe-west4-c \
	--region europe-west4

gcloud compute forwarding-rules create ulubey-app-int-lb-forwarding-rule \
	--load-balancing-scheme internal \
	--address 10.11.12.20 \
	--ports 80 \
	--network ulubeynet \
	--subnet subnet-ulubey \
	--region europe-west4 \
	--backend-service ulubey-app-int-lb 

gcloud compute firewall-rules create ulubey-allow-internal-lb \
	--network ulubeynet \
	--source-ranges 10.11.12.0/24 \
	--target-tags int-lb \
	--allow tcp:80


clear
echo "API Address: " $(gcloud compute forwarding-rules list|grep ulubey-www-rule | awk '{print $3}')

