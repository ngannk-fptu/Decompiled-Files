/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.draw;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.util.ArrayList;

public class VerticalPositionMark
implements DrawInterface,
Element {
    protected DrawInterface drawInterface = null;
    protected float offset = 0.0f;

    public VerticalPositionMark() {
    }

    public VerticalPositionMark(DrawInterface drawInterface, float offset) {
        this.drawInterface = drawInterface;
        this.offset = offset;
    }

    @Override
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        if (this.drawInterface != null) {
            this.drawInterface.draw(canvas, llx, lly, urx, ury, y + this.offset);
        }
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException e) {
            return false;
        }
    }

    @Override
    public int type() {
        return 55;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return false;
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> list = new ArrayList<Element>();
        list.add(new Chunk((DrawInterface)this, true));
        return list;
    }

    public DrawInterface getDrawInterface() {
        return this.drawInterface;
    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.drawInterface = drawInterface;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}

