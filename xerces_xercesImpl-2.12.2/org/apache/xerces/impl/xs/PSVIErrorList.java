/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.util.AbstractList;
import org.apache.xerces.xs.StringList;

final class PSVIErrorList
extends AbstractList
implements StringList {
    private final String[] fArray;
    private final int fLength;
    private final int fOffset;

    public PSVIErrorList(String[] stringArray, boolean bl) {
        this.fArray = stringArray;
        this.fLength = this.fArray.length >> 1;
        this.fOffset = bl ? 0 : 1;
    }

    @Override
    public boolean contains(String string) {
        if (string == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[(i << 1) + this.fOffset] != null) continue;
                return true;
            }
        } else {
            for (int i = 0; i < this.fLength; ++i) {
                if (!string.equals(this.fArray[(i << 1) + this.fOffset])) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public String item(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[(n << 1) + this.fOffset];
    }

    public Object get(int n) {
        if (n >= 0 && n < this.fLength) {
            return this.fArray[(n << 1) + this.fOffset];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    @Override
    public int size() {
        return this.getLength();
    }
}

