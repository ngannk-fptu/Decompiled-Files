/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class HeadersFootersAtom
extends RecordAtom {
    public static final int fHasDate = 1;
    public static final int fHasTodayDate = 2;
    public static final int fHasUserDate = 4;
    public static final int fHasSlideNumber = 8;
    public static final int fHasHeader = 16;
    public static final int fHasFooter = 32;
    private static final int[] PLACEHOLDER_MASKS = new int[]{1, 2, 4, 8, 16, 32};
    private static final String[] PLACEHOLDER_NAMES = new String[]{"DATE", "TODAY_DATE", "USER_DATE", "SLIDE_NUMBER", "HEADER", "FOOTER"};
    private final byte[] _header;
    private final byte[] _recdata;

    HeadersFootersAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._recdata = IOUtils.safelyClone(source, start + 8, len - 8, HeadersFootersAtom.getMaxRecordLength());
    }

    public HeadersFootersAtom() {
        this._recdata = new byte[4];
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._recdata.length);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.HeadersFootersAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._recdata);
    }

    public int getFormatId() {
        return LittleEndian.getShort(this._recdata, 0);
    }

    public void setFormatId(int formatId) {
        LittleEndian.putUShort(this._recdata, 0, formatId);
    }

    public int getMask() {
        return LittleEndian.getShort(this._recdata, 2);
    }

    public void setMask(int mask) {
        LittleEndian.putUShort(this._recdata, 2, mask);
    }

    public boolean getFlag(int bit) {
        return (this.getMask() & bit) != 0;
    }

    public void setFlag(int bit, boolean value) {
        int mask = this.getMask();
        mask = value ? (mask |= bit) : (mask &= ~bit);
        this.setMask(mask);
    }

    public String toString() {
        return "HeadersFootersAtom\n\tFormatId: " + this.getFormatId() + "\n\tMask    : " + this.getMask() + "\n\t  fHasDate        : " + this.getFlag(1) + "\n\t  fHasTodayDate   : " + this.getFlag(2) + "\n\t  fHasUserDate    : " + this.getFlag(4) + "\n\t  fHasSlideNumber : " + this.getFlag(8) + "\n\t  fHasHeader      : " + this.getFlag(16) + "\n\t  fHasFooter      : " + this.getFlag(32) + "\n";
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("formatIndex", this::getFormatId, "flags", GenericRecordUtil.getBitsAsString(this::getMask, PLACEHOLDER_MASKS, PLACEHOLDER_NAMES));
    }
}

