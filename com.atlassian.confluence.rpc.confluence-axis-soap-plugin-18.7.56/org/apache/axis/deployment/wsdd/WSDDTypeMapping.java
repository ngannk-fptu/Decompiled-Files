/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDTypeMapping
extends WSDDElement {
    protected QName qname = null;
    protected String serializer = null;
    protected String deserializer = null;
    protected QName typeQName = null;
    protected String ref = null;
    protected String encodingStyle = null;

    public WSDDTypeMapping() {
    }

    public WSDDTypeMapping(Element e) throws WSDDException {
        this.serializer = e.getAttribute("serializer");
        this.deserializer = e.getAttribute("deserializer");
        Attr attrNode = e.getAttributeNode("encodingStyle");
        this.encodingStyle = attrNode == null ? Constants.URI_DEFAULT_SOAP_ENC : attrNode.getValue();
        String qnameStr = e.getAttribute("qname");
        this.qname = XMLUtils.getQNameFromString(qnameStr, e);
        String typeStr = e.getAttribute("type");
        this.typeQName = XMLUtils.getQNameFromString(typeStr, e);
        if (typeStr == null || typeStr.equals("")) {
            typeStr = e.getAttribute("languageSpecificType");
            this.typeQName = XMLUtils.getQNameFromString(typeStr, e);
        }
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "encodingStyle", "encodingStyle", "CDATA", this.encodingStyle);
        attrs.addAttribute("", "serializer", "serializer", "CDATA", this.serializer);
        attrs.addAttribute("", "deserializer", "deserializer", "CDATA", this.deserializer);
        String typeStr = context.qName2String(this.typeQName);
        attrs.addAttribute("", "type", "type", "CDATA", typeStr);
        String qnameStr = context.attributeQName2String(this.qname);
        attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
        context.startElement(QNAME_TYPEMAPPING, attrs);
        context.endElement();
    }

    protected QName getElementName() {
        return QNAME_TYPEMAPPING;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getEncodingStyle() {
        return this.encodingStyle;
    }

    public void setEncodingStyle(String es) {
        this.encodingStyle = es;
    }

    public QName getQName() {
        return this.qname;
    }

    public void setQName(QName name) {
        this.qname = name;
    }

    public Class getLanguageSpecificType() throws ClassNotFoundException {
        if (this.typeQName != null) {
            if (!"http://xml.apache.org/axis/wsdd/providers/java".equals(this.typeQName.getNamespaceURI())) {
                throw new ClassNotFoundException(Messages.getMessage("badTypeNamespace00", this.typeQName.getNamespaceURI(), "http://xml.apache.org/axis/wsdd/providers/java"));
            }
            String loadName = JavaUtils.getLoadableClassName(this.typeQName.getLocalPart());
            if (JavaUtils.getWrapper(loadName) != null) {
                loadName = "java.lang." + JavaUtils.getWrapper(loadName);
            }
            return ClassUtils.forName(loadName);
        }
        throw new ClassNotFoundException(Messages.getMessage("noTypeQName00"));
    }

    public void setLanguageSpecificType(Class javaType) {
        String type = javaType.getName();
        this.typeQName = new QName("http://xml.apache.org/axis/wsdd/providers/java", type);
    }

    public void setLanguageSpecificType(String javaType) {
        this.typeQName = new QName("http://xml.apache.org/axis/wsdd/providers/java", javaType);
    }

    public Class getSerializer() throws ClassNotFoundException {
        return ClassUtils.forName(this.serializer);
    }

    public String getSerializerName() {
        return this.serializer;
    }

    public void setSerializer(Class ser) {
        this.serializer = ser.getName();
    }

    public void setSerializer(String ser) {
        this.serializer = ser;
    }

    public Class getDeserializer() throws ClassNotFoundException {
        return ClassUtils.forName(this.deserializer);
    }

    public String getDeserializerName() {
        return this.deserializer;
    }

    public void setDeserializer(Class deser) {
        this.deserializer = deser.getName();
    }

    public void setDeserializer(String deser) {
        this.deserializer = deser;
    }
}

