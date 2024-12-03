/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.afm.FontMetrics
 *  org.apache.fontbox.encoding.BuiltInEncoding
 *  org.apache.fontbox.pfb.PfbParser
 *  org.apache.fontbox.type1.Type1Font
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.encoding.BuiltInEncoding;
import org.apache.fontbox.pfb.PfbParser;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;

class PDType1FontEmbedder {
    private final Encoding fontEncoding;
    private final Type1Font type1;

    PDType1FontEmbedder(PDDocument doc, COSDictionary dict, InputStream pfbStream, Encoding encoding) throws IOException {
        dict.setItem(COSName.SUBTYPE, (COSBase)COSName.TYPE1);
        byte[] pfbBytes = IOUtils.toByteArray(pfbStream);
        PfbParser pfbParser = new PfbParser(pfbBytes);
        this.type1 = Type1Font.createWithPFB((byte[])pfbBytes);
        this.fontEncoding = encoding == null ? Type1Encoding.fromFontBox(this.type1.getEncoding()) : encoding;
        PDFontDescriptor fd = PDType1FontEmbedder.buildFontDescriptor(this.type1);
        PDStream fontStream = new PDStream(doc, pfbParser.getInputStream(), COSName.FLATE_DECODE);
        fontStream.getCOSObject().setInt("Length", pfbParser.size());
        for (int i = 0; i < pfbParser.getLengths().length; ++i) {
            fontStream.getCOSObject().setInt("Length" + (i + 1), pfbParser.getLengths()[i]);
        }
        fd.setFontFile(fontStream);
        dict.setItem(COSName.FONT_DESC, (COSObjectable)fd);
        dict.setName(COSName.BASE_FONT, this.type1.getName());
        ArrayList<Integer> widths = new ArrayList<Integer>(256);
        for (int code = 0; code <= 255; ++code) {
            String name = this.fontEncoding.getName(code);
            int width = Math.round(this.type1.getWidth(name));
            widths.add(width);
        }
        dict.setInt(COSName.FIRST_CHAR, 0);
        dict.setInt(COSName.LAST_CHAR, 255);
        dict.setItem(COSName.WIDTHS, (COSBase)COSArrayList.converterToCOSArray(widths));
        dict.setItem(COSName.ENCODING, (COSObjectable)encoding);
    }

    static PDFontDescriptor buildFontDescriptor(Type1Font type1) {
        boolean isSymbolic = type1.getEncoding() instanceof BuiltInEncoding;
        BoundingBox bbox = type1.getFontBBox();
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(type1.getName());
        fd.setFontFamily(type1.getFamilyName());
        fd.setNonSymbolic(!isSymbolic);
        fd.setSymbolic(isSymbolic);
        fd.setFontBoundingBox(new PDRectangle(bbox));
        fd.setItalicAngle(type1.getItalicAngle());
        fd.setAscent(bbox.getUpperRightY());
        fd.setDescent(bbox.getLowerLeftY());
        fd.setCapHeight(((Number)type1.getBlueValues().get(2)).floatValue());
        fd.setStemV(0.0f);
        return fd;
    }

    static PDFontDescriptor buildFontDescriptor(FontMetrics metrics) {
        boolean isSymbolic = metrics.getEncodingScheme().equals("FontSpecific");
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(metrics.getFontName());
        fd.setFontFamily(metrics.getFamilyName());
        fd.setNonSymbolic(!isSymbolic);
        fd.setSymbolic(isSymbolic);
        fd.setFontBoundingBox(new PDRectangle(metrics.getFontBBox()));
        fd.setItalicAngle(metrics.getItalicAngle());
        fd.setAscent(metrics.getAscender());
        fd.setDescent(metrics.getDescender());
        fd.setCapHeight(metrics.getCapHeight());
        fd.setXHeight(metrics.getXHeight());
        fd.setAverageWidth(metrics.getAverageCharacterWidth());
        fd.setCharacterSet(metrics.getCharacterSet());
        fd.setStemV(0.0f);
        return fd;
    }

    public Encoding getFontEncoding() {
        return this.fontEncoding;
    }

    public GlyphList getGlyphList() {
        return GlyphList.getAdobeGlyphList();
    }

    public Type1Font getType1Font() {
        return this.type1;
    }
}

