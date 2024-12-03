/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class DocumentSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$DocumentSerializer;

    public DocumentSerializerFactory() {
        super(class$org$apache$axis$encoding$ser$DocumentSerializer == null ? (class$org$apache$axis$encoding$ser$DocumentSerializer = DocumentSerializerFactory.class$("org.apache.axis.encoding.ser.DocumentSerializer")) : class$org$apache$axis$encoding$ser$DocumentSerializer);
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

