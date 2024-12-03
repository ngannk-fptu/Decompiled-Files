/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class ArraySerializer
implements Serializer {
    QName xmlType = null;
    Class javaType = null;
    QName componentType = null;
    QName componentQName = null;
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$ArraySerializer == null ? (class$org$apache$axis$encoding$ser$ArraySerializer = ArraySerializer.class$("org.apache.axis.encoding.ser.ArraySerializer")) : class$org$apache$axis$encoding$ser$ArraySerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ArraySerializer;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$util$Collection;

    public ArraySerializer(Class javaType, QName xmlType) {
        this.javaType = javaType;
        this.xmlType = xmlType;
    }

    public ArraySerializer(Class javaType, QName xmlType, QName componentType) {
        this(javaType, xmlType);
        this.componentType = componentType;
    }

    public ArraySerializer(Class javaType, QName xmlType, QName componentType, QName componentQName) {
        this(javaType, xmlType, componentType);
        this.componentQName = componentQName;
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        boolean maxOccursUsage;
        if (value == null) {
            throw new IOException(Messages.getMessage("cantDoNullArray00"));
        }
        MessageContext msgContext = context.getMessageContext();
        SchemaVersion schema = SchemaVersion.SCHEMA_2001;
        SOAPConstants soap = SOAPConstants.SOAP11_CONSTANTS;
        boolean encoded = context.isEncoded();
        if (msgContext != null) {
            schema = msgContext.getSchemaVersion();
            soap = msgContext.getSOAPConstants();
        }
        Class<?> cls = value.getClass();
        Collection list = null;
        if (!cls.isArray()) {
            if (!(value instanceof Collection)) {
                throw new IOException(Messages.getMessage("cantSerialize00", cls.getName()));
            }
            list = (Collection)value;
        }
        Class componentClass = list == null ? cls.getComponentType() : (class$java$lang$Object == null ? (class$java$lang$Object = ArraySerializer.class$("java.lang.Object")) : class$java$lang$Object);
        QName componentTypeQName = this.componentType;
        String dims = "";
        if (componentTypeQName != null) {
            TypeMapping tm = context.getTypeMapping();
            SerializerFactory factory = (SerializerFactory)tm.getSerializer(componentClass, componentTypeQName);
            while (componentClass.isArray() && factory instanceof ArraySerializerFactory) {
                ArraySerializerFactory asf = (ArraySerializerFactory)factory;
                componentClass = componentClass.getComponentType();
                QName componentType = null;
                if (asf.getComponentType() != null) {
                    componentType = asf.getComponentType();
                    if (encoded) {
                        componentTypeQName = componentType;
                    }
                }
                factory = (SerializerFactory)tm.getSerializer(componentClass, componentType);
                if (soap == SOAPConstants.SOAP12_CONSTANTS) {
                    dims = dims + "* ";
                    continue;
                }
                dims = dims + "[]";
            }
        } else {
            while (componentClass.isArray()) {
                componentClass = componentClass.getComponentType();
                if (soap == SOAPConstants.SOAP12_CONSTANTS) {
                    dims = dims + "* ";
                    continue;
                }
                dims = dims + "[]";
            }
        }
        if (componentTypeQName == null && (componentTypeQName = context.getCurrentXMLType()) != null && (componentTypeQName.equals(this.xmlType) || componentTypeQName.equals(Constants.XSD_ANYTYPE) || componentTypeQName.equals(soap.getArrayType()))) {
            componentTypeQName = null;
        }
        if (componentTypeQName == null) {
            componentTypeQName = context.getItemType();
        }
        if (componentTypeQName == null) {
            componentTypeQName = context.getQNameForClass(componentClass);
        }
        if (componentTypeQName == null) {
            Class searchCls;
            for (searchCls = componentClass; searchCls != null && componentTypeQName == null; searchCls = searchCls.getSuperclass()) {
                componentTypeQName = context.getQNameForClass(searchCls);
            }
            if (componentTypeQName != null) {
                componentClass = searchCls;
            }
        }
        if (componentTypeQName == null) {
            throw new IOException(Messages.getMessage("noType00", componentClass.getName()));
        }
        int len = list == null ? Array.getLength(value) : list.size();
        String arrayType = "";
        int dim2Len = -1;
        if (encoded) {
            arrayType = soap == SOAPConstants.SOAP12_CONSTANTS ? dims + len : dims + "[" + len + "]";
            boolean enable2Dim = false;
            if (msgContext != null) {
                enable2Dim = JavaUtils.isTrueExplicitly(msgContext.getProperty("enable2DArrayEncoding"));
            }
            if (enable2Dim && !dims.equals("") && cls.isArray() && len > 0) {
                boolean okay = true;
                for (int i = 0; i < len && okay; ++i) {
                    Object elementValue = Array.get(value, i);
                    if (elementValue == null) {
                        okay = false;
                        continue;
                    }
                    if (dim2Len < 0) {
                        dim2Len = Array.getLength(elementValue);
                        if (dim2Len > 0) continue;
                        okay = false;
                        continue;
                    }
                    if (dim2Len == Array.getLength(elementValue)) continue;
                    okay = false;
                }
                if (okay) {
                    dims = dims.substring(0, dims.length() - 2);
                    arrayType = soap == SOAPConstants.SOAP12_CONSTANTS ? dims + len + " " + dim2Len : dims + "[" + len + "," + dim2Len + "]";
                } else {
                    dim2Len = -1;
                }
            }
        }
        QName itemQName = context.getItemQName();
        boolean bl = maxOccursUsage = !encoded && itemQName == null && componentTypeQName.equals(context.getCurrentXMLType());
        if (encoded) {
            AttributesImpl attrs = attributes == null ? new AttributesImpl() : (attributes instanceof AttributesImpl ? (AttributesImpl)attributes : new AttributesImpl(attributes));
            String compType = context.attributeQName2String(componentTypeQName);
            if (attrs.getIndex(soap.getEncodingURI(), soap.getAttrItemType()) == -1) {
                String encprefix = context.getPrefixForURI(soap.getEncodingURI());
                if (soap != SOAPConstants.SOAP12_CONSTANTS) {
                    compType = compType + arrayType;
                    attrs.addAttribute(soap.getEncodingURI(), soap.getAttrItemType(), encprefix + ":arrayType", "CDATA", compType);
                } else {
                    attrs.addAttribute(soap.getEncodingURI(), soap.getAttrItemType(), encprefix + ":itemType", "CDATA", compType);
                    attrs.addAttribute(soap.getEncodingURI(), "arraySize", encprefix + ":arraySize", "CDATA", arrayType);
                }
            }
            String qname = context.getPrefixForURI(schema.getXsiURI(), "xsi") + ":type";
            QName soapArray = soap == SOAPConstants.SOAP12_CONSTANTS ? Constants.SOAP_ARRAY12 : Constants.SOAP_ARRAY;
            int typeI = attrs.getIndex(schema.getXsiURI(), "type");
            if (typeI != -1) {
                attrs.setAttribute(typeI, schema.getXsiURI(), "type", qname, "CDATA", context.qName2String(soapArray));
            } else {
                attrs.addAttribute(schema.getXsiURI(), "type", qname, "CDATA", context.qName2String(soapArray));
            }
            attributes = attrs;
        }
        QName elementName = name;
        Attributes serializeAttr = attributes;
        if (!maxOccursUsage) {
            serializeAttr = null;
            context.startElement(name, attributes);
            if (itemQName != null) {
                elementName = itemQName;
            } else if (this.componentQName != null) {
                elementName = this.componentQName;
            }
        }
        if (dim2Len < 0) {
            Object aValue;
            if (list == null) {
                for (int index = 0; index < len; ++index) {
                    aValue = Array.get(value, index);
                    context.serialize(elementName, serializeAttr == null ? serializeAttr : new AttributesImpl(serializeAttr), aValue, componentTypeQName);
                }
            } else {
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    aValue = iterator.next();
                    context.serialize(elementName, serializeAttr == null ? serializeAttr : new AttributesImpl(serializeAttr), aValue, componentTypeQName);
                }
            }
        } else {
            for (int index = 0; index < len; ++index) {
                for (int index2 = 0; index2 < dim2Len; ++index2) {
                    Object aValue = Array.get(Array.get(value, index), index2);
                    context.serialize(elementName, null, aValue, componentTypeQName);
                }
            }
        }
        if (!maxOccursUsage) {
            context.endElement();
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    private static boolean isArray(Class clazz) {
        return clazz.isArray() || (class$java$util$Collection == null ? (class$java$util$Collection = ArraySerializer.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(clazz);
    }

    private static Class getComponentType(Class clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType();
        }
        if ((class$java$util$Collection == null ? (class$java$util$Collection = ArraySerializer.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(clazz)) {
            return class$java$lang$Object == null ? (class$java$lang$Object = ArraySerializer.class$("java.lang.Object")) : class$java$lang$Object;
        }
        return null;
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        boolean encoded = true;
        MessageContext mc = MessageContext.getCurrentContext();
        if (mc != null) {
            encoded = mc.isEncoded();
        } else {
            boolean bl = encoded = types.getServiceDesc().getUse() == Use.ENCODED;
        }
        if (!encoded) {
            Class cType;
            Class clazz = cType = class$java$lang$Object == null ? (class$java$lang$Object = ArraySerializer.class$("java.lang.Object")) : class$java$lang$Object;
            if (javaType.isArray()) {
                cType = javaType.getComponentType();
            }
            String typeName = types.writeType(cType);
            return types.createLiteralArrayElement(typeName, null);
        }
        String componentTypeName = null;
        Class componentType = null;
        if (ArraySerializer.isArray(javaType)) {
            String dimString = "[]";
            componentType = ArraySerializer.getComponentType(javaType);
            while (ArraySerializer.isArray(componentType)) {
                dimString = dimString + "[]";
                componentType = ArraySerializer.getComponentType(componentType);
            }
            types.writeType(componentType, null);
            componentTypeName = types.getQNameString(types.getTypeQName(componentType)) + dimString;
        }
        return types.createArrayElement(componentTypeName);
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

