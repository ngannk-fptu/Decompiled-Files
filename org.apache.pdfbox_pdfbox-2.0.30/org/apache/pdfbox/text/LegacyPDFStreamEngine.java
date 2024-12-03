/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.contentstream.operator.text.BeginText;
import org.apache.pdfbox.contentstream.operator.text.EndText;
import org.apache.pdfbox.contentstream.operator.text.MoveText;
import org.apache.pdfbox.contentstream.operator.text.MoveTextSetLeading;
import org.apache.pdfbox.contentstream.operator.text.NextLine;
import org.apache.pdfbox.contentstream.operator.text.SetCharSpacing;
import org.apache.pdfbox.contentstream.operator.text.SetFontAndSize;
import org.apache.pdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import org.apache.pdfbox.contentstream.operator.text.SetTextLeading;
import org.apache.pdfbox.contentstream.operator.text.SetTextRenderingMode;
import org.apache.pdfbox.contentstream.operator.text.SetTextRise;
import org.apache.pdfbox.contentstream.operator.text.SetWordSpacing;
import org.apache.pdfbox.contentstream.operator.text.ShowText;
import org.apache.pdfbox.contentstream.operator.text.ShowTextAdjusted;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLine;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

class LegacyPDFStreamEngine
extends PDFStreamEngine {
    private static final Log LOG = LogFactory.getLog(LegacyPDFStreamEngine.class);
    private int pageRotation;
    private PDRectangle pageSize;
    private Matrix translateMatrix;
    private static final GlyphList GLYPHLIST;
    private final Map<COSDictionary, Float> fontHeightMap = new WeakHashMap<COSDictionary, Float>();

    LegacyPDFStreamEngine() throws IOException {
        this.addOperator(new BeginText());
        this.addOperator(new Concatenate());
        this.addOperator(new DrawObject());
        this.addOperator(new EndText());
        this.addOperator(new SetGraphicsStateParameters());
        this.addOperator(new Save());
        this.addOperator(new Restore());
        this.addOperator(new NextLine());
        this.addOperator(new SetCharSpacing());
        this.addOperator(new MoveText());
        this.addOperator(new MoveTextSetLeading());
        this.addOperator(new SetFontAndSize());
        this.addOperator(new ShowText());
        this.addOperator(new ShowTextAdjusted());
        this.addOperator(new SetTextLeading());
        this.addOperator(new SetMatrix());
        this.addOperator(new SetTextRenderingMode());
        this.addOperator(new SetTextRise());
        this.addOperator(new SetWordSpacing());
        this.addOperator(new SetTextHorizontalScaling());
        this.addOperator(new ShowTextLine());
        this.addOperator(new ShowTextLineAndSpace());
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        this.pageRotation = page.getRotation();
        this.pageSize = page.getCropBox();
        this.translateMatrix = this.pageSize.getLowerLeftX() == 0.0f && this.pageSize.getLowerLeftY() == 0.0f ? null : Matrix.getTranslateInstance(-this.pageSize.getLowerLeftX(), -this.pageSize.getLowerLeftY());
        super.processPage(page);
    }

    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
        Matrix translatedTextRenderingMatrix;
        PDGraphicsState state = this.getGraphicsState();
        Matrix ctm = state.getCurrentTransformationMatrix();
        float fontSize = state.getTextState().getFontSize();
        float horizontalScaling = state.getTextState().getHorizontalScaling() / 100.0f;
        Matrix textMatrix = this.getTextMatrix();
        float displacementX = displacement.getX();
        if (font.isVertical()) {
            PDCIDFont cidFont;
            displacementX = font.getWidth(code) / 1000.0f;
            TrueTypeFont ttf = null;
            if (font instanceof PDTrueTypeFont) {
                ttf = ((PDTrueTypeFont)font).getTrueTypeFont();
            } else if (font instanceof PDType0Font && (cidFont = ((PDType0Font)font).getDescendantFont()) instanceof PDCIDFontType2) {
                ttf = ((PDCIDFontType2)cidFont).getTrueTypeFont();
            }
            if (ttf != null && ttf.getUnitsPerEm() != 1000) {
                displacementX *= 1000.0f / (float)ttf.getUnitsPerEm();
            }
        }
        float tx = displacementX * fontSize * horizontalScaling;
        float ty = displacement.getY() * fontSize;
        Matrix td = Matrix.getTranslateInstance(tx, ty);
        Matrix nextTextRenderingMatrix = td.multiply(textMatrix).multiply(ctm);
        float nextX = nextTextRenderingMatrix.getTranslateX();
        float nextY = nextTextRenderingMatrix.getTranslateY();
        float dxDisplay = nextX - textRenderingMatrix.getTranslateX();
        Float fontHeight = this.fontHeightMap.get(font.getCOSObject());
        if (fontHeight == null) {
            fontHeight = Float.valueOf(this.computeFontHeight(font));
            this.fontHeightMap.put(font.getCOSObject(), fontHeight);
        }
        float dyDisplay = fontHeight.floatValue() * textRenderingMatrix.getScalingFactorY();
        float glyphSpaceToTextSpaceFactor = 0.001f;
        if (font instanceof PDType3Font) {
            glyphSpaceToTextSpaceFactor = font.getFontMatrix().getScaleX();
        }
        float spaceWidthText = 0.0f;
        try {
            spaceWidthText = font.getSpaceWidth() * glyphSpaceToTextSpaceFactor;
        }
        catch (Throwable exception) {
            LOG.warn((Object)exception, exception);
        }
        if (spaceWidthText == 0.0f) {
            spaceWidthText = font.getAverageFontWidth() * glyphSpaceToTextSpaceFactor;
            spaceWidthText *= 0.8f;
        }
        if (spaceWidthText == 0.0f) {
            spaceWidthText = 1.0f;
        }
        float spaceWidthDisplay = spaceWidthText * textRenderingMatrix.getScalingFactorX();
        String unicodeMapping = font.toUnicode(code, GLYPHLIST);
        if (unicodeMapping == null) {
            if (font instanceof PDSimpleFont) {
                char c = (char)code;
                unicodeMapping = new String(new char[]{c});
            } else {
                return;
            }
        }
        if (this.translateMatrix == null) {
            translatedTextRenderingMatrix = textRenderingMatrix;
        } else {
            translatedTextRenderingMatrix = Matrix.concatenate(this.translateMatrix, textRenderingMatrix);
            nextX -= this.pageSize.getLowerLeftX();
            nextY -= this.pageSize.getLowerLeftY();
        }
        this.processTextPosition(new TextPosition(this.pageRotation, this.pageSize.getWidth(), this.pageSize.getHeight(), translatedTextRenderingMatrix, nextX, nextY, Math.abs(dyDisplay), dxDisplay, Math.abs(spaceWidthDisplay), unicodeMapping, new int[]{code}, font, fontSize, (int)(fontSize * textMatrix.getScalingFactorX())));
    }

    protected float computeFontHeight(PDFont font) throws IOException {
        BoundingBox bbox = font.getBoundingBox();
        if (bbox.getLowerLeftY() < -32768.0f) {
            bbox.setLowerLeftY(-(bbox.getLowerLeftY() + 65536.0f));
        }
        float glyphHeight = bbox.getHeight() / 2.0f;
        PDFontDescriptor fontDescriptor = font.getFontDescriptor();
        if (fontDescriptor != null) {
            float capHeight = fontDescriptor.getCapHeight();
            if (Float.compare(capHeight, 0.0f) != 0 && (capHeight < glyphHeight || Float.compare(glyphHeight, 0.0f) == 0)) {
                glyphHeight = capHeight;
            }
            float ascent = fontDescriptor.getAscent();
            float descent = fontDescriptor.getDescent();
            if (capHeight > ascent && ascent > 0.0f && descent < 0.0f && ((ascent - descent) / 2.0f < glyphHeight || Float.compare(glyphHeight, 0.0f) == 0)) {
                glyphHeight = (ascent - descent) / 2.0f;
            }
        }
        float height = font instanceof PDType3Font ? font.getFontMatrix().transformPoint((float)0.0f, (float)glyphHeight).y : glyphHeight / 1000.0f;
        return height;
    }

    protected void processTextPosition(TextPosition text) {
    }

    static {
        String path = "/org/apache/pdfbox/resources/glyphlist/additional.txt";
        InputStream input = GlyphList.class.getResourceAsStream(path);
        try {
            GLYPHLIST = new GlyphList(GlyphList.getAdobeGlyphList(), input);
            input.close();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

