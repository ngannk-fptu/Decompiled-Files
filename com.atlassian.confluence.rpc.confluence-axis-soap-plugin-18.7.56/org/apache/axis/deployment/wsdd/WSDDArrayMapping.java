/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDArrayMapping
extends WSDDTypeMapping {
    private QName innerType = null;

    public WSDDArrayMapping() {
    }

    public WSDDArrayMapping(Element e) throws WSDDException {
        super(e);
        Attr innerTypeAttr = e.getAttributeNode("innerType");
        if (innerTypeAttr != null) {
            String qnameStr = innerTypeAttr.getValue();
            this.innerType = XMLUtils.getQNameFromString(qnameStr, e);
        }
        this.serializer = "org.apache.axis.encoding.ser.ArraySerializerFactory";
        this.deserializer = "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
    }

    protected QName getElementName() {
        return QNAME_ARRAYMAPPING;
    }

    public QName getInnerType() {
        return this.innerType;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        String typeStr = context.qName2String(this.typeQName);
        attrs.addAttribute("", "languageSpecificType", "languageSpecificType", "CDATA", typeStr);
        String qnameStr = context.qName2String(this.qname);
        attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
        String innerTypeStr = context.qName2String(this.innerType);
        attrs.addAttribute("", "innerType", "innerType", "CDATA", innerTypeStr);
        context.startElement(QNAME_ARRAYMAPPING, attrs);
        context.endElement();
    }
}

