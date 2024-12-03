/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class PaletteRecord
extends StandardRecord {
    public static final short sid = 146;
    public static final byte STANDARD_PALETTE_SIZE = 56;
    public static final short FIRST_COLOR_INDEX = 8;
    private static final int[] DEFAULT_COLORS = new int[]{0, 0xFFFFFF, 0xFF0000, 65280, 255, 0xFFFF00, 0xFF00FF, 65535, 0x800000, 32768, 128, 0x808000, 0x800080, 32896, 0xC0C0C0, 0x808080, 0x9999FF, 0x993366, 0xFFFFCC, 0xCCFFFF, 0x660066, 0xFF8080, 26316, 0xCCCCFF, 128, 0xFF00FF, 0xFFFF00, 65535, 0x800080, 0x800000, 32896, 255, 52479, 0xCCFFFF, 0xCCFFCC, 0xFFFF99, 0x99CCFF, 0xFF99CC, 0xCC99FF, 0xFFCC99, 0x3366FF, 0x33CCCC, 0x99CC00, 0xFFCC00, 0xFF9900, 0xFF6600, 0x666699, 0x969696, 13158, 0x339966, 13056, 0x333300, 0x993300, 0x993366, 0x333399, 0x333333};
    private final ArrayList<PColor> _colors = new ArrayList(100);

    public PaletteRecord() {
        Arrays.stream(DEFAULT_COLORS).mapToObj(PColor::new).forEach(this._colors::add);
    }

    public PaletteRecord(PaletteRecord other) {
        super(other);
        this._colors.ensureCapacity(other._colors.size());
        other._colors.stream().map(PColor::new).forEach(this._colors::add);
    }

    public PaletteRecord(RecordInputStream in) {
        int field_1_numcolors = in.readShort();
        this._colors.ensureCapacity(field_1_numcolors);
        for (int k = 0; k < field_1_numcolors; ++k) {
            this._colors.add(new PColor(in));
        }
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._colors.size());
        for (PColor color : this._colors) {
            color.serialize(out);
        }
    }

    @Override
    protected int getDataSize() {
        return 2 + this._colors.size() * 4;
    }

    @Override
    public short getSid() {
        return 146;
    }

    public byte[] getColor(int byteIndex) {
        int i = byteIndex - 8;
        if (i < 0 || i >= this._colors.size()) {
            return null;
        }
        return this._colors.get(i).getTriplet();
    }

    public void setColor(short byteIndex, byte red, byte green, byte blue) {
        int i = byteIndex - 8;
        if (i < 0 || i >= 56) {
            return;
        }
        while (this._colors.size() <= i) {
            this._colors.add(new PColor(0, 0, 0));
        }
        PColor custColor = new PColor(red, green, blue);
        this._colors.set(i, custColor);
    }

    @Override
    public PaletteRecord copy() {
        return new PaletteRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PALETTE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("colors", () -> this._colors);
    }

    private static final class PColor
    implements GenericRecord {
        public static final short ENCODED_SIZE = 4;
        private final int _red;
        private final int _green;
        private final int _blue;

        PColor(int rgb) {
            this._red = rgb >>> 16 & 0xFF;
            this._green = rgb >>> 8 & 0xFF;
            this._blue = rgb & 0xFF;
        }

        PColor(int red, int green, int blue) {
            this._red = red;
            this._green = green;
            this._blue = blue;
        }

        PColor(PColor other) {
            this._red = other._red;
            this._green = other._green;
            this._blue = other._blue;
        }

        PColor(RecordInputStream in) {
            this._red = in.readByte();
            this._green = in.readByte();
            this._blue = in.readByte();
            in.readByte();
        }

        byte[] getTriplet() {
            return new byte[]{(byte)this._red, (byte)this._green, (byte)this._blue};
        }

        void serialize(LittleEndianOutput out) {
            out.writeByte(this._red);
            out.writeByte(this._green);
            out.writeByte(this._blue);
            out.writeByte(0);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("red", () -> this._red & 0xFF, "green", () -> this._green & 0xFF, "blue", () -> this._blue & 0xFF);
        }
    }
}

