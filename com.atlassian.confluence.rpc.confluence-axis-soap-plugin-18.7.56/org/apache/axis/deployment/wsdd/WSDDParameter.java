/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDocumentation;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDParameter
extends WSDDElement {
    OperationDesc parent;
    ParameterDesc parameter = new ParameterDesc();

    public WSDDParameter(Element e, OperationDesc parent) throws WSDDException {
        super(e);
        Element docElem;
        String itemTypeStr;
        String itemQNameStr;
        String typeStr;
        String outHStr;
        String inHStr;
        this.parent = parent;
        String nameStr = e.getAttribute("qname");
        if (nameStr != null && !nameStr.equals("")) {
            this.parameter.setQName(XMLUtils.getQNameFromString(nameStr, e));
        } else {
            nameStr = e.getAttribute("name");
            if (nameStr != null && !nameStr.equals("")) {
                this.parameter.setQName(new QName(null, nameStr));
            }
        }
        String modeStr = e.getAttribute("mode");
        if (modeStr != null && !modeStr.equals("")) {
            this.parameter.setMode(ParameterDesc.modeFromString(modeStr));
        }
        if ((inHStr = e.getAttribute("inHeader")) != null) {
            this.parameter.setInHeader(JavaUtils.isTrueExplicitly(inHStr));
        }
        if ((outHStr = e.getAttribute("outHeader")) != null) {
            this.parameter.setOutHeader(JavaUtils.isTrueExplicitly(outHStr));
        }
        if ((typeStr = e.getAttribute("type")) != null && !typeStr.equals("")) {
            this.parameter.setTypeQName(XMLUtils.getQNameFromString(typeStr, e));
        }
        if ((itemQNameStr = e.getAttribute("itemQName")) != null && !itemQNameStr.equals("")) {
            this.parameter.setItemQName(XMLUtils.getQNameFromString(itemQNameStr, e));
        }
        if ((itemTypeStr = e.getAttribute("itemType")) != null && !itemTypeStr.equals("")) {
            this.parameter.setItemType(XMLUtils.getQNameFromString(itemTypeStr, e));
        }
        if ((docElem = this.getChildElement(e, "documentation")) != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(docElem);
            this.parameter.setDocumentation(documentation.getValue());
        }
    }

    public WSDDParameter() {
    }

    public WSDDParameter(ParameterDesc parameter) {
        this.parameter = parameter;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        QName itemType;
        QName itemQName;
        QName typeQName;
        byte mode;
        AttributesImpl attrs = new AttributesImpl();
        QName qname = this.parameter.getQName();
        if (qname != null) {
            if (qname.getNamespaceURI() != null && !qname.getNamespaceURI().equals("")) {
                attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.parameter.getQName()));
            } else {
                attrs.addAttribute("", "name", "name", "CDATA", this.parameter.getQName().getLocalPart());
            }
        }
        if ((mode = this.parameter.getMode()) != 1) {
            String modeStr = ParameterDesc.getModeAsString(mode);
            attrs.addAttribute("", "mode", "mode", "CDATA", modeStr);
        }
        if (this.parameter.isInHeader()) {
            attrs.addAttribute("", "inHeader", "inHeader", "CDATA", "true");
        }
        if (this.parameter.isOutHeader()) {
            attrs.addAttribute("", "outHeader", "outHeader", "CDATA", "true");
        }
        if ((typeQName = this.parameter.getTypeQName()) != null) {
            attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(typeQName));
        }
        if ((itemQName = this.parameter.getItemQName()) != null) {
            attrs.addAttribute("", "itemQName", "itemQName", "CDATA", context.qName2String(itemQName));
        }
        if ((itemType = this.parameter.getItemType()) != null) {
            attrs.addAttribute("", "itemType", "itemType", "CDATA", context.qName2String(itemType));
        }
        context.startElement(this.getElementName(), attrs);
        if (this.parameter.getDocumentation() != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(this.parameter.getDocumentation());
            documentation.writeToContext(context);
        }
        context.endElement();
    }

    public ParameterDesc getParameter() {
        return this.parameter;
    }

    public void setParameter(ParameterDesc parameter) {
        this.parameter = parameter;
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_PARAM;
    }
}

