/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;

public abstract class ArrayAccess
implements Serializable,
AutoCloseable {
    private static final long serialVersionUID = -7899494275459577958L;
    private int offset;
    private int length;

    protected ArrayAccess(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public abstract ArrayAccess subsequence(int var1, int var2);

    public abstract Object getData() throws ApfloatRuntimeException;

    public int[] getIntData() throws UnsupportedOperationException, ApfloatRuntimeException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public long[] getLongData() throws UnsupportedOperationException, ApfloatRuntimeException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public float[] getFloatData() throws UnsupportedOperationException, ApfloatRuntimeException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public double[] getDoubleData() throws UnsupportedOperationException, ApfloatRuntimeException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public abstract void close() throws ApfloatRuntimeException;
}

