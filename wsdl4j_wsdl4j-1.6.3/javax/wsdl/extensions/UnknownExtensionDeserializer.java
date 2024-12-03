/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import com.ibm.wsdl.util.xml.DOMUtils;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class UnknownExtensionDeserializer
implements ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        UnknownExtensibilityElement unknownExt = new UnknownExtensibilityElement();
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        unknownExt.setElementType(elementType);
        if (requiredStr != null) {
            unknownExt.setRequired(new Boolean(requiredStr));
        }
        unknownExt.setElement(el);
        return unknownExt;
    }
}

