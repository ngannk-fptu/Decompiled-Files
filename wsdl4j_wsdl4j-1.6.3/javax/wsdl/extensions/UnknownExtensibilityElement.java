/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import java.io.Serializable;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class UnknownExtensibilityElement
implements ExtensibilityElement,
Serializable {
    protected QName elementType = null;
    protected Boolean required = null;
    protected Element element = null;
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

    public void setElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("UnknownExtensibilityElement (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.element != null) {
            strBuf.append("\nelement=" + this.element);
        }
        return strBuf.toString();
    }
}

