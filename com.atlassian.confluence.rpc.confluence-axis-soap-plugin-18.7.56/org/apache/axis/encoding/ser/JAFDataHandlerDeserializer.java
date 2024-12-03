/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentUtils;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class JAFDataHandlerDeserializer
extends DeserializerImpl {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer = JAFDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.JAFDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        String href;
        if (!context.isDoneParsing() && this.myElement == null) {
            try {
                this.myElement = this.makeNewElement(namespace, localName, prefix, attributes, context);
            }
            catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
            context.pushNewElement(this.myElement);
        }
        SOAPConstants soapConstants = context.getSOAPConstants();
        QName type = context.getTypeFromAttributes(namespace, localName, attributes);
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("gotType00", "Deser", "" + type));
        }
        if ((href = attributes.getValue(soapConstants.getAttrHref())) != null) {
            Object ref = context.getObjectByRef(href);
            try {
                ref = AttachmentUtils.getActivationDataHandler((Part)ref);
            }
            catch (AxisFault e) {
                // empty catch block
            }
            this.setValue(ref);
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        throw new SAXException(Messages.getMessage("noSubElements", namespace + ":" + localName));
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

