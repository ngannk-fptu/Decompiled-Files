/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.ConnectionFactory
 *  javax.jms.InvalidDestinationException
 *  javax.jms.JMSException
 *  javax.jms.JMSSecurityException
 *  javax.jms.Message
 *  javax.jms.Queue
 *  javax.jms.QueueConnectionFactory
 *  javax.jms.QueueSession
 *  javax.jms.Topic
 *  javax.jms.TopicConnectionFactory
 *  javax.jms.TopicSession
 */
package org.apache.axis.components.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jms.ConnectionFactory;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.transport.jms.JMSURLHelper;

public abstract class JMSVendorAdapter {
    public static final int SEND_ACTION = 0;
    public static final int CONNECT_ACTION = 1;
    public static final int SUBSCRIBE_ACTION = 2;
    public static final int RECEIVE_ACTION = 3;
    public static final int ON_EXCEPTION_ACTION = 4;

    public abstract QueueConnectionFactory getQueueConnectionFactory(HashMap var1) throws Exception;

    public abstract TopicConnectionFactory getTopicConnectionFactory(HashMap var1) throws Exception;

    public abstract void addVendorConnectionFactoryProperties(JMSURLHelper var1, HashMap var2);

    public abstract boolean isMatchingConnectionFactory(ConnectionFactory var1, JMSURLHelper var2, HashMap var3);

    public String getVendorId() {
        int index;
        String name = this.getClass().getName();
        if (name.endsWith("VendorAdapter")) {
            index = name.lastIndexOf("VendorAdapter");
            name = name.substring(0, index);
        }
        if ((index = name.lastIndexOf(".")) > 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    public HashMap getJMSConnectorProperties(JMSURLHelper jmsurl) {
        String timeoutTime;
        String numSessions;
        String numRetries;
        String domain;
        String interactRetryInterval;
        String connectRetryInterval;
        HashMap<String, Object> connectorProps = new HashMap<String, Object>();
        connectorProps.put("transport.jms.EndpointAddress", jmsurl);
        String clientID = jmsurl.getPropertyValue("clientID");
        if (clientID != null) {
            connectorProps.put("transport.jms.clientID", clientID);
        }
        if ((connectRetryInterval = jmsurl.getPropertyValue("connectRetryInterval")) != null) {
            connectorProps.put("transport.jms.connectRetryInterval", connectRetryInterval);
        }
        if ((interactRetryInterval = jmsurl.getPropertyValue("interactRetryInterval")) != null) {
            connectorProps.put("transport.jms.interactRetryInterval", interactRetryInterval);
        }
        if ((domain = jmsurl.getPropertyValue("domain")) != null) {
            connectorProps.put("transport.jms.domain", domain);
        }
        if ((numRetries = jmsurl.getPropertyValue("numRetries")) != null) {
            connectorProps.put("transport.jms.numRetries", numRetries);
        }
        if ((numSessions = jmsurl.getPropertyValue("numSessions")) != null) {
            connectorProps.put("transport.jms.numSessions", numSessions);
        }
        if ((timeoutTime = jmsurl.getPropertyValue("timeoutTime")) != null) {
            connectorProps.put("transport.jms.timeoutTime", timeoutTime);
        }
        return connectorProps;
    }

    public HashMap getJMSConnectionFactoryProperties(JMSURLHelper jmsurl) {
        HashMap<String, Object> cfProps = new HashMap<String, Object>();
        cfProps.put("transport.jms.EndpointAddress", jmsurl);
        String domain = jmsurl.getPropertyValue("domain");
        if (domain != null) {
            cfProps.put("transport.jms.domain", domain);
        }
        this.addVendorConnectionFactoryProperties(jmsurl, cfProps);
        return cfProps;
    }

    public Queue getQueue(QueueSession session, String name) throws Exception {
        return session.createQueue(name);
    }

    public Topic getTopic(TopicSession session, String name) throws Exception {
        return session.createTopic(name);
    }

    public boolean isRecoverable(Throwable thrown, int action) {
        if (thrown instanceof RuntimeException || thrown instanceof Error || thrown instanceof JMSSecurityException || thrown instanceof InvalidDestinationException) {
            return false;
        }
        return action != 4;
    }

    public void setProperties(Message message, HashMap props) throws JMSException {
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)props.get(key);
            message.setStringProperty(key, value);
        }
    }

    public void setupMessageContext(MessageContext context, Call call, JMSURLHelper jmsurl) {
        Object tmp = null;
        String jmsurlDestination = null;
        if (jmsurl != null) {
            jmsurlDestination = jmsurl.getDestination();
        }
        if (jmsurlDestination != null) {
            context.setProperty("transport.jms.Destination", jmsurlDestination);
        } else {
            tmp = call.getProperty("transport.jms.Destination");
            if (tmp != null && tmp instanceof String) {
                context.setProperty("transport.jms.Destination", tmp);
            } else {
                context.removeProperty("transport.jms.Destination");
            }
        }
        String delivMode = null;
        if (jmsurl != null) {
            delivMode = jmsurl.getPropertyValue("deliveryMode");
        }
        if (delivMode != null) {
            int mode = 1;
            if (delivMode.equalsIgnoreCase("Persistent")) {
                mode = 2;
            } else if (delivMode.equalsIgnoreCase("Nonpersistent")) {
                mode = 1;
            }
            context.setProperty("transport.jms.deliveryMode", new Integer(mode));
        } else {
            tmp = call.getProperty("transport.jms.deliveryMode");
            if (tmp != null && tmp instanceof Integer) {
                context.setProperty("transport.jms.deliveryMode", tmp);
            } else {
                context.removeProperty("transport.jms.deliveryMode");
            }
        }
        String prio = null;
        if (jmsurl != null) {
            prio = jmsurl.getPropertyValue("priority");
        }
        if (prio != null) {
            context.setProperty("transport.jms.priority", Integer.valueOf(prio));
        } else {
            tmp = call.getProperty("transport.jms.priority");
            if (tmp != null && tmp instanceof Integer) {
                context.setProperty("transport.jms.priority", tmp);
            } else {
                context.removeProperty("transport.jms.priority");
            }
        }
        String ttl = null;
        if (jmsurl != null) {
            ttl = jmsurl.getPropertyValue("ttl");
        }
        if (ttl != null) {
            context.setProperty("transport.jms.ttl", Long.valueOf(ttl));
        } else {
            tmp = call.getProperty("transport.jms.ttl");
            if (tmp != null && tmp instanceof Long) {
                context.setProperty("transport.jms.ttl", tmp);
            } else {
                context.removeProperty("transport.jms.ttl");
            }
        }
        String wait = null;
        if (jmsurl != null) {
            wait = jmsurl.getPropertyValue("waitForResponse");
        }
        if (wait != null) {
            context.setProperty("transport.jms.waitForResponse", Boolean.valueOf(wait));
        } else {
            tmp = call.getProperty("transport.jms.waitForResponse");
            if (tmp != null && tmp instanceof Boolean) {
                context.setProperty("transport.jms.waitForResponse", tmp);
            } else {
                context.removeProperty("transport.jms.waitForResponse");
            }
        }
        this.setupApplicationProperties(context, call, jmsurl);
    }

    public void setupApplicationProperties(MessageContext context, Call call, JMSURLHelper jmsurl) {
        Map callProps;
        Map ctxProps;
        HashMap<String, String> appProps = new HashMap<String, String>();
        if (jmsurl != null && jmsurl.getApplicationProperties() != null) {
            Iterator itr = jmsurl.getApplicationProperties().iterator();
            while (itr.hasNext()) {
                String name = (String)itr.next();
                appProps.put(name, jmsurl.getPropertyValue(name));
            }
        }
        if ((ctxProps = (Map)context.getProperty("transport.jms.msgProps")) != null) {
            appProps.putAll(ctxProps);
        }
        if ((callProps = (Map)call.getProperty("transport.jms.msgProps")) != null) {
            appProps.putAll(callProps);
        }
        context.setProperty("transport.jms.msgProps", appProps);
    }
}

