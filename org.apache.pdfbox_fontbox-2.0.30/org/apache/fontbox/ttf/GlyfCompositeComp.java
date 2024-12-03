/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.TTFDataStream;

public class GlyfCompositeComp {
    protected static final short ARG_1_AND_2_ARE_WORDS = 1;
    protected static final short ARGS_ARE_XY_VALUES = 2;
    protected static final short ROUND_XY_TO_GRID = 4;
    protected static final short WE_HAVE_A_SCALE = 8;
    protected static final short MORE_COMPONENTS = 32;
    protected static final short WE_HAVE_AN_X_AND_Y_SCALE = 64;
    protected static final short WE_HAVE_A_TWO_BY_TWO = 128;
    protected static final short WE_HAVE_INSTRUCTIONS = 256;
    protected static final short USE_MY_METRICS = 512;
    private int firstIndex;
    private int firstContour;
    private final short argument1;
    private final short argument2;
    private final short flags;
    private final int glyphIndex;
    private double xscale = 1.0;
    private double yscale = 1.0;
    private double scale01 = 0.0;
    private double scale10 = 0.0;
    private int xtranslate = 0;
    private int ytranslate = 0;
    private int point1 = 0;
    private int point2 = 0;

    GlyfCompositeComp(TTFDataStream bais) throws IOException {
        this.flags = bais.readSignedShort();
        this.glyphIndex = bais.readUnsignedShort();
        if ((this.flags & 1) != 0) {
            this.argument1 = bais.readSignedShort();
            this.argument2 = bais.readSignedShort();
        } else {
            this.argument1 = (short)bais.readSignedByte();
            this.argument2 = (short)bais.readSignedByte();
        }
        if ((this.flags & 2) != 0) {
            this.xtranslate = this.argument1;
            this.ytranslate = this.argument2;
        } else {
            this.point1 = this.argument1;
            this.point2 = this.argument2;
        }
        if ((this.flags & 8) != 0) {
            short i = bais.readSignedShort();
            this.xscale = this.yscale = (double)i / 16384.0;
        } else if ((this.flags & 0x40) != 0) {
            short i = bais.readSignedShort();
            this.xscale = (double)i / 16384.0;
            i = bais.readSignedShort();
            this.yscale = (double)i / 16384.0;
        } else if ((this.flags & 0x80) != 0) {
            short i = bais.readSignedShort();
            this.xscale = (double)i / 16384.0;
            i = bais.readSignedShort();
            this.scale01 = (double)i / 16384.0;
            i = bais.readSignedShort();
            this.scale10 = (double)i / 16384.0;
            i = bais.readSignedShort();
            this.yscale = (double)i / 16384.0;
        }
    }

    public void setFirstIndex(int idx) {
        this.firstIndex = idx;
    }

    public int getFirstIndex() {
        return this.firstIndex;
    }

    public void setFirstContour(int idx) {
        this.firstContour = idx;
    }

    public int getFirstContour() {
        return this.firstContour;
    }

    public short getArgument1() {
        return this.argument1;
    }

    public short getArgument2() {
        return this.argument2;
    }

    public short getFlags() {
        return this.flags;
    }

    public int getGlyphIndex() {
        return this.glyphIndex;
    }

    public double getScale01() {
        return this.scale01;
    }

    public double getScale10() {
        return this.scale10;
    }

    public double getXScale() {
        return this.xscale;
    }

    public double getYScale() {
        return this.yscale;
    }

    public int getXTranslate() {
        return this.xtranslate;
    }

    public int getYTranslate() {
        return this.ytranslate;
    }

    public int scaleX(int x, int y) {
        return Math.round((float)((double)x * this.xscale + (double)y * this.scale10));
    }

    public int scaleY(int x, int y) {
        return Math.round((float)((double)x * this.scale01 + (double)y * this.yscale));
    }
}

