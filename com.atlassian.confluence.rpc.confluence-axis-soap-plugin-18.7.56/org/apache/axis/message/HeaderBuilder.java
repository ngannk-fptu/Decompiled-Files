/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HeaderBuilder
extends SOAPHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$HeaderBuilder == null ? (class$org$apache$axis$message$HeaderBuilder = HeaderBuilder.class$("org.apache.axis.message.HeaderBuilder")) : class$org$apache$axis$message$HeaderBuilder).getName());
    private SOAPHeaderElement header;
    private SOAPEnvelope envelope;
    static /* synthetic */ Class class$org$apache$axis$message$HeaderBuilder;

    HeaderBuilder(SOAPEnvelope envelope) {
        this.envelope = envelope;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Header"), null, null, null);
            throw new SAXException(fault);
        }
        if (!context.isDoneParsing()) {
            if (this.myElement == null) {
                try {
                    this.myElement = new SOAPHeader(namespace, localName, prefix, attributes, context, this.envelope.getSOAPConstants());
                }
                catch (AxisFault axisFault) {
                    throw new SAXException(axisFault);
                }
                this.envelope.setHeader((SOAPHeader)this.myElement);
            }
            context.pushNewElement(this.myElement);
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        try {
            this.header = new SOAPHeaderElement(namespace, localName, prefix, attributes, context);
        }
        catch (AxisFault axisFault) {
            throw new SAXException(axisFault);
        }
        SOAPHandler handler = new SOAPHandler();
        handler.myElement = this.header;
        return handler;
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) {
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

