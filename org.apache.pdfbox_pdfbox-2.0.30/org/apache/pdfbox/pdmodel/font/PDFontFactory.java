/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDMMType1Font;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;

public final class PDFontFactory {
    private static final Log LOG = LogFactory.getLog(PDFontFactory.class);

    private PDFontFactory() {
    }

    public static PDFont createFont(COSDictionary dictionary) throws IOException {
        return PDFontFactory.createFont(dictionary, null);
    }

    public static PDFont createFont(COSDictionary dictionary, ResourceCache resourceCache) throws IOException {
        COSName subType;
        COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
        if (!COSName.FONT.equals(type)) {
            LOG.error((Object)("Expected 'Font' dictionary but found '" + type.getName() + "'"));
        }
        if (COSName.TYPE1.equals(subType = dictionary.getCOSName(COSName.SUBTYPE))) {
            COSBase fd = dictionary.getDictionaryObject(COSName.FONT_DESC);
            if (fd instanceof COSDictionary && ((COSDictionary)fd).containsKey(COSName.FONT_FILE3)) {
                return new PDType1CFont(dictionary);
            }
            return new PDType1Font(dictionary);
        }
        if (COSName.MM_TYPE1.equals(subType)) {
            COSBase fd = dictionary.getDictionaryObject(COSName.FONT_DESC);
            if (fd instanceof COSDictionary && ((COSDictionary)fd).containsKey(COSName.FONT_FILE3)) {
                return new PDType1CFont(dictionary);
            }
            return new PDMMType1Font(dictionary);
        }
        if (COSName.TRUE_TYPE.equals(subType)) {
            return new PDTrueTypeFont(dictionary);
        }
        if (COSName.TYPE3.equals(subType)) {
            return new PDType3Font(dictionary, resourceCache);
        }
        if (COSName.TYPE0.equals(subType)) {
            return new PDType0Font(dictionary);
        }
        if (COSName.CID_FONT_TYPE0.equals(subType)) {
            throw new IOException("Type 0 descendant font not allowed");
        }
        if (COSName.CID_FONT_TYPE2.equals(subType)) {
            throw new IOException("Type 2 descendant font not allowed");
        }
        LOG.warn((Object)("Invalid font subtype '" + subType + "'"));
        return new PDType1Font(dictionary);
    }

    static PDCIDFont createDescendantFont(COSDictionary dictionary, PDType0Font parent) throws IOException {
        COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
        if (!COSName.FONT.equals(type)) {
            throw new IOException("Expected 'Font' dictionary but found '" + type.getName() + "'");
        }
        COSName subType = dictionary.getCOSName(COSName.SUBTYPE);
        if (COSName.CID_FONT_TYPE0.equals(subType)) {
            return new PDCIDFontType0(dictionary, parent);
        }
        if (COSName.CID_FONT_TYPE2.equals(subType)) {
            return new PDCIDFontType2(dictionary, parent);
        }
        throw new IOException("Invalid font type: " + type);
    }

    @Deprecated
    public static PDFont createDefaultFont() throws IOException {
        return PDType1Font.HELVETICA;
    }
}

