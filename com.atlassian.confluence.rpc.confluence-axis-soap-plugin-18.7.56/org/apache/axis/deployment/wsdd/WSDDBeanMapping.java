/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDBeanMapping
extends WSDDTypeMapping {
    public WSDDBeanMapping() {
    }

    public WSDDBeanMapping(Element e) throws WSDDException {
        super(e);
        this.serializer = "org.apache.axis.encoding.ser.BeanSerializerFactory";
        this.deserializer = "org.apache.axis.encoding.ser.BeanDeserializerFactory";
        this.encodingStyle = null;
    }

    protected QName getElementName() {
        return QNAME_BEANMAPPING;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        String typeStr = context.qName2String(this.typeQName);
        attrs.addAttribute("", "languageSpecificType", "languageSpecificType", "CDATA", typeStr);
        String qnameStr = context.qName2String(this.qname);
        attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
        context.startElement(WSDDConstants.QNAME_BEANMAPPING, attrs);
        context.endElement();
    }
}

