/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDFault
extends WSDDElement {
    FaultDesc desc;

    public WSDDFault(FaultDesc desc) {
        this.desc = desc;
    }

    public WSDDFault(Element e) throws WSDDException {
        super(e);
        String xmlTypeStr;
        String classNameStr;
        String qNameStr;
        this.desc = new FaultDesc();
        String nameStr = e.getAttribute("name");
        if (nameStr != null && !nameStr.equals("")) {
            this.desc.setName(nameStr);
        }
        if ((qNameStr = e.getAttribute("qname")) != null && !qNameStr.equals("")) {
            this.desc.setQName(XMLUtils.getQNameFromString(qNameStr, e));
        }
        if ((classNameStr = e.getAttribute("class")) != null && !classNameStr.equals("")) {
            this.desc.setClassName(classNameStr);
        }
        if ((xmlTypeStr = e.getAttribute("type")) != null && !xmlTypeStr.equals("")) {
            this.desc.setXmlType(XMLUtils.getQNameFromString(xmlTypeStr, e));
        }
    }

    protected QName getElementName() {
        return QNAME_FAULT;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.desc.getQName()));
        attrs.addAttribute("", "class", "class", "CDATA", this.desc.getClassName());
        attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(this.desc.getXmlType()));
        context.startElement(this.getElementName(), attrs);
        context.endElement();
    }

    public FaultDesc getFaultDesc() {
        return this.desc;
    }

    public void setFaultDesc(FaultDesc desc) {
        this.desc = desc;
    }
}

