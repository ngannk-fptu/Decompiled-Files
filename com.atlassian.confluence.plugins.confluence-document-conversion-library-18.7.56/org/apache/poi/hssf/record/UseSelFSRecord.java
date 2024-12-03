/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class UseSelFSRecord
extends StandardRecord {
    public static final short sid = 352;
    private static final BitField useNaturalLanguageFormulasFlag = BitFieldFactory.getInstance(1);
    private int _options;

    private UseSelFSRecord(UseSelFSRecord other) {
        super(other);
        this._options = other._options;
    }

    private UseSelFSRecord(int options) {
        this._options = options;
    }

    public UseSelFSRecord(RecordInputStream in) {
        this(in.readUShort());
    }

    public UseSelFSRecord(boolean b) {
        this(0);
        this._options = useNaturalLanguageFormulasFlag.setBoolean(this._options, b);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._options);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 352;
    }

    @Override
    public UseSelFSRecord copy() {
        return new UseSelFSRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.USE_SEL_FS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", GenericRecordUtil.getBitsAsString(() -> this._options, new BitField[]{useNaturalLanguageFormulasFlag}, new String[]{"USE_NATURAL_LANGUAGE_FORMULAS_FLAG"}));
    }
}

