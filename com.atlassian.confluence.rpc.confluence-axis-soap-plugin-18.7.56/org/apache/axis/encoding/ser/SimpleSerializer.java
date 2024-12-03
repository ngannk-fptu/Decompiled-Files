/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleType;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class SimpleSerializer
implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;
    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private TypeDesc typeDesc = null;
    public static final String VALUE_PROPERTY = "_value";
    static /* synthetic */ Class class$java$lang$Object;

    public SimpleSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.init();
    }

    public SimpleSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.init();
    }

    private void init() {
        if (this.typeDesc == null) {
            this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
        }
        if (this.typeDesc != null) {
            this.propertyDescriptor = this.typeDesc.getPropertyDescriptors();
        } else if (!JavaUtils.isBasic(this.javaType)) {
            this.propertyDescriptor = BeanUtils.getPd(this.javaType, null);
        }
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (value != null && value.getClass() == (class$java$lang$Object == null ? (class$java$lang$Object = SimpleSerializer.class$("java.lang.Object")) : class$java$lang$Object)) {
            throw new IOException(Messages.getMessage("cantSerialize02"));
        }
        attributes = this.getObjectAttributes(value, attributes, context);
        String valueStr = null;
        if (value != null) {
            valueStr = this.getValueAsString(value, context);
        }
        context.startElement(name, attributes);
        if (valueStr != null) {
            context.writeSafeString(valueStr);
        }
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        BeanPropertyDescriptor pd;
        if (value instanceof Float || value instanceof Double) {
            double data = 0.0;
            data = value instanceof Float ? ((Float)value).doubleValue() : ((Double)value).doubleValue();
            if (Double.isNaN(data)) {
                return "NaN";
            }
            if (data == Double.POSITIVE_INFINITY) {
                return "INF";
            }
            if (data == Double.NEGATIVE_INFINITY) {
                return "-INF";
            }
        } else if (value instanceof QName) {
            return context.qName2String((QName)value);
        }
        if (this.propertyDescriptor != null && !(value instanceof SimpleType) && (pd = BeanUtils.getSpecificPD(this.propertyDescriptor, VALUE_PROPERTY)) != null) {
            try {
                return pd.get(value).toString();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        return value.toString();
    }

    private Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context) {
        if (this.typeDesc != null && !this.typeDesc.hasAttributes()) {
            return attributes;
        }
        AttributesImpl attrs = attributes == null ? new AttributesImpl() : (attributes instanceof AttributesImpl ? (AttributesImpl)attributes : new AttributesImpl(attributes));
        try {
            for (int i = 0; this.propertyDescriptor != null && i < this.propertyDescriptor.length; ++i) {
                Object propValue;
                String propName = this.propertyDescriptor[i].getName();
                if (propName.equals("class")) continue;
                QName qname = null;
                if (this.typeDesc != null) {
                    FieldDesc field = this.typeDesc.getFieldByName(propName);
                    if (field == null || field.isElement()) continue;
                    qname = field.getXmlName();
                } else if (propName.equals(VALUE_PROPERTY)) continue;
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
        Element complexType = types.createElement("complexType");
        types.writeSchemaTypeDecl(this.xmlType, complexType);
        complexType.setAttribute("name", this.xmlType.getLocalPart());
        Element simpleContent = types.createElement("simpleContent");
        complexType.appendChild(simpleContent);
        Element extension = types.createElement("extension");
        simpleContent.appendChild(extension);
        String base = "string";
        for (int i = 0; this.propertyDescriptor != null && i < this.propertyDescriptor.length; ++i) {
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

