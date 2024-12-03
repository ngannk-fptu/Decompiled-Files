/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfLine;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class VerticalText {
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected ArrayList<PdfChunk> chunks = new ArrayList();
    protected PdfContentByte text;
    protected int alignment = 0;
    protected int currentChunkMarker = -1;
    protected PdfChunk currentStandbyChunk;
    protected String splittedChunkText;
    protected float leading;
    protected float startX;
    protected float startY;
    protected int maxLines;
    protected float height;

    public VerticalText(PdfContentByte text) {
        this.text = text;
    }

    public void addText(Phrase phrase) {
        for (Element o : phrase.getChunks()) {
            this.chunks.add(new PdfChunk((Chunk)o, null));
        }
    }

    public void addText(Chunk chunk) {
        this.chunks.add(new PdfChunk(chunk, null));
    }

    public void setVerticalLayout(float startX, float startY, float height, int maxLines, float leading) {
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.maxLines = maxLines;
        this.setLeading(leading);
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }

    public float getLeading() {
        return this.leading;
    }

    protected PdfLine createLine(float width) {
        if (this.chunks.isEmpty()) {
            return null;
        }
        this.splittedChunkText = null;
        this.currentStandbyChunk = null;
        PdfLine line = new PdfLine(0.0f, width, this.alignment, 0.0f);
        this.currentChunkMarker = 0;
        while (this.currentChunkMarker < this.chunks.size()) {
            PdfChunk original = this.chunks.get(this.currentChunkMarker);
            String total = original.toString();
            this.currentStandbyChunk = line.add(original);
            if (this.currentStandbyChunk != null) {
                this.splittedChunkText = original.toString();
                original.setValue(total);
                return line;
            }
            ++this.currentChunkMarker;
        }
        return line;
    }

    protected void shortenChunkArray() {
        if (this.currentChunkMarker < 0) {
            return;
        }
        if (this.currentChunkMarker >= this.chunks.size()) {
            this.chunks.clear();
            return;
        }
        PdfChunk split = this.chunks.get(this.currentChunkMarker);
        split.setValue(this.splittedChunkText);
        this.chunks.set(this.currentChunkMarker, this.currentStandbyChunk);
        if (this.currentChunkMarker > 0) {
            this.chunks.subList(0, this.currentChunkMarker).clear();
        }
    }

    public int go() {
        return this.go(false);
    }

    public int go(boolean simulate) {
        boolean dirty = false;
        PdfContentByte graphics = null;
        if (this.text != null) {
            graphics = this.text.getDuplicate();
        } else if (!simulate) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("verticaltext.go.with.simulate.eq.eq.false.and.text.eq.eq.null"));
        }
        int status = 0;
        while (true) {
            if (this.maxLines <= 0) {
                status = 2;
                if (!this.chunks.isEmpty()) break;
                status |= 1;
                break;
            }
            if (this.chunks.isEmpty()) {
                status = 1;
                break;
            }
            PdfLine line = this.createLine(this.height);
            if (!simulate && !dirty) {
                this.text.beginText();
                dirty = true;
            }
            this.shortenChunkArray();
            if (!simulate) {
                this.text.setTextMatrix(this.startX, this.startY - line.indentLeft());
                this.writeLine(line, this.text, graphics);
            }
            --this.maxLines;
            this.startX -= this.leading;
        }
        if (dirty) {
            this.text.endText();
            this.text.add(graphics);
        }
        return status;
    }

    void writeLine(PdfLine line, PdfContentByte text, PdfContentByte graphics) {
        PdfFont currentFont = null;
        Iterator j = line.iterator();
        while (j.hasNext()) {
            Color color;
            PdfChunk chunk = (PdfChunk)j.next();
            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            if ((color = chunk.color()) != null) {
                text.setColorFill(color);
            }
            text.showText(chunk.toString());
            if (color == null) continue;
            text.resetRGBColorFill();
        }
    }

    public void setOrigin(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public float getOriginX() {
        return this.startX;
    }

    public float getOriginY() {
        return this.startY;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getAlignment() {
        return this.alignment;
    }
}

