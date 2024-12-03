/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.FieldPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class BeanSerializer
implements Serializer,
Serializable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$BeanSerializer == null ? (class$org$apache$axis$encoding$ser$BeanSerializer = BeanSerializer.class$("org.apache.axis.encoding.ser.BeanSerializer")) : class$org$apache$axis$encoding$ser$BeanSerializer).getName());
    private static final QName MUST_UNDERSTAND_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
    private static final Object[] ZERO_ARGS = new Object[]{"0"};
    QName xmlType;
    Class javaType;
    protected BeanPropertyDescriptor[] propertyDescriptor = null;
    protected TypeDesc typeDesc = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanSerializer;
    static /* synthetic */ Class class$java$lang$Number;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$lang$Exception;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$lang$RuntimeException;
    static /* synthetic */ Class class$java$rmi$RemoteException;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;

    public BeanSerializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this(javaType, xmlType, typeDesc, null);
        this.propertyDescriptor = typeDesc != null ? typeDesc.getPropertyDescriptors() : BeanUtils.getPd(javaType, null);
    }

    public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc, BeanPropertyDescriptor[] propertyDescriptor) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.propertyDescriptor = propertyDescriptor;
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        Object newVal;
        boolean suppressElement;
        Attributes beanAttrs = this.getObjectAttributes(value, attributes, context);
        boolean isEncoded = context.isEncoded();
        boolean bl = suppressElement = !isEncoded && name.getNamespaceURI().equals("") && name.getLocalPart().equals("any");
        if (!suppressElement) {
            context.startElement(name, beanAttrs);
        }
        if (value.getClass().isArray() && (newVal = JavaUtils.convert(value, this.javaType)) != null && this.javaType.isAssignableFrom(newVal.getClass())) {
            value = newVal;
        }
        try {
            Object anyVal;
            BeanPropertyDescriptor anyDesc;
            for (int i = 0; i < this.propertyDescriptor.length; ++i) {
                FieldDesc field;
                String propName = this.propertyDescriptor[i].getName();
                if (propName.equals("class")) continue;
                QName qname = null;
                QName xmlType = null;
                Class javaType = this.propertyDescriptor[i].getType();
                boolean isOmittable = false;
                boolean isNillable = Types.isNullable(javaType);
                boolean isArray = false;
                QName itemQName = null;
                if (this.typeDesc != null && (field = this.typeDesc.getFieldByName(propName)) != null) {
                    if (!field.isElement()) continue;
                    ElementDesc element = (ElementDesc)field;
                    qname = isEncoded ? new QName(element.getXmlName().getLocalPart()) : element.getXmlName();
                    isOmittable = element.isMinOccursZero();
                    isNillable = element.isNillable();
                    isArray = element.isMaxOccursUnbounded();
                    xmlType = element.getXmlType();
                    itemQName = element.getItemQName();
                    context.setItemQName(itemQName);
                }
                if (qname == null) {
                    qname = new QName(isEncoded ? "" : name.getNamespaceURI(), propName);
                }
                if (xmlType == null) {
                    xmlType = context.getQNameForClass(javaType);
                }
                if (!this.propertyDescriptor[i].isReadable()) continue;
                if (itemQName != null || !this.propertyDescriptor[i].isIndexed() && !isArray) {
                    Object propValue = this.propertyDescriptor[i].get(value);
                    if (propValue == null) {
                        if (!isNillable && !isOmittable) {
                            if ((class$java$lang$Number == null ? BeanSerializer.class$("java.lang.Number") : class$java$lang$Number).isAssignableFrom(javaType)) {
                                try {
                                    Constructor constructor = javaType.getConstructor(SimpleDeserializer.STRING_CLASS);
                                    propValue = constructor.newInstance(ZERO_ARGS);
                                }
                                catch (Exception e) {
                                    // empty catch block
                                }
                            }
                            if (propValue == null) {
                                throw new IOException(Messages.getMessage("nullNonNillableElement", propName));
                            }
                        }
                        if (isOmittable && !isEncoded) continue;
                    }
                    context.serialize(qname, null, propValue, xmlType);
                    continue;
                }
                int j = 0;
                while (j >= 0) {
                    Object propValue = null;
                    try {
                        propValue = this.propertyDescriptor[i].get(value, j);
                        ++j;
                    }
                    catch (Exception e) {
                        j = -1;
                    }
                    if (j < 0) continue;
                    context.serialize(qname, null, propValue, xmlType);
                }
            }
            BeanPropertyDescriptor beanPropertyDescriptor = anyDesc = this.typeDesc == null ? null : this.typeDesc.getAnyDesc();
            if (anyDesc != null && (anyVal = anyDesc.get(value)) != null && anyVal instanceof MessageElement[]) {
                MessageElement[] anyContent = (MessageElement[])anyVal;
                for (int i = 0; i < anyContent.length; ++i) {
                    MessageElement element = anyContent[i];
                    element.output(context);
                }
            }
        }
        catch (InvocationTargetException ite) {
            Throwable target = ite.getTargetException();
            log.error((Object)Messages.getMessage("exception00"), target);
            throw new IOException(target.toString());
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw new IOException(e.toString());
        }
        if (!suppressElement) {
            context.endElement();
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        Element e = null;
        Class superClass = javaType.getSuperclass();
        BeanPropertyDescriptor[] superPd = null;
        List stopClasses = types.getStopClasses();
        if (!(superClass == null || superClass == (class$java$lang$Object == null ? (class$java$lang$Object = BeanSerializer.class$("java.lang.Object")) : class$java$lang$Object) || superClass == (class$java$lang$Exception == null ? (class$java$lang$Exception = BeanSerializer.class$("java.lang.Exception")) : class$java$lang$Exception) || superClass == (class$java$lang$Throwable == null ? (class$java$lang$Throwable = BeanSerializer.class$("java.lang.Throwable")) : class$java$lang$Throwable) || superClass == (class$java$lang$RuntimeException == null ? (class$java$lang$RuntimeException = BeanSerializer.class$("java.lang.RuntimeException")) : class$java$lang$RuntimeException) || superClass == (class$java$rmi$RemoteException == null ? (class$java$rmi$RemoteException = BeanSerializer.class$("java.rmi.RemoteException")) : class$java$rmi$RemoteException) || superClass == (class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = BeanSerializer.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault) || stopClasses != null && stopClasses.contains(superClass.getName()))) {
            String base = types.writeType(superClass);
            Element complexContent = types.createElement("complexContent");
            complexType.appendChild(complexContent);
            Element extension = types.createElement("extension");
            complexContent.appendChild(extension);
            extension.setAttribute("base", base);
            e = extension;
            TypeDesc superTypeDesc = TypeDesc.getTypeDescForClass(superClass);
            superPd = superTypeDesc != null ? superTypeDesc.getPropertyDescriptors() : BeanUtils.getPd(superClass, null);
        } else {
            e = complexType;
        }
        Element all = types.createElement("sequence");
        e.appendChild(all);
        if (Modifier.isAbstract(javaType.getModifiers())) {
            complexType.setAttribute("abstract", "true");
        }
        for (int i = 0; i < this.propertyDescriptor.length; ++i) {
            FieldPropertyDescriptor fpd;
            Class<?> clazz;
            String propName = this.propertyDescriptor[i].getName();
            boolean writeProperty = true;
            if (propName.equals("class")) {
                writeProperty = false;
            }
            if (superPd != null && writeProperty) {
                for (int j = 0; j < superPd.length && writeProperty; ++j) {
                    if (!propName.equals(superPd[j].getName())) continue;
                    writeProperty = false;
                }
            }
            if (!writeProperty) continue;
            if (this.typeDesc != null) {
                Class fieldType = this.propertyDescriptor[i].getType();
                FieldDesc field = this.typeDesc.getFieldByName(propName);
                if (field != null) {
                    boolean isAnonymous;
                    QName qname = field.getXmlName();
                    QName fieldXmlType = field.getXmlType();
                    boolean bl = isAnonymous = fieldXmlType != null && fieldXmlType.getLocalPart().startsWith(">");
                    if (qname != null) {
                        propName = qname.getLocalPart();
                    }
                    if (!field.isElement()) {
                        this.writeAttribute(types, propName, fieldType, fieldXmlType, complexType);
                        continue;
                    }
                    this.writeField(types, propName, fieldXmlType, fieldType, this.propertyDescriptor[i].isIndexed(), field.isMinOccursZero(), all, isAnonymous, ((ElementDesc)field).getItemQName());
                    continue;
                }
                this.writeField(types, propName, null, fieldType, this.propertyDescriptor[i].isIndexed(), false, all, false, null);
                continue;
            }
            boolean done = false;
            if (this.propertyDescriptor[i] instanceof FieldPropertyDescriptor && types.getTypeQName(clazz = (fpd = (FieldPropertyDescriptor)this.propertyDescriptor[i]).getField().getType()) != null) {
                this.writeField(types, propName, null, clazz, false, false, all, false, null);
                done = true;
            }
            if (done) continue;
            this.writeField(types, propName, null, this.propertyDescriptor[i].getType(), this.propertyDescriptor[i].isIndexed(), false, all, false, null);
        }
        return complexType;
    }

    protected void writeField(Types types, String fieldName, QName xmlType, Class fieldType, boolean isUnbounded, boolean isOmittable, Element where, boolean isAnonymous, QName itemQName) throws Exception {
        Element elem;
        String elementType = null;
        if (isAnonymous) {
            elem = types.createElementWithAnonymousType(fieldName, fieldType, isOmittable, where.getOwnerDocument());
        } else {
            FieldDesc field;
            QName typeQName;
            if (!SchemaUtils.isSimpleSchemaType(xmlType) && Types.isArray(fieldType)) {
                xmlType = null;
            }
            if (itemQName != null && SchemaUtils.isSimpleSchemaType(xmlType) && Types.isArray(fieldType)) {
                xmlType = null;
            }
            if ((elementType = types.getQNameString(typeQName = types.writeTypeAndSubTypeForPart(fieldType, xmlType))) == null) {
                QName anyQN = Constants.XSD_ANYTYPE;
                String prefix = types.getNamespaces().getCreatePrefix(anyQN.getNamespaceURI());
                elementType = prefix + ":" + anyQN.getLocalPart();
            }
            boolean isNillable = Types.isNullable(fieldType);
            if (this.typeDesc != null && (field = this.typeDesc.getFieldByName(fieldName)) != null && field.isElement()) {
                isNillable = ((ElementDesc)field).isNillable();
            }
            elem = types.createElement(fieldName, elementType, isNillable, isOmittable, where.getOwnerDocument());
        }
        if (isUnbounded) {
            elem.setAttribute("maxOccurs", "unbounded");
        }
        where.appendChild(elem);
    }

    protected void writeAttribute(Types types, String fieldName, Class fieldType, QName fieldXmlType, Element where) throws Exception {
        if (!types.isAcceptableAsAttribute(fieldType)) {
            throw new AxisFault(Messages.getMessage("AttrNotSimpleType00", fieldName, fieldType.getName()));
        }
        Element elem = types.createAttributeElement(fieldName, fieldType, fieldXmlType, false, where.getOwnerDocument());
        where.appendChild(elem);
    }

    protected Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context) {
        if (this.typeDesc == null || !this.typeDesc.hasAttributes()) {
            return attributes;
        }
        AttributesImpl attrs = attributes == null ? new AttributesImpl() : (attributes instanceof AttributesImpl ? (AttributesImpl)attributes : new AttributesImpl(attributes));
        try {
            for (int i = 0; i < this.propertyDescriptor.length; ++i) {
                FieldDesc field;
                String propName = this.propertyDescriptor[i].getName();
                if (propName.equals("class") || (field = this.typeDesc.getFieldByName(propName)) == null || field.isElement()) continue;
                QName qname = field.getXmlName();
                if (qname == null) {
                    qname = new QName("", propName);
                }
                if (!this.propertyDescriptor[i].isReadable() || this.propertyDescriptor[i].isIndexed()) continue;
                Object propValue = this.propertyDescriptor[i].get(value);
                if (qname.equals(MUST_UNDERSTAND_QNAME)) {
                    if (propValue.equals(Boolean.TRUE)) {
                        propValue = "1";
                    } else if (propValue.equals(Boolean.FALSE)) {
                        propValue = "0";
                    }
                }
                if (propValue == null) continue;
                this.setAttributeProperty(propValue, qname, field.getXmlType(), attrs, context);
            }
        }
        catch (Exception e) {
            return attrs;
        }
        return attrs;
    }

    private void setAttributeProperty(Object propValue, QName qname, QName xmlType, AttributesImpl attrs, SerializationContext context) throws Exception {
        String localName;
        String namespace = qname.getNamespaceURI();
        if (attrs.getIndex(namespace, localName = qname.getLocalPart()) != -1) {
            return;
        }
        String propString = context.getValueAsString(propValue, xmlType);
        attrs.addAttribute(namespace, localName, context.attributeQName2String(qname), "CDATA", propString);
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

