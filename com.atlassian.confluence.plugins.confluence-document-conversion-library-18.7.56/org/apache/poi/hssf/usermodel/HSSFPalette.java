/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel;

import java.util.Locale;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.util.StringUtil;

public final class HSSFPalette {
    private PaletteRecord _palette;

    protected HSSFPalette(PaletteRecord palette) {
        this._palette = palette;
    }

    public HSSFColor getColor(short index) {
        if (index == HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex()) {
            return HSSFColor.HSSFColorPredefined.AUTOMATIC.getColor();
        }
        byte[] b = this._palette.getColor(index);
        return b == null ? null : new CustomColor(index, b);
    }

    public HSSFColor getColor(int index) {
        return this.getColor((short)index);
    }

    public HSSFColor findColor(byte red, byte green, byte blue) {
        byte[] b = this._palette.getColor(8);
        short i = 8;
        while (b != null) {
            if (b[0] == red && b[1] == green && b[2] == blue) {
                return new CustomColor(i, b);
            }
            i = (short)(i + 1);
            b = this._palette.getColor(i);
        }
        return null;
    }

    public HSSFColor findSimilarColor(byte red, byte green, byte blue) {
        return this.findSimilarColor(this.unsignedInt(red), this.unsignedInt(green), this.unsignedInt(blue));
    }

    public HSSFColor findSimilarColor(int red, int green, int blue) {
        HSSFColor result = null;
        int minColorDistance = Integer.MAX_VALUE;
        byte[] b = this._palette.getColor(8);
        short i = 8;
        while (b != null) {
            int colorDistance = Math.abs(red - this.unsignedInt(b[0])) + Math.abs(green - this.unsignedInt(b[1])) + Math.abs(blue - this.unsignedInt(b[2]));
            if (colorDistance < minColorDistance) {
                minColorDistance = colorDistance;
                result = this.getColor(i);
            }
            i = (short)(i + 1);
            b = this._palette.getColor(i);
        }
        return result;
    }

    private int unsignedInt(byte b) {
        return 0xFF & b;
    }

    public void setColorAtIndex(short index, byte red, byte green, byte blue) {
        this._palette.setColor(index, red, green, blue);
    }

    public HSSFColor addColor(byte red, byte green, byte blue) {
        byte[] b = this._palette.getColor(8);
        for (short i = 8; i < 64; i = (short)(i + 1)) {
            if (b == null) {
                this.setColorAtIndex(i, red, green, blue);
                return this.getColor(i);
            }
            b = this._palette.getColor(i);
        }
        throw new RuntimeException("Could not find free color index");
    }

    private static final class CustomColor
    extends HSSFColor {
        private short _byteOffset;
        private byte _red;
        private byte _green;
        private byte _blue;

        public CustomColor(short byteOffset, byte[] colors) {
            this(byteOffset, colors[0], colors[1], colors[2]);
        }

        private CustomColor(short byteOffset, byte red, byte green, byte blue) {
            this._byteOffset = byteOffset;
            this._red = red;
            this._green = green;
            this._blue = blue;
        }

        @Override
        public short getIndex() {
            return this._byteOffset;
        }

        @Override
        public short[] getTriplet() {
            return new short[]{(short)(this._red & 0xFF), (short)(this._green & 0xFF), (short)(this._blue & 0xFF)};
        }

        @Override
        public String getHexString() {
            return this.getGnumericPart(this._red) + ":" + this.getGnumericPart(this._green) + ":" + this.getGnumericPart(this._blue);
        }

        private String getGnumericPart(byte color) {
            StringBuilder s;
            if (color == 0) {
                s = new StringBuilder("0");
            } else {
                int c = color & 0xFF;
                s = new StringBuilder(Integer.toHexString(c = c << 8 | c).toUpperCase(Locale.ROOT));
                int need0count = 4 - s.length();
                if (need0count > 0) {
                    s.insert(0, StringUtil.repeat('0', need0count));
                }
            }
            return s.toString();
        }
    }
}

