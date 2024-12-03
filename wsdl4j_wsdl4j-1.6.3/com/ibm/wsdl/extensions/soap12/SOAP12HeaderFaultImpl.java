/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import com.ibm.wsdl.extensions.soap12.SOAP12Constants;
import javax.wsdl.extensions.soap12.SOAP12HeaderFault;
import javax.xml.namespace.QName;

public class SOAP12HeaderFaultImpl
implements SOAP12HeaderFault {
    protected QName elementType = SOAP12Constants.Q_ELEM_SOAP_HEADER_FAULT;
    protected Boolean required = null;
    protected QName message = null;
    protected String part = null;
    protected String use = null;
    protected String encodingStyle = null;
    protected String namespaceURI = null;
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

    public void setEncodingStyle(String encodingStyle) {
        this.encodingStyle = encodingStyle;
    }

    public String getEncodingStyle() {
        return this.encodingStyle;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SOAPHeaderFault (" + this.elementType + "):");
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
        if (this.encodingStyle != null) {
            strBuf.append("\nencodingStyles=" + this.encodingStyle);
        }
        if (this.namespaceURI != null) {
            strBuf.append("\nnamespaceURI=" + this.namespaceURI);
        }
        return strBuf.toString();
    }
}

