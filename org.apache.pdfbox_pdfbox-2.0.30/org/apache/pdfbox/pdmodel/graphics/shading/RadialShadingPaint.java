/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType3;
import org.apache.pdfbox.pdmodel.graphics.shading.RadialShadingContext;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadingPaint;
import org.apache.pdfbox.util.Matrix;

public class RadialShadingPaint
extends ShadingPaint<PDShadingType3> {
    private static final Log LOG = LogFactory.getLog(RadialShadingPaint.class);

    RadialShadingPaint(PDShadingType3 shading, Matrix matrix) {
        super(shading, matrix);
    }

    @Override
    public int getTransparency() {
        return 0;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        try {
            return new RadialShadingContext((PDShadingType3)this.shading, cm, xform, this.matrix, deviceBounds);
        }
        catch (IOException e) {
            LOG.error((Object)"An error occurred while painting", (Throwable)e);
            return new Color(0, 0, 0, 0).createContext(cm, deviceBounds, userBounds, xform, hints);
        }
    }
}

