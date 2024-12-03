/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import com.ibm.wsdl.extensions.soap.SOAPConstants;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

public class SOAPOperationImpl
implements SOAPOperation {
    protected QName elementType = SOAPConstants.Q_ELEM_SOAP_OPERATION;
    protected Boolean required = null;
    protected String soapActionURI = null;
    protected String style = null;
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

    public void setSoapActionURI(String soapActionURI) {
        this.soapActionURI = soapActionURI;
    }

    public String getSoapActionURI() {
        return this.soapActionURI;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return this.style;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SOAPOperation (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.soapActionURI != null) {
            strBuf.append("\nsoapActionURI=" + this.soapActionURI);
        }
        if (this.style != null) {
            strBuf.append("\nstyle=" + this.style);
        }
        return strBuf.toString();
    }
}

