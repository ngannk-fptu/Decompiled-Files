/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

public abstract class IndexInput
extends DataInput
implements Cloneable,
Closeable {
    private final String resourceDescription;

    protected IndexInput(String resourceDescription) {
        if (resourceDescription == null) {
            throw new IllegalArgumentException("resourceDescription must not be null");
        }
        this.resourceDescription = resourceDescription;
    }

    @Override
    public abstract void close() throws IOException;

    public abstract long getFilePointer();

    public abstract void seek(long var1) throws IOException;

    public abstract long length();

    public String toString() {
        return this.resourceDescription;
    }

    @Override
    public IndexInput clone() {
        return (IndexInput)super.clone();
    }

    protected String getFullSliceDescription(String sliceDescription) {
        if (sliceDescription == null) {
            return this.toString();
        }
        return this.toString() + " [slice=" + sliceDescription + "]";
    }
}

