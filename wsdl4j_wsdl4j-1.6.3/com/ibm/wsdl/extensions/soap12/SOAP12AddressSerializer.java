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
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SOAP12AddressSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Address soapAddress = (SOAP12Address)extension;
        if (soapAddress != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/soap12/", "address", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("location", soapAddress.getLocationURI(), pw);
            Boolean required = soapAddress.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        SOAP12Address soapAddress = (SOAP12Address)extReg.createExtension(parentType, elementType);
        String locationURI = DOMUtils.getAttribute(el, "location");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (locationURI != null) {
            soapAddress.setLocationURI(locationURI);
        }
        if (requiredStr != null) {
            soapAddress.setRequired(new Boolean(requiredStr));
        }
        return soapAddress;
    }
}

