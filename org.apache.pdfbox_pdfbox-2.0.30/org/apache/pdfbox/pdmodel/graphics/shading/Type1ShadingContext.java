/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.PaintContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType1;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext;
import org.apache.pdfbox.util.Matrix;

class Type1ShadingContext
extends ShadingContext
implements PaintContext {
    private static final Log LOG = LogFactory.getLog(Type1ShadingContext.class);
    private PDShadingType1 type1ShadingType;
    private AffineTransform rat;
    private final float[] domain;

    Type1ShadingContext(PDShadingType1 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.type1ShadingType = shading;
        this.domain = shading.getDomain() != null ? shading.getDomain().toFloatArray() : new float[]{0.0f, 1.0f, 0.0f, 1.0f};
        try {
            this.rat = shading.getMatrix().createAffineTransform().createInverse();
            this.rat.concatenate(matrix.createAffineTransform().createInverse());
            this.rat.concatenate(xform.createInverse());
        }
        catch (NoninvertibleTransformException ex) {
            LOG.error((Object)(ex.getMessage() + ", matrix: " + matrix), (Throwable)ex);
            this.rat = new AffineTransform();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        this.type1ShadingType = null;
    }

    @Override
    public ColorModel getColorModel() {
        return super.getColorModel();
    }

    @Override
    public Raster getRaster(int x, int y, int w, int h) {
        WritableRaster raster = this.getColorModel().createCompatibleWritableRaster(w, h);
        int[] data = new int[w * h * 4];
        float[] values = new float[2];
        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) {
                float[] tmpValues;
                int index = (j * w + i) * 4;
                boolean useBackground = false;
                values[0] = x + i;
                values[1] = y + j;
                this.rat.transform(values, 0, values, 0, 1);
                if (values[0] < this.domain[0] || values[0] > this.domain[1] || values[1] < this.domain[2] || values[1] > this.domain[3]) {
                    if (this.getBackground() == null) continue;
                    useBackground = true;
                }
                if (useBackground) {
                    tmpValues = this.getBackground();
                } else {
                    try {
                        tmpValues = this.type1ShadingType.evalFunction(values);
                    }
                    catch (IOException e) {
                        LOG.error((Object)"error while processing a function", (Throwable)e);
                        continue;
                    }
                }
                PDColorSpace shadingColorSpace = this.getShadingColorSpace();
                if (shadingColorSpace != null) {
                    try {
                        tmpValues = shadingColorSpace.toRGB(tmpValues);
                    }
                    catch (IOException e) {
                        LOG.error((Object)"error processing color space", (Throwable)e);
                        continue;
                    }
                }
                data[index] = (int)(tmpValues[0] * 255.0f);
                data[index + 1] = (int)(tmpValues[1] * 255.0f);
                data[index + 2] = (int)(tmpValues[2] * 255.0f);
                data[index + 3] = 255;
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }

    public float[] getDomain() {
        return this.domain;
    }
}

