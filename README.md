# covid-live-dashboard

This application helps you build a live COVID-19 dashboard which shows the current deaths / recovered / confirmed cases accross all the countries and you can visualize them on Kibana Dashboard

### Follow below steps to build your own live COVID tracker 

- Spin up a docker instance with ELK (Elastic, logstash & Kibana) stack and make sure they are up and running
    - Run `sudo docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name elk sebp/elk`
    - Now you basically have ElastiSearch `port 9200`, Kibana `port 5601` & Logstash running in docker and you should see respective applications webpage in UI  
- Checkout this project and run the Java code 
    - This is a `gradle project` so, make sure you have gradle installed in your machine 
    - Run the main class `com.newton.schedulerspring.ElasticSearchIndex` which is the starting point. This will basically pull the COVID data and index them in ElasticSearch
- You are almost there just navigate to Kibana dashboard and build your own way of visualizations
    - Navigate to `http://127.0.0.1:5601/`
    - Navigate to `resources/kibana-config` and import the config to Kibana `kibana management -> saved objects -> import`. Import `resources/kibana-config/COVID-dashboard.ndjson` config and you have your LIVE Dashboard up and running

## COVID live tracker using Kibana 

![alt text](https://github.com/Udaykiranreddy0608/covid-live-dashboard/blob/master/src/main/resources/covid.PNG?raw=true)

#### Reference

- ELK Docker image - https://hub.docker.com/r/sebp/elk/
- Live COVID data provider - https://pomber.github.io/covid19/timeseries.json 

#### Improvements 

- We can leverage AWS data lake for COVID built and maintained by AWS - https://go.aws/covid-19-data-lake 
 