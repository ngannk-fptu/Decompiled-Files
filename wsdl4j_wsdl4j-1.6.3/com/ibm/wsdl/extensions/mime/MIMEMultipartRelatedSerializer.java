/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.mime;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.extensions.mime.MIMEConstants;
import com.ibm.wsdl.util.xml.DOMUtils;
import com.ibm.wsdl.util.xml.QNameUtils;
import com.ibm.wsdl.util.xml.XPathUtils;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class MIMEMultipartRelatedSerializer
implements ExtensionSerializer,
ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        MIMEMultipartRelated mimeMultipartRelated = (MIMEMultipartRelated)extension;
        if (mimeMultipartRelated != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/mime/", "multipartRelated", def);
            if (parentType != null && MIMEPart.class.isAssignableFrom(parentType)) {
                pw.print("    ");
            }
            pw.print("        <" + tagName);
            Boolean required = mimeMultipartRelated.getRequired();
            if (required != null) {
                DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
            }
            pw.println('>');
            this.printMIMEParts(mimeMultipartRelated.getMIMEParts(), pw, def, extReg);
            if (parentType != null && MIMEPart.class.isAssignableFrom(parentType)) {
                pw.print("    ");
            }
            pw.println("        </" + tagName + '>');
        }
    }

    private void printMIMEParts(List mimeParts, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        if (mimeParts != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/mime/", "part", def);
            for (MIMEPart mimePart : mimeParts) {
                if (mimePart == null) continue;
                pw.print("          <" + tagName);
                Boolean required = mimePart.getRequired();
                if (required != null) {
                    DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
                }
                pw.println('>');
                List extensibilityElements = mimePart.getExtensibilityElements();
                if (extensibilityElements != null) {
                    for (ExtensibilityElement ext : extensibilityElements) {
                        QName elementType = ext.getElementType();
                        ExtensionSerializer extSer = extReg.querySerializer(MIMEPart.class, elementType);
                        extSer.marshall(MIMEPart.class, elementType, ext, pw, def, extReg);
                    }
                }
                pw.println("          </" + tagName + '>');
            }
        }
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        MIMEMultipartRelated mimeMultipartRelated = (MIMEMultipartRelated)extReg.createExtension(parentType, elementType);
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        Element tempEl = DOMUtils.getFirstChildElement(el);
        while (tempEl != null) {
            if (QNameUtils.matches(MIMEConstants.Q_ELEM_MIME_PART, tempEl)) {
                mimeMultipartRelated.addMIMEPart(this.parseMIMEPart(MIMEMultipartRelated.class, MIMEConstants.Q_ELEM_MIME_PART, tempEl, def, extReg));
            } else {
                DOMUtils.throwWSDLException(tempEl);
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        if (requiredStr != null) {
            mimeMultipartRelated.setRequired(new Boolean(requiredStr));
        }
        return mimeMultipartRelated;
    }

    private MIMEPart parseMIMEPart(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        MIMEPart mimePart = (MIMEPart)extReg.createExtension(parentType, elementType);
        String requiredStr = DOMUtils.getAttributeNS(el, "http://schemas.xmlsoap.org/wsdl/", "required");
        if (requiredStr != null) {
            mimePart.setRequired(new Boolean(requiredStr));
        }
        Element tempEl = DOMUtils.getFirstChildElement(el);
        while (tempEl != null) {
            try {
                QName tempElType = QNameUtils.newQName(tempEl);
                ExtensionDeserializer extDS = extReg.queryDeserializer(MIMEPart.class, tempElType);
                ExtensibilityElement ext = extDS.unmarshall(MIMEPart.class, tempElType, tempEl, def, extReg);
                mimePart.addExtensibilityElement(ext);
            }
            catch (WSDLException e) {
                if (e.getLocation() == null) {
                    e.setLocation(XPathUtils.getXPathExprFromNode(tempEl));
                }
                throw e;
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        return mimePart;
    }
}

