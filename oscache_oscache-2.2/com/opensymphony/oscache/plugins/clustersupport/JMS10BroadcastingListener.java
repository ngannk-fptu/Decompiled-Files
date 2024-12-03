/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.JMSException
 *  javax.jms.Message
 *  javax.jms.MessageListener
 *  javax.jms.ObjectMessage
 *  javax.jms.Topic
 *  javax.jms.TopicConnection
 *  javax.jms.TopicConnectionFactory
 *  javax.jms.TopicPublisher
 *  javax.jms.TopicSession
 *  javax.jms.TopicSubscriber
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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMS10BroadcastingListener
extends AbstractBroadcastingListener {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$plugins$clustersupport$JMS10BroadcastingListener == null ? (class$com$opensymphony$oscache$plugins$clustersupport$JMS10BroadcastingListener = JMS10BroadcastingListener.class$("com.opensymphony.oscache.plugins.clustersupport.JMS10BroadcastingListener")) : class$com$opensymphony$oscache$plugins$clustersupport$JMS10BroadcastingListener));
    private String clusterNode;
    private TopicConnection connection;
    private TopicPublisher publisher;
    private TopicSession publisherSession;
    static /* synthetic */ Class class$com$opensymphony$oscache$plugins$clustersupport$JMS10BroadcastingListener;

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
            TopicConnectionFactory connectionFactory = (TopicConnectionFactory)jndi.lookup(topicFactory);
            this.connection = connectionFactory.createTopicConnection();
            this.publisherSession = this.connection.createTopicSession(false, 1);
            TopicSession subSession = this.connection.createTopicSession(false, 1);
            Topic chatTopic = (Topic)jndi.lookup(topic);
            this.publisher = this.publisherSession.createPublisher(chatTopic);
            TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
            subscriber.setMessageListener(new MessageListener(){

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
                        if (!objectMessage.getStringProperty("nodeName").equals(JMS10BroadcastingListener.this.clusterNode)) {
                            ClusterNotification notification = (ClusterNotification)objectMessage.getObject();
                            JMS10BroadcastingListener.this.handleClusterNotification(notification);
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
            throw new InitializationException("Initialization of the JMS10BroadcastingListener failed: " + e);
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
            this.publisher.publish((Message)objectMessage);
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

