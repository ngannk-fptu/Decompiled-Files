/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class DateSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$DateSerializer;

    public DateSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$DateSerializer == null ? (class$org$apache$axis$encoding$ser$DateSerializer = DateSerializerFactory.class$("org.apache.axis.encoding.ser.DateSerializer")) : class$org$apache$axis$encoding$ser$DateSerializer, xmlType, javaType);
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

