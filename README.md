# RealTimeNotification
AWS SNS Free Equivalent - LMAX Disruptor/MQTT/RabbitMQ/ based Real-time x-Platform Notification Framework


#Why we need RealTimeNotification Framework?

As you all know Publish/Subscribe is one of the Key Design paradigms that has a place in most distributed architecutres.
For instance Mobile Push Notification is nothing but a distributed Pub/Sub. APNS and GCM are doing cloud backed Pub/Sub for apps.

1. Are you imaptient and need to deploy a X-platform MQTT Push Notification Infrastructure for iOS and Android?
2. Are you using distributed notification service($$) like AMAZON Simple Notification Service and looking for free alternative?
3. Are you willing to deploy a highly scalable distributed Pub/Sub system for IoT / Big data backed systems?

RTN is the a unified framework that will take care of the above use-cases with minimal effort from your side.

#RTN Framework

1. By default this can run in a single machine and the requirment is to have to have RabbitMQ with MQTT plugin enabled and Elastic Search installed locally.
2. Uses LMAX disruptor for high speed thread communciation.
3. Uses RabbitMQ as a AMQP and MQTT Broker.
4. Can use Commodity hardware for horizontal scalability. (Ofcourse you need to spin out additional hardware)

The Horizontal scalability here needs a bit of explanation and I dont want to Risk misguiding you on the inner working of RTN.

Heart of the RTN is a high speed Inter Threading component using LMAX disruptor pattern called RTNAgent. RTNAgent itself runs in single machine. i.e RTNAgent is vertically scalable. All it does is checks ElasticSearch for subscriptions for a particular Event and sends notifications to the respective subscribers i.e sends notification messages to RabbitMQ queues. Matching the event load you can scale RabbitMQ. If you have lots of subscriptions and ElasticSearch seem to slow you down you could scale and shard it, but that wouldnt be the case 99% of the time. 

For v1.0 RTN supports only MQTTNotifiers and for most requiremets RabbitMQ scale wouldnt be required as this is already high performant. 

# Getting Started

1. Download and install RabbitMQ.
2. Download and install ElasticSearch (if you have requirements for Broadcast/Multicast notifications).
3. Override config.properties K,V pairs as required.
4. Down RTN code and start RTNAgent
5. Refer pushtest.java to create Server side push code.
6. To create mobile notifiction listener On mobile side refer to my other library here    https://github.com/PrithivirajDamodaran/MQTTPush4Android
7. Create subscriptions in ElasticSearch using if you have requirements for Broadcast/Multicast notifications.


#ElasticSearch Configurations
1. Create an Index called rtn - PUT http://localhost:9200/rtn/
2. Create a sample subscription - 
  POST http://localhost:9200/rtn/subscriptions/1
   {
  "eventorigin": "MobilePush",
  "eventid": "NewUpdates",
  "subscriberid": "user77@yahoo.com",
  "stype": "PushNotification",
  "status": "A"
 }  

#Creating your own Notification message

You had tweak GPMessage.java, PushEntry.java and PublishRequest.java to have your own messages. This isnt trivial you need to change RingBufferConsumer.java as well accordingly.

# Roadmap:

1. Add support Email, SMS, Queue end points.
2. Add Publish and Subscribe as REST API.
2. Add support for some more protocols like STOMP, CoAP.
2. If I get enough requests and/or use-cases to have RTNAgent Horizontally scalable - I would introduce Apache storm. 



