ActiveMQ Quick Performance Test
===============================

This project contains a simple producer and consumer that send 100,000 messages
to a Queue, and time the throughput.

Assumes a running Apache ActiveMQ or JBoss A-MQ broker running on port 61616
with user credentials admin/admin. You can modify
`src/main/resources/jndi.properties` for different host:port, username, and
password.

To run the Consumer

    mvn -P consumer

To run the Producer (different shell)

    mvn -P producer