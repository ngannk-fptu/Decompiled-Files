/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class SectionDescriptor
implements Duplicatable {
    private short fn;
    private int fcSepx;
    private short fnMpr;
    private int fcMpr;

    public SectionDescriptor() {
    }

    public SectionDescriptor(SectionDescriptor other) {
        this.fn = other.fn;
        this.fcSepx = other.fcSepx;
        this.fnMpr = other.fnMpr;
        this.fcMpr = other.fcMpr;
    }

    public SectionDescriptor(byte[] buf, int offset) {
        this.fn = LittleEndian.getShort(buf, offset);
        this.fcSepx = LittleEndian.getInt(buf, offset += 2);
        this.fnMpr = LittleEndian.getShort(buf, offset += 4);
        this.fcMpr = LittleEndian.getInt(buf, offset += 2);
    }

    public int getFc() {
        return this.fcSepx;
    }

    public void setFc(int fc) {
        this.fcSepx = fc;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SectionDescriptor)) {
            return false;
        }
        SectionDescriptor sed = (SectionDescriptor)o;
        return sed.fn == this.fn && sed.fnMpr == this.fnMpr;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public byte[] toByteArray() {
        int offset = 0;
        byte[] buf = new byte[12];
        LittleEndian.putShort(buf, offset, this.fn);
        LittleEndian.putInt(buf, offset += 2, this.fcSepx);
        LittleEndian.putShort(buf, offset += 4, this.fnMpr);
        LittleEndian.putInt(buf, offset += 2, this.fcMpr);
        return buf;
    }

    public String toString() {
        return "[SED] (fn: " + this.fn + "; fcSepx: " + this.fcSepx + "; fnMpr: " + this.fnMpr + "; fcMpr: " + this.fcMpr + ")";
    }

    @Override
    public SectionDescriptor copy() {
        return new SectionDescriptor(this);
    }
}

