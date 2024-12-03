/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import org.apache.avro.Schema;
import org.apache.avro.path.PathElement;

public interface PathTracingException<T extends Throwable> {
    public void tracePath(PathElement var1);

    public T summarize(Schema var1);
}

