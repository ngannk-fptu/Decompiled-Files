/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.soap12;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.extensions.soap12.SOAP12Constants;
import com.ibm.wsdl.util.xml.DOMUtils;
import com.ibm.wsdl.util.xml.QNameUtils;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.soap12.SOAP12Header;
import javax.wsdl.extensions.soap12.SOAP12HeaderFault;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAP12HeaderSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Header soapHeader = (SOAP12Header)extension;
        if (soapHeader != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap12/", "header", def);
            pw.print("        <" + tagName);
            DOMUtils.printQualifiedAttribute("message", soapHeader.getMessage(), def, pw);
            DOMUtils.printAttribute("part", soapHeader.getPart(), pw);
            DOMUtils.printAttribute("use", soapHeader.getUse(), pw);
            DOMUtils.printAttribute("encodingStyle", soapHeader.getEncodingStyle(), pw);
            DOMUtils.printAttribute("namespace", soapHeader.getNamespaceURI(), pw);
            Boolean required = soapHeader.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println('>');
            SOAP12HeaderSerializer.printSoapHeaderFaults(soapHeader.getSOAP12HeaderFaults(), def, pw);
            pw.println("        </" + tagName + '>');
        }
    }

    private static void printSoapHeaderFaults(List soapHeaderFaults, Definition def, PrintWriter pw) throws WSDLException {
        if (soapHeaderFaults != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap12/", "headerfault", def);
            for (SOAP12HeaderFault soapHeaderFault : soapHeaderFaults) {
                if (soapHeaderFault == null) continue;
                pw.print("          <" + tagName);
                DOMUtils.printQualifiedAttribute("message", soapHeaderFault.getMessage(), def, pw);
                DOMUtils.printAttribute("part", soapHeaderFault.getPart(), pw);
                DOMUtils.printAttribute("use", soapHeaderFault.getUse(), pw);
                DOMUtils.printAttribute("encodingStyle", soapHeaderFault.getEncodingStyle(), pw);
                DOMUtils.printAttribute("namespace", soapHeaderFault.getNamespaceURI(), pw);
                Boolean required = soapHeaderFault.getRequired();
                if (required != null) {
                    DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
                }
                pw.println("/>");
            }
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Header soapHeader = (SOAP12Header)extReg.createExtension(parentType, elementType);
        QName message = DOMUtils.getQualifiedAttributeValue(el, "message", "header", false, def);
        String part = DOMUtils.getAttribute(el, "part");
        String use = DOMUtils.getAttribute(el, "use");
        String encStyleStr = DOMUtils.getAttribute(el, "encodingStyle");
        String namespaceURI = DOMUtils.getAttribute(el, "namespace");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (message != null) {
            soapHeader.setMessage(message);
        }
        if (part != null) {
            soapHeader.setPart(part);
        }
        if (use != null) {
            soapHeader.setUse(use);
        }
        if (encStyleStr != null) {
            soapHeader.setEncodingStyle(encStyleStr);
        }
        if (namespaceURI != null) {
            soapHeader.setNamespaceURI(namespaceURI);
        }
        if (requiredStr != null) {
            soapHeader.setRequired(new Boolean(requiredStr));
        }
        Element tempEl = DOMUtils.getFirstChildElement(el);
        while (tempEl != null) {
            if (QNameUtils.matches(SOAP12Constants.Q_ELEM_SOAP_HEADER_FAULT, tempEl)) {
                soapHeader.addSOAP12HeaderFault(SOAP12HeaderSerializer.parseSoapHeaderFault(SOAP12Header.class, SOAP12Constants.Q_ELEM_SOAP_HEADER_FAULT, tempEl, extReg, def));
            } else {
                DOMUtils.throwWSDLException(tempEl);
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        return soapHeader;
    }

    private static SOAP12HeaderFault parseSoapHeaderFault(Class parentType, QName elementType, Element el, ExtensionRegistry extReg, Definition def) throws WSDLException {
        SOAP12HeaderFault soapHeaderFault = (SOAP12HeaderFault)extReg.createExtension(parentType, elementType);
        QName message = DOMUtils.getQualifiedAttributeValue(el, "message", "header", false, def);
        String part = DOMUtils.getAttribute(el, "part");
        String use = DOMUtils.getAttribute(el, "use");
        String encStyleStr = DOMUtils.getAttribute(el, "encodingStyle");
        String namespaceURI = DOMUtils.getAttribute(el, "namespace");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (message != null) {
            soapHeaderFault.setMessage(message);
        }
        if (part != null) {
            soapHeaderFault.setPart(part);
        }
        if (use != null) {
            soapHeaderFault.setUse(use);
        }
        if (encStyleStr != null) {
            soapHeaderFault.setEncodingStyle(encStyleStr);
        }
        if (namespaceURI != null) {
            soapHeaderFault.setNamespaceURI(namespaceURI);
        }
        if (requiredStr != null) {
            soapHeaderFault.setRequired(new Boolean(requiredStr));
        }
        return soapHeaderFault;
    }
}

