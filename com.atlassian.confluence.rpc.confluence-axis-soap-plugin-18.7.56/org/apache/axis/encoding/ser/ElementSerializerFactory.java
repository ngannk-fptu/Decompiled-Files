/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class ElementSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ElementSerializer;

    public ElementSerializerFactory() {
        super(class$org$apache$axis$encoding$ser$ElementSerializer == null ? (class$org$apache$axis$encoding$ser$ElementSerializer = ElementSerializerFactory.class$("org.apache.axis.encoding.ser.ElementSerializer")) : class$org$apache$axis$encoding$ser$ElementSerializer);
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

