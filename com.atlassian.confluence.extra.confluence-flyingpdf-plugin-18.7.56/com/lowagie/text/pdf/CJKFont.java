/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

class CJKFont
extends BaseFont {
    static final String CJK_ENCODING = "UnicodeBigUnmarked";
    private static final int FIRST = 0;
    private static final int BRACKET = 1;
    private static final int SERIAL = 2;
    private static final int V1Y = 880;
    static Properties cjkFonts = new Properties();
    static Properties cjkEncodings = new Properties();
    Hashtable<String, char[]> allCMaps = new Hashtable();
    static ConcurrentHashMap<String, HashMap<Object, Object>> allFonts = new ConcurrentHashMap(500, 0.85f, 64);
    private static boolean propertiesLoaded = false;
    private static Object initLock = new Object();
    private String fontName;
    private String style = "";
    private String CMap;
    private boolean cidDirect = false;
    private char[] translationMap;
    private IntHashtable vMetrics;
    private IntHashtable hMetrics;
    private HashMap<Object, Object> fontDesc;
    private boolean vertical = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadProperties() {
        if (propertiesLoaded) {
            return;
        }
        Object object = initLock;
        synchronized (object) {
            if (propertiesLoaded) {
                return;
            }
            try {
                InputStream is = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkfonts.properties");
                cjkFonts.load(is);
                is.close();
                is = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkencodings.properties");
                cjkEncodings.load(is);
                is.close();
            }
            catch (Exception e) {
                cjkFonts = new Properties();
                cjkEncodings = new Properties();
            }
            propertiesLoaded = true;
        }
    }

    CJKFont(String fontName, String enc, boolean emb) throws DocumentException {
        CJKFont.loadProperties();
        this.fontType = 2;
        String nameBase = CJKFont.getBaseName(fontName);
        if (!CJKFont.isCJKFont(nameBase, enc)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("font.1.with.2.encoding.is.not.a.cjk.font", fontName, enc));
        }
        if (nameBase.length() < fontName.length()) {
            this.style = fontName.substring(nameBase.length());
            fontName = nameBase;
        }
        this.fontName = fontName;
        this.encoding = CJK_ENCODING;
        this.vertical = enc.endsWith("V");
        this.CMap = enc;
        if (enc.startsWith("Identity-")) {
            this.cidDirect = true;
            String s = cjkFonts.getProperty(fontName);
            char[] c = this.allCMaps.get(s = s.substring(0, s.indexOf(95)));
            if (c == null) {
                c = CJKFont.readCMap(s);
                if (c == null) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.cmap.1.does.not.exist.as.a.resource", s));
                }
                c[Short.MAX_VALUE] = 10;
                this.allCMaps.put(s, c);
            }
            this.translationMap = c;
        } else {
            char[] c = this.allCMaps.get(enc);
            if (c == null) {
                String s = cjkEncodings.getProperty(enc);
                if (s == null) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.resource.cjkencodings.properties.does.not.contain.the.encoding.1", enc));
                }
                StringTokenizer tk = new StringTokenizer(s);
                String nt = tk.nextToken();
                c = this.allCMaps.get(nt);
                if (c == null) {
                    c = CJKFont.readCMap(nt);
                    this.allCMaps.put(nt, c);
                }
                if (tk.hasMoreTokens()) {
                    String nt2 = tk.nextToken();
                    char[] m2 = CJKFont.readCMap(nt2);
                    for (int k = 0; k < 65536; ++k) {
                        if (m2[k] != '\u0000') continue;
                        m2[k] = c[k];
                    }
                    this.allCMaps.put(enc, m2);
                    c = m2;
                }
            }
            this.translationMap = c;
        }
        this.fontDesc = allFonts.get(fontName);
        if (this.fontDesc == null) {
            this.fontDesc = CJKFont.readFontProperties(fontName);
            allFonts.putIfAbsent(fontName, this.fontDesc);
            this.fontDesc = allFonts.get(fontName);
        }
        this.hMetrics = (IntHashtable)this.fontDesc.get("W");
        this.vMetrics = (IntHashtable)this.fontDesc.get("W2");
    }

    public static boolean isCJKFont(String fontName, String enc) {
        CJKFont.loadProperties();
        String encodings = cjkFonts.getProperty(fontName);
        return encodings != null && (enc.equals("Identity-H") || enc.equals("Identity-V") || encodings.contains("_" + enc + "_"));
    }

    @Override
    public int getWidth(int char1) {
        int v;
        int c = char1;
        if (!this.cidDirect) {
            c = this.translationMap[c];
        }
        if ((v = this.vertical ? this.vMetrics.get(c) : this.hMetrics.get(c)) > 0) {
            return v;
        }
        return 1000;
    }

    @Override
    public int getWidth(String text) {
        int total = 0;
        for (int k = 0; k < text.length(); ++k) {
            int v;
            char c = text.charAt(k);
            if (!this.cidDirect) {
                c = this.translationMap[c];
            }
            if ((v = this.vertical ? this.vMetrics.get(c) : this.hMetrics.get(c)) > 0) {
                total += v;
                continue;
            }
            total += 1000;
        }
        return total;
    }

    @Override
    int getRawWidth(int c, String name) {
        return 0;
    }

    @Override
    public int getKerning(int char1, int char2) {
        return 0;
    }

    private PdfDictionary getFontDescriptor() {
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfLiteral((String)this.fontDesc.get("Ascent")));
        dic.put(PdfName.CAPHEIGHT, new PdfLiteral((String)this.fontDesc.get("CapHeight")));
        dic.put(PdfName.DESCENT, new PdfLiteral((String)this.fontDesc.get("Descent")));
        dic.put(PdfName.FLAGS, new PdfLiteral((String)this.fontDesc.get("Flags")));
        dic.put(PdfName.FONTBBOX, new PdfLiteral((String)this.fontDesc.get("FontBBox")));
        dic.put(PdfName.FONTNAME, new PdfName(this.fontName + this.style));
        dic.put(PdfName.ITALICANGLE, new PdfLiteral((String)this.fontDesc.get("ItalicAngle")));
        dic.put(PdfName.STEMV, new PdfLiteral((String)this.fontDesc.get("StemV")));
        PdfDictionary pdic = new PdfDictionary();
        pdic.put(PdfName.PANOSE, new PdfString((String)this.fontDesc.get("Panose"), null));
        dic.put(PdfName.STYLE, pdic);
        return dic;
    }

    private PdfDictionary getCIDFont(PdfIndirectReference fontDescriptor, IntHashtable cjkTag) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
        dic.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        int[] keys = cjkTag.toOrderedKeys();
        String w = CJKFont.convertToHCIDMetrics(keys, this.hMetrics);
        if (w != null) {
            dic.put(PdfName.W, new PdfLiteral(w));
        }
        if (this.vertical) {
            w = CJKFont.convertToVCIDMetrics(keys, this.vMetrics, this.hMetrics);
            if (w != null) {
                dic.put(PdfName.W2, new PdfLiteral(w));
            }
        } else {
            dic.put(PdfName.DW, new PdfNumber(1000));
        }
        PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString((String)this.fontDesc.get("Registry"), null));
        cdic.put(PdfName.ORDERING, new PdfString((String)this.fontDesc.get("Ordering"), null));
        cdic.put(PdfName.SUPPLEMENT, new PdfLiteral((String)this.fontDesc.get("Supplement")));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference CIDFont) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        String name = this.fontName;
        if (this.style.length() > 0) {
            name = name + "-" + this.style.substring(1);
        }
        name = name + "-" + this.CMap;
        dic.put(PdfName.BASEFONT, new PdfName(name));
        dic.put(PdfName.ENCODING, new PdfName(this.CMap));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(CIDFont));
        return dic;
    }

    @Override
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
        IntHashtable cjkTag = (IntHashtable)params[0];
        PdfIndirectReference ind_font = null;
        PdfDictionary pobj = null;
        PdfIndirectObject obj = null;
        pobj = this.getFontDescriptor();
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        if ((pobj = this.getCIDFont(ind_font, cjkTag)) != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font);
        writer.addToBody((PdfObject)pobj, ref);
    }

    @Override
    public PdfStream getFullFontStream() {
        return null;
    }

    private float getDescNumber(String name) {
        return Integer.parseInt((String)this.fontDesc.get(name));
    }

    private float getBBox(int idx) {
        String s = (String)this.fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(s, " []\r\n\t\f");
        String ret = tk.nextToken();
        for (int k = 0; k < idx; ++k) {
            ret = tk.nextToken();
        }
        return Integer.parseInt(ret);
    }

    @Override
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1: 
            case 9: {
                return this.getDescNumber("Ascent") * fontSize / 1000.0f;
            }
            case 2: {
                return this.getDescNumber("CapHeight") * fontSize / 1000.0f;
            }
            case 3: 
            case 10: {
                return this.getDescNumber("Descent") * fontSize / 1000.0f;
            }
            case 4: {
                return this.getDescNumber("ItalicAngle");
            }
            case 5: {
                return fontSize * this.getBBox(0) / 1000.0f;
            }
            case 6: {
                return fontSize * this.getBBox(1) / 1000.0f;
            }
            case 7: {
                return fontSize * this.getBBox(2) / 1000.0f;
            }
            case 8: {
                return fontSize * this.getBBox(3) / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return fontSize * (this.getBBox(2) - this.getBBox(0)) / 1000.0f;
            }
        }
        return 0.0f;
    }

    @Override
    public String getPostscriptFontName() {
        return this.fontName;
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
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    static char[] readCMap(String name) {
        try {
            name = name + ".cmap";
            InputStream is = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/" + name);
            char[] c = new char[65536];
            for (int k = 0; k < 65536; ++k) {
                c[k] = (char)((is.read() << 8) + is.read());
            }
            is.close();
            return c;
        }
        catch (Exception exception) {
            return null;
        }
    }

    static IntHashtable createMetric(String s) {
        IntHashtable h = new IntHashtable();
        StringTokenizer tk = new StringTokenizer(s);
        while (tk.hasMoreTokens()) {
            int n1 = Integer.parseInt(tk.nextToken());
            h.put(n1, Integer.parseInt(tk.nextToken()));
        }
        return h;
    }

    static String convertToHCIDMetrics(int[] keys, IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        for (int start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = h.get(lastCid);
            if (lastValue == 0) continue;
            ++start;
            break;
        }
        if (lastValue == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; ++k) {
            int cid = keys[k];
            int value = h.get(cid);
            if (value == 0) continue;
            switch (state) {
                case 0: {
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = 2;
                        break;
                    }
                    if (cid == lastCid + 1) {
                        state = 1;
                        buf.append('[').append(lastValue);
                        break;
                    }
                    buf.append('[').append(lastValue).append(']').append(cid);
                    break;
                }
                case 1: {
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = 2;
                        buf.append(']').append(lastCid);
                        break;
                    }
                    if (cid == lastCid + 1) {
                        buf.append(' ').append(lastValue);
                        break;
                    }
                    state = 0;
                    buf.append(' ').append(lastValue).append(']').append(cid);
                    break;
                }
                case 2: {
                    if (cid == lastCid + 1 && value == lastValue) break;
                    buf.append(' ').append(lastCid).append(' ').append(lastValue).append(' ').append(cid);
                    state = 0;
                }
            }
            lastValue = value;
            lastCid = cid;
        }
        switch (state) {
            case 0: {
                buf.append('[').append(lastValue).append("]]");
                break;
            }
            case 1: {
                buf.append(' ').append(lastValue).append("]]");
                break;
            }
            case 2: {
                buf.append(' ').append(lastCid).append(' ').append(lastValue).append(']');
            }
        }
        return buf.toString();
    }

    static String convertToVCIDMetrics(int[] keys, IntHashtable v, IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int lastHValue = 0;
        for (int start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = v.get(lastCid);
            if (lastValue != 0) {
                ++start;
                break;
            }
            lastHValue = h.get(lastCid);
        }
        if (lastValue == 0) {
            return null;
        }
        if (lastHValue == 0) {
            lastHValue = 1000;
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; ++k) {
            int cid = keys[k];
            int value = v.get(cid);
            if (value == 0) continue;
            int hValue = h.get(lastCid);
            if (hValue == 0) {
                hValue = 1000;
            }
            switch (state) {
                case 0: {
                    if (cid == lastCid + 1 && value == lastValue && hValue == lastHValue) {
                        state = 2;
                        break;
                    }
                    buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(' ').append(cid);
                    break;
                }
                case 2: {
                    if (cid == lastCid + 1 && value == lastValue && hValue == lastHValue) break;
                    buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(' ').append(cid);
                    state = 0;
                }
            }
            lastValue = value;
            lastCid = cid;
            lastHValue = hValue;
        }
        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(" ]");
        return buf.toString();
    }

    static HashMap<Object, Object> readFontProperties(String name) {
        try {
            name = name + ".properties";
            InputStream is = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/" + name);
            Properties p = new Properties();
            p.load(is);
            is.close();
            IntHashtable W = CJKFont.createMetric(p.getProperty("W"));
            p.remove("W");
            IntHashtable W2 = CJKFont.createMetric(p.getProperty("W2"));
            p.remove("W2");
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            Enumeration<Object> e = p.keys();
            while (e.hasMoreElements()) {
                Object obj = e.nextElement();
                map.put(obj, p.getProperty((String)obj));
            }
            map.put("W", W);
            map.put("W2", W2);
            return map;
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    public int getUnicodeEquivalent(int c) {
        if (this.cidDirect) {
            return this.translationMap[c];
        }
        return c;
    }

    @Override
    public int getCidCode(int c) {
        if (this.cidDirect) {
            return c;
        }
        return this.translationMap[c];
    }

    @Override
    public boolean hasKernPairs() {
        return false;
    }

    @Override
    public boolean charExists(int c) {
        return this.translationMap[c] != '\u0000';
    }

    @Override
    public boolean setCharAdvance(int c, int advance) {
        return false;
    }

    @Override
    public void setPostscriptFontName(String name) {
        this.fontName = name;
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
}

