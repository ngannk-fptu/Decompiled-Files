/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class Base64DeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$Base64Deserializer;

    public Base64DeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$Base64Deserializer == null ? (class$org$apache$axis$encoding$ser$Base64Deserializer = Base64DeserializerFactory.class$("org.apache.axis.encoding.ser.Base64Deserializer")) : class$org$apache$axis$encoding$ser$Base64Deserializer, xmlType, javaType);
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

