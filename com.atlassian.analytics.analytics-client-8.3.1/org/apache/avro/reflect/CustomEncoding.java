/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;

public abstract class CustomEncoding<T> {
    protected Schema schema;

    protected abstract void write(Object var1, Encoder var2) throws IOException;

    protected abstract T read(Object var1, Decoder var2) throws IOException;

    T read(Decoder in) throws IOException {
        return this.read(null, in);
    }

    protected Schema getSchema() {
        return this.schema;
    }
}

