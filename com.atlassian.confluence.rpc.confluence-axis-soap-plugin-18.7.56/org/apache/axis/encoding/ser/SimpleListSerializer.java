/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleType;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.encoding.ser.QNameSerializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class SimpleListSerializer
implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;
    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private TypeDesc typeDesc = null;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$org$apache$axis$encoding$SimpleType;

    public SimpleListSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public SimpleListSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (value != null && value.getClass() == (class$java$lang$Object == null ? (class$java$lang$Object = SimpleListSerializer.class$("java.lang.Object")) : class$java$lang$Object)) {
            throw new IOException(Messages.getMessage("cantSerialize02"));
        }
        if (value instanceof SimpleType) {
            attributes = this.getObjectAttributes(value, attributes, context);
        }
        String strValue = null;
        if (value != null) {
            strValue = this.getValueAsString(value, context);
        }
        context.startElement(name, attributes);
        if (strValue != null) {
            context.writeSafeString(strValue);
        }
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        int length = Array.getLength(value);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            Object object = Array.get(value, i);
            if (object instanceof Float || object instanceof Double) {
                double data = 0.0;
                data = object instanceof Float ? ((Float)object).doubleValue() : ((Double)object).doubleValue();
                if (Double.isNaN(data)) {
                    result.append("NaN");
                } else if (data == Double.POSITIVE_INFINITY) {
                    result.append("INF");
                } else if (data == Double.NEGATIVE_INFINITY) {
                    result.append("-INF");
                } else {
                    result.append(object.toString());
                }
            } else if (object instanceof QName) {
                result.append(QNameSerializer.qName2String((QName)object, context));
            } else {
                result.append(object.toString());
            }
            if (i >= length - 1) continue;
            result.append(' ');
        }
        return result.toString();
    }

    private Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context) {
        if (this.typeDesc == null || !this.typeDesc.hasAttributes()) {
            return attributes;
        }
        AttributesImpl attrs = attributes == null ? new AttributesImpl() : (attributes instanceof AttributesImpl ? (AttributesImpl)attributes : new AttributesImpl(attributes));
        try {
            for (int i = 0; i < this.propertyDescriptor.length; ++i) {
                Object propValue;
                FieldDesc field;
                String propName = this.propertyDescriptor[i].getName();
                if (propName.equals("class") || (field = this.typeDesc.getFieldByName(propName)) == null || field.isElement()) continue;
                QName qname = field.getXmlName();
                if (qname == null) {
                    qname = new QName("", propName);
                }
                if (!this.propertyDescriptor[i].isReadable() || this.propertyDescriptor[i].isIndexed() || (propValue = this.propertyDescriptor[i].get(value)) == null) continue;
                String propString = this.getValueAsString(propValue, context);
                String namespace = qname.getNamespaceURI();
                String localName = qname.getLocalPart();
                attrs.addAttribute(namespace, localName, context.qName2String(qname), "CDATA", propString);
            }
        }
        catch (Exception e) {
            return attrs;
        }
        return attrs;
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        if (!(class$org$apache$axis$encoding$SimpleType == null ? (class$org$apache$axis$encoding$SimpleType = SimpleListSerializer.class$("org.apache.axis.encoding.SimpleType")) : class$org$apache$axis$encoding$SimpleType).isAssignableFrom(javaType)) {
            return null;
        }
        Element complexType = types.createElement("complexType");
        types.writeSchemaElementDecl(this.xmlType, complexType);
        complexType.setAttribute("name", this.xmlType.getLocalPart());
        Element simpleContent = types.createElement("simpleContent");
        complexType.appendChild(simpleContent);
        Element extension = types.createElement("extension");
        simpleContent.appendChild(extension);
        String base = "string";
        for (int i = 0; i < this.propertyDescriptor.length; ++i) {
            String propName = this.propertyDescriptor[i].getName();
            if (!propName.equals("value")) {
                Class fieldType;
                QName qname;
                FieldDesc field;
                if (this.typeDesc == null || (field = this.typeDesc.getFieldByName(propName)) == null) continue;
                if (field.isElement()) {
                    // empty if block
                }
                if ((qname = field.getXmlName()) == null) {
                    qname = new QName("", propName);
                }
                if (!types.isAcceptableAsAttribute(fieldType = this.propertyDescriptor[i].getType())) {
                    throw new AxisFault(Messages.getMessage("AttrNotSimpleType00", propName, fieldType.getName()));
                }
                Element elem = types.createAttributeElement(propName, fieldType, field.getXmlType(), false, extension.getOwnerDocument());
                extension.appendChild(elem);
                continue;
            }
            BeanPropertyDescriptor bpd = this.propertyDescriptor[i];
            Class type = bpd.getType();
            if (!types.isAcceptableAsAttribute(type)) {
                throw new AxisFault(Messages.getMessage("AttrNotSimpleType01", type.getName()));
            }
            base = types.writeType(type);
            extension.setAttribute("base", base);
        }
        return complexType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

