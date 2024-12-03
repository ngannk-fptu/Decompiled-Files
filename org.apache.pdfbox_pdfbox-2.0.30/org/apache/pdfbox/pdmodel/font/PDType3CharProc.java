/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.util.Matrix;

public final class PDType3CharProc
implements COSObjectable,
PDContentStream {
    private static final Log LOG = LogFactory.getLog(PDType3CharProc.class);
    private final PDType3Font font;
    private final COSStream charStream;

    public PDType3CharProc(PDType3Font font, COSStream charStream) {
        this.font = font;
        this.charStream = charStream;
    }

    @Override
    public COSStream getCOSObject() {
        return this.charStream;
    }

    public PDType3Font getFont() {
        return this.font;
    }

    public PDStream getContentStream() {
        return new PDStream(this.charStream);
    }

    @Override
    public InputStream getContents() throws IOException {
        return this.charStream.createInputStream();
    }

    @Override
    public PDResources getResources() {
        if (this.charStream.containsKey(COSName.RESOURCES)) {
            LOG.warn((Object)"Using resources dictionary found in charproc entry");
            LOG.warn((Object)"This should have been in the font or in the page dictionary");
            return new PDResources((COSDictionary)this.charStream.getDictionaryObject(COSName.RESOURCES));
        }
        return this.font.getResources();
    }

    @Override
    public PDRectangle getBBox() {
        return this.font.getFontBBox();
    }

    public PDRectangle getGlyphBBox() throws IOException {
        ArrayList<COSBase> arguments = new ArrayList<COSBase>();
        PDFStreamParser parser = new PDFStreamParser(this);
        Object token = parser.parseNextToken();
        while (token != null) {
            if (token instanceof Operator) {
                if (((Operator)token).getName().equals("d1") && arguments.size() == 6) {
                    for (int i = 0; i < 6; ++i) {
                        if (arguments.get(i) instanceof COSNumber) continue;
                        return null;
                    }
                    float x = ((COSNumber)arguments.get(2)).floatValue();
                    float y = ((COSNumber)arguments.get(3)).floatValue();
                    return new PDRectangle(x, y, ((COSNumber)arguments.get(4)).floatValue() - x, ((COSNumber)arguments.get(5)).floatValue() - y);
                }
                return null;
            }
            arguments.add((COSBase)token);
            token = parser.parseNextToken();
        }
        return null;
    }

    @Override
    public Matrix getMatrix() {
        return this.font.getFontMatrix();
    }

    public float getWidth() throws IOException {
        ArrayList<COSBase> arguments = new ArrayList<COSBase>();
        PDFStreamParser parser = new PDFStreamParser(this);
        Object token = parser.parseNextToken();
        while (token != null) {
            if (token instanceof Operator) {
                return this.parseWidth((Operator)token, arguments);
            }
            arguments.add((COSBase)token);
            token = parser.parseNextToken();
        }
        throw new IOException("Unexpected end of stream");
    }

    private float parseWidth(Operator operator, List<COSBase> arguments) throws IOException {
        if (operator.getName().equals("d0") || operator.getName().equals("d1")) {
            COSBase obj = arguments.get(0);
            if (obj instanceof COSNumber) {
                return ((COSNumber)obj).floatValue();
            }
            throw new IOException("Unexpected argument type: " + obj.getClass().getName());
        }
        throw new IOException("First operator must be d0 or d1");
    }
}

