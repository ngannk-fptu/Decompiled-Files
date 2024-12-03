/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.http;

import com.ibm.wsdl.extensions.http.HTTPConstants;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.xml.namespace.QName;

public class HTTPBindingImpl
implements HTTPBinding {
    protected QName elementType = HTTPConstants.Q_ELEM_HTTP_BINDING;
    protected Boolean required = null;
    protected String verb = null;
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

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return this.verb;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("HTTPBinding (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.verb != null) {
            strBuf.append("\nverb=" + this.verb);
        }
        return strBuf.toString();
    }
}

