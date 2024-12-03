/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.BodyBuilder;
import org.apache.axis.message.HeaderBuilder;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EnvelopeBuilder
extends SOAPHandler {
    private SOAPEnvelope envelope;
    private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
    private boolean gotHeader = false;
    private boolean gotBody = false;

    public EnvelopeBuilder(String messageType, SOAPConstants soapConstants) {
        this.envelope = new SOAPEnvelope(false, soapConstants);
        this.envelope.setMessageType(messageType);
        this.myElement = this.envelope;
    }

    public EnvelopeBuilder(SOAPEnvelope env, String messageType) {
        this.envelope = env;
        this.envelope.setMessageType(messageType);
        this.myElement = this.envelope;
    }

    public SOAPEnvelope getEnvelope() {
        return this.envelope;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (!localName.equals("Envelope")) {
            throw new SAXException(Messages.getMessage("badTag00", localName));
        }
        MessageContext msgContext = context.getMessageContext();
        SOAPConstants singleVersion = null;
        if (msgContext != null) {
            singleVersion = (SOAPConstants)msgContext.getProperty("SingleSOAPVersion");
        }
        this.soapConstants = namespace.equals("http://schemas.xmlsoap.org/soap/envelope/") ? SOAPConstants.SOAP11_CONSTANTS : (namespace.equals("http://www.w3.org/2003/05/soap-envelope") ? SOAPConstants.SOAP12_CONSTANTS : null);
        if (this.soapConstants == null || singleVersion != null && this.soapConstants != singleVersion) {
            this.soapConstants = SOAPConstants.SOAP11_CONSTANTS;
            if (singleVersion == null) {
                singleVersion = this.soapConstants;
            }
            try {
                AxisFault fault = new AxisFault(this.soapConstants.getVerMismatchFaultCodeQName(), null, Messages.getMessage("versionMissmatch00"), null, null, null);
                SOAPHeaderElement newHeader = new SOAPHeaderElement(this.soapConstants.getEnvelopeURI(), "Upgrade");
                MessageElement innerHeader = new MessageElement(this.soapConstants.getEnvelopeURI(), "SupportedEnvelope");
                innerHeader.addAttribute(null, "qname", new QName(singleVersion.getEnvelopeURI(), "Envelope"));
                newHeader.addChildElement(innerHeader);
                fault.addHeader(newHeader);
                throw new SAXException(fault);
            }
            catch (SOAPException e) {
                throw new SAXException(e);
            }
        }
        if (context.getMessageContext() != null) {
            context.getMessageContext().setSOAPConstants(this.soapConstants);
        }
        if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Envelope"), null, null, null);
            throw new SAXException(fault);
        }
        this.envelope.setPrefix(prefix);
        this.envelope.setNamespaceURI(namespace);
        this.envelope.setNSMappings(context.getCurrentNSMappings());
        this.envelope.setSoapConstants(this.soapConstants);
        context.pushNewElement(this.envelope);
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        QName thisQName = new QName(namespace, localName);
        if (thisQName.equals(this.soapConstants.getHeaderQName())) {
            if (this.gotHeader) {
                throw new SAXException(Messages.getMessage("only1Header00"));
            }
            this.gotHeader = true;
            return new HeaderBuilder(this.envelope);
        }
        if (thisQName.equals(this.soapConstants.getBodyQName())) {
            if (this.gotBody) {
                throw new SAXException(Messages.getMessage("only1Body00"));
            }
            this.gotBody = true;
            return new BodyBuilder(this.envelope);
        }
        if (!this.gotBody) {
            throw new SAXException(Messages.getMessage("noCustomElems00"));
        }
        if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            throw new SAXException(Messages.getMessage("noElemAfterBody12"));
        }
        try {
            MessageElement element = new MessageElement(namespace, localName, prefix, attributes, context);
            if (element.getFixupDeserializer() != null) {
                return (SOAPHandler)((Object)element.getFixupDeserializer());
            }
        }
        catch (AxisFault axisFault) {
            throw new SAXException(axisFault);
        }
        return null;
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) {
    }

    public void endElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        this.envelope.setDirty(false);
    }
}

