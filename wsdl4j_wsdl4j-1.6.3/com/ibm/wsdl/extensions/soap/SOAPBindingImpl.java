/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import com.ibm.wsdl.extensions.soap.SOAPConstants;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

public class SOAPBindingImpl
implements SOAPBinding {
    protected QName elementType = SOAPConstants.Q_ELEM_SOAP_BINDING;
    protected Boolean required = null;
    protected String style = null;
    protected String transportURI = null;
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

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return this.style;
    }

    public void setTransportURI(String transportURI) {
        this.transportURI = transportURI;
    }

    public String getTransportURI() {
        return this.transportURI;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SOAPBinding (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.transportURI != null) {
            strBuf.append("\ntransportURI=" + this.transportURI);
        }
        if (this.style != null) {
            strBuf.append("\nstyle=" + this.style);
        }
        return strBuf.toString();
    }
}

