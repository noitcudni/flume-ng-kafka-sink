/*******************************************************************************
 * Copyright 2013 Renjie Yao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.flume.sink.kafka;

import java.util.ArrayList;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
//import kafka.javaapi.producer.ProducerData;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Sink of Kafka which get events from channels and publish to Kafka. I use this in our
 * company production environment which can hit 100k messages per second.
 * <tt>zk.connect: </tt> the zookeeper ip kafka use.<p>
 * <tt>topic: </tt> the topic to read from kafka.<p>
 * <tt>batchSize: </tt> send serveral messages in one request to kafka. <p>
 * <tt>producer.type: </tt> type of producer of kafka, async or sync is available.<o>
 * <tt>serializer.class: </tt>{@kafka.serializer.StringEncoder}
 */
public class KafkaSink extends AbstractSink implements Configurable{
  private static final String PRODUCER_TYPE_CONF = "producer.type";
  private static final String SYNC_BATCH_SIZE_CONF = "syncbatchsize"; //only being used if producer == sync
  private static final int DEFAULT_SYNC_BATCH_SIZE = 1;

  private static final Logger log = LoggerFactory.getLogger(KafkaSink.class);
  private String topic;
  private Producer<String, String> producer;
  private int syncBatchSize;
  private boolean sendViaSync;

  public Status process() throws EventDeliveryException {
    Channel channel = getChannel();
    Transaction tx = channel.getTransaction();
    try {
      tx.begin();

      if (sendViaSync) {
        // sync manual batching
        ArrayList<KeyedMessage<String, String> > arryLst = new ArrayList<KeyedMessage<String, String> >(this.syncBatchSize);
        Event e;
        for (int i = 0; i < this.syncBatchSize; i++) {
          e = channel.take();
          if (e == null) {
            break;
          }
          arryLst.add(new KeyedMessage<String, String>(topic, new String(e.getBody())));
        }
        if (arryLst.size() > 0) {
          producer.send(arryLst);
        } else {
          tx.rollback();
          return Status.BACKOFF;
        }
      } else {
        // async
        Event e = channel.take();
        if(e == null) {
          tx.rollback();
          return Status.BACKOFF;
        }
        producer.send(new KeyedMessage<String, String>(topic, new String(e.getBody())));
        log.trace("Message: {}", e.getBody());
      }

      tx.commit();
      return Status.READY;
    } catch(Exception e) {
      log.error("KafkaSink Exception:{}",e);
      tx.rollback();
      return Status.BACKOFF;
    } finally {
      tx.close();
    }
  }

  public void configure(Context context) {
    topic = context.getString("topic");

    String producerType = context.getString(PRODUCER_TYPE_CONF);
    if (producerType.equals("sync")) {
      syncBatchSize = (context.getInteger(SYNC_BATCH_SIZE_CONF, DEFAULT_SYNC_BATCH_SIZE)).intValue();
      sendViaSync = true;
    } else if (producerType.equals("async")) {
      sendViaSync = false;
    } else {
      throw new ConfigurationException("Kafka's producer type can only be either sync or async");
    }

    if(topic == null) {
      throw new ConfigurationException("Kafka topic must be specified.");
    }
    producer = KafkaSinkUtil.getProducer(context);
  }

  @Override
  public synchronized void start() {
    super.start();
  }

  @Override
  public synchronized void stop() {
    producer.close();
    super.stop();
  }
}
