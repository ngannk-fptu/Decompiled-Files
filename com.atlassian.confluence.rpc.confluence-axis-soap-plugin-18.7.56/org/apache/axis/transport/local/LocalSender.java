/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.local;

import java.net.URL;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class LocalSender
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$local$LocalSender == null ? (class$org$apache$axis$transport$local$LocalSender = LocalSender.class$("org.apache.axis.transport.local.LocalSender")) : class$org$apache$axis$transport$local$LocalSender).getName());
    private volatile AxisServer server;
    static /* synthetic */ Class class$org$apache$axis$transport$local$LocalSender;

    public synchronized void init() {
        this.server = new AxisServer();
    }

    public void invoke(MessageContext clientContext) throws AxisFault {
        String remoteService;
        String transURL;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: LocalSender::invoke");
        }
        AxisServer targetServer = (AxisServer)clientContext.getProperty("LocalTransport.AxisServer");
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("usingServer00", "LocalSender", "" + targetServer));
        }
        if (targetServer == null) {
            if (this.server == null) {
                this.init();
            }
            targetServer = this.server;
        }
        MessageContext serverContext = new MessageContext(targetServer);
        Message clientRequest = clientContext.getRequestMessage();
        String msgStr = clientRequest.getSOAPPartAsString();
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("sendingXML00", "LocalSender"));
            log.debug((Object)msgStr);
        }
        Message serverRequest = new Message(msgStr);
        Attachments serverAttachments = serverRequest.getAttachmentsImpl();
        Attachments clientAttachments = clientRequest.getAttachmentsImpl();
        if (null != clientAttachments && null != serverAttachments) {
            serverAttachments.setAttachmentParts(clientAttachments.getAttachments());
        }
        serverContext.setRequestMessage(serverRequest);
        serverContext.setTransportName("local");
        String user = clientContext.getUsername();
        if (user != null) {
            serverContext.setUsername(user);
            String pass = clientContext.getPassword();
            if (pass != null) {
                serverContext.setPassword(pass);
            }
        }
        if ((transURL = clientContext.getStrProp("transport.url")) != null) {
            try {
                URL url = new URL(transURL);
                String file = url.getFile();
                if (file.length() > 0 && file.charAt(0) == '/') {
                    file = file.substring(1);
                }
                serverContext.setProperty("realpath", file);
                serverContext.setProperty("transport.url", "local:///" + file);
                serverContext.setTargetService(file);
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
        }
        if ((remoteService = clientContext.getStrProp("LocalTransport.RemoteService")) != null) {
            serverContext.setTargetService(remoteService);
        }
        try {
            targetServer.invoke(serverContext);
        }
        catch (AxisFault fault) {
            Message respMsg = serverContext.getResponseMessage();
            if (respMsg == null) {
                respMsg = new Message(fault);
                serverContext.setResponseMessage(respMsg);
            }
            SOAPFault faultEl = new SOAPFault(fault);
            SOAPEnvelope env = respMsg.getSOAPEnvelope();
            env.clearBody();
            env.addBodyElement(faultEl);
        }
        clientContext.setResponseMessage(serverContext.getResponseMessage());
        clientContext.getResponseMessage().getSOAPPartAsString();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: LocalSender::invoke");
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

