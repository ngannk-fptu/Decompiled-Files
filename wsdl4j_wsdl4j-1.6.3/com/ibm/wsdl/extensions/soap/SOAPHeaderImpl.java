/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import com.ibm.wsdl.extensions.soap.SOAPConstants;
import java.util.List;
import java.util.Vector;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.xml.namespace.QName;

public class SOAPHeaderImpl
implements SOAPHeader {
    protected QName elementType = SOAPConstants.Q_ELEM_SOAP_HEADER;
    protected Boolean required = null;
    protected QName message = null;
    protected String part = null;
    protected String use = null;
    protected List encodingStyles = null;
    protected String namespaceURI = null;
    protected List soapHeaderFaults = new Vector();
    public static final long serialVersionUID = 1L;

    public void setElementType(QName elementType) {
        this.elementType = elementType;
    }

    public QName getElementType() {
        return this.elementType;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequired() {
        return this.required;
    }

    public void setMessage(QName message) {
        this.message = message;
    }

    public QName getMessage() {
        return this.message;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getPart() {
        return this.part;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getUse() {
        return this.use;
    }

    public void setEncodingStyles(List encodingStyles) {
        this.encodingStyles = encodingStyles;
    }

    public List getEncodingStyles() {
        return this.encodingStyles;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void addSOAPHeaderFault(SOAPHeaderFault soapHeaderFault) {
        this.soapHeaderFaults.add(soapHeaderFault);
    }

    public SOAPHeaderFault removeSOAPHeaderFault(SOAPHeaderFault soapHeaderFault) {
        if (this.soapHeaderFaults.remove(soapHeaderFault)) {
            return soapHeaderFault;
        }
        return null;
    }

    public List getSOAPHeaderFaults() {
        return this.soapHeaderFaults;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SOAPHeader (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.message != null) {
            strBuf.append("\nmessage=" + this.message);
        }
        if (this.part != null) {
            strBuf.append("\npart=" + this.part);
        }
        if (this.use != null) {
            strBuf.append("\nuse=" + this.use);
        }
        if (this.encodingStyles != null) {
            strBuf.append("\nencodingStyles=" + this.encodingStyles);
        }
        if (this.namespaceURI != null) {
            strBuf.append("\nnamespaceURI=" + this.namespaceURI);
        }
        if (this.soapHeaderFaults != null) {
            strBuf.append("\nsoapHeaderFaults=" + this.soapHeaderFaults);
        }
        return strBuf.toString();
    }
}

