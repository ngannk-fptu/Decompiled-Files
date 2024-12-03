/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.http;

import com.ibm.wsdl.extensions.http.HTTPConstants;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.xml.namespace.QName;

public class HTTPAddressImpl
implements HTTPAddress {
    protected QName elementType = HTTPConstants.Q_ELEM_HTTP_ADDRESS;
    protected Boolean required = null;
    protected String locationURI = null;
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

    public void setLocationURI(String locationURI) {
        this.locationURI = locationURI;
    }

    public String getLocationURI() {
        return this.locationURI;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("HTTPAddress (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.locationURI != null) {
            strBuf.append("\nlocationURI=" + this.locationURI);
        }
        return strBuf.toString();
    }
}

