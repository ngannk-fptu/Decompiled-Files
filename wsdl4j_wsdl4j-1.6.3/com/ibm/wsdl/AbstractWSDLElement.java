/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.WSDLElement;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public abstract class AbstractWSDLElement
implements WSDLElement {
    protected Element docEl;
    protected List extElements = new Vector();
    protected Map extensionAttributes = new HashMap();

    public void setDocumentationElement(Element docEl) {
        this.docEl = docEl;
    }

    public Element getDocumentationElement() {
        return this.docEl;
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

    public void setExtensionAttribute(QName name, Object value) {
        if (value != null) {
            this.extensionAttributes.put(name, value);
        } else {
            this.extensionAttributes.remove(name);
        }
    }

    public Object getExtensionAttribute(QName name) {
        return this.extensionAttributes.get(name);
    }

    public Map getExtensionAttributes() {
        return this.extensionAttributes;
    }

    public String toString() {
        Iterator keys;
        Iterator extIterator;
        StringBuffer strBuf = new StringBuffer();
        if (this.extElements.size() > 0 && (extIterator = this.extElements.iterator()).hasNext()) {
            strBuf.append(extIterator.next());
            while (extIterator.hasNext()) {
                strBuf.append("\n");
                strBuf.append(extIterator.next());
            }
        }
        if (this.extensionAttributes.size() > 0 && (keys = this.extensionAttributes.keySet().iterator()).hasNext()) {
            QName name = (QName)keys.next();
            strBuf.append("extension attribute: ");
            strBuf.append(name);
            strBuf.append("=");
            strBuf.append(this.extensionAttributes.get(name));
            while (keys.hasNext()) {
                name = (QName)keys.next();
                strBuf.append("\n");
                strBuf.append("extension attribute: ");
                strBuf.append(name);
                strBuf.append("=");
                strBuf.append(this.extensionAttributes.get(name));
            }
        }
        return strBuf.toString();
    }
}

