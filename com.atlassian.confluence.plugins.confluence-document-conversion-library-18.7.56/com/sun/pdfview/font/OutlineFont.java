/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.PDFGlyph;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;

public abstract class OutlineFont
extends PDFFont {
    private int firstChar = -1;
    private int lastChar = -1;
    private float[] widths;

    public OutlineFont(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, descriptor);
        PDFObject firstCharObj = fontObj.getDictRef("FirstChar");
        PDFObject lastCharObj = fontObj.getDictRef("LastChar");
        PDFObject widthArrayObj = fontObj.getDictRef("Widths");
        if (firstCharObj != null) {
            this.firstChar = firstCharObj.getIntValue();
        }
        if (lastCharObj != null) {
            this.lastChar = lastCharObj.getIntValue();
        }
        if (widthArrayObj != null) {
            PDFObject[] widthArray = widthArrayObj.getArray();
            this.widths = new float[widthArray.length];
            for (int i = 0; i < widthArray.length; ++i) {
                this.widths[i] = widthArray[i].getFloatValue() / (float)this.getDefaultWidth();
            }
        }
    }

    public int getFirstChar() {
        return this.firstChar;
    }

    public int getLastChar() {
        return this.lastChar;
    }

    public int getDefaultWidth() {
        return 1000;
    }

    public int getCharCount() {
        return this.getLastChar() - this.getFirstChar() + 1;
    }

    public float getWidth(char code, String name) {
        int idx = (code & 0xFF) - this.getFirstChar();
        if (idx < 0 || this.widths == null || idx >= this.widths.length) {
            if (this.getDescriptor() != null) {
                return this.getDescriptor().getMissingWidth();
            }
            return 0.0f;
        }
        return this.widths[idx];
    }

    @Override
    protected PDFGlyph getGlyph(char src, String name) {
        GeneralPath outline = null;
        float width = this.getWidth(src, name);
        if (name != null) {
            outline = this.getOutline(name, width);
        }
        if (outline == null) {
            outline = this.getOutline(src, width);
        }
        Point2D.Float advance = new Point2D.Float(width, 0.0f);
        return new PDFGlyph(src, name, outline, advance);
    }

    protected abstract GeneralPath getOutline(String var1, float var2);

    protected abstract GeneralPath getOutline(char var1, float var2);
}

