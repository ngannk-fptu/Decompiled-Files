/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public class IntMemoryArrayAccess
extends ArrayAccess {
    private static final long serialVersionUID = -1137159053668908693L;
    private int[] data;

    public IntMemoryArrayAccess(int[] data, int offset, int length) {
        super(offset, length);
        this.data = data;
    }

    @Override
    public ArrayAccess subsequence(int offset, int length) {
        return new IntMemoryArrayAccess(this.data, this.getOffset() + offset, length);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public int[] getIntData() {
        return this.data;
    }

    @Override
    public void close() throws ApfloatRuntimeException {
        this.data = null;
    }
}

