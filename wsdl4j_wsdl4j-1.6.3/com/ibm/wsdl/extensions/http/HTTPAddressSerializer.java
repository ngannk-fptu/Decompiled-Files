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
import javax.wsdl.extensions.http.HTTPAddress;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class HTTPAddressSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPAddress httpAddress = (HTTPAddress)extension;
        if (httpAddress != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/http/", "address", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("location", httpAddress.getLocationURI(), pw);
            Boolean required = httpAddress.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        HTTPAddress httpAddress = (HTTPAddress)extReg.createExtension(parentType, elementType);
        String locationURI = DOMUtils.getAttribute(el, "location");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (locationURI != null) {
            httpAddress.setLocationURI(locationURI);
        }
        if (requiredStr != null) {
            httpAddress.setRequired(new Boolean(requiredStr));
        }
        return httpAddress;
    }
}

