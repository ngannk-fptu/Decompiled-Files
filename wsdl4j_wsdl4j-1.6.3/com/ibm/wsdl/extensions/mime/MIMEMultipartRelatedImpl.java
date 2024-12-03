/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

import com.ibm.wsdl.extensions.mime.MIMEConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.xml.namespace.QName;

public class MIMEMultipartRelatedImpl
implements MIMEMultipartRelated {
    protected QName elementType = MIMEConstants.Q_ELEM_MIME_MULTIPART_RELATED;
    protected Boolean required = null;
    protected List mimeParts = new Vector();
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

    public void addMIMEPart(MIMEPart mimePart) {
        this.mimeParts.add(mimePart);
    }

    public MIMEPart removeMIMEPart(MIMEPart mimePart) {
        if (this.mimeParts.remove(mimePart)) {
            return mimePart;
        }
        return null;
    }

    public List getMIMEParts() {
        return this.mimeParts;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("MIMEMultipartRelated (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.mimeParts != null) {
            Iterator mimePartIterator = this.mimeParts.iterator();
            while (mimePartIterator.hasNext()) {
                strBuf.append("\n" + mimePartIterator.next());
            }
        }
        return strBuf.toString();
    }
}

