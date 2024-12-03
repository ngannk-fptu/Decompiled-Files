/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import com.ibm.wsdl.extensions.soap12.SOAP12Constants;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;

public class SOAP12OperationImpl
implements SOAP12Operation {
    protected QName elementType = SOAP12Constants.Q_ELEM_SOAP_OPERATION;
    protected Boolean required = null;
    protected String soapActionURI = null;
    protected Boolean soapActionRequired = null;
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

    public void setSoapActionRequired(Boolean soapActionRequired) {
        this.soapActionRequired = soapActionRequired;
    }

    public Boolean getSoapActionRequired() {
        return this.soapActionRequired;
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
        if (this.soapActionRequired != null) {
            strBuf.append("\nsoapActionRequired=" + this.soapActionRequired);
        }
        if (this.style != null) {
            strBuf.append("\nstyle=" + this.style);
        }
        return strBuf.toString();
    }
}

