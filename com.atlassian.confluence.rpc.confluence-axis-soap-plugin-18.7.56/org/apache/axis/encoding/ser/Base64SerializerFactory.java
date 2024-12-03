/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class Base64SerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$Base64Serializer;

    public Base64SerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$Base64Serializer == null ? (class$org$apache$axis$encoding$ser$Base64Serializer = Base64SerializerFactory.class$("org.apache.axis.encoding.ser.Base64Serializer")) : class$org$apache$axis$encoding$ser$Base64Serializer, xmlType, javaType);
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

