/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMappingDelegate;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.ArrayUtil;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;

public class TypeMappingImpl
implements Serializable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$TypeMappingImpl == null ? (class$org$apache$axis$encoding$TypeMappingImpl = TypeMappingImpl.class$("org.apache.axis.encoding.TypeMappingImpl")) : class$org$apache$axis$encoding$TypeMappingImpl).getName());
    public static boolean dotnet_soapenc_bugfix = false;
    private HashMap qName2Pair = new HashMap();
    private HashMap class2Pair = new HashMap();
    private HashMap pair2SF = new HashMap();
    private HashMap pair2DF = new HashMap();
    private ArrayList namespaces = new ArrayList();
    protected Boolean doAutoTypes = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$TypeMappingImpl;
    static /* synthetic */ Class class$java$util$Collection;
    static /* synthetic */ Class class$java$util$List;
    static /* synthetic */ Class array$Ljava$lang$Object;

    private static boolean isArray(Class clazz) {
        return clazz.isArray() || (class$java$util$Collection == null ? (class$java$util$Collection = TypeMappingImpl.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(clazz);
    }

    public String[] getSupportedEncodings() {
        String[] stringArray = new String[this.namespaces.size()];
        return this.namespaces.toArray(stringArray);
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
        this.namespaces.clear();
        for (int i = 0; i < namespaceURIs.length; ++i) {
            if (this.namespaces.contains(namespaceURIs[i])) continue;
            this.namespaces.add(namespaceURIs[i]);
        }
    }

    public boolean isRegistered(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
        }
        return this.pair2SF.keySet().contains(new Pair(javaType, xmlType));
    }

    public void register(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, javax.xml.rpc.encoding.DeserializerFactory dsf) throws JAXRPCException {
        if (sf == null && dsf == null) {
            throw new JAXRPCException(Messages.getMessage("badSerFac"));
        }
        this.internalRegister(javaType, xmlType, sf, dsf);
    }

    protected void internalRegister(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, javax.xml.rpc.encoding.DeserializerFactory dsf) throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
        }
        Pair pair = new Pair(javaType, xmlType);
        this.qName2Pair.put(xmlType, pair);
        this.class2Pair.put(javaType, pair);
        if (sf != null) {
            this.pair2SF.put(pair, sf);
        }
        if (dsf != null) {
            this.pair2DF.put(pair, dsf);
        }
    }

    public javax.xml.rpc.encoding.SerializerFactory getSerializer(Class javaType, QName xmlType) throws JAXRPCException {
        Pair pair2;
        javax.xml.rpc.encoding.SerializerFactory sf = null;
        if (xmlType == null && (xmlType = this.getTypeQName(javaType, null)) == null) {
            return null;
        }
        Pair pair = new Pair(javaType, xmlType);
        sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair);
        if (sf == null && javaType.isArray()) {
            int dimension = 1;
            Class<?> componentType = javaType.getComponentType();
            while (componentType.isArray()) {
                ++dimension;
                componentType = componentType.getComponentType();
            }
            int[] dimensions = new int[dimension];
            Class<?> superJavaType = null;
            for (componentType = componentType.getSuperclass(); componentType != null && (sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair = new Pair(superJavaType = Array.newInstance(componentType, dimensions).getClass(), xmlType))) == null; componentType = componentType.getSuperclass()) {
            }
        }
        if (sf == null && javaType.isArray() && xmlType != null && (pair2 = (Pair)this.qName2Pair.get(xmlType)) != null && pair2.javaType != null && !pair2.javaType.isPrimitive() && ArrayUtil.isConvertable(pair2.javaType, javaType)) {
            sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair2);
        }
        return sf;
    }

    public SerializerFactory finalGetSerializer(Class javaType) {
        Pair pair = TypeMappingImpl.isArray(javaType) ? (Pair)this.qName2Pair.get(Constants.SOAP_ARRAY) : (Pair)this.class2Pair.get(javaType);
        if (pair != null) {
            return (SerializerFactory)this.pair2SF.get(pair);
        }
        return null;
    }

    public QName getXMLType(Class javaType, QName xmlType, boolean encoded) throws JAXRPCException {
        javax.xml.rpc.encoding.SerializerFactory sf = null;
        if (xmlType == null && (xmlType = this.getTypeQNameRecursive(javaType)) == null) {
            return null;
        }
        Pair pair = new Pair(javaType, xmlType);
        sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair);
        if (sf != null) {
            return xmlType;
        }
        if (TypeMappingImpl.isArray(javaType)) {
            if (encoded) {
                return Constants.SOAP_ARRAY;
            }
            pair = (Pair)this.qName2Pair.get(xmlType);
        }
        if (pair == null) {
            pair = (Pair)this.class2Pair.get(javaType);
        }
        if (pair != null) {
            xmlType = pair.xmlType;
        }
        return xmlType;
    }

    public javax.xml.rpc.encoding.DeserializerFactory getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start) throws JAXRPCException {
        if (javaType == null && (javaType = start.getClassForQName(xmlType)) == null) {
            return null;
        }
        Pair pair = new Pair(javaType, xmlType);
        return (javax.xml.rpc.encoding.DeserializerFactory)this.pair2DF.get(pair);
    }

    public DeserializerFactory finalGetDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start) {
        DeserializerFactory df = null;
        if (javaType != null && javaType.isArray()) {
            QName componentXmlType;
            Class actualClass;
            Class<?> componentType = javaType.getComponentType();
            if (xmlType != null && ((actualClass = start.getClassForQName(xmlType)) == componentType || actualClass != null && componentType.isAssignableFrom(actualClass))) {
                return null;
            }
            Pair pair = (Pair)this.qName2Pair.get(Constants.SOAP_ARRAY);
            df = (DeserializerFactory)this.pair2DF.get(pair);
            if (df instanceof ArrayDeserializerFactory && javaType.isArray() && (componentXmlType = start.getTypeQName(componentType)) != null) {
                df = new ArrayDeserializerFactory(componentXmlType);
            }
        }
        return df;
    }

    public void removeSerializer(Class javaType, QName xmlType) throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
        }
        Pair pair = new Pair(javaType, xmlType);
        this.pair2SF.remove(pair);
    }

    public void removeDeserializer(Class javaType, QName xmlType) throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
        }
        Pair pair = new Pair(javaType, xmlType);
        this.pair2DF.remove(pair);
    }

    public QName getTypeQNameRecursive(Class javaType) {
        QName ret = null;
        while (javaType != null) {
            ret = this.getTypeQName(javaType, null);
            if (ret != null) {
                return ret;
            }
            Class<?>[] interfaces = javaType.getInterfaces();
            if (interfaces != null) {
                for (int i = 0; i < interfaces.length; ++i) {
                    Class<?> iface = interfaces[i];
                    ret = this.getTypeQName(iface, null);
                    if (ret == null) continue;
                    return ret;
                }
            }
            javaType = javaType.getSuperclass();
        }
        return null;
    }

    public QName getTypeQNameExact(Class javaType, TypeMappingDelegate next) {
        if (javaType == null) {
            return null;
        }
        QName xmlType = null;
        Pair pair = (Pair)this.class2Pair.get(javaType);
        if (this.isDotNetSoapEncFixNeeded() && pair != null && Constants.isSOAP_ENC((xmlType = pair.xmlType).getNamespaceURI()) && !xmlType.getLocalPart().equals("Array")) {
            pair = null;
        }
        if (pair == null && next != null) {
            xmlType = next.delegate.getTypeQNameExact(javaType, next.next);
        }
        if (pair != null) {
            xmlType = pair.xmlType;
        }
        return xmlType;
    }

    private boolean isDotNetSoapEncFixNeeded() {
        String dotNetSoapEncFix;
        SOAPService service;
        MessageContext msgContext = MessageContext.getCurrentContext();
        if (msgContext != null && (service = msgContext.getService()) != null && (dotNetSoapEncFix = (String)service.getOption("dotNetSoapEncFix")) != null) {
            return JavaUtils.isTrue(dotNetSoapEncFix);
        }
        return dotnet_soapenc_bugfix;
    }

    public QName getTypeQName(Class javaType, TypeMappingDelegate next) {
        QName xmlType = this.getTypeQNameExact(javaType, next);
        if (this.shouldDoAutoTypes() && javaType != (class$java$util$List == null ? (class$java$util$List = TypeMappingImpl.class$("java.util.List")) : class$java$util$List) && !(class$java$util$List == null ? (class$java$util$List = TypeMappingImpl.class$("java.util.List")) : class$java$util$List).isAssignableFrom(javaType) && xmlType != null && xmlType.equals(Constants.SOAP_ARRAY)) {
            xmlType = new QName(Namespaces.makeNamespace(javaType.getName()), Types.getLocalNameFromFullName(javaType.getName()));
            this.internalRegister(javaType, xmlType, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        }
        if (xmlType == null && TypeMappingImpl.isArray(javaType)) {
            Pair pair = (Pair)this.class2Pair.get(array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = TypeMappingImpl.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object);
            xmlType = pair != null ? pair.xmlType : Constants.SOAP_ARRAY;
        }
        if (xmlType == null && this.shouldDoAutoTypes()) {
            xmlType = new QName(Namespaces.makeNamespace(javaType.getName()), Types.getLocalNameFromFullName(javaType.getName()));
            this.internalRegister(javaType, xmlType, new BeanSerializerFactory(javaType, xmlType), new BeanDeserializerFactory(javaType, xmlType));
        }
        return xmlType;
    }

    public Class getClassForQName(QName xmlType, Class javaType, TypeMappingDelegate next) {
        String pkg;
        Pair pair;
        if (xmlType == null) {
            return null;
        }
        if (javaType != null && this.pair2DF.get(pair = new Pair(javaType, xmlType)) == null && next != null) {
            javaType = next.getClassForQName(xmlType, javaType);
        }
        if (javaType == null) {
            pair = (Pair)this.qName2Pair.get(xmlType);
            if (pair == null && next != null) {
                javaType = next.getClassForQName(xmlType);
            } else if (pair != null) {
                javaType = pair.javaType;
            }
        }
        if (javaType == null && this.shouldDoAutoTypes() && (pkg = Namespaces.getPackage(xmlType.getNamespaceURI())) != null) {
            String className = xmlType.getLocalPart();
            if (pkg.length() > 0) {
                className = pkg + "." + className;
            }
            try {
                javaType = ClassUtils.forName(className);
                this.internalRegister(javaType, xmlType, new BeanSerializerFactory(javaType, xmlType), new BeanDeserializerFactory(javaType, xmlType));
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
        }
        return javaType;
    }

    public void setDoAutoTypes(boolean doAutoTypes) {
        this.doAutoTypes = doAutoTypes ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean shouldDoAutoTypes() {
        if (this.doAutoTypes != null) {
            return this.doAutoTypes;
        }
        MessageContext msgContext = MessageContext.getCurrentContext();
        if (msgContext != null && (msgContext.isPropertyTrue("axis.doAutoTypes") || msgContext.getAxisEngine() != null && JavaUtils.isTrue(msgContext.getAxisEngine().getOption("axis.doAutoTypes")))) {
            this.doAutoTypes = Boolean.TRUE;
        }
        if (this.doAutoTypes == null) {
            this.doAutoTypes = AxisProperties.getProperty("axis.doAutoTypes", "false").equals("true") ? Boolean.TRUE : Boolean.FALSE;
        }
        return this.doAutoTypes;
    }

    public Class[] getAllClasses(TypeMappingDelegate next) {
        HashSet<Class<Object>> temp = new HashSet<Class<Object>>();
        if (next != null) {
            temp.addAll(Arrays.asList(next.getAllClasses()));
        }
        temp.addAll(this.class2Pair.keySet());
        return temp.toArray(new Class[temp.size()]);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static class Pair
    implements Serializable {
        public Class javaType;
        public QName xmlType;

        public Pair(Class javaType, QName xmlType) {
            this.javaType = javaType;
            this.xmlType = xmlType;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            Pair p = (Pair)o;
            if (p.xmlType == this.xmlType && p.javaType == this.javaType) {
                return true;
            }
            return p.xmlType.equals(this.xmlType) && p.javaType.equals(this.javaType);
        }

        public int hashCode() {
            int hashcode = 0;
            if (this.javaType != null) {
                hashcode ^= this.javaType.hashCode();
            }
            if (this.xmlType != null) {
                hashcode ^= this.xmlType.hashCode();
            }
            return hashcode;
        }
    }
}

