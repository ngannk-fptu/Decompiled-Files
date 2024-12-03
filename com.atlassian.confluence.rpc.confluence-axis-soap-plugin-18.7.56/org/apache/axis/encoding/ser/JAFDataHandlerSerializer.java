/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.axis.Part;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class JAFDataHandlerSerializer
implements Serializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer = JAFDataHandlerSerializer.class$("org.apache.axis.encoding.ser.JAFDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        DataHandler dh = (DataHandler)value;
        Attachments attachments = context.getCurrentMessage().getAttachmentsImpl();
        if (attachments == null) {
            throw new IOException(Messages.getMessage("noAttachments"));
        }
        SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();
        Part attachmentPart = attachments.createAttachmentPart(dh);
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null && 0 < attributes.getLength()) {
            attrs.setAttributes(attributes);
        }
        int typeIndex = -1;
        typeIndex = attrs.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
        if (typeIndex != -1) {
            attrs.removeAttribute(typeIndex);
        }
        boolean doTheDIME = false;
        if (attachments.getSendType() == 3) {
            doTheDIME = true;
        }
        attrs.addAttribute("", soapConstants.getAttrHref(), soapConstants.getAttrHref(), "CDATA", doTheDIME ? attachmentPart.getContentId() : attachmentPart.getContentIdRef());
        context.startElement(name, attrs);
        context.endElement();
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
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

