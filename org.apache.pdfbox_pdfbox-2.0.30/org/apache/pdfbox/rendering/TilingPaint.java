/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.rendering;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.util.Matrix;

class TilingPaint
implements Paint {
    private static final Log LOG;
    private final Paint paint;
    private final Matrix patternMatrix;
    private static final int MAXEDGE;
    private static final String DEFAULTMAXEDGE = "3000";

    TilingPaint(PageDrawer drawer, PDTilingPattern pattern, AffineTransform xform) throws IOException {
        this(drawer, pattern, null, null, xform);
    }

    TilingPaint(PageDrawer drawer, PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform) throws IOException {
        this.patternMatrix = Matrix.concatenate(drawer.getInitialMatrix(), pattern.getMatrix());
        Rectangle2D anchorRect = this.getAnchorRect(pattern);
        this.paint = new TexturePaint(this.getImage(drawer, pattern, colorSpace, color, xform, anchorRect), anchorRect);
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        AffineTransform xformPattern = (AffineTransform)xform.clone();
        AffineTransform patternNoScale = this.patternMatrix.createAffineTransform();
        patternNoScale.scale(1.0f / this.patternMatrix.getScalingFactorX(), 1.0f / this.patternMatrix.getScalingFactorY());
        xformPattern.concatenate(patternNoScale);
        return this.paint.createContext(cm, deviceBounds, userBounds, xformPattern, hints);
    }

    private BufferedImage getImage(PageDrawer drawer, PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform, Rectangle2D anchorRect) throws IOException {
        float width = (float)Math.abs(anchorRect.getWidth());
        float height = (float)Math.abs(anchorRect.getHeight());
        Matrix xformMatrix = new Matrix(xform);
        float xScale = Math.abs(xformMatrix.getScalingFactorX());
        float yScale = Math.abs(xformMatrix.getScalingFactorY());
        int rasterWidth = Math.max(1, TilingPaint.ceiling(width *= xScale));
        int rasterHeight = Math.max(1, TilingPaint.ceiling(height *= yScale));
        BufferedImage image = new BufferedImage(rasterWidth, rasterHeight, 2);
        Graphics2D graphics = image.createGraphics();
        if (pattern.getYStep() < 0.0f) {
            graphics.translate(0, rasterHeight);
            graphics.scale(1.0, -1.0);
        }
        if (pattern.getXStep() < 0.0f) {
            graphics.translate(rasterWidth, 0);
            graphics.scale(-1.0, 1.0);
        }
        graphics.scale(xScale, yScale);
        Matrix newPatternMatrix = Matrix.getScaleInstance(Math.abs(this.patternMatrix.getScalingFactorX()), Math.abs(this.patternMatrix.getScalingFactorY()));
        PDRectangle bbox = pattern.getBBox();
        newPatternMatrix.concatenate(Matrix.getTranslateInstance(-bbox.getLowerLeftX(), -bbox.getLowerLeftY()));
        drawer.drawTilingPattern(graphics, pattern, colorSpace, color, newPatternMatrix);
        graphics.dispose();
        return image;
    }

    private static int ceiling(double num) {
        BigDecimal decimal = BigDecimal.valueOf(num);
        decimal = decimal.setScale(5, RoundingMode.CEILING);
        return decimal.intValue();
    }

    @Override
    public int getTransparency() {
        return 3;
    }

    private Rectangle2D getAnchorRect(PDTilingPattern pattern) throws IOException {
        float yScale;
        float height;
        float xScale;
        float width;
        float yStep;
        PDRectangle bbox = pattern.getBBox();
        if (bbox == null) {
            throw new IOException("Pattern /BBox is missing");
        }
        float xStep = pattern.getXStep();
        if (xStep == 0.0f) {
            LOG.warn((Object)"/XStep is 0, using pattern /BBox width");
            xStep = bbox.getWidth();
        }
        if ((yStep = pattern.getYStep()) == 0.0f) {
            LOG.warn((Object)"/YStep is 0, using pattern /BBox height");
            yStep = bbox.getHeight();
        }
        if (Math.abs((width = xStep * (xScale = this.patternMatrix.getScalingFactorX())) * (height = yStep * (yScale = this.patternMatrix.getScalingFactorY()))) > (float)(MAXEDGE * MAXEDGE)) {
            LOG.warn((Object)("Pattern surface larger than " + MAXEDGE + " x " + MAXEDGE + ", will be clipped"));
            LOG.warn((Object)("width: " + width + ", height: " + height));
            LOG.warn((Object)("XStep: " + xStep + ", YStep: " + yStep));
            LOG.warn((Object)("bbox: " + bbox));
            LOG.warn((Object)("pattern matrix: " + pattern.getMatrix()));
            LOG.warn((Object)("concatenated matrix: " + this.patternMatrix));
            LOG.warn((Object)"increase the property 'pdfbox.rendering.tilingpaint.maxedge'");
            width = Math.min((float)MAXEDGE, Math.abs(width)) * Math.signum(width);
            height = Math.min((float)MAXEDGE, Math.abs(height)) * Math.signum(height);
        }
        return new Rectangle2D.Float(bbox.getLowerLeftX() * xScale, bbox.getLowerLeftY() * yScale, width, height);
    }

    static {
        int val;
        LOG = LogFactory.getLog(TilingPaint.class);
        String s = System.getProperty("pdfbox.rendering.tilingpaint.maxedge", DEFAULTMAXEDGE);
        try {
            val = Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            LOG.error((Object)"Default will be used", (Throwable)ex);
            val = Integer.parseInt(DEFAULTMAXEDGE);
        }
        MAXEDGE = val;
    }
}

