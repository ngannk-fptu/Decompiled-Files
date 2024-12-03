/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;

public class HeaderFooter
extends Rectangle {
    private boolean numbered;
    private Phrase before = null;
    private int pageN;
    private Phrase after = null;
    private int alignment;

    public HeaderFooter(Phrase before, Phrase after) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(3);
        this.setBorderWidth(1.0f);
        this.numbered = true;
        this.before = before;
        this.after = after;
    }

    public HeaderFooter(Phrase before, boolean numbered) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(3);
        this.setBorderWidth(1.0f);
        this.numbered = numbered;
        this.before = before;
    }

    public boolean isNumbered() {
        return this.numbered;
    }

    public Phrase getBefore() {
        return this.before;
    }

    public Phrase getAfter() {
        return this.after;
    }

    public void setPageNumber(int pageN) {
        this.pageN = pageN;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public Paragraph paragraph() {
        Paragraph paragraph = new Paragraph(this.before.getLeading());
        paragraph.add(this.before);
        if (this.numbered) {
            paragraph.addSpecial(new Chunk(String.valueOf(this.pageN), this.before.getFont()));
        }
        if (this.after != null) {
            paragraph.addSpecial(this.after);
        }
        paragraph.setAlignment(this.alignment);
        return paragraph;
    }

    public int alignment() {
        return this.alignment;
    }
}

