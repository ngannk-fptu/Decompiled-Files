/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;

public interface DatumReader<D> {
    public void setSchema(Schema var1);

    public D read(D var1, Decoder var2) throws IOException;
}

