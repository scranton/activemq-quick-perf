/*
 * Copyright (C) Red Hat, Inc.
 * http://www.redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fusesource.examples.activemq;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleProducer.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "queue/simple";
    private static final int MESSAGE_TIMEOUT_MILLISECONDS = 120000;
    private static final int NUM_MESSAGES_TO_BE_RECEIVED = 100000;

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);

            LOG.info("Start consuming messages from {} with {} ms timeout", destination.toString(), MESSAGE_TIMEOUT_MILLISECONDS);

            long start = 0;

            // Synchronous message consumer
            int i;
            for (i = 0; i < NUM_MESSAGES_TO_BE_RECEIVED; i++) {
                Message message = consumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        final String text = ((TextMessage) message).getText();
                        if (i == 0) {
                            start = System.currentTimeMillis();
                        }
                        //LOG.debug("Got {}. message: {}", (i++), text);
                    }
                } else {
                    break;
                }
            }

            final long stop = System.currentTimeMillis();

            LOG.info("Received {} messages in {} seconds - throughput {}", new Object[] {i, ((stop - start)/1000), (i/((stop-start)/1000))});

            consumer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error("Error receiving message", t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error("Error closing connection", e);
                }
            }
        }
    }
}