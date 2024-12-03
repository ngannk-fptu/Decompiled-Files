/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOMUtils;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAPBodySerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPBody soapBody = (SOAPBody)extension;
        if (soapBody != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap/", "body", def);
            if (parentType != null && MIMEPart.class.isAssignableFrom(parentType)) {
                pw.print("    ");
            }
            pw.print("        <" + tagName);
            DOMUtils.printAttribute("parts", StringUtils.getNMTokens(soapBody.getParts()), pw);
            DOMUtils.printAttribute("use", soapBody.getUse(), pw);
            DOMUtils.printAttribute("encodingStyle", StringUtils.getNMTokens(soapBody.getEncodingStyles()), pw);
            DOMUtils.printAttribute("namespace", soapBody.getNamespaceURI(), pw);
            Boolean required = soapBody.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPBody soapBody = (SOAPBody)extReg.createExtension(parentType, elementType);
        String partsStr = DOMUtils.getAttribute(el, "parts");
        String use = DOMUtils.getAttribute(el, "use");
        String encStyleStr = DOMUtils.getAttribute(el, "encodingStyle");
        String namespaceURI = DOMUtils.getAttribute(el, "namespace");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (partsStr != null) {
            soapBody.setParts(StringUtils.parseNMTokens(partsStr));
        }
        if (use != null) {
            soapBody.setUse(use);
        }
        if (encStyleStr != null) {
            soapBody.setEncodingStyles(StringUtils.parseNMTokens(encStyleStr));
        }
        if (namespaceURI != null) {
            soapBody.setNamespaceURI(namespaceURI);
        }
        if (requiredStr != null) {
            soapBody.setRequired(new Boolean(requiredStr));
        }
        return soapBody;
    }
}

