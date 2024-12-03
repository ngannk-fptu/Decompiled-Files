/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

public class SOAPHeaderElement
extends MessageElement
implements javax.xml.soap.SOAPHeaderElement {
    protected boolean processed = false;
    protected String actor = "http://schemas.xmlsoap.org/soap/actor/next";
    protected boolean mustUnderstand = false;
    protected boolean relay = false;
    boolean alreadySerialized = false;

    public SOAPHeaderElement(String namespace, String localPart) {
        super(namespace, localPart);
    }

    public SOAPHeaderElement(Name name) {
        super(name);
    }

    public SOAPHeaderElement(QName qname) {
        super(qname);
    }

    public SOAPHeaderElement(String namespace, String localPart, Object value) {
        super(namespace, localPart, value);
    }

    public SOAPHeaderElement(QName qname, Object value) {
        super(qname, value);
    }

    public SOAPHeaderElement(Element elem) {
        super(elem);
        SOAPConstants soapConstants = this.getSOAPConstants();
        String val = elem.getAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand");
        try {
            this.setMustUnderstandFromString(val, soapConstants == SOAPConstants.SOAP12_CONSTANTS);
        }
        catch (AxisFault axisFault) {
            log.error((Object)axisFault);
        }
        QName roleQName = soapConstants.getRoleAttributeQName();
        this.actor = elem.getAttributeNS(roleQName.getNamespaceURI(), roleQName.getLocalPart());
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = elem.getAttributeNS(soapConstants.getEnvelopeURI(), "relay");
            this.relay = relayVal != null && (relayVal.equals("true") || relayVal.equals("1"));
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if (parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        }
        if (parent instanceof SOAPEnvelope) {
            log.warn((Object)Messages.getMessage("bodyHeaderParent"));
            parent = ((SOAPEnvelope)parent).getHeader();
        }
        if (!(parent instanceof SOAPHeader)) {
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        }
        super.setParentElement(parent);
    }

    public SOAPHeaderElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
        SOAPConstants soapConstants = this.getSOAPConstants();
        String val = attributes.getValue(soapConstants.getEnvelopeURI(), "mustUnderstand");
        this.setMustUnderstandFromString(val, soapConstants == SOAPConstants.SOAP12_CONSTANTS);
        QName roleQName = soapConstants.getRoleAttributeQName();
        this.actor = attributes.getValue(roleQName.getNamespaceURI(), roleQName.getLocalPart());
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = attributes.getValue(soapConstants.getEnvelopeURI(), "relay");
            this.relay = relayVal != null && (relayVal.equals("true") || relayVal.equals("1"));
        }
        this.processed = false;
        this.alreadySerialized = true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void setMustUnderstandFromString(String val, boolean isSOAP12) throws AxisFault {
        if (val == null || val.length() <= 0) return;
        if ("0".equals(val)) {
            this.mustUnderstand = false;
            return;
        } else if ("1".equals(val)) {
            this.mustUnderstand = true;
            return;
        } else {
            if (!isSOAP12) throw new AxisFault(Messages.getMessage("badMUVal", val, new QName(this.namespaceURI, this.name).toString()));
            if ("true".equalsIgnoreCase(val)) {
                this.mustUnderstand = true;
                return;
            } else {
                if (!"false".equalsIgnoreCase(val)) throw new AxisFault(Messages.getMessage("badMUVal", val, new QName(this.namespaceURI, this.name).toString()));
                this.mustUnderstand = false;
            }
        }
    }

    public boolean getMustUnderstand() {
        return this.mustUnderstand;
    }

    public void setMustUnderstand(boolean b) {
        this.mustUnderstand = b;
    }

    public String getActor() {
        return this.actor;
    }

    public void setActor(String a) {
        this.actor = a;
    }

    public String getRole() {
        return this.actor;
    }

    public void setRole(String a) {
        this.actor = a;
    }

    public boolean getRelay() {
        return this.relay;
    }

    public void setRelay(boolean relay) {
        this.relay = relay;
    }

    public void setProcessed(boolean value) {
        this.processed = value;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        if (!this.alreadySerialized) {
            SOAPConstants soapVer = this.getSOAPConstants();
            QName roleQName = soapVer.getRoleAttributeQName();
            if (this.actor != null) {
                this.setAttribute(roleQName.getNamespaceURI(), roleQName.getLocalPart(), this.actor);
            }
            String val = context.getMessageContext() != null && context.getMessageContext().getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS ? (this.mustUnderstand ? "true" : "false") : (this.mustUnderstand ? "1" : "0");
            this.setAttribute(soapVer.getEnvelopeURI(), "mustUnderstand", val);
            if (soapVer == SOAPConstants.SOAP12_CONSTANTS && this.relay) {
                this.setAttribute(soapVer.getEnvelopeURI(), "relay", "true");
            }
        }
        super.outputImpl(context);
    }

    public NamedNodeMap getAttributes() {
        this.makeAttributesEditable();
        SOAPConstants soapConstants = this.getSOAPConstants();
        String mustUnderstand = this.attributes.getValue(soapConstants.getEnvelopeURI(), "mustUnderstand");
        QName roleQName = soapConstants.getRoleAttributeQName();
        String actor = this.attributes.getValue(roleQName.getNamespaceURI(), roleQName.getLocalPart());
        if (mustUnderstand == null) {
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                this.setAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand", "false");
            } else {
                this.setAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand", "0");
            }
        }
        if (actor == null) {
            this.setAttributeNS(roleQName.getNamespaceURI(), roleQName.getLocalPart(), this.actor);
        }
        return super.getAttributes();
    }

    private SOAPConstants getSOAPConstants() {
        SOAPConstants soapConstants = null;
        if (this.context != null) {
            return this.context.getSOAPConstants();
        }
        if (this.getNamespaceURI() != null && this.getNamespaceURI().equals(SOAPConstants.SOAP12_CONSTANTS.getEnvelopeURI())) {
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;
        }
        if (soapConstants == null && this.getEnvelope() != null) {
            soapConstants = this.getEnvelope().getSOAPConstants();
        }
        if (soapConstants == null) {
            soapConstants = SOAPConstants.SOAP11_CONSTANTS;
        }
        return soapConstants;
    }
}

