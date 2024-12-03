/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType7;
import org.apache.pdfbox.pdmodel.graphics.shading.PatchMeshesShadingContext;
import org.apache.pdfbox.util.Matrix;

class Type7ShadingContext
extends PatchMeshesShadingContext {
    Type7ShadingContext(PDShadingType7 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, colorModel, xform, matrix, deviceBounds, 16);
    }
}

