/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import org.apache.avro.Schema;

public interface FileReader<D>
extends Iterator<D>,
Iterable<D>,
Closeable {
    public Schema getSchema();

    public D next(D var1) throws IOException;

    public void sync(long var1) throws IOException;

    public boolean pastSync(long var1) throws IOException;

    public long tell() throws IOException;
}

