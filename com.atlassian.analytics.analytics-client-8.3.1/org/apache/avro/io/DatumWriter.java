/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;

public interface DatumWriter<D> {
    public void setSchema(Schema var1);

    public void write(D var1, Encoder var2) throws IOException;
}

