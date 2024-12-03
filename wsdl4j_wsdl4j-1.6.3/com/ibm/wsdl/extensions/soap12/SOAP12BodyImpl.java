/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import com.ibm.wsdl.extensions.soap12.SOAP12Constants;
import java.util.List;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.xml.namespace.QName;

public class SOAP12BodyImpl
implements SOAP12Body {
    protected QName elementType = SOAP12Constants.Q_ELEM_SOAP_BODY;
    protected Boolean required = null;
    protected List parts = null;
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

    public void setParts(List parts) {
        this.parts = parts;
    }

    public List getParts() {
        return this.parts;
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
        strBuf.append("SOAPBody (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.parts != null) {
            strBuf.append("\nparts=" + this.parts);
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
        return strBuf.toString();
    }
}

