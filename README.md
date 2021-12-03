# NEU Info7255 plan management Restful Service 
## Function
- Rest API that can handle any structured data in Json

- Rest API with support for crud operations, including merge support, cascaded delete

- Rest API with support for validation

- Json Schema describing the data model for the use case

- Advanced semantics with rest API operations such as update if not changed(etag)

- Storage of data in key/value store in redis and indexing in Elastic with 

- Search with parent/child using Elastic 

- Queue implementation for indexing operation with RabbitMQ 

- Parent-Child indexing

- Security using JWT With RSA 

## Installation
- Redis,Elastic,RabbitMQ installation requried
- Elastic server:https://www.elastic.co/cn/downloads/elasticsearch
- Elastic web GUI:https://www.elastic.co/cn/downloads/kibana
- RabbitMQ:https://www.rabbitmq.com/download.html
- Redis:...


## Learning reference
- Elastic https://www.elastic.co/guide/en/elasticsearch/guide/current/getting-started.html
- Elastic Java API https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html
- RabbitMQ https://spring.io/guides/gs/messaging-rabbitmq/
