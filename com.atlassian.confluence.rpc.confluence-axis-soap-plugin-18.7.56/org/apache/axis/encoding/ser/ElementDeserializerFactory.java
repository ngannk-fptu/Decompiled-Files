/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class ElementDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ElementDeserializer;

    public ElementDeserializerFactory() {
        super(class$org$apache$axis$encoding$ser$ElementDeserializer == null ? (class$org$apache$axis$encoding$ser$ElementDeserializer = ElementDeserializerFactory.class$("org.apache.axis.encoding.ser.ElementDeserializer")) : class$org$apache$axis$encoding$ser$ElementDeserializer);
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

