/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

import com.ibm.wsdl.extensions.mime.MIMEConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.xml.namespace.QName;

public class MIMEPartImpl
implements MIMEPart {
    protected QName elementType = MIMEConstants.Q_ELEM_MIME_PART;
    protected Boolean required = null;
    protected List extElements = new Vector();
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

    public void addExtensibilityElement(ExtensibilityElement extElement) {
        this.extElements.add(extElement);
    }

    public ExtensibilityElement removeExtensibilityElement(ExtensibilityElement extElement) {
        if (this.extElements.remove(extElement)) {
            return extElement;
        }
        return null;
    }

    public List getExtensibilityElements() {
        return this.extElements;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("MIMEPart (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.extElements != null) {
            Iterator extIterator = this.extElements.iterator();
            while (extIterator.hasNext()) {
                strBuf.append("\n" + extIterator.next());
            }
        }
        return strBuf.toString();
    }
}

