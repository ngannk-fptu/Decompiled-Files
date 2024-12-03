/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class TBDAbstractType {
    private static final BitField jc = new BitField(7);
    private static final BitField tlc = new BitField(56);
    private static final BitField reserved = new BitField(192);
    protected byte field_1_value;

    protected TBDAbstractType() {
    }

    protected TBDAbstractType(TBDAbstractType other) {
        this.field_1_value = other.field_1_value;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_value = data[0 + offset];
    }

    public void serialize(byte[] data, int offset) {
        data[0 + offset] = this.field_1_value;
    }

    public static int getSize() {
        return 1;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[TBD]\n");
        builder.append("    .value                = ");
        builder.append(" (").append(this.getValue()).append(" )\n");
        builder.append("         .jc                       = ").append(this.getJc()).append('\n');
        builder.append("         .tlc                      = ").append(this.getTlc()).append('\n');
        builder.append("         .reserved                 = ").append(this.getReserved()).append('\n');
        builder.append("[/TBD]\n");
        return builder.toString();
    }

    @Internal
    public byte getValue() {
        return this.field_1_value;
    }

    @Internal
    public void setValue(byte field_1_value) {
        this.field_1_value = field_1_value;
    }

    @Internal
    public void setJc(byte value) {
        this.field_1_value = (byte)jc.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getJc() {
        return (byte)jc.getValue(this.field_1_value);
    }

    @Internal
    public void setTlc(byte value) {
        this.field_1_value = (byte)tlc.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getTlc() {
        return (byte)tlc.getValue(this.field_1_value);
    }

    @Internal
    public void setReserved(byte value) {
        this.field_1_value = (byte)reserved.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getReserved() {
        return (byte)reserved.getValue(this.field_1_value);
    }
}

