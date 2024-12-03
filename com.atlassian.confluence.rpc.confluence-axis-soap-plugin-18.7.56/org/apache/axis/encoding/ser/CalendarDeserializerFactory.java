/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class CalendarDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$CalendarDeserializer;

    public CalendarDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$CalendarDeserializer == null ? (class$org$apache$axis$encoding$ser$CalendarDeserializer = CalendarDeserializerFactory.class$("org.apache.axis.encoding.ser.CalendarDeserializer")) : class$org$apache$axis$encoding$ser$CalendarDeserializer, xmlType, javaType);
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

