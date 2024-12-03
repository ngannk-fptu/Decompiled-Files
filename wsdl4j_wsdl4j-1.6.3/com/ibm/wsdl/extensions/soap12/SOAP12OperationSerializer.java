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
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAP12OperationSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Operation soapOperation = (SOAP12Operation)extension;
        if (soapOperation != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap12/", "operation", def);
            pw.print("      <" + tagName);
            Boolean soapActionRequired = soapOperation.getSoapActionRequired();
            String soapActionRequiredString = soapActionRequired == null ? null : soapActionRequired.toString();
            DOMUtils.printAttribute("soapAction", soapOperation.getSoapActionURI(), pw);
            DOMUtils.printAttribute("soapActionRequired", soapActionRequiredString, pw);
            DOMUtils.printAttribute("style", soapOperation.getStyle(), pw);
            Boolean required = soapOperation.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Operation soapOperation = (SOAP12Operation)extReg.createExtension(parentType, elementType);
        String soapActionURI = DOMUtils.getAttribute(el, "soapAction");
        String soapActionRequiredString = DOMUtils.getAttribute(el, "soapActionRequired");
        String style = DOMUtils.getAttribute(el, "style");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (soapActionURI != null) {
            soapOperation.setSoapActionURI(soapActionURI);
        }
        if (soapActionRequiredString != null) {
            Boolean soapActionRequired = new Boolean(soapActionRequiredString);
            soapOperation.setSoapActionRequired(soapActionRequired);
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

