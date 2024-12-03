/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.BytesMessage
 *  javax.jms.Destination
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.SimpleJMSListener;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class SimpleJMSWorker
implements Runnable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$jms$SimpleJMSWorker == null ? (class$org$apache$axis$transport$jms$SimpleJMSWorker = SimpleJMSWorker.class$("org.apache.axis.transport.jms.SimpleJMSWorker")) : class$org$apache$axis$transport$jms$SimpleJMSWorker).getName());
    SimpleJMSListener listener;
    BytesMessage message;
    static /* synthetic */ Class class$org$apache$axis$transport$jms$SimpleJMSWorker;

    public SimpleJMSWorker(SimpleJMSListener listener, BytesMessage message) {
        this.listener = listener;
        this.message = message;
    }

    public void run() {
        ByteArrayInputStream in = null;
        try {
            byte[] buffer = new byte[8192];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bytesRead = this.message.readBytes(buffer);
            while (bytesRead != -1) {
                out.write(buffer, 0, bytesRead);
                bytesRead = this.message.readBytes(buffer);
            }
            in = new ByteArrayInputStream(out.toByteArray());
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            e.printStackTrace();
            return;
        }
        AxisServer server = SimpleJMSListener.getAxisServer();
        String contentType = null;
        try {
            contentType = this.message.getStringProperty("contentType");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = null;
        msg = contentType != null && !contentType.trim().equals("") ? new Message(in, true, contentType, null) : new Message(in);
        MessageContext msgContext = new MessageContext(server);
        msgContext.setRequestMessage(msg);
        try {
            server.invoke(msgContext);
            msg = msgContext.getResponseMessage();
        }
        catch (AxisFault af) {
            msg = new Message(af);
            msg.setMessageContext(msgContext);
        }
        catch (Exception e) {
            msg = new Message(new AxisFault(e.toString()));
            msg.setMessageContext(msgContext);
        }
        try {
            Destination destination = this.message.getJMSReplyTo();
            if (destination == null) {
                return;
            }
            JMSEndpoint replyTo = this.listener.getConnector().createEndpoint(destination);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            replyTo.send(out.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (msgContext.getProperty("quit.requested") != null) {
            try {
                this.listener.shutdown();
            }
            catch (Exception e) {
                // empty catch block
            }
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

