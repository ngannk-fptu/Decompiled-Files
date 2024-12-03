/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.TCAbstractType;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.util.LittleEndian;

public final class TableCellDescriptor
extends TCAbstractType
implements Duplicatable {
    public static final int SIZE = 20;

    public TableCellDescriptor() {
    }

    public TableCellDescriptor(TableCellDescriptor other) {
        super(other);
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_rgf = LittleEndian.getShort(data, 0 + offset);
        this.field_2_wWidth = LittleEndian.getShort(data, 2 + offset);
        this.setBrcTop(new BorderCode(data, 4 + offset));
        this.setBrcLeft(new BorderCode(data, 8 + offset));
        this.setBrcBottom(new BorderCode(data, 12 + offset));
        this.setBrcRight(new BorderCode(data, 16 + offset));
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_rgf);
        LittleEndian.putShort(data, 2 + offset, this.field_2_wWidth);
        this.getBrcTop().serialize(data, 4 + offset);
        this.getBrcLeft().serialize(data, 8 + offset);
        this.getBrcBottom().serialize(data, 12 + offset);
        this.getBrcRight().serialize(data, 16 + offset);
    }

    @Override
    public TableCellDescriptor copy() {
        return new TableCellDescriptor(this);
    }

    public static TableCellDescriptor convertBytesToTC(byte[] buf, int offset) {
        TableCellDescriptor tc = new TableCellDescriptor();
        tc.fillFields(buf, offset);
        return tc;
    }
}

