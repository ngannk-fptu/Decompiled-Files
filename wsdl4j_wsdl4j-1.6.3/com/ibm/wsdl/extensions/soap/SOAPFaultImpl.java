/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import com.ibm.wsdl.extensions.soap.SOAPConstants;
import java.util.List;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.xml.namespace.QName;

public class SOAPFaultImpl
implements SOAPFault {
    protected QName elementType = SOAPConstants.Q_ELEM_SOAP_FAULT;
    protected Boolean required = null;
    protected String name = null;
    protected String use = null;
    protected List encodingStyles = null;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
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

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SOAPFault (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.name != null) {
            strBuf.append("\nname=" + this.name);
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
        return strBuf.toString();
    }
}

