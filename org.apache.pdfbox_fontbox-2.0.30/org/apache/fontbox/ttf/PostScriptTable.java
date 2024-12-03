/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.ttf.WGL4Names;

public class PostScriptTable
extends TTFTable {
    private static final Log LOG = LogFactory.getLog(PostScriptTable.class);
    private float formatType;
    private float italicAngle;
    private short underlinePosition;
    private short underlineThickness;
    private long isFixedPitch;
    private long minMemType42;
    private long maxMemType42;
    private long mimMemType1;
    private long maxMemType1;
    private String[] glyphNames = null;
    public static final String TAG = "post";

    PostScriptTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.formatType = data.read32Fixed();
        this.italicAngle = data.read32Fixed();
        this.underlinePosition = data.readSignedShort();
        this.underlineThickness = data.readSignedShort();
        this.isFixedPitch = data.readUnsignedInt();
        this.minMemType42 = data.readUnsignedInt();
        this.maxMemType42 = data.readUnsignedInt();
        this.mimMemType1 = data.readUnsignedInt();
        this.maxMemType1 = data.readUnsignedInt();
        if (this.formatType == 1.0f) {
            this.glyphNames = new String[258];
            System.arraycopy(WGL4Names.MAC_GLYPH_NAMES, 0, this.glyphNames, 0, 258);
        } else if (this.formatType == 2.0f) {
            int i;
            int numGlyphs = data.readUnsignedShort();
            int[] glyphNameIndex = new int[numGlyphs];
            this.glyphNames = new String[numGlyphs];
            int maxIndex = Integer.MIN_VALUE;
            for (int i2 = 0; i2 < numGlyphs; ++i2) {
                int index;
                glyphNameIndex[i2] = index = data.readUnsignedShort();
                if (index > Short.MAX_VALUE) continue;
                maxIndex = Math.max(maxIndex, index);
            }
            String[] nameArray = null;
            if (maxIndex >= 258) {
                nameArray = new String[maxIndex - 258 + 1];
                for (i = 0; i < nameArray.length; ++i) {
                    int numberOfChars = data.readUnsignedByte();
                    try {
                        nameArray[i] = data.readString(numberOfChars);
                        continue;
                    }
                    catch (IOException ex) {
                        LOG.warn((Object)("Error reading names in PostScript table at entry " + i + " of " + nameArray.length + ", setting remaining entries to .notdef"), (Throwable)ex);
                        for (int j = i; j < nameArray.length; ++j) {
                            nameArray[j] = ".notdef";
                        }
                        break;
                    }
                }
            }
            for (i = 0; i < numGlyphs; ++i) {
                int index = glyphNameIndex[i];
                this.glyphNames[i] = index >= 0 && index < 258 ? WGL4Names.MAC_GLYPH_NAMES[index] : (index >= 258 && index <= Short.MAX_VALUE ? nameArray[index - 258] : ".undefined");
            }
        } else if (this.formatType == 2.5f) {
            int i;
            int[] glyphNameIndex = new int[ttf.getNumberOfGlyphs()];
            for (i = 0; i < glyphNameIndex.length; ++i) {
                int offset = data.readSignedByte();
                glyphNameIndex[i] = i + 1 + offset;
            }
            this.glyphNames = new String[glyphNameIndex.length];
            for (i = 0; i < this.glyphNames.length; ++i) {
                int index = glyphNameIndex[i];
                if (index >= 0 && index < 258) {
                    String name = WGL4Names.MAC_GLYPH_NAMES[index];
                    if (name == null) continue;
                    this.glyphNames[i] = name;
                    continue;
                }
                LOG.debug((Object)("incorrect glyph name index " + index + ", valid numbers 0.." + 258));
            }
        } else if (this.formatType == 3.0f) {
            LOG.debug((Object)("No PostScript name information is provided for the font " + this.font.getName()));
        }
        this.initialized = true;
    }

    public float getFormatType() {
        return this.formatType;
    }

    public void setFormatType(float formatTypeValue) {
        this.formatType = formatTypeValue;
    }

    public long getIsFixedPitch() {
        return this.isFixedPitch;
    }

    public void setIsFixedPitch(long isFixedPitchValue) {
        this.isFixedPitch = isFixedPitchValue;
    }

    public float getItalicAngle() {
        return this.italicAngle;
    }

    public void setItalicAngle(float italicAngleValue) {
        this.italicAngle = italicAngleValue;
    }

    public long getMaxMemType1() {
        return this.maxMemType1;
    }

    public void setMaxMemType1(long maxMemType1Value) {
        this.maxMemType1 = maxMemType1Value;
    }

    public long getMaxMemType42() {
        return this.maxMemType42;
    }

    public void setMaxMemType42(long maxMemType42Value) {
        this.maxMemType42 = maxMemType42Value;
    }

    public long getMinMemType1() {
        return this.mimMemType1;
    }

    public void setMimMemType1(long mimMemType1Value) {
        this.mimMemType1 = mimMemType1Value;
    }

    public long getMinMemType42() {
        return this.minMemType42;
    }

    public void setMinMemType42(long minMemType42Value) {
        this.minMemType42 = minMemType42Value;
    }

    public short getUnderlinePosition() {
        return this.underlinePosition;
    }

    public void setUnderlinePosition(short underlinePositionValue) {
        this.underlinePosition = underlinePositionValue;
    }

    public short getUnderlineThickness() {
        return this.underlineThickness;
    }

    public void setUnderlineThickness(short underlineThicknessValue) {
        this.underlineThickness = underlineThicknessValue;
    }

    public String[] getGlyphNames() {
        return this.glyphNames;
    }

    public void setGlyphNames(String[] glyphNamesValue) {
        this.glyphNames = glyphNamesValue;
    }

    public String getName(int gid) {
        if (gid < 0 || this.glyphNames == null || gid >= this.glyphNames.length) {
            return null;
        }
        return this.glyphNames[gid];
    }
}

