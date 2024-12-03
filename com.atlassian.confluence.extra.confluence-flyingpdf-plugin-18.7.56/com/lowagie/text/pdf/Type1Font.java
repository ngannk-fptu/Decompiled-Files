/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.Pfm2afm;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class Type1Font
extends BaseFont {
    private static FontsResourceAnchor resourceAnchor;
    protected byte[] pfb;
    private String FontName;
    private String FullName;
    private String FamilyName;
    private String Weight = "";
    private float ItalicAngle = 0.0f;
    private boolean IsFixedPitch = false;
    private String CharacterSet;
    private int llx = -50;
    private int lly = -200;
    private int urx = 1000;
    private int ury = 900;
    private int UnderlinePosition = -100;
    private int UnderlineThickness = 50;
    private String EncodingScheme = "FontSpecific";
    private int CapHeight = 700;
    private int XHeight = 480;
    private int Ascender = 800;
    private int Descender = -200;
    private int StdHW;
    private int StdVW = 80;
    private Map<Object, Object[]> CharMetrics = new HashMap<Object, Object[]>();
    private Map<String, Object[]> KernPairs = new HashMap<String, Object[]>();
    private String fileName;
    private boolean builtinFont = false;
    private static final int[] PFB_TYPES;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Type1Font(String afmFile, String enc, boolean emb, byte[] ttfAfm, byte[] pfb, boolean forceRead) throws DocumentException, IOException {
        if (emb && ttfAfm != null && pfb == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("two.byte.arrays.are.needed.if.the.type1.font.is.embedded"));
        }
        if (emb && ttfAfm != null) {
            this.pfb = pfb;
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = afmFile;
        this.fontType = 0;
        RandomAccessFileOrArray rf = null;
        InputStream is = null;
        if (BuiltinFonts14.containsKey(afmFile)) {
            this.embedded = false;
            this.builtinFont = true;
            byte[] buf = new byte[1024];
            try {
                int size;
                if (resourceAnchor == null) {
                    resourceAnchor = new FontsResourceAnchor();
                }
                if ((is = Type1Font.getResourceStream("com/lowagie/text/pdf/fonts/" + afmFile + ".afm", resourceAnchor.getClass().getClassLoader())) == null) {
                    String msg = MessageLocalization.getComposedMessage("1.not.found.as.resource", afmFile);
                    System.err.println(msg);
                    throw new DocumentException(msg);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((size = is.read(buf)) >= 0) {
                    out.write(buf, 0, size);
                }
                buf = out.toByteArray();
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception exception) {}
                }
            }
            try {
                rf = new RandomAccessFileOrArray(buf);
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        if (afmFile.toLowerCase().endsWith(".afm")) {
            try {
                rf = ttfAfm == null ? new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess) : new RandomAccessFileOrArray(ttfAfm);
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (Exception buf) {}
                }
            }
        }
        if (afmFile.toLowerCase().endsWith(".pfm")) {
            try {
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                rf = ttfAfm == null ? new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess) : new RandomAccessFileOrArray(ttfAfm);
                Pfm2afm.convert(rf, ba);
                rf.close();
                rf = new RandomAccessFileOrArray(ba.toByteArray());
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.an.afm.or.pfm.font.file", afmFile));
        this.EncodingScheme = this.EncodingScheme.trim();
        if (this.EncodingScheme.equals("AdobeStandardEncoding") || this.EncodingScheme.equals("StandardEncoding")) {
            this.fontSpecific = false;
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", enc);
        }
        this.createEncoding();
    }

    @Override
    int getRawWidth(int c, String name) {
        Object[] metrics;
        if (name == null) {
            metrics = this.CharMetrics.get(c);
        } else {
            if (name.equals(".notdef")) {
                return 0;
            }
            metrics = this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return (Integer)metrics[1];
        }
        return 0;
    }

    @Override
    public int getKerning(int char1, int char2) {
        String first = GlyphList.unicodeToName(char1);
        if (first == null) {
            return 0;
        }
        String second = GlyphList.unicodeToName(char2);
        if (second == null) {
            return 0;
        }
        Object[] obj = this.KernPairs.get(first);
        if (obj == null) {
            return 0;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (!second.equals(obj[k]) || obj.length <= k + 1) continue;
            return (Integer)obj[k + 1];
        }
        return 0;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void process(RandomAccessFileOrArray rf) throws DocumentException, IOException {
        Object[] space;
        String ident;
        StringTokenizer tok;
        String line;
        boolean isMetrics = false;
        block52: while ((line = rf.readLine()) != null) {
            tok = new StringTokenizer(line, " ,\n\r\t\f");
            if (!tok.hasMoreTokens()) continue;
            switch (ident = tok.nextToken()) {
                case "FontName": {
                    this.FontName = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "FullName": {
                    this.FullName = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "FamilyName": {
                    this.FamilyName = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "Weight": {
                    this.Weight = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "ItalicAngle": {
                    this.ItalicAngle = Float.parseFloat(tok.nextToken());
                    break;
                }
                case "IsFixedPitch": {
                    this.IsFixedPitch = tok.nextToken().equals("true");
                    break;
                }
                case "CharacterSet": {
                    this.CharacterSet = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "FontBBox": {
                    this.llx = (int)Float.parseFloat(tok.nextToken());
                    this.lly = (int)Float.parseFloat(tok.nextToken());
                    this.urx = (int)Float.parseFloat(tok.nextToken());
                    this.ury = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "UnderlinePosition": {
                    this.UnderlinePosition = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "UnderlineThickness": {
                    this.UnderlineThickness = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "EncodingScheme": {
                    this.EncodingScheme = tok.nextToken("\u00ff").substring(1);
                    break;
                }
                case "CapHeight": {
                    this.CapHeight = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "XHeight": {
                    this.XHeight = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "Ascender": {
                    this.Ascender = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "Descender": {
                    this.Descender = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "StdHW": {
                    this.StdHW = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "StdVW": {
                    this.StdVW = (int)Float.parseFloat(tok.nextToken());
                    break;
                }
                case "StartCharMetrics": {
                    isMetrics = true;
                    break block52;
                }
            }
        }
        if (!isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.startcharmetrics.in.1", this.fileName));
        }
        while ((line = rf.readLine()) != null) {
            tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) continue;
            ident = tok.nextToken();
            if (ident.equals("EndCharMetrics")) {
                isMetrics = false;
                break;
            }
            Integer C = -1;
            int WX = 250;
            String N = "";
            int[] B = null;
            tok = new StringTokenizer(line, ";");
            block54: while (tok.hasMoreTokens()) {
                StringTokenizer tokc = new StringTokenizer(tok.nextToken());
                if (!tokc.hasMoreTokens()) continue;
                switch (ident = tokc.nextToken()) {
                    case "C": {
                        C = Integer.valueOf(tokc.nextToken());
                        break;
                    }
                    case "WX": {
                        WX = (int)Float.parseFloat(tokc.nextToken());
                        break;
                    }
                    case "N": {
                        N = tokc.nextToken();
                        break;
                    }
                    case "B": {
                        B = new int[]{Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken())};
                        continue block54;
                    }
                }
            }
            Object[] metrics = new Object[]{C, WX, N, B};
            if (C >= 0) {
                this.CharMetrics.put(C, metrics);
            }
            this.CharMetrics.put(N, metrics);
        }
        if (isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endcharmetrics.in.1", this.fileName));
        }
        if (!this.CharMetrics.containsKey("nonbreakingspace") && (space = this.CharMetrics.get("space")) != null) {
            this.CharMetrics.put("nonbreakingspace", space);
        }
        while ((line = rf.readLine()) != null) {
            tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) continue;
            ident = tok.nextToken();
            if (ident.equals("EndFontMetrics")) {
                return;
            }
            if (!ident.equals("StartKernPairs")) continue;
            isMetrics = true;
            break;
        }
        if (!isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endfontmetrics.in.1", this.fileName));
        }
        while ((line = rf.readLine()) != null) {
            tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) continue;
            ident = tok.nextToken();
            if (ident.equals("KPX")) {
                String first = tok.nextToken();
                String second = tok.nextToken();
                int width = Integer.parseInt(tok.nextToken());
                Object[] relates = this.KernPairs.get(first);
                if (relates == null) {
                    this.KernPairs.put(first, new Object[]{second, width});
                    continue;
                }
                int n = relates.length;
                Object[] relates2 = new Object[n + 2];
                System.arraycopy(relates, 0, relates2, 0, n);
                relates2[n] = second;
                relates2[n + 1] = width;
                this.KernPairs.put(first, relates2);
                continue;
            }
            if (!ident.equals("EndKernPairs")) continue;
            isMetrics = false;
            break;
        }
        if (isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endkernpairs.in.1", this.fileName));
        }
        rf.close();
    }

    @Override
    public PdfStream getFullFontStream() throws DocumentException {
        if (this.builtinFont || !this.embedded) {
            return null;
        }
        RandomAccessFileOrArray rf = null;
        try {
            String filePfb = this.fileName.substring(0, this.fileName.length() - 3) + "pfb";
            rf = this.pfb == null ? new RandomAccessFileOrArray(filePfb, true, Document.plainRandomAccess) : new RandomAccessFileOrArray(this.pfb);
            int fileLength = rf.length();
            byte[] st = new byte[fileLength - 18];
            int[] lengths = new int[3];
            int bytePtr = 0;
            for (int k = 0; k < 3; ++k) {
                if (rf.read() != 128) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("start.marker.missing.in.1", filePfb));
                }
                if (rf.read() != PFB_TYPES[k]) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("incorrect.segment.type.in.1", filePfb));
                }
                int size = rf.read();
                size += rf.read() << 8;
                size += rf.read() << 16;
                lengths[k] = size += rf.read() << 24;
                while (size != 0) {
                    int got = rf.read(st, bytePtr, size);
                    if (got < 0) {
                        throw new DocumentException(MessageLocalization.getComposedMessage("premature.end.in.1", filePfb));
                    }
                    bytePtr += got;
                    size -= got;
                }
            }
            BaseFont.StreamFont streamFont = new BaseFont.StreamFont(st, lengths, this.compressionLevel);
            return streamFont;
        }
        catch (Exception e) {
            throw new DocumentException(e);
        }
        finally {
            if (rf != null) {
                try {
                    rf.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private PdfDictionary getFontDescriptor(PdfIndirectReference fontStream) {
        if (this.builtinFont) {
            return null;
        }
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber(this.Ascender));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber(this.CapHeight));
        dic.put(PdfName.DESCENT, new PdfNumber(this.Descender));
        dic.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        dic.put(PdfName.FONTNAME, new PdfName(this.FontName));
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.ItalicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(this.StdVW));
        if (fontStream != null) {
            dic.put(PdfName.FONTFILE, fontStream);
        }
        int flags = 0;
        if (this.IsFixedPitch) {
            flags |= 1;
        }
        flags |= this.fontSpecific ? 4 : 32;
        if (this.ItalicAngle < 0.0f) {
            flags |= 0x40;
        }
        if (this.FontName.contains("Caps") || this.FontName.endsWith("SC")) {
            flags |= 0x20000;
        }
        if (this.Weight.equals("Bold")) {
            flags |= 0x40000;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags));
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference fontDescriptor, int firstChar, int lastChar, byte[] shortTag) {
        boolean stdEncoding;
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
        dic.put(PdfName.BASEFONT, new PdfName(this.FontName));
        boolean bl = stdEncoding = this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman");
        if (!this.fontSpecific || this.specialMap != null) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (this.differences[k].equals(".notdef")) continue;
                firstChar = k;
                break;
            }
            if (stdEncoding) {
                dic.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary enc = new PdfDictionary(PdfName.ENCODING);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k));
                            gap = false;
                        }
                        dif.add(new PdfName(this.differences[k]));
                        continue;
                    }
                    gap = true;
                }
                enc.put(PdfName.DIFFERENCES, dif);
                dic.put(PdfName.ENCODING, enc);
            }
        }
        if (this.specialMap != null || this.forceWidthsOutput || !this.builtinFont || !this.fontSpecific && !stdEncoding) {
            dic.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
            dic.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
            PdfArray wd = new PdfArray();
            for (int k = firstChar; k <= lastChar; ++k) {
                if (shortTag[k] == 0) {
                    wd.add(new PdfNumber(0));
                    continue;
                }
                wd.add(new PdfNumber(this.widths[k]));
            }
            dic.put(PdfName.WIDTHS, wd);
        }
        if (!this.builtinFont && fontDescriptor != null) {
            dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return dic;
    }

    @Override
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
        boolean subsetp;
        int firstChar = (Integer)params[0];
        int lastChar = (Integer)params[1];
        byte[] shortTag = (byte[])params[2];
        boolean bl = subsetp = (Boolean)params[3] != false && this.subset;
        if (!subsetp) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                shortTag[k] = 1;
            }
        }
        PdfIndirectReference ind_font = null;
        PdfDictionary pobj = null;
        PdfIndirectObject obj = null;
        pobj = this.getFullFontStream();
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        if ((pobj = this.getFontDescriptor(ind_font)) != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, firstChar, lastChar, shortTag);
        writer.addToBody((PdfObject)pobj, ref);
    }

    @Override
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1: 
            case 9: {
                return (float)this.Ascender * fontSize / 1000.0f;
            }
            case 2: {
                return (float)this.CapHeight * fontSize / 1000.0f;
            }
            case 3: 
            case 10: {
                return (float)this.Descender * fontSize / 1000.0f;
            }
            case 4: {
                return this.ItalicAngle;
            }
            case 5: {
                return (float)this.llx * fontSize / 1000.0f;
            }
            case 6: {
                return (float)this.lly * fontSize / 1000.0f;
            }
            case 7: {
                return (float)this.urx * fontSize / 1000.0f;
            }
            case 8: {
                return (float)this.ury * fontSize / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return (float)(this.urx - this.llx) * fontSize / 1000.0f;
            }
            case 13: {
                return (float)this.UnderlinePosition * fontSize / 1000.0f;
            }
            case 14: {
                return (float)this.UnderlineThickness * fontSize / 1000.0f;
            }
        }
        return 0.0f;
    }

    @Override
    public String getPostscriptFontName() {
        return this.FontName;
    }

    @Override
    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", this.FullName}};
    }

    @Override
    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", this.FullName}};
    }

    @Override
    public String[][] getFamilyFontName() {
        return new String[][]{{"", "", "", this.FamilyName}};
    }

    @Override
    public boolean hasKernPairs() {
        return !this.KernPairs.isEmpty();
    }

    @Override
    public void setPostscriptFontName(String name) {
        this.FontName = name;
    }

    @Override
    public boolean setKerning(int char1, int char2, int kern) {
        String first = GlyphList.unicodeToName(char1);
        if (first == null) {
            return false;
        }
        String second = GlyphList.unicodeToName(char2);
        if (second == null) {
            return false;
        }
        Object[] obj = this.KernPairs.get(first);
        if (obj == null) {
            obj = new Object[]{second, kern};
            this.KernPairs.put(first, obj);
            return true;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (!second.equals(obj[k]) || obj.length <= k + 1) continue;
            obj[k + 1] = kern;
            return true;
        }
        int size = obj.length;
        Object[] obj2 = new Object[size + 2];
        System.arraycopy(obj, 0, obj2, 0, size);
        obj2[size] = second;
        obj2[size + 1] = kern;
        this.KernPairs.put(first, obj2);
        return true;
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        Object[] metrics;
        if (name == null) {
            metrics = this.CharMetrics.get(c);
        } else {
            if (name.equals(".notdef")) {
                return null;
            }
            metrics = this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return (int[])metrics[3];
        }
        return null;
    }

    static {
        PFB_TYPES = new int[]{1, 2, 1};
    }
}

