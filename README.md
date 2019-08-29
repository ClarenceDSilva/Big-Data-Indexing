# INFO_7255
Repository for the course Advanced Big-Data Application and Indexing Techniques

## Objective
New data points are being generated at ever increasing rates. Traditional techniques based on relational databases to ingesting, storing, indexing, and analyzing the data are no longer sufficient to deal with the volume, variety, and velocity of new data points. The volume, variety, and velocity of new data points are creating bottlenecks at every stage of the processing chain. 
Through this project, we present The Big Data architecture for building distributed software systems. At the outer endpoint of the distributed system, there is a need to quickly validate the incoming data so as to maintain data quality. When storing the data, write latency can never exceed the tens of milliseconds for any real world application with a healthy user base. When indexing the data, the indexer throughput rate must be high enough to keep up with velocity increase of the incoming data. The indexing technique must support logical operators, wildcards, geolocation, join, and aggregate queries. Once the data is stored and indexed, we are faced with other challenges related to near real-time predictive analytics. The issue for near real time analytics is how quickly we can take advantage of new data points after they are stored in the system to answer a question. This requires that the duration of the workflow required to ingest, store, index, and analyze the data be kept to a minimum. Even after all these requirements are met, there is one additional requirement. The above system must be schema less. That is, the system must support extensibility of its own data models and the addition of new data models without any new programming.

## Architecture
![alt text](https://github.com/ClarenceDSilva/Big-Data-Indexing/blob/master/readme_images/architecture.PNG)


# Project Summary:
Developed a REST application and implemented CRUD functionalities to store JSON data in Redis
Secured the REST API using Google's OAuth 2.0 SSO authentication framework
Used Apache Kafka backed by Zookeeper as a queuing service to publish messages asynchronously
Created a consumer application to consume messages on the Kafka topic and index the data to Elasticsearch. 
Used Kibana to visualize the data and to query Elasticsearch

#Tools and Technologies
- The Restful web services are developed using Springboot framework
- Redis is used as a data source
- Apache Kafka was used as a messaging queue
- Elasticsearch and Kibana were used for indexing and visualizing data
- [Postman](https://www.getpostman.com/) is used for testing REST API's

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
