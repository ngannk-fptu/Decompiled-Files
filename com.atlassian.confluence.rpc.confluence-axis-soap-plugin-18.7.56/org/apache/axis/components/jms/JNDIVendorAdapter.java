/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.ConnectionFactory
 *  javax.jms.Queue
 *  javax.jms.QueueConnectionFactory
 *  javax.jms.QueueSession
 *  javax.jms.Topic
 *  javax.jms.TopicConnectionFactory
 *  javax.jms.TopicSession
 */
package org.apache.axis.components.jms;

import java.util.HashMap;
import java.util.Hashtable;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.transport.jms.JMSURLHelper;

public class JNDIVendorAdapter
extends JMSVendorAdapter {
    public static final String CONTEXT_FACTORY = "java.naming.factory.initial";
    public static final String PROVIDER_URL = "java.naming.provider.url";
    public static final String _CONNECTION_FACTORY_JNDI_NAME = "ConnectionFactoryJNDIName";
    public static final String CONNECTION_FACTORY_JNDI_NAME = "transport.jms.ConnectionFactoryJNDIName";
    private Context context;

    public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig) throws Exception {
        return (QueueConnectionFactory)this.getConnectionFactory(cfConfig);
    }

    public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig) throws Exception {
        return (TopicConnectionFactory)this.getConnectionFactory(cfConfig);
    }

    private ConnectionFactory getConnectionFactory(HashMap cfProps) throws Exception {
        String providerURL;
        if (cfProps == null) {
            throw new IllegalArgumentException("noCFProps");
        }
        String jndiName = (String)cfProps.get(CONNECTION_FACTORY_JNDI_NAME);
        if (jndiName == null || jndiName.trim().length() == 0) {
            throw new IllegalArgumentException("noCFName");
        }
        Hashtable<String, String> environment = new Hashtable<String, String>(cfProps);
        String ctxFactory = (String)cfProps.get(CONTEXT_FACTORY);
        if (ctxFactory != null) {
            environment.put(CONTEXT_FACTORY, ctxFactory);
        }
        if ((providerURL = (String)cfProps.get(PROVIDER_URL)) != null) {
            environment.put(PROVIDER_URL, providerURL);
        }
        this.context = new InitialContext(environment);
        return (ConnectionFactory)this.context.lookup(jndiName);
    }

    public void addVendorConnectionFactoryProperties(JMSURLHelper jmsurl, HashMap cfConfig) {
        String providerURL;
        String ctxFactory;
        String cfJNDIName = jmsurl.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME);
        if (cfJNDIName != null) {
            cfConfig.put(CONNECTION_FACTORY_JNDI_NAME, cfJNDIName);
        }
        if ((ctxFactory = jmsurl.getPropertyValue(CONTEXT_FACTORY)) != null) {
            cfConfig.put(CONTEXT_FACTORY, ctxFactory);
        }
        if ((providerURL = jmsurl.getPropertyValue(PROVIDER_URL)) != null) {
            cfConfig.put(PROVIDER_URL, providerURL);
        }
    }

    public boolean isMatchingConnectionFactory(ConnectionFactory cf, JMSURLHelper originalJMSURL, HashMap cfProps) {
        String originalCfJndiName;
        JMSURLHelper jmsurl = (JMSURLHelper)cfProps.get("transport.jms.EndpointAddress");
        String cfJndiName = jmsurl.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME);
        return cfJndiName.equalsIgnoreCase(originalCfJndiName = originalJMSURL.getPropertyValue(_CONNECTION_FACTORY_JNDI_NAME));
    }

    public Queue getQueue(QueueSession session, String name) throws Exception {
        return (Queue)this.context.lookup(name);
    }

    public Topic getTopic(TopicSession session, String name) throws Exception {
        return (Topic)this.context.lookup(name);
    }
}

