/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.ListItem;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfFont;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfLine {
    protected ArrayList<PdfChunk> line;
    protected float left;
    protected float width;
    protected int alignment;
    protected float height;
    protected Chunk listSymbol = null;
    protected float symbolIndent;
    protected boolean newlineSplit = false;
    protected float originalWidth;
    protected boolean isRTL = false;

    PdfLine(float left, float right, int alignment, float height) {
        this.left = left;
        this.originalWidth = this.width = right - left;
        this.alignment = alignment;
        this.height = height;
        this.line = new ArrayList();
    }

    PdfLine(float left, float originalWidth, float remainingWidth, int alignment, boolean newlineSplit, ArrayList<PdfChunk> line, boolean isRTL) {
        this.left = left;
        this.originalWidth = originalWidth;
        this.width = remainingWidth;
        this.alignment = alignment;
        this.line = line;
        this.newlineSplit = newlineSplit;
        this.isRTL = isRTL;
    }

    PdfChunk add(PdfChunk chunk) {
        if (chunk == null || chunk.toString().equals("")) {
            return null;
        }
        PdfChunk overflow = chunk.split(this.width);
        boolean bl = this.newlineSplit = chunk.isNewlineSplit() || overflow == null;
        if (chunk.isTab()) {
            Object[] tab = (Object[])chunk.getAttribute("TAB");
            float tabPosition = ((Float)tab[1]).floatValue();
            boolean newline = (Boolean)tab[2];
            if (newline && tabPosition < this.originalWidth - this.width) {
                return chunk;
            }
            this.width = this.originalWidth - tabPosition;
            chunk.adjustLeft(this.left);
            this.addToLine(chunk);
        } else if (chunk.length() > 0 || chunk.isImage()) {
            if (overflow != null) {
                chunk.trimLastSpace();
            }
            this.width -= chunk.width();
            this.addToLine(chunk);
        } else {
            if (this.line.size() < 1) {
                chunk = overflow;
                overflow = chunk.truncate(this.width);
                this.width -= chunk.width();
                if (chunk.length() > 0) {
                    this.addToLine(chunk);
                    return overflow;
                }
                if (overflow != null) {
                    this.addToLine(overflow);
                }
                return null;
            }
            this.width += this.line.get(this.line.size() - 1).trimLastSpace();
        }
        return overflow;
    }

    private void addToLine(PdfChunk chunk) {
        float f;
        if (chunk.changeLeading && chunk.isImage() && (f = chunk.getImage().getScaledHeight() + chunk.getImageOffsetY() + chunk.getImage().getBorderWidthTop()) > this.height) {
            this.height = f;
        }
        this.line.add(chunk);
    }

    public int size() {
        return this.line.size();
    }

    public Iterator iterator() {
        return this.line.iterator();
    }

    float height() {
        return this.height;
    }

    float indentLeft() {
        if (this.isRTL) {
            switch (this.alignment) {
                case 0: {
                    return this.left + this.width;
                }
                case 1: {
                    return this.left + this.width / 2.0f;
                }
            }
            return this.left;
        }
        if (this.getSeparatorCount() == 0) {
            switch (this.alignment) {
                case 2: {
                    return this.left + this.width;
                }
                case 1: {
                    return this.left + this.width / 2.0f;
                }
            }
        }
        return this.left;
    }

    public boolean hasToBeJustified() {
        return (this.alignment == 3 || this.alignment == 8) && this.width != 0.0f;
    }

    public void resetAlignment() {
        if (this.alignment == 3) {
            this.alignment = 0;
        }
    }

    void setExtraIndent(float extra) {
        this.left += extra;
        this.width -= extra;
    }

    float widthLeft() {
        return this.width;
    }

    int numberOfSpaces() {
        String string = this.toString();
        int length = string.length();
        int numberOfSpaces = 0;
        for (int i = 0; i < length; ++i) {
            if (string.charAt(i) != ' ') continue;
            ++numberOfSpaces;
        }
        return numberOfSpaces;
    }

    public void setListItem(ListItem listItem) {
        this.listSymbol = listItem.getListSymbol();
        this.symbolIndent = listItem.getIndentationLeft();
    }

    public Chunk listSymbol() {
        return this.listSymbol;
    }

    public float listIndent() {
        return this.symbolIndent;
    }

    public String toString() {
        StringBuilder tmp = new StringBuilder();
        for (PdfChunk o : this.line) {
            tmp.append(((Object)o).toString());
        }
        return tmp.toString();
    }

    public int GetLineLengthUtf32() {
        int total = 0;
        for (PdfChunk o : this.line) {
            total += o.lengthUtf32();
        }
        return total;
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit && this.alignment != 8;
    }

    public int getLastStrokeChunk() {
        PdfChunk chunk;
        int lastIdx;
        for (lastIdx = this.line.size() - 1; lastIdx >= 0 && !(chunk = this.line.get(lastIdx)).isStroked(); --lastIdx) {
        }
        return lastIdx;
    }

    public PdfChunk getChunk(int idx) {
        if (idx < 0 || idx >= this.line.size()) {
            return null;
        }
        return this.line.get(idx);
    }

    public float getOriginalWidth() {
        return this.originalWidth;
    }

    float[] getMaxSize() {
        float normal_leading = 0.0f;
        float image_leading = -10000.0f;
        for (PdfChunk o : this.line) {
            PdfChunk chunk = o;
            if (!chunk.isImage()) {
                normal_leading = Math.max(chunk.font().size(), normal_leading);
                continue;
            }
            image_leading = Math.max(chunk.getImage().getScaledHeight() + chunk.getImageOffsetY(), image_leading);
        }
        return new float[]{normal_leading, image_leading};
    }

    boolean isRTL() {
        return this.isRTL;
    }

    int getSeparatorCount() {
        int s = 0;
        for (PdfChunk o : this.line) {
            PdfChunk ck = o;
            if (ck.isTab()) {
                return 0;
            }
            if (!ck.isHorizontalSeparator()) continue;
            ++s;
        }
        return s;
    }

    public float getWidthCorrected(float charSpacing, float wordSpacing) {
        float total = 0.0f;
        Iterator<PdfChunk> iterator = this.line.iterator();
        while (iterator.hasNext()) {
            PdfChunk o;
            PdfChunk ck = o = iterator.next();
            total += ck.getWidthCorrected(charSpacing, wordSpacing);
        }
        return total;
    }

    public float getAscender() {
        float ascender = 0.0f;
        for (PdfChunk o : this.line) {
            PdfChunk ck = o;
            if (ck.isImage()) {
                ascender = Math.max(ascender, ck.getImage().getScaledHeight() + ck.getImageOffsetY());
                continue;
            }
            PdfFont font = ck.font();
            ascender = Math.max(ascender, font.getFont().getFontDescriptor(1, font.size()));
        }
        return ascender;
    }

    public float getDescender() {
        float descender = 0.0f;
        for (PdfChunk o : this.line) {
            PdfChunk ck = o;
            if (ck.isImage()) {
                descender = Math.min(descender, ck.getImageOffsetY());
                continue;
            }
            PdfFont font = ck.font();
            descender = Math.min(descender, font.getFont().getFontDescriptor(3, font.size()));
        }
        return descender;
    }
}

