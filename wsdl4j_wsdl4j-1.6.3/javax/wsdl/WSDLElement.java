/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.io.Serializable;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ElementExtensible;
import org.w3c.dom.Element;

public interface WSDLElement
extends Serializable,
AttributeExtensible,
ElementExtensible {
    public void setDocumentationElement(Element var1);

    public Element getDocumentationElement();
}

