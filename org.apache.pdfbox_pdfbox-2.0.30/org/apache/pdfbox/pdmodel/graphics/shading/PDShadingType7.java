/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.graphics.shading.PDMeshBasedShadingType;
import org.apache.pdfbox.pdmodel.graphics.shading.Patch;
import org.apache.pdfbox.pdmodel.graphics.shading.TensorPatch;
import org.apache.pdfbox.pdmodel.graphics.shading.Type7ShadingPaint;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType7
extends PDMeshBasedShadingType {
    public PDShadingType7(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 7;
    }

    @Override
    public Paint toPaint(Matrix matrix) {
        return new Type7ShadingPaint(this, matrix);
    }

    @Override
    protected Patch generatePatch(Point2D[] points, float[][] color) {
        return new TensorPatch(points, color);
    }

    @Override
    public Rectangle2D getBounds(AffineTransform xform, Matrix matrix) throws IOException {
        return this.getBounds(xform, matrix, 16);
    }
}

