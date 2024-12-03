/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class Colorref
implements Duplicatable {
    private int value;

    public static Colorref valueOfIco(int ico) {
        switch (ico) {
            case 1: {
                return new Colorref(0);
            }
            case 2: {
                return new Colorref(0xFF0000);
            }
            case 3: {
                return new Colorref(0xFFFF00);
            }
            case 4: {
                return new Colorref(65280);
            }
            case 5: {
                return new Colorref(0xFF00FF);
            }
            case 6: {
                return new Colorref(255);
            }
            case 7: {
                return new Colorref(65535);
            }
            case 8: {
                return new Colorref(0xFFFFFF);
            }
            case 9: {
                return new Colorref(0x8B0000);
            }
            case 10: {
                return new Colorref(0x8B8B00);
            }
            case 11: {
                return new Colorref(25600);
            }
            case 12: {
                return new Colorref(0x8B008B);
            }
            case 13: {
                return new Colorref(139);
            }
            case 14: {
                return new Colorref(52479);
            }
            case 15: {
                return new Colorref(0xA9A9A9);
            }
            case 16: {
                return new Colorref(0xC0C0C0);
            }
        }
        return new Colorref(0);
    }

    public Colorref() {
        this.value = -1;
    }

    public Colorref(Colorref other) {
        this.value = other.value;
    }

    public Colorref(byte[] data, int offset) {
        this.value = LittleEndian.getInt(data, offset);
    }

    public Colorref(int value) {
        this.value = value;
    }

    @Override
    public Colorref copy() {
        return new Colorref(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Colorref other = (Colorref)obj;
        return this.value == other.value;
    }

    public int getValue() {
        return this.value;
    }

    public int hashCode() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value == -1;
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, offset, this.value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public byte[] toByteArray() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Structure state (EMPTY) is not good for serialization");
        }
        byte[] bs = new byte[4];
        this.serialize(bs, 0);
        return bs;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[COLORREF] EMPTY";
        }
        return "[COLORREF] 0x" + Integer.toHexString(this.value);
    }
}

