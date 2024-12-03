/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class SimpleListSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleListSerializer;

    public SimpleListSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$SimpleListSerializer == null ? (class$org$apache$axis$encoding$ser$SimpleListSerializer = SimpleListSerializerFactory.class$("org.apache.axis.encoding.ser.SimpleListSerializer")) : class$org$apache$axis$encoding$ser$SimpleListSerializer, xmlType, javaType);
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

