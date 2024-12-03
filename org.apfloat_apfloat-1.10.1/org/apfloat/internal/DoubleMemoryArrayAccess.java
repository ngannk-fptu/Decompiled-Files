/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public class DoubleMemoryArrayAccess
extends ArrayAccess {
    private static final long serialVersionUID = -8917010087742357783L;
    private double[] data;

    public DoubleMemoryArrayAccess(double[] data, int offset, int length) {
        super(offset, length);
        this.data = data;
    }

    @Override
    public ArrayAccess subsequence(int offset, int length) {
        return new DoubleMemoryArrayAccess(this.data, this.getOffset() + offset, length);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public double[] getDoubleData() {
        return this.data;
    }

    @Override
    public void close() throws ApfloatRuntimeException {
        this.data = null;
    }
}

