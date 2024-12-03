/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.Phrase;
import java.util.ArrayList;

public class Paragraph
extends Phrase {
    private static final long serialVersionUID = 7852314969733375514L;
    protected int alignment = -1;
    protected float multipliedLeading = 0.0f;
    protected float indentationLeft;
    protected float indentationRight;
    private float firstLineIndent = 0.0f;
    protected float spacingBefore;
    protected float spacingAfter;
    private float extraParagraphSpace = 0.0f;
    protected boolean keeptogether = false;

    public Paragraph() {
    }

    public Paragraph(float leading) {
        super(leading);
    }

    public Paragraph(Chunk chunk) {
        super(chunk);
    }

    public Paragraph(float leading, Chunk chunk) {
        super(leading, chunk);
    }

    public Paragraph(String string) {
        super(string);
    }

    public Paragraph(String string, Font font) {
        super(string, font);
    }

    public Paragraph(float leading, String string) {
        super(leading, string);
    }

    public Paragraph(float leading, String string, Font font) {
        super(leading, string, font);
    }

    public Paragraph(Phrase phrase) {
        super(phrase);
        if (phrase instanceof Paragraph) {
            Paragraph p = (Paragraph)phrase;
            this.setAlignment(p.alignment);
            this.setLeading(phrase.getLeading(), p.multipliedLeading);
            this.setIndentationLeft(p.getIndentationLeft());
            this.setIndentationRight(p.getIndentationRight());
            this.setFirstLineIndent(p.getFirstLineIndent());
            this.setSpacingAfter(p.spacingAfter());
            this.setSpacingBefore(p.spacingBefore());
            this.setExtraParagraphSpace(p.getExtraParagraphSpace());
        }
    }

    @Override
    public int type() {
        return 12;
    }

    @Override
    public boolean add(Element o) {
        if (o instanceof List) {
            List list = (List)o;
            list.setIndentationLeft(list.getIndentationLeft() + this.indentationLeft);
            list.setIndentationRight(this.indentationRight);
            return super.add(list);
        }
        if (o instanceof Image) {
            super.addSpecial(o);
            return true;
        }
        if (o instanceof Paragraph) {
            super.add(o);
            ArrayList<Element> chunks = this.getChunks();
            if (!chunks.isEmpty()) {
                Chunk tmp = (Chunk)chunks.get(chunks.size() - 1);
                super.add(new Chunk("\n", tmp.getFont()));
            } else {
                super.add(Chunk.NEWLINE);
            }
            return true;
        }
        return super.add(o);
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public void setAlignment(String alignment) {
        if ("Center".equalsIgnoreCase(alignment)) {
            this.alignment = 1;
            return;
        }
        if ("Right".equalsIgnoreCase(alignment)) {
            this.alignment = 2;
            return;
        }
        if ("Justify".equalsIgnoreCase(alignment)) {
            this.alignment = 3;
            return;
        }
        if ("JustifyAll".equalsIgnoreCase(alignment)) {
            this.alignment = 8;
            return;
        }
        this.alignment = 0;
    }

    @Override
    public void setLeading(float fixedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = 0.0f;
    }

    public void setMultipliedLeading(float multipliedLeading) {
        this.leading = 0.0f;
        this.multipliedLeading = multipliedLeading;
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }

    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public void setKeepTogether(boolean keeptogether) {
        this.keeptogether = keeptogether;
    }

    public boolean getKeepTogether() {
        return this.keeptogether;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public float getTotalLeading() {
        float m;
        float f = m = this.font == null ? 12.0f * this.multipliedLeading : this.font.getCalculatedLeading(this.multipliedLeading);
        if (m > 0.0f && !this.hasLeading()) {
            return m;
        }
        return this.getLeading() + m;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public float getFirstLineIndent() {
        return this.firstLineIndent;
    }

    public float getSpacingBefore() {
        return this.spacingBefore;
    }

    public float getSpacingAfter() {
        return this.spacingAfter;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }

    public float spacingBefore() {
        return this.getSpacingBefore();
    }

    public float spacingAfter() {
        return this.spacingAfter;
    }
}

