/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

import com.ibm.wsdl.extensions.mime.MIMEConstants;
import javax.wsdl.extensions.mime.MIMEContent;
import javax.xml.namespace.QName;

public class MIMEContentImpl
implements MIMEContent {
    protected QName elementType = MIMEConstants.Q_ELEM_MIME_CONTENT;
    protected Boolean required = null;
    protected String part = null;
    protected String type = null;
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

    public void setPart(String part) {
        this.part = part;
    }

    public String getPart() {
        return this.part;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("MIMEContent (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.part != null) {
            strBuf.append("\npart=" + this.part);
        }
        if (this.type != null) {
            strBuf.append("\ntype=" + this.type);
        }
        return strBuf.toString();
    }
}

