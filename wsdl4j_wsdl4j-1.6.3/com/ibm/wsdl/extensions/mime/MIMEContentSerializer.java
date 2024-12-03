/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

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
import javax.wsdl.extensions.mime.MIMEContent;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class MIMEContentSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        MIMEContent mimeContent = (MIMEContent)extension;
        if (mimeContent != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/mime/", "content", def);
            if (parentType != null && MIMEPart.class.isAssignableFrom(parentType)) {
                pw.print("    ");
            }
            pw.print("        <" + tagName);
            DOMUtils.printAttribute("part", mimeContent.getPart(), pw);
            DOMUtils.printAttribute("type", mimeContent.getType(), pw);
            Boolean required = mimeContent.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println("/>");
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        MIMEContent mimeContent = (MIMEContent)extReg.createExtension(parentType, elementType);
        String part = DOMUtils.getAttribute(el, "part");
        String type = DOMUtils.getAttribute(el, "type");
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (part != null) {
            mimeContent.setPart(part);
        }
        if (type != null) {
            mimeContent.setType(type);
        }
        if (requiredStr != null) {
            mimeContent.setRequired(new Boolean(requiredStr));
        }
        return mimeContent;
    }
}

