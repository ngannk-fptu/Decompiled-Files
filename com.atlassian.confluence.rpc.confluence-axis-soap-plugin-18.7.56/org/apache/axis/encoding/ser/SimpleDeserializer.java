/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.CharArrayWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.types.URI;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SimpleDeserializer
extends DeserializerImpl {
    private static final Class[] STRING_STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializer.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializer.class$("java.lang.String")) : class$java$lang$String};
    public static final Class[] STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializer.class$("java.lang.String")) : class$java$lang$String};
    private final CharArrayWriter val = new CharArrayWriter();
    private Constructor constructor = null;
    private Map propertyMap = null;
    private HashMap attributeMap = null;
    public QName xmlType;
    public Class javaType;
    private TypeDesc typeDesc = null;
    protected DeserializationContext context = null;
    protected SimpleDeserializer cacheStringDSer = null;
    protected QName cacheXMLType = null;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$SimpleType;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$org$apache$axis$types$URI;

    public SimpleDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.init();
    }

    public SimpleDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.init();
    }

    private void init() {
        if ((class$org$apache$axis$encoding$SimpleType == null ? (class$org$apache$axis$encoding$SimpleType = SimpleDeserializer.class$("org.apache.axis.encoding.SimpleType")) : class$org$apache$axis$encoding$SimpleType).isAssignableFrom(this.javaType) && this.typeDesc == null) {
            this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
        }
        if (this.typeDesc != null) {
            this.propertyMap = this.typeDesc.getPropertyDescriptorMap();
        } else {
            BeanPropertyDescriptor[] pd = BeanUtils.getPd(this.javaType, null);
            this.propertyMap = new HashMap();
            for (int i = 0; i < pd.length; ++i) {
                BeanPropertyDescriptor descriptor = pd[i];
                this.propertyMap.put(descriptor.getName(), descriptor);
            }
        }
    }

    public void reset() {
        this.val.reset();
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
        this.val.write(chars, start, end);
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        if (this.isNil) {
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
        Object value;
        if (this.javaType == (class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializer.class$("java.lang.String")) : class$java$lang$String)) {
            return source;
        }
        if ((source = source.trim()).length() == 0 && this.typeDesc == null) {
            return null;
        }
        if (this.constructor == null && (value = this.makeBasicValue(source)) != null) {
            return value;
        }
        Object[] args = null;
        boolean isQNameSubclass = (class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = SimpleDeserializer.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName).isAssignableFrom(this.javaType);
        if (isQNameSubclass) {
            int colon = source.lastIndexOf(":");
            String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
            String localPart = colon < 0 ? source : source.substring(colon + 1);
            args = new Object[]{namespace, localPart};
        }
        if (this.constructor == null) {
            try {
                this.constructor = isQNameSubclass ? this.javaType.getDeclaredConstructor(STRING_STRING_CLASS) : this.javaType.getDeclaredConstructor(STRING_CLASS);
            }
            catch (Exception e) {
                return null;
            }
        }
        if (this.constructor.getParameterTypes().length == 0) {
            try {
                Object obj = this.constructor.newInstance(new Object[0]);
                obj.getClass().getMethod("set_value", class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializer.class$("java.lang.String")) : class$java$lang$String).invoke(obj, source);
                return obj;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (args == null) {
            args = new Object[]{source};
        }
        return this.constructor.newInstance(args);
    }

    private Object makeBasicValue(String source) throws Exception {
        if (this.javaType == Boolean.TYPE || this.javaType == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = SimpleDeserializer.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
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
        if (this.javaType == Float.TYPE || this.javaType == (class$java$lang$Float == null ? (class$java$lang$Float = SimpleDeserializer.class$("java.lang.Float")) : class$java$lang$Float)) {
            if (source.equals("NaN")) {
                return new Float(Float.NaN);
            }
            if (source.equals("INF")) {
                return new Float(Float.POSITIVE_INFINITY);
            }
            if (source.equals("-INF")) {
                return new Float(Float.NEGATIVE_INFINITY);
            }
            return new Float(source);
        }
        if (this.javaType == Double.TYPE || this.javaType == (class$java$lang$Double == null ? (class$java$lang$Double = SimpleDeserializer.class$("java.lang.Double")) : class$java$lang$Double)) {
            if (source.equals("NaN")) {
                return new Double(Double.NaN);
            }
            if (source.equals("INF")) {
                return new Double(Double.POSITIVE_INFINITY);
            }
            if (source.equals("-INF")) {
                return new Double(Double.NEGATIVE_INFINITY);
            }
            return new Double(source);
        }
        if (this.javaType == Integer.TYPE || this.javaType == (class$java$lang$Integer == null ? (class$java$lang$Integer = SimpleDeserializer.class$("java.lang.Integer")) : class$java$lang$Integer)) {
            return new Integer(source);
        }
        if (this.javaType == Short.TYPE || this.javaType == (class$java$lang$Short == null ? (class$java$lang$Short = SimpleDeserializer.class$("java.lang.Short")) : class$java$lang$Short)) {
            return new Short(source);
        }
        if (this.javaType == Long.TYPE || this.javaType == (class$java$lang$Long == null ? (class$java$lang$Long = SimpleDeserializer.class$("java.lang.Long")) : class$java$lang$Long)) {
            return new Long(source);
        }
        if (this.javaType == Byte.TYPE || this.javaType == (class$java$lang$Byte == null ? (class$java$lang$Byte = SimpleDeserializer.class$("java.lang.Byte")) : class$java$lang$Byte)) {
            return new Byte(source);
        }
        if (this.javaType == (class$org$apache$axis$types$URI == null ? (class$org$apache$axis$types$URI = SimpleDeserializer.class$("org.apache.axis.types.URI")) : class$org$apache$axis$types$URI)) {
            return new URI(source);
        }
        return null;
    }

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        this.context = context;
        for (int i = 0; i < attributes.getLength(); ++i) {
            Class type;
            BeanPropertyDescriptor bpd;
            QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
            String fieldName = attributes.getLocalName(i);
            if (this.typeDesc != null && (fieldName = this.typeDesc.getFieldNameForAttribute(attrQName)) == null || this.propertyMap == null || (bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName)) == null || !bpd.isWriteable() || bpd.isIndexed()) continue;
            TypeMapping tm = context.getTypeMapping();
            QName qn = tm.getTypeQName(type = bpd.getType());
            if (qn == null) {
                throw new SAXException(Messages.getMessage("unregistered00", type.toString()));
            }
            Deserializer dSer = context.getDeserializerForType(qn);
            if (dSer == null) {
                throw new SAXException(Messages.getMessage("noDeser00", type.toString()));
            }
            if (!(dSer instanceof SimpleDeserializer)) {
                throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), type.toString()));
            }
            if (this.attributeMap == null) {
                this.attributeMap = new HashMap();
            }
            try {
                Object val = ((SimpleDeserializer)dSer).makeValue(attributes.getValue(i));
                this.attributeMap.put(fieldName, val);
                continue;
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }
    }

    private void setSimpleTypeAttributes() throws SAXException {
        if (this.attributeMap == null) {
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

