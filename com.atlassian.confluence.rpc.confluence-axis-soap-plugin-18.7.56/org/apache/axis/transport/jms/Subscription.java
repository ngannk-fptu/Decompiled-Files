/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.MessageListener
 */
package org.apache.axis.transport.jms;

import java.util.HashMap;
import javax.jms.MessageListener;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.MapUtils;

public class Subscription {
    MessageListener m_listener;
    JMSEndpoint m_endpoint;
    String m_messageSelector;
    int m_ackMode;

    Subscription(MessageListener listener, JMSEndpoint endpoint, HashMap properties) {
        this.m_listener = listener;
        this.m_endpoint = endpoint;
        this.m_messageSelector = MapUtils.removeStringProperty(properties, "transport.jms.messageSelector", null);
        this.m_ackMode = MapUtils.removeIntProperty(properties, "transport.jms.acknowledgeMode", 3);
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Subscription)) {
            return false;
        }
        Subscription other = (Subscription)obj;
        if (this.m_messageSelector == null ? other.m_messageSelector != null : other.m_messageSelector == null || !other.m_messageSelector.equals(this.m_messageSelector)) {
            return false;
        }
        return this.m_ackMode == other.m_ackMode && this.m_endpoint.equals(other.m_endpoint) && other.m_listener.equals(this.m_listener);
    }

    public String toString() {
        return this.m_listener.toString();
    }
}

