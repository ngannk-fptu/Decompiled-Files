/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.Connection
 *  javax.jms.ConnectionFactory
 *  javax.jms.Destination
 *  javax.jms.JMSException
 *  javax.jms.Message
 *  javax.jms.MessageConsumer
 *  javax.jms.MessageProducer
 *  javax.jms.Queue
 *  javax.jms.QueueConnection
 *  javax.jms.QueueConnectionFactory
 *  javax.jms.QueueReceiver
 *  javax.jms.QueueSender
 *  javax.jms.QueueSession
 *  javax.jms.Session
 *  javax.jms.TemporaryQueue
 */
package org.apache.axis.transport.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.JMSURLHelper;
import org.apache.axis.transport.jms.Subscription;

public class QueueConnector
extends JMSConnector {
    public QueueConnector(ConnectionFactory factory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl) throws JMSException {
        super(factory, numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
    }

    public JMSEndpoint createEndpoint(String destination) {
        return new QueueEndpoint(destination);
    }

    public JMSEndpoint createEndpoint(Destination destination) throws JMSException {
        if (!(destination instanceof Queue)) {
            throw new IllegalArgumentException("The input must be a queue for this connector");
        }
        return new QueueDestinationEndpoint((Queue)destination);
    }

    protected Connection internalConnect(ConnectionFactory connectionFactory, String username, String password) throws JMSException {
        QueueConnectionFactory qcf = (QueueConnectionFactory)connectionFactory;
        if (username == null) {
            return qcf.createQueueConnection();
        }
        return qcf.createQueueConnection(username, password);
    }

    protected JMSConnector.SyncConnection createSyncConnection(ConnectionFactory factory, Connection connection, int numSessions, String threadName, String clientID, String username, String password) throws JMSException {
        return new QueueSyncConnection((QueueConnectionFactory)factory, (QueueConnection)connection, numSessions, threadName, clientID, username, password);
    }

    private QueueSession createQueueSession(QueueConnection connection, int ackMode) throws JMSException {
        return connection.createQueueSession(false, ackMode);
    }

    private Queue createQueue(QueueSession session, String subject) throws Exception {
        return this.m_adapter.getQueue(session, subject);
    }

    private QueueReceiver createReceiver(QueueSession session, Queue queue, String messageSelector) throws JMSException {
        return session.createReceiver(queue, messageSelector);
    }

    protected JMSConnector.AsyncConnection createAsyncConnection(ConnectionFactory factory, Connection connection, String threadName, String clientID, String username, String password) throws JMSException {
        return new QueueAsyncConnection((QueueConnectionFactory)factory, (QueueConnection)connection, threadName, clientID, username, password);
    }

    private final class QueueAsyncConnection
    extends JMSConnector.AsyncConnection {
        QueueAsyncConnection(QueueConnectionFactory connectionFactory, QueueConnection connection, String threadName, String clientID, String username, String password) throws JMSException {
            super((ConnectionFactory)connectionFactory, (Connection)connection, threadName, clientID, username, password);
        }

        protected JMSConnector.AsyncConnection.ListenerSession createListenerSession(Connection connection, Subscription subscription) throws Exception {
            QueueSession session = QueueConnector.this.createQueueSession((QueueConnection)connection, subscription.m_ackMode);
            QueueReceiver receiver = QueueConnector.this.createReceiver(session, (Queue)subscription.m_endpoint.getDestination((Session)session), subscription.m_messageSelector);
            return new JMSConnector.AsyncConnection.ListenerSession((Session)session, (MessageConsumer)receiver, subscription);
        }
    }

    private final class QueueDestinationEndpoint
    extends QueueEndpoint {
        Queue m_queue;

        QueueDestinationEndpoint(Queue queue) throws JMSException {
            super(queue.getQueueName());
            this.m_queue = queue;
        }

        Destination getDestination(Session session) {
            return this.m_queue;
        }
    }

    private class QueueEndpoint
    extends JMSEndpoint {
        String m_queueName;

        QueueEndpoint(String queueName) {
            super(QueueConnector.this);
            this.m_queueName = queueName;
        }

        Destination getDestination(Session session) throws Exception {
            return QueueConnector.this.createQueue((QueueSession)session, this.m_queueName);
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer("QueueEndpoint:");
            buffer.append(this.m_queueName);
            return buffer.toString();
        }

        public boolean equals(Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (!(object instanceof QueueEndpoint)) {
                return false;
            }
            return this.m_queueName.equals(((QueueEndpoint)object).m_queueName);
        }
    }

    private final class QueueSyncConnection
    extends JMSConnector.SyncConnection {
        QueueSyncConnection(QueueConnectionFactory connectionFactory, QueueConnection connection, int numSessions, String threadName, String clientID, String username, String password) throws JMSException {
            super((ConnectionFactory)connectionFactory, (Connection)connection, numSessions, threadName, clientID, username, password);
        }

        protected JMSConnector.SyncConnection.SendSession createSendSession(Connection connection) throws JMSException {
            QueueSession session = QueueConnector.this.createQueueSession((QueueConnection)connection, 3);
            QueueSender sender = session.createSender(null);
            return new QueueSendSession(session, sender);
        }

        private final class QueueSendSession
        extends JMSConnector.SyncConnection.SendSession {
            QueueSendSession(QueueSession session, QueueSender sender) throws JMSException {
                super((Session)session, (MessageProducer)sender);
            }

            protected MessageConsumer createConsumer(Destination destination) throws JMSException {
                return QueueConnector.this.createReceiver((QueueSession)this.m_session, (Queue)destination, null);
            }

            protected Destination createTemporaryDestination() throws JMSException {
                return ((QueueSession)this.m_session).createTemporaryQueue();
            }

            protected void deleteTemporaryDestination(Destination destination) throws JMSException {
                ((TemporaryQueue)destination).delete();
            }

            protected void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
                ((QueueSender)this.m_producer).send((Queue)destination, message, deliveryMode, priority, timeToLive);
            }
        }
    }
}

