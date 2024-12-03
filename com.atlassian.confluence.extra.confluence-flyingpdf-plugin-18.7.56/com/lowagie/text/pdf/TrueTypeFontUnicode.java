/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.CFFFontSubset;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFont;
import com.lowagie.text.pdf.TrueTypeFontSubSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class TrueTypeFontUnicode
extends TrueTypeFont
implements Comparator {
    boolean vertical = false;
    Map<Integer, Integer> inverseCmap;
    private static final byte[] rotbits = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};

    TrueTypeFontUnicode(String ttFile, String enc, boolean emb, byte[] ttfAfm, boolean forceRead) throws DocumentException, IOException {
        String nameBase = TrueTypeFontUnicode.getBaseName(ttFile);
        String ttcName = TrueTypeFontUnicode.getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        this.fontType = 3;
        if ((this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) && (enc.equals("Identity-H") || enc.equals("Identity-V")) && emb) {
            this.process(ttfAfm, forceRead);
            if (this.os_2.fsType == 2) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.cannot.be.embedded.due.to.licensing.restrictions", this.fileName + this.style));
            }
            if (this.cmap31 == null && !this.fontSpecific || this.cmap10 == null && this.fontSpecific) {
                this.directTextToByte = true;
            }
            if (this.fontSpecific) {
                this.fontSpecific = false;
                String tempEncoding = this.encoding;
                this.encoding = "";
                this.createEncoding();
                this.encoding = tempEncoding;
                this.fontSpecific = true;
            }
        } else {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.2.is.not.a.ttf.font.file", this.fileName, this.style));
        }
        this.vertical = enc.endsWith("V");
    }

    @Override
    void readCMaps() throws DocumentException, IOException {
        super.readCMaps();
        HashMap cmap = null;
        if (this.cmapExt != null) {
            cmap = this.cmapExt;
        } else if (this.cmap31 != null) {
            cmap = this.cmap31;
        }
        if (cmap != null) {
            this.inverseCmap = new HashMap<Integer, Integer>();
            Iterator iterator = cmap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry entry = o = iterator.next();
                Integer code = (Integer)entry.getKey();
                int[] metrics = (int[])entry.getValue();
                this.inverseCmap.put(metrics[0], code);
            }
        }
    }

    protected Integer getCharacterCode(int code) {
        return this.inverseCmap == null ? null : this.inverseCmap.get(code);
    }

    @Override
    public int getWidth(int char1) {
        if (this.vertical) {
            return 1000;
        }
        if (this.fontSpecific) {
            if ((char1 & 0xFF00) == 0 || (char1 & 0xFF00) == 61440) {
                return this.getRawWidth(char1 & 0xFF, null);
            }
            return 0;
        }
        return this.getRawWidth(char1, this.encoding);
    }

    @Override
    public int getWidth(String text) {
        if (this.vertical) {
            return text.length() * 1000;
        }
        int total = 0;
        if (this.fontSpecific) {
            char[] cc = text.toCharArray();
            int len = cc.length;
            for (char c : cc) {
                if ((c & 0xFF00) != 0 && (c & 0xFF00) != 61440) continue;
                total += this.getRawWidth(c & 0xFF, null);
            }
        } else {
            int len = text.length();
            for (int k = 0; k < len; ++k) {
                if (Utilities.isSurrogatePair(text, k)) {
                    total += this.getRawWidth(Utilities.convertToUtf32(text, k), this.encoding);
                    ++k;
                    continue;
                }
                total += this.getRawWidth(text.charAt(k), this.encoding);
            }
        }
        return total;
    }

    private PdfStream getToUnicode(int[][] metrics) {
        if ((metrics = this.filterCmapMetrics(metrics)).length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
        int size = 0;
        for (int k = 0; k < metrics.length; ++k) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, metrics.length - k);
                buf.append(size).append(" beginbfrange\n");
            }
            --size;
            int[] metric = metrics[k];
            String fromTo = TrueTypeFontUnicode.toHex(metric[0]);
            buf.append(fromTo).append(fromTo).append(TrueTypeFontUnicode.toHex(metric[2])).append('\n');
        }
        buf.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
        String s = buf.toString();
        PdfStream stream = new PdfStream(PdfEncodings.convertToBytes(s, null));
        stream.flateCompress(this.compressionLevel);
        return stream;
    }

    private int[][] filterCmapMetrics(int[][] metrics) {
        if (metrics.length == 0) {
            return metrics;
        }
        ArrayList<int[]> cmapMetrics = new ArrayList<int[]>(metrics.length);
        for (int[] metric1 : metrics) {
            int[] metric = metric1;
            if (metric.length < 3) continue;
            cmapMetrics.add(metric);
        }
        if (cmapMetrics.size() == metrics.length) {
            return metrics;
        }
        return (int[][])cmapMetrics.toArray((T[])new int[0][]);
    }

    private static String toHex4(int n) {
        String s = "0000" + Integer.toHexString(n);
        return s.substring(s.length() - 4);
    }

    static String toHex(int n) {
        if (n < 65536) {
            return "<" + TrueTypeFontUnicode.toHex4(n) + ">";
        }
        int high = (n -= 65536) / 1024 + 55296;
        int low = n % 1024 + 56320;
        return "[<" + TrueTypeFontUnicode.toHex4(high) + TrueTypeFontUnicode.toHex4(low) + ">]";
    }

    private PdfDictionary getCIDFontType2(PdfIndirectReference fontDescriptor, String subsetPrefix, int[][] metrics) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        } else {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        if (!this.cff) {
            dic.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY);
        }
        PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString("Adobe"));
        cdic.put(PdfName.ORDERING, new PdfString("Identity"));
        cdic.put(PdfName.SUPPLEMENT, new PdfNumber(0));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        if (!this.vertical) {
            dic.put(PdfName.DW, new PdfNumber(1000));
            StringBuilder buf = new StringBuilder("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int[] metric1 : metrics) {
                int[] metric = metric1;
                if (metric[1] == 1000) continue;
                int m = metric[0];
                if (m == lastNumber + 1) {
                    buf.append(' ').append(metric[1]);
                } else {
                    if (!firstTime) {
                        buf.append(']');
                    }
                    firstTime = false;
                    buf.append(m).append('[').append(metric[1]);
                }
                lastNumber = m;
            }
            if (buf.length() > 1) {
                buf.append("]]");
                dic.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
        }
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference descendant, String subsetPrefix, PdfIndirectReference toUnicode) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        if (this.cff) {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        } else {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.ENCODING, new PdfName(this.encoding));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(descendant));
        if (toUnicode != null) {
            dic.put(PdfName.TOUNICODE, toUnicode);
        }
        return dic;
    }

    public int compare(Object o1, Object o2) {
        int m1 = ((int[])o1)[0];
        int m2 = ((int[])o2)[0];
        return Integer.compare(m1, m2);
    }

    @Override
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
        byte[] b;
        HashMap longTag = (HashMap)params[0];
        this.addRangeUni(longTag, true, this.subset);
        int[][] metrics = (int[][])longTag.values().toArray((T[])new int[0][]);
        Arrays.sort(metrics, this);
        PdfIndirectReference ind_font = null;
        PdfDictionary pobj = null;
        PdfIndirectObject obj = null;
        PdfIndirectReference cidset = null;
        if (writer.getPDFXConformance() == 3 || writer.getPDFXConformance() == 4) {
            PdfStream stream;
            if (metrics.length == 0) {
                stream = new PdfStream(new byte[]{-128});
            } else {
                int top = metrics[metrics.length - 1][0];
                byte[] bt = new byte[top / 8 + 1];
                for (int[] metric : metrics) {
                    int v = metric[0];
                    int n = v / 8;
                    bt[n] = (byte)(bt[n] | rotbits[v % 8]);
                }
                stream = new PdfStream(bt);
                stream.flateCompress(this.compressionLevel);
            }
            cidset = writer.addToBody(stream).getIndirectReference();
        }
        if (this.cff) {
            b = this.readCffFont();
            if (this.subset || this.subsetRanges != null) {
                CFFFontSubset cff = new CFFFontSubset(new RandomAccessFileOrArray(b), longTag);
                b = cff.Process(cff.getNames()[0]);
            }
            pobj = new BaseFont.StreamFont(b, "CIDFontType0C", this.compressionLevel);
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        } else {
            if (this.subset || this.directoryOffset != 0) {
                TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), longTag, this.directoryOffset, false, false);
                b = sb.process();
            } else {
                b = this.getFullFont();
            }
            int[] lengths = new int[]{b.length};
            pobj = new BaseFont.StreamFont(b, lengths, this.compressionLevel);
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        String subsetPrefix = "";
        if (this.subset) {
            subsetPrefix = TrueTypeFontUnicode.createSubsetPrefix();
        }
        PdfDictionary dic = this.getFontDescriptor(ind_font, subsetPrefix, cidset);
        obj = writer.addToBody(dic);
        ind_font = obj.getIndirectReference();
        pobj = this.getCIDFontType2(ind_font, subsetPrefix, metrics);
        obj = writer.addToBody(pobj);
        ind_font = obj.getIndirectReference();
        pobj = this.getToUnicode(metrics);
        PdfIndirectReference toUnicodeRef = null;
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            toUnicodeRef = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, subsetPrefix, toUnicodeRef);
        writer.addToBody((PdfObject)pobj, ref);
    }

    @Override
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(this.readCffFont(), "CIDFontType0C", this.compressionLevel);
        }
        return super.getFullFontStream();
    }

    @Override
    byte[] convertToBytes(String text) {
        return null;
    }

    @Override
    byte[] convertToBytes(int char1) {
        return null;
    }

    @Override
    public int[] getMetricsTT(int c) {
        if (this.cmapExt != null) {
            return (int[])this.cmapExt.get(c);
        }
        HashMap map = this.fontSpecific ? this.cmap10 : this.cmap31;
        if (map == null) {
            return null;
        }
        if (this.fontSpecific) {
            if ((c & 0xFFFFFF00) == 0 || (c & 0xFFFFFF00) == 61440) {
                return (int[])map.get(c & 0xFF);
            }
            return null;
        }
        return (int[])map.get(c);
    }

    @Override
    public boolean charExists(int c) {
        return this.getMetricsTT(c) != null;
    }

    @Override
    public boolean setCharAdvance(int c, int advance) {
        int[] m = this.getMetricsTT(c);
        if (m == null) {
            return false;
        }
        m[1] = advance;
        return true;
    }

    @Override
    public int[] getCharBBox(int c) {
        if (this.bboxes == null) {
            return null;
        }
        int[] m = this.getMetricsTT(c);
        if (m == null) {
            return null;
        }
        return this.bboxes[m[0]];
    }
}

