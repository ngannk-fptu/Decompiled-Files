/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.Destination
 *  javax.jms.MessageListener
 *  javax.jms.Session
 */
package org.apache.axis.transport.jms;

import java.util.HashMap;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.Subscription;

public abstract class JMSEndpoint {
    private JMSConnector m_connector;

    protected JMSEndpoint(JMSConnector connector) {
        this.m_connector = connector;
    }

    abstract Destination getDestination(Session var1) throws Exception;

    public byte[] call(byte[] message, long timeout) throws Exception {
        return this.m_connector.getSendConnection().call(this, message, timeout, null);
    }

    public byte[] call(byte[] message, long timeout, HashMap properties) throws Exception {
        if (properties != null) {
            properties = (HashMap)properties.clone();
        }
        return this.m_connector.getSendConnection().call(this, message, timeout, properties);
    }

    public void send(byte[] message) throws Exception {
        this.m_connector.getSendConnection().send(this, message, null);
    }

    public void send(byte[] message, HashMap properties) throws Exception {
        if (properties != null) {
            properties = (HashMap)properties.clone();
        }
        this.m_connector.getSendConnection().send(this, message, properties);
    }

    public void registerListener(MessageListener listener) throws Exception {
        this.m_connector.getReceiveConnection().subscribe(this.createSubscription(listener, null));
    }

    public void registerListener(MessageListener listener, HashMap properties) throws Exception {
        if (properties != null) {
            properties = (HashMap)properties.clone();
        }
        this.m_connector.getReceiveConnection().subscribe(this.createSubscription(listener, properties));
    }

    public void unregisterListener(MessageListener listener) {
        this.m_connector.getReceiveConnection().unsubscribe(this.createSubscription(listener, null));
    }

    public void unregisterListener(MessageListener listener, HashMap properties) {
        if (properties != null) {
            properties = (HashMap)properties.clone();
        }
        this.m_connector.getReceiveConnection().unsubscribe(this.createSubscription(listener, properties));
    }

    protected Subscription createSubscription(MessageListener listener, HashMap properties) {
        return new Subscription(listener, this, properties);
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof JMSEndpoint;
    }
}

