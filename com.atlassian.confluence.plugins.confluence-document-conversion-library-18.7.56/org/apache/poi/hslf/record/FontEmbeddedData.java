/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.fonts.FontFacet;
import org.apache.poi.common.usermodel.fonts.FontHeader;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class FontEmbeddedData
extends RecordAtom
implements FontFacet {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 5000000;
    private static int MAX_RECORD_LENGTH = 5000000;
    private byte[] _header;
    private byte[] _data;
    private FontHeader fontHeader;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    FontEmbeddedData() {
        this._header = new byte[8];
        this._data = new byte[4];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    FontEmbeddedData(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, MAX_RECORD_LENGTH);
        if (this._data.length < 4) {
            throw new IllegalArgumentException("The length of the data for a ExObjListAtom must be at least 4 bytes, but was only " + this._data.length);
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.FontEmbeddedData.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    public void setFontData(byte[] fontData) {
        this.fontHeader = null;
        this._data = (byte[])fontData.clone();
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    public FontHeader getFontHeader() {
        if (this.fontHeader == null) {
            FontHeader h = new FontHeader();
            h.init(this._data, 0, this._data.length);
            this.fontHeader = h;
        }
        return this.fontHeader;
    }

    @Override
    public int getWeight() {
        return this.getFontHeader().getWeight();
    }

    @Override
    public boolean isItalic() {
        return this.getFontHeader().isItalic();
    }

    public String getTypeface() {
        return this.getFontHeader().getFamilyName();
    }

    @Override
    public Object getFontData() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("fontHeader", this::getFontHeader);
    }
}

