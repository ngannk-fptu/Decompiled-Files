/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

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
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAPBindingSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPBinding soapBinding = (SOAPBinding)extension;
        if (soapBinding != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap/", "binding", def);
            pw.print("    <" + tagName);
            DOMUtils.printAttribute("style", soapBinding.getStyle(), pw);
            DOMUtils.printAttribute("transport", soapBinding.getTransportURI(), pw);
            Boolean required = soapBinding.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPBinding soapBinding = (SOAPBinding)extReg.createExtension(parentType, elementType);
        String transportURI = DOMUtils.getAttribute(el, "transport");
        String style = DOMUtils.getAttribute(el, "style");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (transportURI != null) {
            soapBinding.setTransportURI(transportURI);
        }
        if (style != null) {
            soapBinding.setStyle(style);
        }
        if (requiredStr != null) {
            soapBinding.setRequired(new Boolean(requiredStr));
        }
        return soapBinding;
    }
}

