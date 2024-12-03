/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SimpleListDeserializer
extends SimpleDeserializer {
    StringBuffer val = new StringBuffer();
    private Constructor constructor = null;
    private Map propertyMap = null;
    private HashMap attributeMap = null;
    private DeserializationContext context = null;
    public QName xmlType;
    public Class javaType;
    private TypeDesc typeDesc = null;
    protected SimpleListDeserializer cacheStringDSer = null;
    protected QName cacheXMLType = null;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$org$apache$axis$encoding$SimpleType;

    public SimpleListDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public SimpleListDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        super(javaType, xmlType, typeDesc);
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
    }

    public void reset() {
        this.val.setLength(0);
        this.attributeMap = null;
        this.isNil = false;
        this.isEnded = false;
    }

    public void setConstructor(Constructor c) {
        this.constructor = c;
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        throw new SAXException(Messages.getMessage("cantHandle00", "SimpleDeserializer"));
    }

    public void characters(char[] chars, int start, int end) throws SAXException {
        this.val.append(chars, start, end);
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        if (this.isNil || this.val == null) {
            this.value = null;
            return;
        }
        try {
            this.value = this.makeValue(this.val.toString());
        }
        catch (InvocationTargetException ite) {
            Throwable realException = ite.getTargetException();
            if (realException instanceof Exception) {
                throw new SAXException((Exception)realException);
            }
            throw new SAXException(ite.getMessage());
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
        this.setSimpleTypeAttributes();
    }

    public Object makeValue(String source) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(source.trim());
        int length = tokenizer.countTokens();
        Object list = Array.newInstance(this.javaType, length);
        for (int i = 0; i < length; ++i) {
            String token = tokenizer.nextToken();
            Array.set(list, i, this.makeUnitValue(token));
        }
        return list;
    }

    private Object makeUnitValue(String source) throws Exception {
        if (this.javaType == Boolean.TYPE || this.javaType == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = SimpleListDeserializer.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
            switch (source.charAt(0)) {
                case '0': 
                case 'F': 
                case 'f': {
                    return Boolean.FALSE;
                }
                case '1': 
                case 'T': 
                case 't': {
                    return Boolean.TRUE;
                }
            }
            throw new NumberFormatException(Messages.getMessage("badBool00"));
        }
        if (this.javaType == Float.TYPE || this.javaType == (class$java$lang$Float == null ? (class$java$lang$Float = SimpleListDeserializer.class$("java.lang.Float")) : class$java$lang$Float)) {
            if (source.equals("NaN")) {
                return new Float(Float.NaN);
            }
            if (source.equals("INF")) {
                return new Float(Float.POSITIVE_INFINITY);
            }
            if (source.equals("-INF")) {
                return new Float(Float.NEGATIVE_INFINITY);
            }
        }
        if (this.javaType == Double.TYPE || this.javaType == (class$java$lang$Double == null ? (class$java$lang$Double = SimpleListDeserializer.class$("java.lang.Double")) : class$java$lang$Double)) {
            if (source.equals("NaN")) {
                return new Double(Double.NaN);
            }
            if (source.equals("INF")) {
                return new Double(Double.POSITIVE_INFINITY);
            }
            if (source.equals("-INF")) {
                return new Double(Double.NEGATIVE_INFINITY);
            }
        }
        if (this.javaType == (class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = SimpleListDeserializer.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName)) {
            int colon = source.lastIndexOf(":");
            String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
            String localPart = colon < 0 ? source : source.substring(colon + 1);
            return new QName(namespace, localPart);
        }
        return this.constructor.newInstance(source);
    }

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        this.context = context;
        if (this.typeDesc == null) {
            return;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            Class type;
            BeanPropertyDescriptor bpd;
            QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
            String fieldName = this.typeDesc.getFieldNameForAttribute(attrQName);
            if (fieldName == null || (bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName)) == null || !bpd.isWriteable() || bpd.isIndexed()) continue;
            TypeMapping tm = context.getTypeMapping();
            QName qn = tm.getTypeQName(type = bpd.getType());
            if (qn == null) {
                throw new SAXException(Messages.getMessage("unregistered00", type.toString()));
            }
            Deserializer dSer = context.getDeserializerForType(qn);
            if (dSer == null) {
                throw new SAXException(Messages.getMessage("noDeser00", type.toString()));
            }
            if (!(dSer instanceof SimpleListDeserializer)) {
                throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), type.toString()));
            }
            if (this.attributeMap == null) {
                this.attributeMap = new HashMap();
            }
            try {
                Object val = ((SimpleListDeserializer)dSer).makeValue(attributes.getValue(i));
                this.attributeMap.put(fieldName, val);
                continue;
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }
    }

    private void setSimpleTypeAttributes() throws SAXException {
        if (!(class$org$apache$axis$encoding$SimpleType == null ? (class$org$apache$axis$encoding$SimpleType = SimpleListDeserializer.class$("org.apache.axis.encoding.SimpleType")) : class$org$apache$axis$encoding$SimpleType).isAssignableFrom(this.javaType) || this.attributeMap == null) {
            return;
        }
        Set entries = this.attributeMap.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String name = (String)entry.getKey();
            Object val = entry.getValue();
            BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(name);
            if (!bpd.isWriteable() || bpd.isIndexed()) continue;
            try {
                bpd.set(this.value, val);
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }
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

