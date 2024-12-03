/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.utils.Messages;

public class TypeMappingDelegate
implements TypeMapping {
    static final TypeMappingImpl placeholder = new TypeMappingImpl();
    TypeMappingImpl delegate;
    TypeMappingDelegate next;

    TypeMappingDelegate(TypeMappingImpl delegate) {
        if (delegate == null) {
            throw new RuntimeException(Messages.getMessage("NullDelegate"));
        }
        this.delegate = delegate;
    }

    public String[] getSupportedEncodings() {
        return this.delegate.getSupportedEncodings();
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
        this.delegate.setSupportedEncodings(namespaceURIs);
    }

    public void register(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory dsf) throws JAXRPCException {
        this.delegate.register(javaType, xmlType, sf, dsf);
    }

    public SerializerFactory getSerializer(Class javaType, QName xmlType) throws JAXRPCException {
        SerializerFactory sf = this.delegate.getSerializer(javaType, xmlType);
        if (sf == null && this.next != null) {
            sf = this.next.getSerializer(javaType, xmlType);
        }
        if (sf == null) {
            sf = this.delegate.finalGetSerializer(javaType);
        }
        return sf;
    }

    public SerializerFactory getSerializer(Class javaType) throws JAXRPCException {
        return this.getSerializer(javaType, null);
    }

    public DeserializerFactory getDeserializer(Class javaType, QName xmlType) throws JAXRPCException {
        return this.getDeserializer(javaType, xmlType, this);
    }

    public DeserializerFactory getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start) throws JAXRPCException {
        DeserializerFactory df = this.delegate.getDeserializer(javaType, xmlType, start);
        if (df == null && this.next != null) {
            df = this.next.getDeserializer(javaType, xmlType, start);
        }
        if (df == null) {
            df = this.delegate.finalGetDeserializer(javaType, xmlType, start);
        }
        return df;
    }

    public DeserializerFactory getDeserializer(QName xmlType) throws JAXRPCException {
        return this.getDeserializer(null, xmlType);
    }

    public void removeSerializer(Class javaType, QName xmlType) throws JAXRPCException {
        this.delegate.removeSerializer(javaType, xmlType);
    }

    public void removeDeserializer(Class javaType, QName xmlType) throws JAXRPCException {
        this.delegate.removeDeserializer(javaType, xmlType);
    }

    public boolean isRegistered(Class javaType, QName xmlType) {
        boolean result = this.delegate.isRegistered(javaType, xmlType);
        if (!result && this.next != null) {
            return this.next.isRegistered(javaType, xmlType);
        }
        return result;
    }

    public QName getTypeQName(Class javaType) {
        return this.delegate.getTypeQName(javaType, this.next);
    }

    public Class getClassForQName(QName xmlType) {
        return this.getClassForQName(xmlType, null);
    }

    public Class getClassForQName(QName xmlType, Class javaType) {
        return this.delegate.getClassForQName(xmlType, javaType, this.next);
    }

    public QName getTypeQNameExact(Class javaType) {
        QName result = this.delegate.getTypeQNameExact(javaType, this.next);
        return result;
    }

    public void setNext(TypeMappingDelegate next) {
        if (next == this) {
            return;
        }
        this.next = next;
    }

    public TypeMappingDelegate getNext() {
        return this.next;
    }

    public Class[] getAllClasses() {
        return this.delegate.getAllClasses(this.next);
    }

    public QName getXMLType(Class javaType, QName xmlType, boolean encoded) throws JAXRPCException {
        QName result = this.delegate.getXMLType(javaType, xmlType, encoded);
        if (result == null && this.next != null) {
            return this.next.getXMLType(javaType, xmlType, encoded);
        }
        return result;
    }

    public void setDoAutoTypes(boolean doAutoTypes) {
        this.delegate.setDoAutoTypes(doAutoTypes);
    }
}

