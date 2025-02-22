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
import javax.wsdl.extensions.http.HTTPUrlEncoded;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class HTTPUrlEncodedSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPUrlEncoded httpUrlEncoded = (HTTPUrlEncoded)extension;
        if (httpUrlEncoded != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/http/", "urlEncoded", def);
            pw.print("        <" + tagName);
            Boolean required = httpUrlEncoded.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPUrlEncoded httpUrlEncoded = (HTTPUrlEncoded)extReg.createExtension(parentType, elementType);
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (requiredStr != null) {
            httpUrlEncoded.setRequired(new Boolean(requiredStr));
        }
        return httpUrlEncoded;
    }
}

