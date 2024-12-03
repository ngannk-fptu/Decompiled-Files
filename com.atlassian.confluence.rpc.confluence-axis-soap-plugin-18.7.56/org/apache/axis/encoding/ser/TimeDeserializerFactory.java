/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class TimeDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$TimeDeserializer;

    public TimeDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$TimeDeserializer == null ? (class$org$apache$axis$encoding$ser$TimeDeserializer = TimeDeserializerFactory.class$("org.apache.axis.encoding.ser.TimeDeserializer")) : class$org$apache$axis$encoding$ser$TimeDeserializer, xmlType, javaType);
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

