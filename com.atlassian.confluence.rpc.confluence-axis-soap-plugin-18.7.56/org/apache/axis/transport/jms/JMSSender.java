/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.Destination
 */
package org.apache.axis.transport.jms;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSConnectorManager;
import org.apache.axis.transport.jms.JMSEndpoint;

public class JMSSender
extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        JMSConnector connector = null;
        try {
            block10: {
                try {
                    String contentType;
                    Object destination = msgContext.getProperty("transport.jms.Destination");
                    if (destination == null) {
                        throw new AxisFault("noDestination");
                    }
                    connector = (JMSConnector)msgContext.getProperty("transport.jms.Connector");
                    JMSEndpoint endpoint = null;
                    endpoint = destination instanceof String ? connector.createEndpoint((String)destination) : connector.createEndpoint((Destination)destination);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    msgContext.getRequestMessage().writeTo(out);
                    HashMap props = this.createSendProperties(msgContext);
                    Object ret = null;
                    Message message = msgContext.getRequestMessage();
                    Attachments mAttachments = message.getAttachmentsImpl();
                    if (mAttachments != null && 0 != mAttachments.getAttachmentCount() && (contentType = mAttachments.getContentType()) != null && !contentType.trim().equals("")) {
                        props.put("contentType", contentType);
                    }
                    boolean waitForResponse = true;
                    if (msgContext.containsProperty("transport.jms.waitForResponse")) {
                        waitForResponse = (Boolean)msgContext.getProperty("transport.jms.waitForResponse");
                    }
                    if (waitForResponse) {
                        long timeout = msgContext.getTimeout();
                        byte[] response = endpoint.call(out.toByteArray(), timeout, props);
                        Message msg = new Message(response);
                        msgContext.setResponseMessage(msg);
                        break block10;
                    }
                    endpoint.send(out.toByteArray(), props);
                }
                catch (Exception e) {
                    throw new AxisFault("failedSend", e);
                }
            }
            Object var16_16 = null;
            if (connector != null) {
                JMSConnectorManager.getInstance().release(connector);
            }
        }
        catch (Throwable throwable) {
            Object var16_17 = null;
            if (connector != null) {
                JMSConnectorManager.getInstance().release(connector);
            }
            throw throwable;
        }
    }

    private HashMap createSendProperties(MessageContext context) {
        HashMap props = this.createApplicationProperties(context);
        if (context.containsProperty("transport.jms.priority")) {
            props.put("transport.jms.priority", context.getProperty("transport.jms.priority"));
        }
        if (context.containsProperty("transport.jms.deliveryMode")) {
            props.put("transport.jms.deliveryMode", context.getProperty("transport.jms.deliveryMode"));
        }
        if (context.containsProperty("transport.jms.ttl")) {
            props.put("transport.jms.ttl", context.getProperty("transport.jms.ttl"));
        }
        if (context.containsProperty("transport.jms.jmsCorrelationID")) {
            props.put("transport.jms.jmsCorrelationID", context.getProperty("transport.jms.jmsCorrelationID"));
        }
        return props;
    }

    protected HashMap createApplicationProperties(MessageContext context) {
        HashMap props = null;
        if (context.containsProperty("transport.jms.msgProps")) {
            props = new HashMap();
            props.putAll((Map)context.getProperty("transport.jms.msgProps"));
        }
        return props;
    }
}

