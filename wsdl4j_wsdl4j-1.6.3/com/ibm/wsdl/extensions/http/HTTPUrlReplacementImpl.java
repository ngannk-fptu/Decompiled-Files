/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.http;

import com.ibm.wsdl.extensions.http.HTTPConstants;
import javax.wsdl.extensions.http.HTTPUrlReplacement;
import javax.xml.namespace.QName;

public class HTTPUrlReplacementImpl
implements HTTPUrlReplacement {
    protected QName elementType = HTTPConstants.Q_ELEM_HTTP_URL_REPLACEMENT;
    protected Boolean required = null;
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

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("HTTPUrlReplacement (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        return strBuf.toString();
    }
}

