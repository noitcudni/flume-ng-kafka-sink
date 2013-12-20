flume-ng-kafka-sink
================

This project is used for [flume-ng](https://github.com/apache/flume) to communicate with [kafka 0.7,2](http://kafka.apache.org/07/quickstart.html).

Configuration of Kafka Sink
----------

    agent_log.sinks.kafka.type = org.apache.flume.sink.kafka.KafkaSink
    agent_log.sinks.kafka.channel = all_channel
    agent_log.sinks.kafka.zk.connect = 127.0.0.1:2181
    agent_log.sinks.kafka.topic = all
    agent_log.sinks.kafka.batchsize = 200
    agent_log.sinks.kafka.producer.type = async
    agent_log.sinks.kafka.serializer.class = kafka.serializer.StringEncoder
    agent_log.sinks.kafka.metadata.broker.list = [kafka_producer_host_name:port]

Install as flume plugin
------------
* Compile this repo
* Inside /usr/lib/flume
1. mkdir plugin.d
2. mkdir plugin.d/flume-ng-kafka-sink
3. mkdir plugin.d/flume-ng-kafka-sink/lib # The flume plugin goes here
4. mkdir plugin.d/flume-ng-kafka-sink/libext # All dependency jars go here.

* copy the jar file from target to /usr/lib/flume/plugin.d/flume-ng-kafka-sink/lib/,
* copy the following jars to /usr/lib/flume/plugin.d/flume-ng-kafka-sink/libext/
1. kafka-0.8-SNAPSHOT.jar
2. scala-reflect-2.10.1.jar
3. scala-compiler-2.10.1.jar
4. scala-library-2.8.0.jar
5. metrics-core-2.2.0.jar
6. metrics-annotation-2.2.0.jar

Special Thanks
---------

In fact I'm a newbie in Java. I have learnt a lot from [flumg-ng-rabbitmq](https://github.com/jcustenborder/flume-ng-rabbitmq). Thanks to [Jeremy Custenborder](https://github.com/jcustenborder).



