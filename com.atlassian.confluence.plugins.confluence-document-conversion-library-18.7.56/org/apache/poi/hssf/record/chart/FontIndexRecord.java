/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class FontIndexRecord
extends StandardRecord {
    public static final short sid = 4134;
    private short field_1_fontIndex;

    public FontIndexRecord() {
    }

    public FontIndexRecord(FontIndexRecord other) {
        super(other);
        this.field_1_fontIndex = other.field_1_fontIndex;
    }

    public FontIndexRecord(RecordInputStream in) {
        this.field_1_fontIndex = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_fontIndex);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4134;
    }

    @Override
    public FontIndexRecord copy() {
        return new FontIndexRecord(this);
    }

    public short getFontIndex() {
        return this.field_1_fontIndex;
    }

    public void setFontIndex(short field_1_fontIndex) {
        this.field_1_fontIndex = field_1_fontIndex;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FONT_INDEX;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("fontIdex", this::getFontIndex);
    }
}

