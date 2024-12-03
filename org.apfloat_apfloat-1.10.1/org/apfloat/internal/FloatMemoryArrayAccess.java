/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public class FloatMemoryArrayAccess
extends ArrayAccess {
    private static final long serialVersionUID = 7704133670961317045L;
    private float[] data;

    public FloatMemoryArrayAccess(float[] data, int offset, int length) {
        super(offset, length);
        this.data = data;
    }

    @Override
    public ArrayAccess subsequence(int offset, int length) {
        return new FloatMemoryArrayAccess(this.data, this.getOffset() + offset, length);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public float[] getFloatData() {
        return this.data;
    }

    @Override
    public void close() throws ApfloatRuntimeException {
        this.data = null;
    }
}

