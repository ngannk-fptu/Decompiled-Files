/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.util.Map;
import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.LineMetricsAdapter;
import org.xhtmlrenderer.swing.AWTFSFont;
import org.xhtmlrenderer.swing.AWTFSGlyphVector;
import org.xhtmlrenderer.swing.Java2DFontContext;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.util.Configuration;

public class Java2DTextRenderer
implements TextRenderer {
    protected float scale = Configuration.valueAsFloat("xr.text.scale", 1.0f);
    protected float threshold = Configuration.valueAsFloat("xr.text.aa-fontsize-threshhold", 25.0f);
    protected Object antiAliasRenderingHint;
    protected Object fractionalFontMetricsHint;

    public Java2DTextRenderer() {
        Object dummy = new Object();
        Object aaHint = Configuration.valueFromClassConstant("xr.text.aa-rendering-hint", dummy);
        if (aaHint == dummy) {
            try {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Map map = (Map)tk.getDesktopProperty("awt.font.desktophints");
                this.antiAliasRenderingHint = map.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            }
            catch (Exception e) {
                this.antiAliasRenderingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
            }
        } else {
            this.antiAliasRenderingHint = aaHint;
        }
        this.fractionalFontMetricsHint = "true".equals(Configuration.valueFor("xr.text.fractional-font-metrics", "false")) ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }

    @Override
    public void drawString(OutputDevice outputDevice, String string, float x, float y) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        if ((float)graphics.getFont().getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        graphics.drawString(string, (int)x, (int)y);
        if ((float)graphics.getFont().getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
    }

    @Override
    public void drawString(OutputDevice outputDevice, String string, float x, float y, JustificationInfo info) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        if ((float)graphics.getFont().getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), string);
        this.adjustGlyphPositions(string, info, vector);
        graphics.drawGlyphVector(vector, x, y);
        if ((float)graphics.getFont().getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
    }

    private void adjustGlyphPositions(String string, JustificationInfo info, GlyphVector vector) {
        float adjust = 0.0f;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (i != 0) {
                Point2D point = vector.getGlyphPosition(i);
                vector.setGlyphPosition(i, new Point2D.Double(point.getX() + (double)adjust, point.getY()));
            }
            if (c == ' ' || c == '\u00a0' || c == '\u3000') {
                adjust += info.getSpaceAdjust();
                continue;
            }
            adjust += info.getNonSpaceAdjust();
        }
    }

    @Override
    public void drawGlyphVector(OutputDevice outputDevice, FSGlyphVector fsGlyphVector, float x, float y) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        if ((float)graphics.getFont().getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = ((AWTFSGlyphVector)fsGlyphVector).getGlyphVector();
        graphics.drawGlyphVector(vector, (int)x, (int)y);
        if ((float)graphics.getFont().getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
    }

    @Override
    public void setup(FontContext fontContext) {
    }

    @Override
    public void setFontScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void setSmoothingThreshold(float fontsize) {
        this.threshold = fontsize;
    }

    @Override
    public void setSmoothingLevel(int level) {
    }

    @Override
    public FSFontMetrics getFSFontMetrics(FontContext fc, FSFont font, String string) {
        Object fracHint = null;
        Graphics2D graphics = ((Java2DFontContext)fc).getGraphics();
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        LineMetricsAdapter adapter = new LineMetricsAdapter(((AWTFSFont)font).getAWTFont().getLineMetrics(string, graphics.getFontRenderContext()));
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return adapter;
    }

    @Override
    public int getWidth(FontContext fc, FSFont font, String string) {
        Object fracHint = null;
        Graphics2D graphics = ((Java2DFontContext)fc).getGraphics();
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        Font awtFont = ((AWTFSFont)font).getAWTFont();
        int width = 0;
        width = this.fractionalFontMetricsHint == RenderingHints.VALUE_FRACTIONALMETRICS_ON ? (int)Math.round(graphics.getFontMetrics(awtFont).getStringBounds(string, graphics).getWidth()) : (int)Math.ceil(graphics.getFontMetrics(awtFont).getStringBounds(string, graphics).getWidth());
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return width;
    }

    @Override
    public float getFontScale() {
        return this.scale;
    }

    @Override
    public int getSmoothingLevel() {
        return 0;
    }

    public Object getRenderingHints() {
        return this.antiAliasRenderingHint;
    }

    public void setRenderingHints(Object renderingHints) {
        this.antiAliasRenderingHint = renderingHints;
    }

    public float[] getGlyphPositions(OutputDevice outputDevice, FSFont font, String text) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        Font awtFont = ((AWTFSFont)font).getAWTFont();
        if ((float)awtFont.getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = awtFont.createGlyphVector(graphics.getFontRenderContext(), text);
        float[] result = vector.getGlyphPositions(0, text.length() + 1, null);
        if ((float)awtFont.getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return result;
    }

    @Override
    public Rectangle getGlyphBounds(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector, int index, float x, float y) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        Font awtFont = ((AWTFSFont)font).getAWTFont();
        if ((float)awtFont.getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = ((AWTFSGlyphVector)fsGlyphVector).getGlyphVector();
        Rectangle result = vector.getGlyphPixelBounds(index, graphics.getFontRenderContext(), x, y);
        if ((float)awtFont.getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return result;
    }

    @Override
    public float[] getGlyphPositions(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        Font awtFont = ((AWTFSFont)font).getAWTFont();
        if ((float)awtFont.getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = ((AWTFSGlyphVector)fsGlyphVector).getGlyphVector();
        float[] result = vector.getGlyphPositions(0, vector.getNumGlyphs() + 1, null);
        if ((float)awtFont.getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return result;
    }

    @Override
    public FSGlyphVector getGlyphVector(OutputDevice outputDevice, FSFont font, String text) {
        Object aaHint = null;
        Object fracHint = null;
        Graphics2D graphics = ((Java2DOutputDevice)outputDevice).getGraphics();
        Font awtFont = ((AWTFSFont)font).getAWTFont();
        if ((float)awtFont.getSize() > this.threshold) {
            aaHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasRenderingHint);
        }
        fracHint = graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalFontMetricsHint);
        GlyphVector vector = awtFont.createGlyphVector(graphics.getFontRenderContext(), text);
        if ((float)awtFont.getSize() > this.threshold) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        }
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fracHint);
        return new AWTFSGlyphVector(vector);
    }
}

