/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.marshaller.SAX2DOMEx
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFactory
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message.saaj;

import com.sun.xml.bind.marshaller.SAX2DOMEx;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentEx;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SaajStaxWriter;
import com.sun.xml.ws.message.saaj.SAAJMessage;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SAAJFactory {
    private static final SAAJFactory instance = new SAAJFactory();

    public static MessageFactory getMessageFactory(String protocol) throws SOAPException {
        for (SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            MessageFactory mf = s.createMessageFactory(protocol);
            if (mf == null) continue;
            return mf;
        }
        return instance.createMessageFactory(protocol);
    }

    public static SOAPFactory getSOAPFactory(String protocol) throws SOAPException {
        for (SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            SOAPFactory sf = s.createSOAPFactory(protocol);
            if (sf == null) continue;
            return sf;
        }
        return instance.createSOAPFactory(protocol);
    }

    public static Message create(SOAPMessage saaj) {
        for (SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            Message m = s.createMessage(saaj);
            if (m == null) continue;
            return m;
        }
        return instance.createMessage(saaj);
    }

    public static SOAPMessage read(SOAPVersion soapVersion, Message message) throws SOAPException {
        for (SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            SOAPMessage msg = s.readAsSOAPMessage(soapVersion, message);
            if (msg == null) continue;
            return msg;
        }
        return instance.readAsSOAPMessage(soapVersion, message);
    }

    public static SOAPMessage read(SOAPVersion soapVersion, Message message, Packet packet) throws SOAPException {
        SOAPMessage msg;
        SAAJFactory saajfac = packet.getSAAJFactory();
        if (saajfac != null && (msg = saajfac.readAsSOAPMessage(soapVersion, message, packet)) != null) {
            return msg;
        }
        for (SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            SOAPMessage msg2 = s.readAsSOAPMessage(soapVersion, message, packet);
            if (msg2 == null) continue;
            return msg2;
        }
        return instance.readAsSOAPMessage(soapVersion, message, packet);
    }

    public static SAAJMessage read(Packet packet) throws SOAPException {
        SAAJMessage msg;
        SAAJFactory saajfac = packet.getSAAJFactory();
        if (saajfac != null && (msg = saajfac.readAsSAAJ(packet)) != null) {
            return msg;
        }
        ServiceFinder<SAAJFactory> factories = packet.component != null ? ServiceFinder.find(SAAJFactory.class, packet.component) : ServiceFinder.find(SAAJFactory.class);
        for (SAAJFactory s : factories) {
            SAAJMessage msg2 = s.readAsSAAJ(packet);
            if (msg2 == null) continue;
            return msg2;
        }
        return instance.readAsSAAJ(packet);
    }

    public SAAJMessage readAsSAAJ(Packet packet) throws SOAPException {
        SOAPVersion v = packet.getMessage().getSOAPVersion();
        SOAPMessage msg = this.readAsSOAPMessage(v, packet.getMessage());
        return new SAAJMessage(msg);
    }

    public MessageFactory createMessageFactory(String protocol) throws SOAPException {
        return MessageFactory.newInstance((String)protocol);
    }

    public SOAPFactory createSOAPFactory(String protocol) throws SOAPException {
        return SOAPFactory.newInstance((String)protocol);
    }

    public Message createMessage(SOAPMessage saaj) {
        return new SAAJMessage(saaj);
    }

    public SOAPMessage readAsSOAPMessage(SOAPVersion soapVersion, Message message) throws SOAPException {
        SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
        SaajStaxWriter writer = new SaajStaxWriter(msg, soapVersion.nsUri);
        try {
            message.writeTo(writer);
        }
        catch (XMLStreamException e) {
            throw e.getCause() instanceof SOAPException ? (SOAPException)e.getCause() : new SOAPException((Throwable)e);
        }
        msg = writer.getSOAPMessage();
        SAAJFactory.addAttachmentsToSOAPMessage(msg, message);
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
        return msg;
    }

    public SOAPMessage readAsSOAPMessageSax2Dom(SOAPVersion soapVersion, Message message) throws SOAPException {
        SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
        SAX2DOMEx s2d = new SAX2DOMEx((Node)msg.getSOAPPart());
        try {
            message.writeTo((ContentHandler)s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
        }
        catch (SAXException e) {
            throw new SOAPException((Throwable)e);
        }
        SAAJFactory.addAttachmentsToSOAPMessage(msg, message);
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
        return msg;
    }

    protected static void addAttachmentsToSOAPMessage(SOAPMessage msg, Message message) {
        for (Attachment att : message.getAttachments()) {
            AttachmentPart part = msg.createAttachmentPart();
            part.setDataHandler(att.asDataHandler());
            String cid = att.getContentId();
            if (cid != null) {
                if (cid.startsWith("<") && cid.endsWith(">")) {
                    part.setContentId(cid);
                } else {
                    part.setContentId('<' + cid + '>');
                }
            }
            if (att instanceof AttachmentEx) {
                AttachmentEx ax = (AttachmentEx)att;
                Iterator<AttachmentEx.MimeHeader> imh = ax.getMimeHeaders();
                while (imh.hasNext()) {
                    AttachmentEx.MimeHeader ame = imh.next();
                    if ("Content-ID".equalsIgnoreCase(ame.getName()) || "Content-Type".equalsIgnoreCase(ame.getName())) continue;
                    part.addMimeHeader(ame.getName(), ame.getValue());
                }
            }
            msg.addAttachmentPart(part);
        }
    }

    public SOAPMessage readAsSOAPMessage(SOAPVersion soapVersion, Message message, Packet packet) throws SOAPException {
        return this.readAsSOAPMessage(soapVersion, message);
    }
}

