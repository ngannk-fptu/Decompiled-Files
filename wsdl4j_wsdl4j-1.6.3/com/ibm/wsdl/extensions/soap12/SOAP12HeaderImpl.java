/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import com.ibm.wsdl.extensions.soap12.SOAP12Constants;
import java.util.List;
import java.util.Vector;
import javax.wsdl.extensions.soap12.SOAP12Header;
import javax.wsdl.extensions.soap12.SOAP12HeaderFault;
import javax.xml.namespace.QName;

public class SOAP12HeaderImpl
implements SOAP12Header {
    protected QName elementType = SOAP12Constants.Q_ELEM_SOAP_HEADER;
    protected Boolean required = null;
    protected QName message = null;
    protected String part = null;
    protected String use = null;
    protected String encodingStyle = null;
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

    public void addSOAP12HeaderFault(SOAP12HeaderFault soap12HeaderFault) {
        this.soapHeaderFaults.add(soap12HeaderFault);
    }

    public SOAP12HeaderFault removeSOAP12HeaderFault(SOAP12HeaderFault soap12HeaderFault) {
        if (this.soapHeaderFaults.remove(soap12HeaderFault)) {
            return soap12HeaderFault;
        }
        return null;
    }

    public List getSOAP12HeaderFaults() {
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
        if (this.encodingStyle != null) {
            strBuf.append("\nencodingStyle=" + this.encodingStyle);
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

