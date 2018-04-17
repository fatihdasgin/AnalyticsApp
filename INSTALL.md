# Installation Steps

1. Make sure you have installed Google Cloud SDK

```bash
bash provision.bash
```

2. It may take time. Once it finishes, copy the ip at the top of the screen. Afterwards change your directory where `analyticsapp-0.1.tar.gz` exists

```bash
cd directory
gcloud compute scp analyticsapp-0.1.tar.gz ulubey-app-01:/tmp/
gcloud compute scp analyticsapp-0.1.tar.gz ulubey-app-02:/tmp/
```

3. Change your directory to your google cloud project authentication file

```bash
cd directory
gcloud compute scp yourJsonFile.json ulubey-app-01:/tmp/
gcloud compute scp yourJsonFile.json ulubey-app-02:/tmp/
```

4. Log into application server

```bash
gcloud compute ssh ulubey-app-01
sudo -i
mv /tmp/analyticsapp-0.1.tar.gz /opt/
mv /tmp/yourJsonFile /opt/
cd /opt/
tar -xzvf analyticsapp-0.1.tar.gz
chown -R root:root /opt/analyticsapp-0.1
```

5. Change fields in analyticsapp-0.1/conf/application.conf file with your favorite text editor (I hope it is vim :) );

```
gcp {
  path: "/opt/yourJsonFile.json"
  dataset: "createDatasetOnBigQueryAndSetNameHere"
  project: "yourProjectNameShouldBeSameAsProjectIdInYourJsonFile"
  table-prefix: "anyTableNameYouWant"
}
```

Optionally you can also change `analyticsapp-0.1/conf/application.ini` file to set heap memory by using `-Xmx{yourGBValue}G`.

6. Run application

```bash
cd
bash /opt/analyticsapp-0.1/bin/analyticsapp &
```

7. Repeat step 4-5-6 for ulubey-app-02

8. You can now reach out to application by the ip that you copied;

```
POST -> http://ipAddress/analytics?timestamp={millis_since_epoch}&user={username}&{click|impression}
GET -> http://ipAddress/analytics?timestamp={millis_since_epoch}
```

**NOTE:** If you send Get request with non-exist timestamp, I simply return `InternalServerError`.

# Remove Steps

1. To remove instances

```bash
bash remove.bash
```

# Import Steps

1. Import Source from `build.sbt` and then run `doc` command in sbt shell to create documents in html format. You can find them in `target/scala-2.12/api` folder.