/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public final class FontEntityAtom
extends RecordAtom {
    private static final int[] FLAGS_MASKS = new int[]{1, 256, 512, 1024, 2048};
    private static final String[] FLAGS_NAMES = new String[]{"EMBED_SUBSETTED", "RASTER_FONT", "DEVICE_FONT", "TRUETYPE_FONT", "NO_FONT_SUBSTITUTION"};
    private final byte[] _header;
    private byte[] _recdata;

    FontEntityAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._recdata = IOUtils.safelyClone(source, start + 8, len - 8, FontEntityAtom.getMaxRecordLength());
    }

    public FontEntityAtom() {
        this._recdata = new byte[68];
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._recdata.length);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.FontEntityAtom.typeID;
    }

    public String getFontName() {
        int maxLen = Math.min(this._recdata.length, 64) / 2;
        return StringUtil.getFromUnicodeLE0Terminated(this._recdata, 0, maxLen);
    }

    public void setFontName(String name) {
        int nameLen = name.length() + (name.endsWith("\u0000") ? 0 : 1);
        if (nameLen > 32) {
            throw new HSLFException("The length of the font name, including null termination, must not exceed 32 characters");
        }
        byte[] bytes = StringUtil.getToUnicodeLE(name);
        System.arraycopy(bytes, 0, this._recdata, 0, bytes.length);
        Arrays.fill(this._recdata, bytes.length, 64, (byte)0);
    }

    public void setFontIndex(int idx) {
        LittleEndian.putShort(this._header, 0, (short)idx);
    }

    public int getFontIndex() {
        return LittleEndian.getShort(this._header, 0) >> 4;
    }

    public void setCharSet(int charset) {
        this._recdata[64] = (byte)charset;
    }

    public int getCharSet() {
        return this._recdata[64];
    }

    public void setFontFlags(int flags) {
        this._recdata[65] = (byte)flags;
    }

    public int getFontFlags() {
        return this._recdata[65];
    }

    public void setFontType(int type) {
        this._recdata[66] = (byte)type;
    }

    public int getFontType() {
        return this._recdata[66];
    }

    public void setPitchAndFamily(int val) {
        this._recdata[67] = (byte)val;
    }

    public int getPitchAndFamily() {
        return this._recdata[67];
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._recdata);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("fontName", this::getFontName, "fontIndex", this::getFontIndex, "charset", this::getCharSet, "fontFlags", GenericRecordUtil.getBitsAsString(this::getFontFlags, FLAGS_MASKS, FLAGS_NAMES), "fontPitch", () -> FontPitch.valueOfPitchFamily((byte)this.getPitchAndFamily()), "fontFamily", () -> FontFamily.valueOfPitchFamily((byte)this.getPitchAndFamily()));
    }
}

