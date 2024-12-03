/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.fonts.cmaps.CMap;
import com.lowagie.text.pdf.fonts.cmaps.CMapParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CMapAwareDocumentFont
extends DocumentFont {
    private PdfDictionary fontDic;
    private int spaceWidth;
    private CMap toUnicodeCmap;
    private char[] cidbyte2uni;

    public CMapAwareDocumentFont(PRIndirectReference refFont) {
        super(refFont);
        this.fontDic = (PdfDictionary)PdfReader.getPdfObjectRelease(refFont);
        this.processToUnicode();
        if (this.toUnicodeCmap == null) {
            this.processUni2Byte();
        }
        this.spaceWidth = super.getWidth(32);
        if (this.spaceWidth == 0) {
            this.spaceWidth = this.computeAverageWidth();
        }
    }

    private void processToUnicode() {
        PdfObject toUni = this.fontDic.get(PdfName.TOUNICODE);
        if (toUni != null) {
            try {
                byte[] touni = PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObjectRelease(toUni));
                CMapParser cmapParser = new CMapParser();
                this.toUnicodeCmap = cmapParser.parse(new ByteArrayInputStream(touni));
            }
            catch (IOException e) {
                throw new Error("Unable to process ToUnicode map - " + e.getMessage(), e);
            }
        }
    }

    private void processUni2Byte() {
        IntHashtable uni2byte = this.getUni2Byte();
        int[] e = uni2byte.toOrderedKeys();
        this.cidbyte2uni = new char[256];
        for (int element : e) {
            int n = uni2byte.get(element);
            if (this.cidbyte2uni[n] != '\u0000') continue;
            this.cidbyte2uni[n] = (char)element;
        }
    }

    private int computeAverageWidth() {
        int count = 0;
        int total = 0;
        for (int width : this.widths) {
            if (width == 0) continue;
            total += width;
            ++count;
        }
        return count != 0 ? total / count : 0;
    }

    @Override
    public int getWidth(int char1) {
        if (char1 == 32) {
            return this.spaceWidth;
        }
        return super.getWidth(char1);
    }

    private String decodeSingleCID(byte[] bytes, int offset, int len) {
        if (this.hasUnicodeCMAP()) {
            if (offset + len > bytes.length) {
                throw new ArrayIndexOutOfBoundsException(MessageLocalization.getComposedMessage("invalid.index.1", offset + len));
            }
            return this.toUnicodeCmap.lookup(bytes, offset, len);
        }
        if (len == 1) {
            return new String(this.cidbyte2uni, 0xFF & bytes[offset], 1);
        }
        throw new Error("Multi-byte glyphs not implemented yet");
    }

    public boolean hasUnicodeCMAP() {
        return this.toUnicodeCmap != null;
    }

    public String decode(byte[] cidbytes, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + len; ++i) {
            String rslt = this.decodeSingleCID(cidbytes, i, 1);
            if (rslt == null && i + 1 < offset + len) {
                rslt = this.decodeSingleCID(cidbytes, i, 2);
                ++i;
            }
            if (rslt == null) continue;
            sb.append(rslt);
        }
        return sb.toString();
    }

    public String decode(String chars) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars.toCharArray()) {
            String result = this.decode(c);
            if (result == null) continue;
            sb.append(result);
        }
        return sb.toString();
    }

    public String decode(char c) throws Error {
        String result;
        if (this.hasUnicodeCMAP()) {
            result = this.toUnicodeCmap.lookup(c);
        } else if (c <= '\u00ff') {
            result = new String(this.cidbyte2uni, 0xFF & c, 1);
        } else {
            throw new Error("Multi-byte glyphs not implemented yet");
        }
        return result;
    }

    @Deprecated
    public String encode(byte[] bytes, int offset, int len) {
        return this.decode(bytes, offset, len);
    }
}

