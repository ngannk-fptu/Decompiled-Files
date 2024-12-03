/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType3;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext;
import org.apache.pdfbox.util.Matrix;

public class RadialShadingContext
extends ShadingContext
implements PaintContext {
    private static final Log LOG = LogFactory.getLog(RadialShadingContext.class);
    private PDShadingType3 radialShadingType;
    private final float[] coords;
    private final float[] domain;
    private final boolean[] extend;
    private final double x1x0;
    private final double y1y0;
    private final double r1r0;
    private final double r0pow2;
    private final float d1d0;
    private final double denom;
    private final int factor;
    private final int[] colorTable;
    private AffineTransform rat;

    public RadialShadingContext(PDShadingType3 shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.radialShadingType = shading;
        this.coords = shading.getCoords().toFloatArray();
        this.domain = this.radialShadingType.getDomain() != null ? shading.getDomain().toFloatArray() : new float[]{0.0f, 1.0f};
        COSArray extendValues = shading.getExtend();
        if (extendValues != null) {
            this.extend = new boolean[2];
            this.extend[0] = ((COSBoolean)extendValues.getObject(0)).getValue();
            this.extend[1] = ((COSBoolean)extendValues.getObject(1)).getValue();
        } else {
            this.extend = new boolean[]{false, false};
        }
        this.x1x0 = this.coords[3] - this.coords[0];
        this.y1y0 = this.coords[4] - this.coords[1];
        this.r1r0 = this.coords[5] - this.coords[2];
        this.r0pow2 = Math.pow(this.coords[2], 2.0);
        this.denom = Math.pow(this.x1x0, 2.0) + Math.pow(this.y1y0, 2.0) - Math.pow(this.r1r0, 2.0);
        this.d1d0 = this.domain[1] - this.domain[0];
        try {
            this.rat = matrix.createAffineTransform().createInverse();
            this.rat.concatenate(xform.createInverse());
        }
        catch (NoninvertibleTransformException ex) {
            LOG.error((Object)(ex.getMessage() + ", matrix: " + matrix), (Throwable)ex);
            this.rat = new AffineTransform();
        }
        AffineTransform shadingToDevice = (AffineTransform)xform.clone();
        shadingToDevice.concatenate(matrix.createAffineTransform());
        double dist = Math.sqrt(Math.pow(deviceBounds.getMaxX() - deviceBounds.getMinX(), 2.0) + Math.pow(deviceBounds.getMaxY() - deviceBounds.getMinY(), 2.0));
        this.factor = (int)Math.ceil(dist);
        this.colorTable = this.calcColorTable();
    }

    private int[] calcColorTable() throws IOException {
        int[] map = new int[this.factor + 1];
        if (this.factor == 0 || this.d1d0 == 0.0f) {
            float[] values = this.radialShadingType.evalFunction(this.domain[0]);
            map[0] = this.convertToRGB(values);
        } else {
            for (int i = 0; i <= this.factor; ++i) {
                float t = this.domain[0] + this.d1d0 * (float)i / (float)this.factor;
                float[] values = this.radialShadingType.evalFunction(t);
                map[i] = this.convertToRGB(values);
            }
        }
        return map;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.radialShadingType = null;
    }

    @Override
    public ColorModel getColorModel() {
        return super.getColorModel();
    }

    @Override
    public Raster getRaster(int x, int y, int w, int h) {
        WritableRaster raster = this.getColorModel().createCompatibleWritableRaster(w, h);
        float inputValue = -1.0f;
        int[] data = new int[w * h * 4];
        float[] values = new float[2];
        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) {
                int value;
                values[0] = x + i;
                values[1] = y + j;
                this.rat.transform(values, 0, values, 0, 1);
                boolean useBackground = false;
                float[] inputValues = this.calculateInputValues(values[0], values[1]);
                if (Float.isNaN(inputValues[0]) && Float.isNaN(inputValues[1])) {
                    if (this.getBackground() == null) continue;
                    useBackground = true;
                } else {
                    if (inputValues[0] >= 0.0f && inputValues[0] <= 1.0f) {
                        inputValue = inputValues[1] >= 0.0f && inputValues[1] <= 1.0f ? Math.max(inputValues[0], inputValues[1]) : inputValues[0];
                    } else if (inputValues[1] >= 0.0f && inputValues[1] <= 1.0f) {
                        inputValue = inputValues[1];
                    } else if (this.extend[0] && this.extend[1]) {
                        inputValue = Math.max(inputValues[0], inputValues[1]);
                    } else if (this.extend[0]) {
                        inputValue = inputValues[0];
                    } else if (this.extend[1]) {
                        inputValue = inputValues[1];
                    } else {
                        if (this.getBackground() == null) continue;
                        useBackground = true;
                    }
                    if (inputValue > 1.0f) {
                        if (this.extend[1] && this.coords[5] > 0.0f) {
                            inputValue = 1.0f;
                        } else {
                            if (this.getBackground() == null) continue;
                            useBackground = true;
                        }
                    } else if (inputValue < 0.0f) {
                        if (this.extend[0] && this.coords[2] > 0.0f) {
                            inputValue = 0.0f;
                        } else {
                            if (this.getBackground() == null) continue;
                            useBackground = true;
                        }
                    }
                }
                if (useBackground) {
                    value = this.getRgbBackground();
                } else {
                    int key = (int)(inputValue * (float)this.factor);
                    value = this.colorTable[key];
                }
                int index = (j * w + i) * 4;
                data[index] = value & 0xFF;
                data[index + 1] = (value >>= 8) & 0xFF;
                data[index + 2] = (value >>= 8) & 0xFF;
                data[index + 3] = 255;
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }

    private float[] calculateInputValues(double x, double y) {
        double p = -(x - (double)this.coords[0]) * this.x1x0 - (y - (double)this.coords[1]) * this.y1y0 - (double)this.coords[2] * this.r1r0;
        double q = Math.pow(x - (double)this.coords[0], 2.0) + Math.pow(y - (double)this.coords[1], 2.0) - this.r0pow2;
        double root = Math.sqrt(p * p - this.denom * q);
        float root1 = (float)((-p + root) / this.denom);
        float root2 = (float)((-p - root) / this.denom);
        if (this.denom < 0.0) {
            return new float[]{root1, root2};
        }
        return new float[]{root2, root1};
    }

    public float[] getCoords() {
        return this.coords;
    }

    public float[] getDomain() {
        return this.domain;
    }

    public boolean[] getExtend() {
        return this.extend;
    }

    public PDFunction getFunction() throws IOException {
        return this.radialShadingType.getFunction();
    }
}

