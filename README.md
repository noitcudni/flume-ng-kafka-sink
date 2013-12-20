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

Install as flume plugin
------------
* Compile this repo
* Inside /usr/lib/flume
-- mkdir plugin.d
-- mkdir plugin.d/flume-ng-kafka-sink
-- mkdir plugin.d/flume-ng-kafka-sink/lib # The flume plugin goes here
-- mkdir plugin.d/flume-ng-kafka-sink/libext # All dependency jars go here.

* copy the jar file from target to /usr/lib/flume/plugin.d/flume-ng-kafka-sink/lib/,
* copy the following jars to /usr/lib/flume/plugin.d/flume-ng-kafka-sink/libext/
-- kafka-0.8-SNAPSHOT.jar
-- scala-reflect-2.10.1.jar
-- scala-compiler-2.10.1.jar
-- scala-library-2.8.0.jar


Special Thanks
---------

In fact I'm a newbie in Java. I have learnt a lot from [flumg-ng-rabbitmq](https://github.com/jcustenborder/flume-ng-rabbitmq). Thanks to [Jeremy Custenborder](https://github.com/jcustenborder).



