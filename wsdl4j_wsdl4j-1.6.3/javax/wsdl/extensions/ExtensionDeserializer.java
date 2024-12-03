/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public interface ExtensionDeserializer {
    public ExtensibilityElement unmarshall(Class var1, QName var2, Element var3, Definition var4, ExtensionRegistry var5) throws WSDLException;
}

