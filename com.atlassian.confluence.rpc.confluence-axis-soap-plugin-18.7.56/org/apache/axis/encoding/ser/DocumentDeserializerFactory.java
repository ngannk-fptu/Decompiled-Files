/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class DocumentDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$DocumentDeserializer;

    public DocumentDeserializerFactory() {
        super(class$org$apache$axis$encoding$ser$DocumentDeserializer == null ? (class$org$apache$axis$encoding$ser$DocumentDeserializer = DocumentDeserializerFactory.class$("org.apache.axis.encoding.ser.DocumentDeserializer")) : class$org$apache$axis$encoding$ser$DocumentDeserializer);
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

