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
 *  javax.jms.MessageListener
 *  javax.jms.MessageProducer
 *  javax.jms.Session
 *  javax.jms.TemporaryTopic
 *  javax.jms.Topic
 *  javax.jms.TopicConnection
 *  javax.jms.TopicConnectionFactory
 *  javax.jms.TopicPublisher
 *  javax.jms.TopicSession
 *  javax.jms.TopicSubscriber
 */
package org.apache.axis.transport.jms;

import java.util.HashMap;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.JMSURLHelper;
import org.apache.axis.transport.jms.MapUtils;
import org.apache.axis.transport.jms.Subscription;

public class TopicConnector
extends JMSConnector {
    public TopicConnector(TopicConnectionFactory factory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl) throws JMSException {
        super((ConnectionFactory)factory, numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
    }

    protected Connection internalConnect(ConnectionFactory connectionFactory, String username, String password) throws JMSException {
        TopicConnectionFactory tcf = (TopicConnectionFactory)connectionFactory;
        if (username == null) {
            return tcf.createTopicConnection();
        }
        return tcf.createTopicConnection(username, password);
    }

    protected JMSConnector.SyncConnection createSyncConnection(ConnectionFactory factory, Connection connection, int numSessions, String threadName, String clientID, String username, String password) throws JMSException {
        return new TopicSyncConnection((TopicConnectionFactory)factory, (TopicConnection)connection, numSessions, threadName, clientID, username, password);
    }

    protected JMSConnector.AsyncConnection createAsyncConnection(ConnectionFactory factory, Connection connection, String threadName, String clientID, String username, String password) throws JMSException {
        return new TopicAsyncConnection((TopicConnectionFactory)factory, (TopicConnection)connection, threadName, clientID, username, password);
    }

    public JMSEndpoint createEndpoint(String destination) {
        return new TopicEndpoint(destination);
    }

    public JMSEndpoint createEndpoint(Destination destination) throws JMSException {
        if (!(destination instanceof Topic)) {
            throw new IllegalArgumentException("The input be a topic for this connector");
        }
        return new TopicDestinationEndpoint((Topic)destination);
    }

    private TopicSession createTopicSession(TopicConnection connection, int ackMode) throws JMSException {
        return connection.createTopicSession(false, ackMode);
    }

    private Topic createTopic(TopicSession session, String subject) throws Exception {
        return this.m_adapter.getTopic(session, subject);
    }

    private TopicSubscriber createSubscriber(TopicSession session, TopicSubscription subscription) throws Exception {
        if (subscription.isDurable()) {
            return this.createDurableSubscriber(session, (Topic)subscription.m_endpoint.getDestination((Session)session), subscription.m_subscriptionName, subscription.m_messageSelector, subscription.m_noLocal);
        }
        return this.createSubscriber(session, (Topic)subscription.m_endpoint.getDestination((Session)session), subscription.m_messageSelector, subscription.m_noLocal);
    }

    private TopicSubscriber createDurableSubscriber(TopicSession session, Topic topic, String subscriptionName, String messageSelector, boolean noLocal) throws JMSException {
        return session.createDurableSubscriber(topic, subscriptionName, messageSelector, noLocal);
    }

    private TopicSubscriber createSubscriber(TopicSession session, Topic topic, String messageSelector, boolean noLocal) throws JMSException {
        return session.createSubscriber(topic, messageSelector, noLocal);
    }

    private final class TopicDestinationEndpoint
    extends TopicEndpoint {
        Topic m_topic;

        TopicDestinationEndpoint(Topic topic) throws JMSException {
            super(topic.getTopicName());
            this.m_topic = topic;
        }

        Destination getDestination(Session session) {
            return this.m_topic;
        }
    }

    private final class TopicSubscription
    extends Subscription {
        String m_subscriptionName;
        boolean m_unsubscribe;
        boolean m_noLocal;

        TopicSubscription(MessageListener listener, JMSEndpoint endpoint, HashMap properties) {
            super(listener, endpoint, properties);
            this.m_subscriptionName = MapUtils.removeStringProperty(properties, "transport.jms.subscriptionName", null);
            this.m_unsubscribe = MapUtils.removeBooleanProperty(properties, "transport.jms.unsubscribe", false);
            this.m_noLocal = MapUtils.removeBooleanProperty(properties, "transport.jms.noLocal", false);
        }

        boolean isDurable() {
            return this.m_subscriptionName != null;
        }

        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            if (!(obj instanceof TopicSubscription)) {
                return false;
            }
            TopicSubscription other = (TopicSubscription)obj;
            if (other.m_unsubscribe != this.m_unsubscribe || other.m_noLocal != this.m_noLocal) {
                return false;
            }
            if (this.isDurable()) {
                return other.isDurable() && other.m_subscriptionName.equals(this.m_subscriptionName);
            }
            return !other.isDurable();
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer(super.toString());
            buffer.append(":").append(this.m_noLocal).append(":").append(this.m_unsubscribe);
            if (this.isDurable()) {
                buffer.append(":");
                buffer.append(this.m_subscriptionName);
            }
            return buffer.toString();
        }
    }

    private class TopicEndpoint
    extends JMSEndpoint {
        String m_topicName;

        TopicEndpoint(String topicName) {
            super(TopicConnector.this);
            this.m_topicName = topicName;
        }

        Destination getDestination(Session session) throws Exception {
            return TopicConnector.this.createTopic((TopicSession)session, this.m_topicName);
        }

        protected Subscription createSubscription(MessageListener listener, HashMap properties) {
            return new TopicSubscription(listener, this, properties);
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer("TopicEndpoint:");
            buffer.append(this.m_topicName);
            return buffer.toString();
        }

        public boolean equals(Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (!(object instanceof TopicEndpoint)) {
                return false;
            }
            return this.m_topicName.equals(((TopicEndpoint)object).m_topicName);
        }
    }

    private final class TopicSyncConnection
    extends JMSConnector.SyncConnection {
        TopicSyncConnection(TopicConnectionFactory connectionFactory, TopicConnection connection, int numSessions, String threadName, String clientID, String username, String password) throws JMSException {
            super((ConnectionFactory)connectionFactory, (Connection)connection, numSessions, threadName, clientID, username, password);
        }

        protected JMSConnector.SyncConnection.SendSession createSendSession(Connection connection) throws JMSException {
            TopicSession session = TopicConnector.this.createTopicSession((TopicConnection)connection, 3);
            TopicPublisher publisher = session.createPublisher(null);
            return new TopicSendSession(session, publisher);
        }

        private final class TopicSendSession
        extends JMSConnector.SyncConnection.SendSession {
            TopicSendSession(TopicSession session, TopicPublisher publisher) throws JMSException {
                super((Session)session, (MessageProducer)publisher);
            }

            protected MessageConsumer createConsumer(Destination destination) throws JMSException {
                return TopicConnector.this.createSubscriber((TopicSession)this.m_session, (Topic)destination, null, false);
            }

            protected void deleteTemporaryDestination(Destination destination) throws JMSException {
                ((TemporaryTopic)destination).delete();
            }

            protected Destination createTemporaryDestination() throws JMSException {
                return ((TopicSession)this.m_session).createTemporaryTopic();
            }

            protected void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
                ((TopicPublisher)this.m_producer).publish((Topic)destination, message, deliveryMode, priority, timeToLive);
            }
        }
    }

    private final class TopicAsyncConnection
    extends JMSConnector.AsyncConnection {
        TopicAsyncConnection(TopicConnectionFactory connectionFactory, TopicConnection connection, String threadName, String clientID, String username, String password) throws JMSException {
            super((ConnectionFactory)connectionFactory, (Connection)connection, threadName, clientID, username, password);
        }

        protected JMSConnector.AsyncConnection.ListenerSession createListenerSession(Connection connection, Subscription subscription) throws Exception {
            TopicSession session = TopicConnector.this.createTopicSession((TopicConnection)connection, subscription.m_ackMode);
            TopicSubscriber subscriber = TopicConnector.this.createSubscriber(session, (TopicSubscription)subscription);
            return new TopicListenerSession(session, subscriber, (TopicSubscription)subscription);
        }

        private final class TopicListenerSession
        extends JMSConnector.AsyncConnection.ListenerSession {
            TopicListenerSession(TopicSession session, TopicSubscriber subscriber, TopicSubscription subscription) throws Exception {
                super((Session)session, (MessageConsumer)subscriber, subscription);
            }

            void cleanup() {
                try {
                    this.m_consumer.close();
                }
                catch (Exception ignore) {
                    // empty catch block
                }
                try {
                    TopicSubscription sub = (TopicSubscription)this.m_subscription;
                    if (sub.isDurable() && sub.m_unsubscribe) {
                        ((TopicSession)this.m_session).unsubscribe(sub.m_subscriptionName);
                    }
                }
                catch (Exception ignore) {
                    // empty catch block
                }
                try {
                    this.m_session.close();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }
}

