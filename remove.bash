gcloud compute firewall-rules delete ulubey-allow-internal-lb -q
gcloud compute forwarding-rules delete ulubey-app-int-lb-forwarding-rule --region europe-west4 -q
gcloud compute backend-services delete ulubey-app-int-lb --region europe-west4 -q
gcloud compute health-checks delete ulubey-tcp-health-check -q
gcloud compute instance-groups unmanaged delete ulubey-app-group-1 --zone europe-west4-c -q
gcloud compute firewall-rules delete ulubey-internal-fw -q
gcloud compute forwarding-rules delete ulubey-www-rule --region europe-west4 -q
gcloud compute target-pools delete ulubey-web-pool --region europe-west4 -q
gcloud compute http-health-checks delete ulubey-http-health-check-ext -q
gcloud compute addresses delete ulubey-network-lb-ip-1 --region europe-west4 -q
gcloud compute firewall-rules delete ulubey-web-firewall-lb -q
gcloud compute instances delete ulubey-app-02 --zone europe-west4-c -q
gcloud compute instances delete ulubey-app-01 --zone europe-west4-c -q
gcloud compute instances delete ulubey-web-02 --zone europe-west4-c -q
gcloud compute instances delete ulubey-web-01 --zone europe-west4-c -q
gcloud compute firewall-rules delete ulubey-allow-all-10-11-12-0-24 -q
gcloud compute networks subnets delete subnet-ulubey --region europe-west4 -q
gcloud compute networks delete ulubeynet  -q
