/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class EnumSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumSerializer;

    public EnumSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$EnumSerializer == null ? (class$org$apache$axis$encoding$ser$EnumSerializer = EnumSerializerFactory.class$("org.apache.axis.encoding.ser.EnumSerializer")) : class$org$apache$axis$encoding$ser$EnumSerializer, xmlType, javaType);
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

