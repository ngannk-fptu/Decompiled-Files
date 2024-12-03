/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class IconMultiStateThreshold
extends Threshold
implements Duplicatable {
    public static final byte EQUALS_EXCLUDE = 0;
    public static final byte EQUALS_INCLUDE = 1;
    private byte equals;

    public IconMultiStateThreshold() {
        this.equals = 1;
    }

    public IconMultiStateThreshold(IconMultiStateThreshold other) {
        super(other);
        this.equals = other.equals;
    }

    public IconMultiStateThreshold(LittleEndianInput in) {
        super(in);
        this.equals = in.readByte();
        in.readInt();
    }

    public byte getEquals() {
        return this.equals;
    }

    public void setEquals(byte equals) {
        this.equals = equals;
    }

    @Override
    public int getDataLength() {
        return super.getDataLength() + 5;
    }

    @Override
    public IconMultiStateThreshold copy() {
        return new IconMultiStateThreshold(this);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        super.serialize(out);
        out.writeByte(this.equals);
        out.writeInt(0);
    }
}

