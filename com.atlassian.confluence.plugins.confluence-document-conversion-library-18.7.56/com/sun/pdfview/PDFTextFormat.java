/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.font.PDFGlyph;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

public class PDFTextFormat
implements Cloneable {
    private float tc = 0.0f;
    private float tw = 0.0f;
    private float th = 1.0f;
    private float tl = 0.0f;
    private float tr = 0.0f;
    private int tm = 2;
    private float tk = 0.0f;
    private AffineTransform cur;
    private AffineTransform line;
    private PDFFont font;
    private float fsize = 1.0f;
    private boolean inuse = false;
    private StringBuffer word = new StringBuffer();
    private Point2D.Float wordStart;
    private Point2D.Float prevEnd;

    public PDFTextFormat() {
        this.cur = new AffineTransform();
        this.line = new AffineTransform();
        this.wordStart = new Point2D.Float(-100.0f, -100.0f);
        this.prevEnd = new Point2D.Float(-100.0f, -100.0f);
    }

    public void reset() {
        this.cur.setToIdentity();
        this.line.setToIdentity();
        this.inuse = true;
        this.word.setLength(0);
    }

    public void end() {
        this.inuse = false;
    }

    public float getCharSpacing() {
        return this.tc;
    }

    public void setCharSpacing(float spc) {
        this.tc = spc;
    }

    public float getWordSpacing() {
        return this.tw;
    }

    public void setWordSpacing(float spc) {
        this.tw = spc;
    }

    public float getHorizontalScale() {
        return this.th * 100.0f;
    }

    public void setHorizontalScale(float scl) {
        this.th = scl / 100.0f;
    }

    public float getLeading() {
        return this.tl;
    }

    public void setLeading(float spc) {
        this.tl = spc;
    }

    public PDFFont getFont() {
        return this.font;
    }

    public float getFontSize() {
        return this.fsize;
    }

    public void setFont(PDFFont f, float size) {
        this.font = f;
        this.fsize = size;
    }

    public int getMode() {
        return this.tm;
    }

    public void setMode(int m) {
        int mode = 0;
        if ((m & 1) == 0) {
            mode |= 2;
        }
        if ((m & 4) != 0) {
            mode |= 4;
        }
        if ((m & 1 ^ (m & 2) >> 1) != 0) {
            mode |= 1;
        }
        this.tm = mode;
    }

    public void setTextFormatMode(int mode) {
        this.tm = mode;
    }

    public float getRise() {
        return this.tr;
    }

    public void setRise(float spc) {
        this.tr = spc;
    }

    public void carriageReturn() {
        this.carriageReturn(0.0f, -this.tl);
    }

    public void carriageReturn(float x, float y) {
        this.line.concatenate(AffineTransform.getTranslateInstance(x, y));
        this.cur.setTransform(this.line);
    }

    public AffineTransform getTransform() {
        return this.cur;
    }

    public void setMatrix(float[] matrix) {
        this.line = new AffineTransform(matrix);
        this.cur.setTransform(this.line);
    }

    public void doText(PDFPage cmds, String text) {
        Point2D.Float zero = new Point2D.Float();
        AffineTransform scale = new AffineTransform(this.fsize, 0.0f, 0.0f, this.fsize * this.th, 0.0f, this.tr);
        AffineTransform at = new AffineTransform();
        List<PDFGlyph> l = this.font.getGlyphs(text);
        for (PDFGlyph glyph : l) {
            at.setTransform(this.cur);
            at.concatenate(scale);
            Point2D advance = glyph.addCommands(cmds, at, this.tm);
            double advanceX = advance.getX() * (double)this.fsize + (double)this.tc;
            if (glyph.getChar() == ' ') {
                advanceX += (double)this.tw;
            }
            this.cur.translate(advanceX *= (double)this.th, advance.getY());
        }
        this.cur.transform(zero, this.prevEnd);
    }

    public void doText(PDFPage cmds, Object[] ary) throws PDFParseException {
        for (int i = 0; i < ary.length; ++i) {
            if (ary[i] instanceof String) {
                this.doText(cmds, (String)ary[i]);
                continue;
            }
            if (ary[i] instanceof Double) {
                float val = ((Double)ary[i]).floatValue() / 1000.0f;
                this.cur.translate(-val * this.fsize * this.th, 0.0);
                continue;
            }
            throw new PDFParseException("Bad element in TJ array");
        }
    }

    public void flush() {
    }

    public Object clone() {
        PDFTextFormat newFormat = new PDFTextFormat();
        newFormat.setCharSpacing(this.getCharSpacing());
        newFormat.setWordSpacing(this.getWordSpacing());
        newFormat.setHorizontalScale(this.getHorizontalScale());
        newFormat.setLeading(this.getLeading());
        newFormat.setTextFormatMode(this.getMode());
        newFormat.setRise(this.getRise());
        newFormat.setFont(this.getFont(), this.getFontSize());
        return newFormat;
    }
}

