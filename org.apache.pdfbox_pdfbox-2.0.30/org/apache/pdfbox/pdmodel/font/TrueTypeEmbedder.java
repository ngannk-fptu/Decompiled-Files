/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.ttf.CmapLookup
 *  org.apache.fontbox.ttf.CmapSubtable
 *  org.apache.fontbox.ttf.HeaderTable
 *  org.apache.fontbox.ttf.HorizontalHeaderTable
 *  org.apache.fontbox.ttf.OS2WindowsMetricsTable
 *  org.apache.fontbox.ttf.PostScriptTable
 *  org.apache.fontbox.ttf.TTFParser
 *  org.apache.fontbox.ttf.TTFSubsetter
 *  org.apache.fontbox.ttf.TrueTypeFont
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TTFSubsetter;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.Subsetter;

abstract class TrueTypeEmbedder
implements Subsetter {
    private static final int ITALIC = 1;
    private static final int OBLIQUE = 512;
    private static final String BASE25 = "BCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final PDDocument document;
    protected TrueTypeFont ttf;
    protected PDFontDescriptor fontDescriptor;
    @Deprecated
    protected final CmapSubtable cmap;
    protected final CmapLookup cmapLookup;
    private final Set<Integer> subsetCodePoints = new HashSet<Integer>();
    private final boolean embedSubset;

    TrueTypeEmbedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        this.document = document;
        this.embedSubset = embedSubset;
        this.ttf = ttf;
        this.fontDescriptor = this.createFontDescriptor(ttf);
        if (!this.isEmbeddingPermitted(ttf)) {
            throw new IOException("This font does not permit embedding");
        }
        if (!embedSubset) {
            InputStream is = ttf.getOriginalData();
            byte[] b = new byte[4];
            is.mark(b.length);
            if (is.read(b) == b.length && new String(b).equals("ttcf")) {
                is.close();
                throw new IOException("Full embedding of TrueType font collections not supported");
            }
            if (is.markSupported()) {
                is.reset();
            } else {
                is.close();
                is = ttf.getOriginalData();
            }
            PDStream stream = new PDStream(document, is, COSName.FLATE_DECODE);
            stream.getCOSObject().setLong(COSName.LENGTH1, ttf.getOriginalDataSize());
            this.fontDescriptor.setFontFile2(stream);
        }
        dict.setName(COSName.BASE_FONT, ttf.getName());
        this.cmap = ttf.getUnicodeCmap();
        this.cmapLookup = ttf.getUnicodeCmapLookup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void buildFontFile2(InputStream ttfStream) throws IOException {
        PDStream stream = new PDStream(this.document, ttfStream, COSName.FLATE_DECODE);
        COSInputStream input = null;
        try {
            input = stream.createInputStream();
            this.ttf = new TTFParser().parseEmbedded((InputStream)input);
            if (!this.isEmbeddingPermitted(this.ttf)) {
                throw new IOException("This font does not permit embedding");
            }
            if (this.fontDescriptor == null) {
                this.fontDescriptor = this.createFontDescriptor(this.ttf);
            }
        }
        finally {
            IOUtils.closeQuietly(input);
        }
        stream.getCOSObject().setLong(COSName.LENGTH1, this.ttf.getOriginalDataSize());
        this.fontDescriptor.setFontFile2(stream);
    }

    boolean isEmbeddingPermitted(TrueTypeFont ttf) throws IOException {
        if (ttf.getOS2Windows() != null) {
            short fsType = ttf.getOS2Windows().getFsType();
            int maskedFsType = fsType & 0xF;
            if (maskedFsType == 2) {
                return false;
            }
            if ((fsType & 0x200) == 512) {
                return false;
            }
        }
        return true;
    }

    private boolean isSubsettingPermitted(TrueTypeFont ttf) throws IOException {
        short fsType;
        return ttf.getOS2Windows() == null || ((fsType = ttf.getOS2Windows().getFsType()) & 0x100) != 256;
    }

    private PDFontDescriptor createFontDescriptor(TrueTypeFont ttf) throws IOException {
        String ttfName = ttf.getName();
        OS2WindowsMetricsTable os2 = ttf.getOS2Windows();
        if (os2 == null) {
            throw new IOException("os2 table is missing in font " + ttfName);
        }
        PostScriptTable post = ttf.getPostScript();
        if (post == null) {
            throw new IOException("post table is missing in font " + ttfName);
        }
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(ttfName);
        HorizontalHeaderTable hhea = ttf.getHorizontalHeader();
        fd.setFixedPitch(post.getIsFixedPitch() > 0L || hhea.getNumberOfHMetrics() == 1);
        int fsSelection = os2.getFsSelection();
        fd.setItalic((fsSelection & 0x201) != 0);
        switch (os2.getFamilyClass()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 7: {
                fd.setSerif(true);
                break;
            }
            case 10: {
                fd.setScript(true);
                break;
            }
        }
        fd.setFontWeight(os2.getWeightClass());
        fd.setSymbolic(true);
        fd.setNonSymbolic(false);
        fd.setItalicAngle(post.getItalicAngle());
        HeaderTable header = ttf.getHeader();
        PDRectangle rect = new PDRectangle();
        float scaling = 1000.0f / (float)header.getUnitsPerEm();
        rect.setLowerLeftX((float)header.getXMin() * scaling);
        rect.setLowerLeftY((float)header.getYMin() * scaling);
        rect.setUpperRightX((float)header.getXMax() * scaling);
        rect.setUpperRightY((float)header.getYMax() * scaling);
        fd.setFontBoundingBox(rect);
        fd.setAscent((float)hhea.getAscender() * scaling);
        fd.setDescent((float)hhea.getDescender() * scaling);
        if ((double)os2.getVersion() >= 1.2) {
            fd.setCapHeight((float)os2.getCapHeight() * scaling);
            fd.setXHeight((float)os2.getHeight() * scaling);
        } else {
            GeneralPath capHPath = ttf.getPath("H");
            if (capHPath != null) {
                fd.setCapHeight((float)Math.round(capHPath.getBounds2D().getMaxY()) * scaling);
            } else {
                fd.setCapHeight((float)(os2.getTypoAscender() + os2.getTypoDescender()) * scaling);
            }
            GeneralPath xPath = ttf.getPath("x");
            if (xPath != null) {
                fd.setXHeight((float)Math.round(xPath.getBounds2D().getMaxY()) * scaling);
            } else {
                fd.setXHeight((float)os2.getTypoAscender() / 2.0f * scaling);
            }
        }
        fd.setStemV(fd.getFontBoundingBox().getWidth() * 0.13f);
        return fd;
    }

    @Deprecated
    public TrueTypeFont getTrueTypeFont() {
        return this.ttf;
    }

    public PDFontDescriptor getFontDescriptor() {
        return this.fontDescriptor;
    }

    @Override
    public void addToSubset(int codePoint) {
        this.subsetCodePoints.add(codePoint);
    }

    @Override
    public void subset() throws IOException {
        if (!this.isSubsettingPermitted(this.ttf)) {
            throw new IOException("This font does not permit subsetting");
        }
        if (!this.embedSubset) {
            throw new IllegalStateException("Subsetting is disabled");
        }
        ArrayList<String> tables = new ArrayList<String>();
        tables.add("head");
        tables.add("hhea");
        tables.add("loca");
        tables.add("maxp");
        tables.add("cvt ");
        tables.add("prep");
        tables.add("glyf");
        tables.add("hmtx");
        tables.add("fpgm");
        tables.add("gasp");
        TTFSubsetter subsetter = new TTFSubsetter(this.ttf, tables);
        subsetter.addAll(this.subsetCodePoints);
        Map gidToCid = subsetter.getGIDMap();
        String tag = this.getTag(gidToCid);
        subsetter.setPrefix(tag);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        subsetter.writeToStream((OutputStream)out);
        this.buildSubset(new ByteArrayInputStream(out.toByteArray()), tag, gidToCid);
        this.ttf.close();
    }

    public boolean needsSubset() {
        return this.embedSubset;
    }

    protected abstract void buildSubset(InputStream var1, String var2, Map<Integer, Integer> var3) throws IOException;

    public String getTag(Map<Integer, Integer> gidToCid) {
        long div;
        long num = gidToCid.hashCode();
        StringBuilder sb = new StringBuilder();
        do {
            div = num / 25L;
            int mod = (int)(num % 25L);
            sb.append(BASE25.charAt(mod));
        } while ((num = div) != 0L && sb.length() < 6);
        while (sb.length() < 6) {
            sb.insert(0, 'A');
        }
        sb.append('+');
        return sb.toString();
    }
}

