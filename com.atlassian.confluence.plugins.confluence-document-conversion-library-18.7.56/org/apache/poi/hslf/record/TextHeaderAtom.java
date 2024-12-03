/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.ParentAwareRecord;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;

public final class TextHeaderAtom
extends RecordAtom
implements ParentAwareRecord {
    public static final long _type = RecordTypes.TextHeaderAtom.typeID;
    private byte[] _header;
    private RecordContainer parentRecord;
    private int textType;
    private int index = -1;

    public int getTextType() {
        return this.textType;
    }

    public void setTextType(int type) {
        this.textType = type;
    }

    public TextShape.TextPlaceholder getTextTypeEnum() {
        return TextShape.TextPlaceholder.fromNativeId(this.textType);
    }

    public void setTextTypeEnum(TextShape.TextPlaceholder placeholder) {
        this.textType = placeholder.nativeId;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public RecordContainer getParentRecord() {
        return this.parentRecord;
    }

    @Override
    public void setParentRecord(RecordContainer record) {
        this.parentRecord = record;
    }

    protected TextHeaderAtom(byte[] source, int start, int len) {
        if (len < 12) {
            len = 12;
            if (source.length - start < 12) {
                throw new HSLFException("Not enough data to form a TextHeaderAtom (always 12 bytes long) - found " + (source.length - start));
            }
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.textType = LittleEndian.getInt(source, start + 8);
    }

    public TextHeaderAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)_type);
        LittleEndian.putInt(this._header, 4, 4);
        this.textType = TextShape.TextPlaceholder.OTHER.nativeId;
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        TextHeaderAtom.writeLittleEndian(this.textType, out);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("index", this::getIndex, "textType", this::getTextTypeEnum);
    }
}

