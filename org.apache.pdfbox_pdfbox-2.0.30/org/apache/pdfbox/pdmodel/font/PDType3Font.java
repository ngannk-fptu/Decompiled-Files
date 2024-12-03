/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class PDType3Font
extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType3Font.class);
    private PDResources resources;
    private COSDictionary charProcs;
    private Matrix fontMatrix;
    private BoundingBox fontBBox;
    private final ResourceCache resourceCache;

    public PDType3Font(COSDictionary fontDictionary) throws IOException {
        this(fontDictionary, null);
    }

    public PDType3Font(COSDictionary fontDictionary, ResourceCache resourceCache) throws IOException {
        super(fontDictionary);
        this.resourceCache = resourceCache;
        this.readEncoding();
    }

    @Override
    public String getName() {
        return this.dict.getNameAsString(COSName.NAME);
    }

    @Override
    protected final void readEncoding() throws IOException {
        COSBase encodingBase = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encodingBase instanceof COSName) {
            COSName encodingName = (COSName)encodingBase;
            this.encoding = Encoding.getInstance(encodingName);
            if (this.encoding == null) {
                LOG.warn((Object)("Unknown encoding: " + encodingName.getName()));
            }
        } else if (encodingBase instanceof COSDictionary) {
            this.encoding = new DictionaryEncoding((COSDictionary)encodingBase);
        }
        this.glyphList = GlyphList.getAdobeGlyphList();
    }

    @Override
    protected Encoding readEncodingFromFont() throws IOException {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override
    protected Boolean isFontSymbolic() {
        return false;
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override
    public boolean hasGlyph(String name) throws IOException {
        COSDictionary cp = this.getCharProcs();
        return cp != null && cp.getCOSStream(COSName.getPDFName(name)) != null;
    }

    @Override
    public FontBoxFont getFontBoxFont() {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override
    public Vector getDisplacement(int code) throws IOException {
        return this.getFontMatrix().transform(new Vector(this.getWidth(code), 0.0f));
    }

    @Override
    public float getWidth(int code) throws IOException {
        int firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1);
        int lastChar = this.dict.getInt(COSName.LAST_CHAR, -1);
        List<Float> widths = this.getWidths();
        if (!widths.isEmpty() && code >= firstChar && code <= lastChar) {
            if (code - firstChar >= widths.size()) {
                return 0.0f;
            }
            Float w = widths.get(code - firstChar);
            return w == null ? 0.0f : w.floatValue();
        }
        PDFontDescriptor fd = this.getFontDescriptor();
        if (fd != null) {
            return fd.getMissingWidth();
        }
        return this.getWidthFromFont(code);
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        PDType3CharProc charProc = this.getCharProc(code);
        if (charProc == null || charProc.getContentStream().getLength() == 0) {
            return 0.0f;
        }
        return charProc.getWidth();
    }

    @Override
    public boolean isEmbedded() {
        return true;
    }

    @Override
    public float getHeight(int code) throws IOException {
        PDFontDescriptor desc = this.getFontDescriptor();
        if (desc != null) {
            PDRectangle bbox = desc.getFontBoundingBox();
            float retval = 0.0f;
            if (bbox != null) {
                retval = bbox.getHeight() / 2.0f;
            }
            if (retval == 0.0f) {
                retval = desc.getCapHeight();
            }
            if (retval == 0.0f) {
                retval = desc.getAscent();
            }
            if (retval == 0.0f && (retval = desc.getXHeight()) > 0.0f) {
                retval -= desc.getDescent();
            }
            return retval;
        }
        return 0.0f;
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        throw new UnsupportedOperationException("Not implemented: Type3");
    }

    @Override
    public int readCode(InputStream in) throws IOException {
        return in.read();
    }

    @Override
    public Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            COSArray matrix = this.dict.getCOSArray(COSName.FONT_MATRIX);
            this.fontMatrix = this.checkFontMatrixValues(matrix) ? Matrix.createMatrix(matrix) : super.getFontMatrix();
        }
        return this.fontMatrix;
    }

    private boolean checkFontMatrixValues(COSArray matrix) {
        if (matrix == null || matrix.size() != 6) {
            return false;
        }
        for (COSBase cOSBase : matrix.toList()) {
            if (cOSBase instanceof COSNumber) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isDamaged() {
        return false;
    }

    @Override
    public boolean isStandard14() {
        return false;
    }

    public PDResources getResources() {
        COSBase base;
        if (this.resources == null && (base = this.dict.getDictionaryObject(COSName.RESOURCES)) instanceof COSDictionary) {
            this.resources = new PDResources((COSDictionary)base, this.resourceCache);
        }
        return this.resources;
    }

    public PDRectangle getFontBBox() {
        COSBase base = this.dict.getDictionaryObject(COSName.FONT_BBOX);
        PDRectangle retval = null;
        if (base instanceof COSArray) {
            retval = new PDRectangle((COSArray)base);
        }
        return retval;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (this.fontBBox == null) {
            this.fontBBox = this.generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() {
        COSDictionary cp;
        PDRectangle rect = this.getFontBBox();
        if (rect == null) {
            LOG.warn((Object)"FontBBox missing, returning empty rectangle");
            return new BoundingBox();
        }
        if (rect.getLowerLeftX() == 0.0f && rect.getLowerLeftY() == 0.0f && rect.getUpperRightX() == 0.0f && rect.getUpperRightY() == 0.0f && (cp = this.getCharProcs()) != null) {
            for (COSName name : cp.keySet()) {
                COSStream typ3CharProcStream = cp.getCOSStream(name);
                if (typ3CharProcStream == null) continue;
                PDType3CharProc charProc = new PDType3CharProc(this, typ3CharProcStream);
                try {
                    PDRectangle glyphBBox = charProc.getGlyphBBox();
                    if (glyphBBox == null) continue;
                    rect.setLowerLeftX(Math.min(rect.getLowerLeftX(), glyphBBox.getLowerLeftX()));
                    rect.setLowerLeftY(Math.min(rect.getLowerLeftY(), glyphBBox.getLowerLeftY()));
                    rect.setUpperRightX(Math.max(rect.getUpperRightX(), glyphBBox.getUpperRightX()));
                    rect.setUpperRightY(Math.max(rect.getUpperRightY(), glyphBBox.getUpperRightY()));
                }
                catch (IOException iOException) {}
            }
        }
        return new BoundingBox(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getUpperRightY());
    }

    public COSDictionary getCharProcs() {
        if (this.charProcs == null) {
            this.charProcs = this.dict.getCOSDictionary(COSName.CHAR_PROCS);
        }
        return this.charProcs;
    }

    public PDType3CharProc getCharProc(int code) {
        if (this.getEncoding() == null || this.getCharProcs() == null) {
            return null;
        }
        String name = this.getEncoding().getName(code);
        COSStream stream = this.getCharProcs().getCOSStream(COSName.getPDFName(name));
        return stream != null ? new PDType3CharProc(this, stream) : null;
    }
}

