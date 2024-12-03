/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.io.InputStream;
import java.security.MessageDigest;
import javax.activation.DataHandler;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentUtils;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class MD5AttachHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$MD5AttachHandler == null ? (class$org$apache$axis$handlers$MD5AttachHandler = MD5AttachHandler.class$("org.apache.axis.handlers.MD5AttachHandler")) : class$org$apache$axis$handlers$MD5AttachHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$MD5AttachHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: MD5AttachHandler::invoke");
        try {
            Node n;
            Message msg = msgContext.getRequestMessage();
            SOAPConstants soapConstants = msgContext.getSOAPConstants();
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPBodyElement sbe = env.getFirstBody();
            Element sbElement = sbe.getAsDOM();
            for (n = sbElement.getFirstChild(); n != null && !(n instanceof Element); n = n.getNextSibling()) {
            }
            Element paramElement = (Element)n;
            String href = paramElement.getAttribute(soapConstants.getAttrHref());
            Part ap = msg.getAttachmentsImpl().getAttachmentByReference(href);
            DataHandler dh = AttachmentUtils.getActivationDataHandler(ap);
            Node timeNode = paramElement.getFirstChild();
            long startTime = -1L;
            if (timeNode != null && timeNode instanceof Text) {
                String startTimeStr = ((Text)timeNode).getData();
                startTime = Long.parseLong(startTimeStr);
            }
            long receivedTime = System.currentTimeMillis();
            long elapsedTime = -1L;
            if (startTime > 0L) {
                elapsedTime = receivedTime - startTime;
            }
            String elapsedTimeStr = elapsedTime + "";
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream attachmentStream = dh.getInputStream();
            int bread = 0;
            byte[] buf = new byte[65536];
            do {
                if ((bread = attachmentStream.read(buf)) <= 0) continue;
                md.update(buf, 0, bread);
            } while (bread > -1);
            attachmentStream.close();
            buf = null;
            String contentType = dh.getContentType();
            if (contentType != null && contentType.length() != 0) {
                md.update(contentType.getBytes("US-ASCII"));
            }
            sbe = env.getFirstBody();
            sbElement = sbe.getAsDOM();
            for (n = sbElement.getFirstChild(); n != null && !(n instanceof Element); n = n.getNextSibling()) {
            }
            paramElement = (Element)n;
            String MD5String = Base64.encode(md.digest());
            String senddata = " elapsedTime=" + elapsedTimeStr + " MD5=" + MD5String;
            paramElement.appendChild(paramElement.getOwnerDocument().createTextNode(senddata));
            sbe = new SOAPBodyElement(sbElement);
            env.clearBody();
            env.addBodyElement(sbe);
            msg = new Message(env);
            msgContext.setResponseMessage(msg);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
        log.debug((Object)"Exit: MD5AttachHandler::invoke");
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

