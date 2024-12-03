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
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAPOperationSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPOperation soapOperation = (SOAPOperation)extension;
        if (soapOperation != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap/", "operation", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("soapAction", soapOperation.getSoapActionURI(), pw);
            DOMUtils.printAttribute("style", soapOperation.getStyle(), pw);
            Boolean required = soapOperation.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAPOperation soapOperation = (SOAPOperation)extReg.createExtension(parentType, elementType);
        String soapActionURI = DOMUtils.getAttribute(el, "soapAction");
        String style = DOMUtils.getAttribute(el, "style");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (soapActionURI != null) {
            soapOperation.setSoapActionURI(soapActionURI);
        }
        if (style != null) {
            soapOperation.setStyle(style);
        }
        if (requiredStr != null) {
            soapOperation.setRequired(new Boolean(requiredStr));
        }
        return soapOperation;
    }
}

