/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class CalendarSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$CalendarSerializer;

    public CalendarSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$CalendarSerializer == null ? (class$org$apache$axis$encoding$ser$CalendarSerializer = CalendarSerializerFactory.class$("org.apache.axis.encoding.ser.CalendarSerializer")) : class$org$apache$axis$encoding$ser$CalendarSerializer, xmlType, javaType);
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

