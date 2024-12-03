/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.pivottable;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ViewFieldsRecord
extends StandardRecord {
    public static final short sid = 177;
    private static final int STRING_NOT_PRESENT_LEN = 65535;
    private static final int BASE_SIZE = 10;
    private final int _sxaxis;
    private final int _cSub;
    private final int _grbitSub;
    private final int _cItm;
    private String _name;

    public ViewFieldsRecord(ViewFieldsRecord other) {
        super(other);
        this._sxaxis = other._sxaxis;
        this._cSub = other._cSub;
        this._grbitSub = other._grbitSub;
        this._cItm = other._cItm;
        this._name = other._name;
    }

    public ViewFieldsRecord(RecordInputStream in) {
        this._sxaxis = in.readShort();
        this._cSub = in.readShort();
        this._grbitSub = in.readShort();
        this._cItm = in.readShort();
        int cchName = in.readUShort();
        if (cchName != 65535) {
            byte flag = in.readByte();
            this._name = (flag & 1) != 0 ? in.readUnicodeLEString(cchName) : in.readCompressedUnicode(cchName);
        }
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this._sxaxis);
        out.writeShort(this._cSub);
        out.writeShort(this._grbitSub);
        out.writeShort(this._cItm);
        if (this._name != null) {
            StringUtil.writeUnicodeString(out, this._name);
        } else {
            out.writeShort(65535);
        }
    }

    @Override
    protected int getDataSize() {
        if (this._name == null) {
            return 10;
        }
        return 11 + this._name.length() * (StringUtil.hasMultibyte(this._name) ? 2 : 1);
    }

    @Override
    public short getSid() {
        return 177;
    }

    @Override
    public ViewFieldsRecord copy() {
        return new ViewFieldsRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.VIEW_FIELDS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("sxaxis", () -> this._sxaxis, "cSub", () -> this._cSub, "grbitSub", () -> this._grbitSub, "cItm", () -> this._cItm, "name", () -> this._name);
    }

    private static enum Axis {
        NO_AXIS(0),
        ROW(1),
        COLUMN(2),
        PAGE(4),
        DATA(8);

        final int id;

        private Axis(int id) {
            this.id = id;
        }
    }
}

