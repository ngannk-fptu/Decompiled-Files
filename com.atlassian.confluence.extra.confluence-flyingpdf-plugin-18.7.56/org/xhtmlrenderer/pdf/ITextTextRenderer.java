/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.BaseFont;
import java.awt.Rectangle;
import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.pdf.ITextFSFont;
import org.xhtmlrenderer.pdf.ITextFSFontMetrics;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.JustificationInfo;

public class ITextTextRenderer
implements TextRenderer {
    private static float TEXT_MEASURING_DELTA = 0.01f;

    @Override
    public void setup(FontContext context) {
    }

    @Override
    public void drawString(OutputDevice outputDevice, String string, float x, float y) {
        ((ITextOutputDevice)outputDevice).drawString(string, x, y, null);
    }

    @Override
    public void drawString(OutputDevice outputDevice, String string, float x, float y, JustificationInfo info) {
        ((ITextOutputDevice)outputDevice).drawString(string, x, y, info);
    }

    @Override
    public FSFontMetrics getFSFontMetrics(FontContext context, FSFont font, String string) {
        ITextFontResolver.FontDescription descr = ((ITextFSFont)font).getFontDescription();
        BaseFont bf = descr.getFont();
        float size = font.getSize2D();
        ITextFSFontMetrics result = new ITextFSFontMetrics();
        result.setAscent(bf.getFontDescriptor(8, size));
        result.setDescent(-bf.getFontDescriptor(6, size));
        result.setStrikethroughOffset(-descr.getYStrikeoutPosition() / 1000.0f * size);
        if (descr.getYStrikeoutSize() != 0.0f) {
            result.setStrikethroughThickness(descr.getYStrikeoutSize() / 1000.0f * size);
        } else {
            result.setStrikethroughThickness(size / 12.0f);
        }
        result.setUnderlineOffset(-descr.getUnderlinePosition() / 1000.0f * size);
        result.setUnderlineThickness(descr.getUnderlineThickness() / 1000.0f * size);
        return result;
    }

    @Override
    public int getWidth(FontContext context, FSFont font, String string) {
        BaseFont bf = ((ITextFSFont)font).getFontDescription().getFont();
        float result = bf.getWidthPoint(string, font.getSize2D());
        if ((double)result - Math.floor(result) < (double)TEXT_MEASURING_DELTA) {
            return (int)result;
        }
        return (int)Math.ceil(result);
    }

    @Override
    public void setFontScale(float scale) {
    }

    @Override
    public float getFontScale() {
        return 1.0f;
    }

    @Override
    public void setSmoothingThreshold(float fontsize) {
    }

    @Override
    public int getSmoothingLevel() {
        return 0;
    }

    @Override
    public void setSmoothingLevel(int level) {
    }

    @Override
    public Rectangle getGlyphBounds(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector, int index, float x, float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] getGlyphPositions(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FSGlyphVector getGlyphVector(OutputDevice outputDevice, FSFont font, String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawGlyphVector(OutputDevice outputDevice, FSGlyphVector vector, float x, float y) {
        throw new UnsupportedOperationException();
    }
}

