/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class EnumDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumDeserializer;

    public EnumDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$EnumDeserializer == null ? (class$org$apache$axis$encoding$ser$EnumDeserializer = EnumDeserializerFactory.class$("org.apache.axis.encoding.ser.EnumDeserializer")) : class$org$apache$axis$encoding$ser$EnumDeserializer, xmlType, javaType);
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

