# INFO_7255
Repository for the course Advanced Big-Data Application and Indexing Techniques

## Architecture
![alt text](https://github.com/ClarenceDSilva/Big-Data-Indexing/blob/master/readme_images/architecture.PNG)


# Project Summary:
Developed a REST application and implemented CRUD functionalities to store JSON data in Redis
Secured the REST API using Google's OAuth 2.0 SSO authentication framework
Used Apache Kafka backed by Zookeeper as a queuing service to publish messages asynchronously
Created a consumer application to consume messages on the Kafka topic and index the data to Elasticsearch. 
Used Kibana to visualize the data and to query Elasticsearch

#Tools and Technologies
-The Restful web services are developed using Springboot framework
-Redis is used as a data source
-Apache Kafka was used as a messaging queue
-Elasticsearch and Kibana were used for indexing and visualizing data
-[Postman](https://www.getpostman.com/) is used for testing REST API's

# Prerequisites
1. Download [Redis](https://redis.io/download) 
2. Download and install [Apache Kafka](https://kafka.apache.org/downloads)
3. Download [Apache Kafka](https://www.elastic.co/downloads/elasticsearch)
4. Download [Kibana](https://www.elastic.co/downloads/kibana)

# How to execute the project
1. Start the Redis server
2. Start Zookeeper and then Kafka
3. Start Elasticsearch and then Kibana
4. Run the ProducerApp as a springboot app
5. Run the ConsumerApp as a springboot app
6. Before executing any request, create the Index in Elasticsearch (I have provided my index as 'Elasticsearch_Indexer')
7. Sample Elasticsearch queries are also provided. You can create your own queries or modify mine
8. Enjoy!
