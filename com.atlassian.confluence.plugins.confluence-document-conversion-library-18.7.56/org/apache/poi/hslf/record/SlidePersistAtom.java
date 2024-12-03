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
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class SlidePersistAtom
extends RecordAtom {
    private static final int MAX_RECORD_LENGTH = 32;
    private static final long _type = 1011L;
    private static final int HAS_SHAPES_OTHER_THAN_PLACEHOLDERS = 4;
    private static final int[] FLAGS_MASKS = new int[]{4};
    private static final String[] FLAGS_NAMES = new String[]{"HAS_SHAPES_OTHER_THAN_PLACEHOLDERS"};
    private final byte[] _header;
    private int refID;
    private int flags;
    private int numPlaceholderTexts;
    private int slideIdentifier;
    private byte[] reservedFields;

    public int getRefID() {
        return this.refID;
    }

    public int getSlideIdentifier() {
        return this.slideIdentifier;
    }

    public int getNumPlaceholderTexts() {
        return this.numPlaceholderTexts;
    }

    public boolean getHasShapesOtherThanPlaceholders() {
        return (this.flags & 4) != 0;
    }

    public void setRefID(int id) {
        this.refID = id;
    }

    public void setSlideIdentifier(int id) {
        this.slideIdentifier = id;
    }

    protected SlidePersistAtom(byte[] source, int start, int len) {
        if (len < 8) {
            len = 8;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.refID = LittleEndian.getInt(source, start + 8);
        this.flags = LittleEndian.getInt(source, start + 12);
        this.numPlaceholderTexts = LittleEndian.getInt(source, start + 16);
        this.slideIdentifier = LittleEndian.getInt(source, start + 20);
        this.reservedFields = IOUtils.safelyClone(source, start + 24, len - 24, 32);
    }

    public SlidePersistAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, 1011);
        LittleEndian.putInt(this._header, 4, 20);
        this.flags = 4;
        this.reservedFields = new byte[4];
    }

    @Override
    public long getRecordType() {
        return 1011L;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        SlidePersistAtom.writeLittleEndian(this.refID, out);
        SlidePersistAtom.writeLittleEndian(this.flags, out);
        SlidePersistAtom.writeLittleEndian(this.numPlaceholderTexts, out);
        SlidePersistAtom.writeLittleEndian(this.slideIdentifier, out);
        out.write(this.reservedFields);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("refID", this::getRefID, "flags", GenericRecordUtil.getBitsAsString(() -> this.flags, FLAGS_MASKS, FLAGS_NAMES), "numPlaceholderTexts", this::getNumPlaceholderTexts, "slideIdentifier", this::getSlideIdentifier);
    }
}

