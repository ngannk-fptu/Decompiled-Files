/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.graphics.shading.GouraudShadingContext;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType4;
import org.apache.pdfbox.util.Matrix;

class Type4ShadingContext
extends GouraudShadingContext {
    private static final Log LOG = LogFactory.getLog(Type4ShadingContext.class);
    private final int bitsPerFlag;

    Type4ShadingContext(PDShadingType4 shading, ColorModel cm, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, cm, xform, matrix);
        LOG.debug((Object)"Type4ShadingContext");
        this.bitsPerFlag = shading.getBitsPerFlag();
        LOG.debug((Object)("bitsPerFlag: " + this.bitsPerFlag));
        this.setTriangleList(shading.collectTriangles(xform, matrix));
        this.createPixelTable(deviceBounds);
    }
}

