/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.TTFFont;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class CIDFontType2
extends TTFFont {
    private Map<Character, Float> widths = null;
    private Map<Character, Float> widthsVertical = null;
    private int defaultWidth = 1000;
    private int defaultWidthVertical = 1000;
    private ByteBuffer cidToGidMap;

    public CIDFontType2(String baseName, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseName, fontObj, descriptor);
        this.parseWidths(fontObj);
        PDFObject systemInfoObj = fontObj.getDictRef("CIDSystemInfo");
        PDFObject mapObj = fontObj.getDictRef("CIDToGIDMap");
        if (mapObj != null && mapObj.getType() == 7) {
            this.cidToGidMap = mapObj.getStreamBuffer();
        }
    }

    private void parseWidths(PDFObject fontObj) throws IOException {
        float value;
        Character key;
        int c;
        int i;
        PDFObject[] widthArray;
        PDFObject defaultWidthObj = fontObj.getDictRef("DW");
        if (defaultWidthObj != null) {
            this.defaultWidth = defaultWidthObj.getIntValue();
        }
        int entryIdx = 0;
        int first = 0;
        int last = 0;
        PDFObject widthObj = fontObj.getDictRef("W");
        if (widthObj != null) {
            this.widths = new HashMap<Character, Float>();
            widthArray = widthObj.getArray();
            for (i = 0; i < widthArray.length; ++i) {
                if (entryIdx == 0) {
                    first = widthArray[i].getIntValue();
                } else if (entryIdx == 1) {
                    if (widthArray[i].getType() == 5) {
                        PDFObject[] entries = widthArray[i].getArray();
                        for (c = 0; c < entries.length; ++c) {
                            key = new Character((char)(c + first));
                            value = entries[c].getIntValue();
                            this.widths.put(key, new Float(value));
                        }
                        entryIdx = -1;
                    } else {
                        last = widthArray[i].getIntValue();
                    }
                } else {
                    int value2 = widthArray[i].getIntValue();
                    for (c = first; c <= last; ++c) {
                        this.widths.put(new Character((char)c), new Float(value2));
                    }
                    entryIdx = -1;
                }
                ++entryIdx;
            }
        }
        if ((defaultWidthObj = fontObj.getDictRef("DW2")) != null) {
            this.defaultWidthVertical = defaultWidthObj.getIntValue();
        }
        if ((widthObj = fontObj.getDictRef("W2")) != null) {
            this.widthsVertical = new HashMap<Character, Float>();
            widthArray = widthObj.getArray();
            entryIdx = 0;
            first = 0;
            last = 0;
            for (i = 0; i < widthArray.length; ++i) {
                if (entryIdx == 0) {
                    first = widthArray[i].getIntValue();
                } else if (entryIdx == 1) {
                    if (widthArray[i].getType() == 5) {
                        PDFObject[] entries = widthArray[i].getArray();
                        for (c = 0; c < entries.length; ++c) {
                            key = new Character((char)(c + first));
                            value = entries[c].getIntValue();
                            this.widthsVertical.put(key, new Float(value));
                        }
                        entryIdx = -1;
                    } else {
                        last = widthArray[i].getIntValue();
                    }
                } else {
                    int value3 = widthArray[i].getIntValue();
                    for (c = first; c <= last; ++c) {
                        this.widthsVertical.put(new Character((char)c), new Float(value3));
                    }
                    entryIdx = -1;
                }
                ++entryIdx;
            }
        }
    }

    @Override
    public int getDefaultWidth() {
        return this.defaultWidth;
    }

    @Override
    public float getWidth(char code, String name) {
        if (this.widths == null) {
            return 1.0f;
        }
        Float w = this.widths.get(new Character(code));
        if (w == null) {
            return 1.0f;
        }
        return w.floatValue() / (float)this.getDefaultWidth();
    }

    public int getDefaultWidthVertical() {
        return this.defaultWidthVertical;
    }

    public float getWidthVertical(char code, String name) {
        if (this.widthsVertical == null) {
            return 1.0f;
        }
        Float w = this.widthsVertical.get(new Character(code));
        if (w == null) {
            return 1.0f;
        }
        return w.floatValue() / (float)this.getDefaultWidth();
    }

    @Override
    protected synchronized GeneralPath getOutline(char src, float width) {
        int glyphId = src & 0xFFFF;
        if (this.cidToGidMap != null) {
            glyphId = this.cidToGidMap.getChar(glyphId * 2);
        }
        return this.getOutline(glyphId, width);
    }
}

