/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public class LongMemoryArrayAccess
extends ArrayAccess {
    private static final long serialVersionUID = 844248131988537796L;
    private long[] data;

    public LongMemoryArrayAccess(long[] data, int offset, int length) {
        super(offset, length);
        this.data = data;
    }

    @Override
    public ArrayAccess subsequence(int offset, int length) {
        return new LongMemoryArrayAccess(this.data, this.getOffset() + offset, length);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public long[] getLongData() {
        return this.data;
    }

    @Override
    public void close() throws ApfloatRuntimeException {
        this.data = null;
    }
}

