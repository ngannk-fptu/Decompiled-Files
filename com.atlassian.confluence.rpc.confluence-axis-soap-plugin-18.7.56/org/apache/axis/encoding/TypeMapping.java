/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.Serializable;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

public interface TypeMapping
extends javax.xml.rpc.encoding.TypeMapping,
Serializable {
    public SerializerFactory getSerializer(Class var1) throws JAXRPCException;

    public DeserializerFactory getDeserializer(QName var1) throws JAXRPCException;

    public QName getTypeQName(Class var1);

    public QName getTypeQNameExact(Class var1);

    public Class getClassForQName(QName var1);

    public Class getClassForQName(QName var1, Class var2);

    public Class[] getAllClasses();

    public QName getXMLType(Class var1, QName var2, boolean var3) throws JAXRPCException;
}

