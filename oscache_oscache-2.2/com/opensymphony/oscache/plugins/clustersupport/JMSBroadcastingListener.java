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
 *  javax.jms.ObjectMessage
 *  javax.jms.Session
 *  javax.jms.Topic
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.plugins.clustersupport;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.plugins.clustersupport.AbstractBroadcastingListener;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;
import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSBroadcastingListener
extends AbstractBroadcastingListener {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$plugins$clustersupport$JMSBroadcastingListener == null ? (class$com$opensymphony$oscache$plugins$clustersupport$JMSBroadcastingListener = JMSBroadcastingListener.class$("com.opensymphony.oscache.plugins.clustersupport.JMSBroadcastingListener")) : class$com$opensymphony$oscache$plugins$clustersupport$JMSBroadcastingListener));
    private Connection connection;
    private MessageProducer messagePublisher;
    private Session publisherSession;
    private String clusterNode;
    static /* synthetic */ Class class$com$opensymphony$oscache$plugins$clustersupport$JMSBroadcastingListener;

    public void initialize(Cache cache, Config config) throws InitializationException {
        super.initialize(cache, config);
        this.clusterNode = config.getProperty("cache.cluster.jms.node.name");
        String topic = config.getProperty("cache.cluster.jms.topic.name");
        String topicFactory = config.getProperty("cache.cluster.jms.topic.factory");
        if (log.isInfoEnabled()) {
            log.info((Object)("Starting JMS clustering (node name=" + this.clusterNode + ", topic=" + topic + ", topic factory=" + topicFactory + ")"));
        }
        try {
            InitialContext jndi = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)jndi.lookup(topicFactory);
            this.connection = connectionFactory.createConnection();
            this.publisherSession = this.connection.createSession(false, 1);
            Session subSession = this.connection.createSession(false, 1);
            Topic chatTopic = (Topic)jndi.lookup(topic);
            this.messagePublisher = this.publisherSession.createProducer((Destination)chatTopic);
            MessageConsumer messageConsumer = subSession.createConsumer((Destination)chatTopic);
            messageConsumer.setMessageListener(new MessageListener(){

                public void onMessage(Message message) {
                    try {
                        ObjectMessage objectMessage = null;
                        if (!(message instanceof ObjectMessage)) {
                            log.error((Object)("Cannot handle message of type (class=" + message.getClass().getName() + "). Notification ignored."));
                            return;
                        }
                        objectMessage = (ObjectMessage)message;
                        if (!(objectMessage.getObject() instanceof ClusterNotification)) {
                            log.error((Object)("An unknown cluster notification message received (class=" + objectMessage.getObject().getClass().getName() + "). Notification ignored."));
                            return;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)objectMessage.getObject());
                        }
                        if (!objectMessage.getStringProperty("nodeName").equals(JMSBroadcastingListener.this.clusterNode)) {
                            ClusterNotification notification = (ClusterNotification)objectMessage.getObject();
                            JMSBroadcastingListener.this.handleClusterNotification(notification);
                        }
                    }
                    catch (JMSException jmsEx) {
                        log.error((Object)"Cannot handle cluster Notification", (Throwable)jmsEx);
                    }
                }
            });
            this.connection.start();
        }
        catch (Exception e) {
            throw new InitializationException("Initialization of the JMSBroadcastingListener failed: " + e);
        }
    }

    public void finialize() throws FinalizationException {
        try {
            if (log.isInfoEnabled()) {
                log.info((Object)"Shutting down JMS clustering...");
            }
            this.connection.close();
            if (log.isInfoEnabled()) {
                log.info((Object)"JMS clustering shutdown complete.");
            }
        }
        catch (JMSException e) {
            log.warn((Object)"A problem was encountered when closing the JMS connection", (Throwable)e);
        }
    }

    protected void sendNotification(ClusterNotification message) {
        try {
            ObjectMessage objectMessage = this.publisherSession.createObjectMessage();
            objectMessage.setObject((Serializable)message);
            objectMessage.setStringProperty("nodeName", this.clusterNode);
            this.messagePublisher.send((Message)objectMessage);
        }
        catch (JMSException e) {
            log.error((Object)("Cannot send notification " + message), (Throwable)e);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

