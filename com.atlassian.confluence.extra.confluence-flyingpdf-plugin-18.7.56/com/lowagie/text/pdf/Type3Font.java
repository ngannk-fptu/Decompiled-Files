/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.Type3Glyph;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Type3Font
extends BaseFont {
    private boolean[] usedSlot;
    private IntHashtable widths3 = new IntHashtable();
    private Map<Integer, Type3Glyph> char2glyph = new HashMap<Integer, Type3Glyph>();
    private PdfWriter writer;
    private float llx = Float.NaN;
    private float lly;
    private float urx;
    private float ury;
    private PageResources pageResources = new PageResources();
    private boolean colorized;

    public Type3Font(PdfWriter writer, char[] chars, boolean colorized) {
        this(writer, colorized);
    }

    public Type3Font(PdfWriter writer, boolean colorized) {
        this.writer = writer;
        this.colorized = colorized;
        this.fontType = 5;
        this.usedSlot = new boolean[256];
    }

    public PdfContentByte defineGlyph(char c, float wx, float llx, float lly, float urx, float ury) {
        if (c == '\u0000' || c > '\u00ff') {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.char.1.doesn.t.belong.in.this.type3.font", c));
        }
        this.usedSlot[c] = true;
        Integer ck = c;
        Type3Glyph glyph = this.char2glyph.get(ck);
        if (glyph != null) {
            return glyph;
        }
        this.widths3.put(c, (int)wx);
        if (!this.colorized) {
            if (Float.isNaN(this.llx)) {
                this.llx = llx;
                this.lly = lly;
                this.urx = urx;
                this.ury = ury;
            } else {
                this.llx = Math.min(this.llx, llx);
                this.lly = Math.min(this.lly, lly);
                this.urx = Math.max(this.urx, urx);
                this.ury = Math.max(this.ury, ury);
            }
        }
        glyph = new Type3Glyph(this.writer, this.pageResources, wx, llx, lly, urx, ury, this.colorized);
        this.char2glyph.put(ck, glyph);
        return glyph;
    }

    @Override
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    @Override
    public float getFontDescriptor(int key, float fontSize) {
        return 0.0f;
    }

    @Override
    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", ""}};
    }

    @Override
    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", ""}};
    }

    @Override
    public int getKerning(int char1, int char2) {
        return 0;
    }

    @Override
    public String getPostscriptFontName() {
        return "";
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        return null;
    }

    @Override
    int getRawWidth(int c, String name) {
        return 0;
    }

    @Override
    public boolean hasKernPairs() {
        return false;
    }

    @Override
    public boolean setKerning(int char1, int char2, int kern) {
        return false;
    }

    @Override
    public void setPostscriptFontName(String name) {
    }

    @Override
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
        int lastChar;
        int firstChar;
        if (this.writer != writer) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("type3.font.used.with.the.wrong.pdfwriter"));
        }
        for (firstChar = 0; firstChar < this.usedSlot.length && !this.usedSlot[firstChar]; ++firstChar) {
        }
        if (firstChar == this.usedSlot.length) {
            throw new DocumentException(MessageLocalization.getComposedMessage("no.glyphs.defined.for.type3.font"));
        }
        for (lastChar = this.usedSlot.length - 1; lastChar >= firstChar && !this.usedSlot[lastChar]; --lastChar) {
        }
        int[] widths = new int[lastChar - firstChar + 1];
        int[] invOrd = new int[lastChar - firstChar + 1];
        int invOrdIndx = 0;
        int w = 0;
        int u = firstChar;
        while (u <= lastChar) {
            if (this.usedSlot[u]) {
                invOrd[invOrdIndx++] = u;
                widths[w] = this.widths3.get(u);
            }
            ++u;
            ++w;
        }
        PdfArray diffs = new PdfArray();
        PdfDictionary charprocs = new PdfDictionary();
        int last = -1;
        for (int k = 0; k < invOrdIndx; ++k) {
            int c = invOrd[k];
            if (c > last) {
                last = c;
                diffs.add(new PdfNumber(last));
            }
            ++last;
            int c2 = invOrd[k];
            String s = GlyphList.unicodeToName(c2);
            if (s == null) {
                s = "a" + c2;
            }
            PdfName n = new PdfName(s);
            diffs.add(n);
            Type3Glyph glyph = this.char2glyph.get(c2);
            PdfStream stream = new PdfStream(glyph.toPdf(null));
            stream.flateCompress(this.compressionLevel);
            PdfIndirectReference refp = writer.addToBody(stream).getIndirectReference();
            charprocs.put(n, refp);
        }
        PdfDictionary font = new PdfDictionary(PdfName.FONT);
        font.put(PdfName.SUBTYPE, PdfName.TYPE3);
        if (this.colorized) {
            font.put(PdfName.FONTBBOX, new PdfRectangle(0.0f, 0.0f, 0.0f, 0.0f));
        } else {
            font.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        }
        font.put(PdfName.FONTMATRIX, new PdfArray(new float[]{0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f}));
        font.put(PdfName.CHARPROCS, writer.addToBody(charprocs).getIndirectReference());
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.DIFFERENCES, diffs);
        font.put(PdfName.ENCODING, writer.addToBody(encoding).getIndirectReference());
        font.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
        font.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
        font.put(PdfName.WIDTHS, writer.addToBody(new PdfArray(widths)).getIndirectReference());
        if (this.pageResources.hasResources()) {
            font.put(PdfName.RESOURCES, writer.addToBody(this.pageResources.getResources()).getIndirectReference());
        }
        writer.addToBody((PdfObject)font, ref);
    }

    @Override
    public PdfStream getFullFontStream() {
        return null;
    }

    @Override
    byte[] convertToBytes(String text) {
        char[] cc = text.toCharArray();
        byte[] b = new byte[cc.length];
        int p = 0;
        for (char c : cc) {
            if (!this.charExists(c)) continue;
            b[p++] = (byte)c;
        }
        if (b.length == p) {
            return b;
        }
        byte[] b2 = new byte[p];
        System.arraycopy(b, 0, b2, 0, p);
        return b2;
    }

    @Override
    byte[] convertToBytes(int char1) {
        if (this.charExists(char1)) {
            return new byte[]{(byte)char1};
        }
        return new byte[0];
    }

    @Override
    public int getWidth(int char1) {
        if (!this.widths3.containsKey(char1)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.char.1.is.not.defined.in.a.type3.font", char1));
        }
        return this.widths3.get(char1);
    }

    @Override
    public int getWidth(String text) {
        char[] c = text.toCharArray();
        int total = 0;
        for (char c1 : c) {
            total += this.getWidth(c1);
        }
        return total;
    }

    @Override
    public int[] getCharBBox(int c) {
        return null;
    }

    @Override
    public boolean charExists(int c) {
        if (c > 0 && c < 256) {
            return this.usedSlot[c];
        }
        return false;
    }

    @Override
    public boolean setCharAdvance(int c, int advance) {
        return false;
    }
}

