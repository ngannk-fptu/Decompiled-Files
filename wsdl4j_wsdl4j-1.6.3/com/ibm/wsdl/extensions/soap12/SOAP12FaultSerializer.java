/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

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
import javax.wsdl.extensions.soap12.SOAP12Fault;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAP12FaultSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Fault soapFault = (SOAP12Fault)extension;
        if (soapFault != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap12/", "fault", def);
            pw.print("        <" + tagName);
            DOMUtils.printAttribute("name", soapFault.getName(), pw);
            DOMUtils.printAttribute("use", soapFault.getUse(), pw);
            DOMUtils.printAttribute("encodingStyle", soapFault.getEncodingStyle(), pw);
            DOMUtils.printAttribute("namespace", soapFault.getNamespaceURI(), pw);
            Boolean required = soapFault.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Fault soapFault = (SOAP12Fault)extReg.createExtension(parentType, elementType);
        QName message = DOMUtils.getQualifiedAttributeValue(el, "message", "header", false, def);
        String name = DOMUtils.getAttribute(el, "name");
        String use = DOMUtils.getAttribute(el, "use");
        String encStyleStr = DOMUtils.getAttribute(el, "encodingStyle");
        String namespaceURI = DOMUtils.getAttribute(el, "namespace");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (name != null) {
            soapFault.setName(name);
        }
        if (use != null) {
            soapFault.setUse(use);
        }
        if (encStyleStr != null) {
            soapFault.setEncodingStyle(encStyleStr);
        }
        if (namespaceURI != null) {
            soapFault.setNamespaceURI(namespaceURI);
        }
        if (requiredStr != null) {
            soapFault.setRequired(new Boolean(requiredStr));
        }
        return soapFault;
    }
}

