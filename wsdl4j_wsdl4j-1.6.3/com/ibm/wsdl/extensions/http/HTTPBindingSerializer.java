/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.http;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.util.xml.DOMUtils;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class HTTPBindingSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPBinding httpBinding = (HTTPBinding)extension;
        if (httpBinding != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/http/", "binding", def);
            pw.print("    <" + tagName);
            DOMUtils.printAttribute("verb", httpBinding.getVerb(), pw);
            Boolean required = httpBinding.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPBinding httpBinding = (HTTPBinding)extReg.createExtension(parentType, elementType);
        String verb = DOMUtils.getAttribute(el, "verb");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (verb != null) {
            httpBinding.setVerb(verb);
        }
        if (requiredStr != null) {
            httpBinding.setRequired(new Boolean(requiredStr));
        }
        return httpBinding;
    }
}

