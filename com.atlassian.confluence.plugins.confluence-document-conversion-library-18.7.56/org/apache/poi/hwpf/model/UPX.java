/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.util.Internal;

@Internal
public final class UPX {
    private byte[] _upx;

    public UPX(byte[] upx) {
        this._upx = upx;
    }

    public byte[] getUPX() {
        return this._upx;
    }

    public int size() {
        return this._upx.length;
    }

    public boolean equals(Object o) {
        if (!(o instanceof UPX)) {
            return false;
        }
        UPX upx = (UPX)o;
        return Arrays.equals(this._upx, upx._upx);
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public String toString() {
        return "[UPX] " + Arrays.toString(this._upx);
    }
}

