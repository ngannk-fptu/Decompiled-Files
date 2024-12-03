/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.util.HashMap;

public class DocumentFont
extends BaseFont {
    private HashMap<Integer, int[]> metrics = new HashMap();
    private String fontName;
    private PRIndirectReference refFont;
    private PdfDictionary font;
    private IntHashtable uni2byte = new IntHashtable();
    private IntHashtable diffmap;
    private float Ascender = 800.0f;
    private float CapHeight = 700.0f;
    private float Descender = -200.0f;
    private float ItalicAngle = 0.0f;
    private float llx = -50.0f;
    private float lly = -200.0f;
    private float urx = 100.0f;
    private float ury = 900.0f;
    private boolean isType0 = false;
    private BaseFont cjkMirror;
    private static String[] cjkNames = new String[]{"HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs = new String[]{"UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H"};
    private static String[] cjkNames2 = new String[]{"MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs2 = new String[]{"UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H"};
    private static final int[] stdEnc = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258, 0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191, 0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711, 8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0, 0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0};

    DocumentFont(PRIndirectReference refFont) {
        this.encoding = "";
        this.fontSpecific = false;
        this.refFont = refFont;
        this.fontType = 4;
        this.font = (PdfDictionary)PdfReader.getPdfObject(refFont);
        PdfName asName = this.font.getAsName(PdfName.BASEFONT);
        this.fontName = asName != null ? PdfName.decodeName(asName.toString()) : "badFontName";
        PdfName subType = this.font.getAsName(PdfName.SUBTYPE);
        if (PdfName.TYPE1.equals(subType) || PdfName.TRUETYPE.equals(subType)) {
            this.doType1TT();
        } else {
            for (int k = 0; k < cjkNames.length; ++k) {
                if (!this.fontName.startsWith(cjkNames[k])) continue;
                this.fontName = cjkNames[k];
                try {
                    this.cjkMirror = BaseFont.createFont(this.fontName, cjkEncs[k], false);
                }
                catch (Exception e) {
                    throw new ExceptionConverter(e);
                }
                return;
            }
            PdfName encName = this.font.getAsName(PdfName.ENCODING);
            if (encName != null) {
                String enc = PdfName.decodeName(encName.toString());
                for (int k = 0; k < cjkEncs2.length; ++k) {
                    if (!enc.startsWith(cjkEncs2[k])) continue;
                    try {
                        if (k > 3) {
                            k -= 4;
                        }
                        this.cjkMirror = BaseFont.createFont(cjkNames2[k], cjkEncs2[k], false);
                    }
                    catch (Exception e) {
                        throw new ExceptionConverter(e);
                    }
                    return;
                }
                this.encoding = enc;
                if (PdfName.TYPE0.equals(subType) && enc.equals("Identity-H")) {
                    this.processType0(this.font);
                    this.isType0 = true;
                }
            }
        }
    }

    private void processType0(PdfDictionary font) {
        try {
            PdfObject toUniObject = PdfReader.getPdfObjectRelease(font.get(PdfName.TOUNICODE));
            PdfArray df = (PdfArray)PdfReader.getPdfObjectRelease(font.get(PdfName.DESCENDANTFONTS));
            PdfDictionary cidft = (PdfDictionary)PdfReader.getPdfObjectRelease(df.getPdfObject(0));
            PdfNumber dwo = (PdfNumber)PdfReader.getPdfObjectRelease(cidft.get(PdfName.DW));
            int dw = 1000;
            if (dwo != null) {
                dw = dwo.intValue();
            }
            IntHashtable widths = this.readWidths((PdfArray)PdfReader.getPdfObjectRelease(cidft.get(PdfName.W)));
            PdfDictionary fontDesc = (PdfDictionary)PdfReader.getPdfObjectRelease(cidft.get(PdfName.FONTDESCRIPTOR));
            this.fillFontDesc(fontDesc);
            if (toUniObject != null) {
                this.fillMetrics(PdfReader.getStreamBytes((PRStream)toUniObject), widths, dw);
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private IntHashtable readWidths(PdfArray ws) {
        IntHashtable hh = new IntHashtable();
        if (ws == null) {
            return hh;
        }
        for (int k = 0; k < ws.size(); ++k) {
            PdfObject obj;
            int c1 = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(k))).intValue();
            if ((obj = PdfReader.getPdfObjectRelease(ws.getPdfObject(++k))).isArray()) {
                PdfArray a2 = (PdfArray)obj;
                for (int j = 0; j < a2.size(); ++j) {
                    int c2 = ((PdfNumber)PdfReader.getPdfObjectRelease(a2.getPdfObject(j))).intValue();
                    hh.put(c1++, c2);
                }
                continue;
            }
            int c2 = ((PdfNumber)obj).intValue();
            int w = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(++k))).intValue();
            while (c1 <= c2) {
                hh.put(c1, w);
                ++c1;
            }
        }
        return hh;
    }

    private String decodeString(PdfString ps) {
        if (ps.isHexWriting()) {
            return PdfEncodings.convertToString(ps.getBytes(), "UnicodeBigUnmarked");
        }
        return ps.toUnicodeString();
    }

    private void fillMetrics(byte[] touni, IntHashtable widths, int dw) {
        try {
            PdfContentParser ps = new PdfContentParser(new PRTokeniser(touni));
            PdfObject ob = null;
            PdfObject last = null;
            while ((ob = ps.readPRObject()) != null) {
                if (ob.type() == 200) {
                    int k;
                    int n;
                    if (ob.toString().equals("beginbfchar")) {
                        n = ((PdfNumber)last).intValue();
                        for (k = 0; k < n; ++k) {
                            String cid = this.decodeString((PdfString)ps.readPRObject());
                            String uni = this.decodeString((PdfString)ps.readPRObject());
                            if (uni.length() != 1) continue;
                            char cidc = cid.charAt(0);
                            char unic = uni.charAt(uni.length() - 1);
                            int w = dw;
                            if (widths.containsKey(cidc)) {
                                w = widths.get(cidc);
                            }
                            this.metrics.put(Integer.valueOf(unic), new int[]{cidc, w});
                        }
                        continue;
                    }
                    if (!ob.toString().equals("beginbfrange")) continue;
                    n = ((PdfNumber)last).intValue();
                    for (k = 0; k < n; ++k) {
                        String cid1 = this.decodeString((PdfString)ps.readPRObject());
                        String cid2 = this.decodeString((PdfString)ps.readPRObject());
                        int cid1c = cid1.charAt(0);
                        char cid2c = cid2.charAt(0);
                        PdfObject ob2 = ps.readPRObject();
                        if (ob2.isString()) {
                            String uni = this.decodeString((PdfString)ob2);
                            if (uni.length() != 1) continue;
                            int unic = uni.charAt(uni.length() - 1);
                            while (cid1c <= cid2c) {
                                int w = dw;
                                if (widths.containsKey(cid1c)) {
                                    w = widths.get(cid1c);
                                }
                                this.metrics.put(unic, new int[]{cid1c++, w});
                                ++unic;
                            }
                            continue;
                        }
                        PdfArray a = (PdfArray)ob2;
                        int j = 0;
                        while (j < a.size()) {
                            String uni = this.decodeString(a.getAsString(j));
                            if (uni.length() == 1) {
                                char unic = uni.charAt(uni.length() - 1);
                                int w = dw;
                                if (widths.containsKey(cid1c)) {
                                    w = widths.get(cid1c);
                                }
                                this.metrics.put(Integer.valueOf(unic), new int[]{cid1c, w});
                            }
                            ++j;
                            ++cid1c;
                        }
                    }
                    continue;
                }
                last = ob;
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void doType1TT() {
        PdfObject enc = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
        if (enc == null) {
            this.fillEncoding(null);
        } else if (enc.isName()) {
            this.fillEncoding((PdfName)enc);
        } else {
            PdfDictionary encDic = (PdfDictionary)enc;
            if ((enc = PdfReader.getPdfObject(encDic.get(PdfName.BASEENCODING))) == null) {
                this.fillEncoding(null);
            } else {
                this.fillEncoding((PdfName)enc);
            }
            PdfArray diffs = encDic.getAsArray(PdfName.DIFFERENCES);
            if (diffs != null) {
                this.diffmap = new IntHashtable();
                int currentNumber = 0;
                for (int k = 0; k < diffs.size(); ++k) {
                    PdfObject obj = diffs.getPdfObject(k);
                    if (obj.isNumber()) {
                        currentNumber = ((PdfNumber)obj).intValue();
                        continue;
                    }
                    int[] c = GlyphList.nameToUnicode(PdfName.decodeName(obj.toString()));
                    if (c != null && c.length > 0) {
                        this.uni2byte.put(c[0], currentNumber);
                        this.diffmap.put(c[0], currentNumber);
                    }
                    ++currentNumber;
                }
            }
        }
        PdfArray newWidths = this.font.getAsArray(PdfName.WIDTHS);
        PdfNumber first = this.font.getAsNumber(PdfName.FIRSTCHAR);
        PdfNumber last = this.font.getAsNumber(PdfName.LASTCHAR);
        if (BuiltinFonts14.containsKey(this.fontName)) {
            int n;
            BaseFont bf;
            try {
                bf = BaseFont.createFont(this.fontName, "Cp1252", false);
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
            for (int i : e = this.uni2byte.toOrderedKeys()) {
                n = this.uni2byte.get(i);
                this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(i));
            }
            if (this.diffmap != null) {
                for (int i : e = this.diffmap.toOrderedKeys()) {
                    n = this.diffmap.get(i);
                    this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(i));
                }
                this.diffmap = null;
            }
            this.Ascender = bf.getFontDescriptor(1, 1000.0f);
            this.CapHeight = bf.getFontDescriptor(2, 1000.0f);
            this.Descender = bf.getFontDescriptor(3, 1000.0f);
            this.ItalicAngle = bf.getFontDescriptor(4, 1000.0f);
            this.llx = bf.getFontDescriptor(5, 1000.0f);
            this.lly = bf.getFontDescriptor(6, 1000.0f);
            this.urx = bf.getFontDescriptor(7, 1000.0f);
            this.ury = bf.getFontDescriptor(8, 1000.0f);
        }
        if (first != null && last != null && newWidths != null) {
            int f = first.intValue();
            for (int k = 0; k < newWidths.size(); ++k) {
                this.widths[f + k] = newWidths.getAsNumber(k).intValue();
            }
        }
        this.fillFontDesc(this.font.getAsDict(PdfName.FONTDESCRIPTOR));
    }

    private void fillFontDesc(PdfDictionary fontDesc) {
        PdfArray bbox;
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.ASCENT);
        if (v != null) {
            this.Ascender = v.floatValue();
        }
        if ((v = fontDesc.getAsNumber(PdfName.CAPHEIGHT)) != null) {
            this.CapHeight = v.floatValue();
        }
        if ((v = fontDesc.getAsNumber(PdfName.DESCENT)) != null) {
            this.Descender = v.floatValue();
        }
        if ((v = fontDesc.getAsNumber(PdfName.ITALICANGLE)) != null) {
            this.ItalicAngle = v.floatValue();
        }
        if ((bbox = fontDesc.getAsArray(PdfName.FONTBBOX)) != null) {
            float t;
            this.llx = bbox.getAsNumber(0).floatValue();
            this.lly = bbox.getAsNumber(1).floatValue();
            this.urx = bbox.getAsNumber(2).floatValue();
            this.ury = bbox.getAsNumber(3).floatValue();
            if (this.llx > this.urx) {
                t = this.llx;
                this.llx = this.urx;
                this.urx = t;
            }
            if (this.lly > this.ury) {
                t = this.lly;
                this.lly = this.ury;
                this.ury = t;
            }
        }
    }

    private void fillEncoding(PdfName encoding) {
        if (PdfName.MAC_ROMAN_ENCODING.equals(encoding) || PdfName.WIN_ANSI_ENCODING.equals(encoding)) {
            byte[] b = new byte[256];
            for (int k = 0; k < 256; ++k) {
                b[k] = (byte)k;
            }
            String enc = "Cp1252";
            if (PdfName.MAC_ROMAN_ENCODING.equals(encoding)) {
                enc = "MacRoman";
            }
            String cv = PdfEncodings.convertToString(b, enc);
            char[] arr = cv.toCharArray();
            for (int k = 0; k < 256; ++k) {
                this.uni2byte.put(arr[k], k);
            }
        } else {
            for (int k = 0; k < 256; ++k) {
                this.uni2byte.put(stdEnc[k], k);
            }
        }
    }

    @Override
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    @Override
    public float getFontDescriptor(int key, float fontSize) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getFontDescriptor(key, fontSize);
        }
        switch (key) {
            case 1: 
            case 9: {
                return this.Ascender * fontSize / 1000.0f;
            }
            case 2: {
                return this.CapHeight * fontSize / 1000.0f;
            }
            case 3: 
            case 10: {
                return this.Descender * fontSize / 1000.0f;
            }
            case 4: {
                return this.ItalicAngle;
            }
            case 5: {
                return this.llx * fontSize / 1000.0f;
            }
            case 6: {
                return this.lly * fontSize / 1000.0f;
            }
            case 7: {
                return this.urx * fontSize / 1000.0f;
            }
            case 8: {
                return this.ury * fontSize / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return (this.urx - this.llx) * fontSize / 1000.0f;
            }
        }
        return 0.0f;
    }

    @Override
    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", this.fontName}};
    }

    @Override
    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", this.fontName}};
    }

    @Override
    public int getKerning(int char1, int char2) {
        return 0;
    }

    @Override
    public String getPostscriptFontName() {
        return this.fontName;
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
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException {
    }

    @Override
    public PdfStream getFullFontStream() {
        return null;
    }

    @Override
    public int getWidth(int char1) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(char1);
        }
        if (this.isType0) {
            int[] ws = this.metrics.get(char1);
            if (ws != null) {
                return ws[1];
            }
            return 0;
        }
        return super.getWidth(char1);
    }

    @Override
    public int getWidth(String text) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(text);
        }
        if (this.isType0) {
            char[] chars = text.toCharArray();
            int total = 0;
            for (char aChar : chars) {
                int[] ws = this.metrics.get(Character.getNumericValue(aChar));
                if (ws == null) continue;
                total += ws[1];
            }
            return total;
        }
        return super.getWidth(text);
    }

    @Override
    byte[] convertToBytes(String text) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes(text, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            char[] chars = text.toCharArray();
            int len = chars.length;
            byte[] b = new byte[len * 2];
            int bptr = 0;
            for (char aChar : chars) {
                int[] ws = this.metrics.get(Character.getNumericValue(aChar));
                if (ws == null) continue;
                int g = ws[0];
                b[bptr++] = (byte)(g / 256);
                b[bptr++] = (byte)g;
            }
            if (bptr == b.length) {
                return b;
            }
            byte[] nb = new byte[bptr];
            System.arraycopy(b, 0, nb, 0, bptr);
            return nb;
        }
        char[] cc = text.toCharArray();
        byte[] b = new byte[cc.length];
        int ptr = 0;
        for (char c : cc) {
            if (!this.uni2byte.containsKey(c)) continue;
            b[ptr++] = (byte)this.uni2byte.get(c);
        }
        if (ptr == b.length) {
            return b;
        }
        byte[] b2 = new byte[ptr];
        System.arraycopy(b, 0, b2, 0, ptr);
        return b2;
    }

    @Override
    byte[] convertToBytes(int char1) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes((char)char1, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            int[] ws = this.metrics.get(char1);
            if (ws != null) {
                int g = ws[0];
                return new byte[]{(byte)(g / 256), (byte)g};
            }
            return new byte[0];
        }
        if (this.uni2byte.containsKey(char1)) {
            return new byte[]{(byte)this.uni2byte.get(char1)};
        }
        return new byte[0];
    }

    PdfIndirectReference getIndirectReference() {
        return this.refFont;
    }

    @Override
    public boolean charExists(int c) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.charExists(c);
        }
        if (this.isType0) {
            return this.metrics.containsKey(c);
        }
        return super.charExists(c);
    }

    @Override
    public void setPostscriptFontName(String name) {
    }

    @Override
    public boolean setKerning(int char1, int char2, int kern) {
        return false;
    }

    @Override
    public int[] getCharBBox(int c) {
        return null;
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        return null;
    }

    IntHashtable getUni2Byte() {
        return this.uni2byte;
    }
}

