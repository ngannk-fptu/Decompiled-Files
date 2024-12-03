/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.pdmodel.graphics.shading.TriangleBasedShadingContext;
import org.apache.pdfbox.util.Matrix;

abstract class GouraudShadingContext
extends TriangleBasedShadingContext {
    private List<ShadedTriangle> triangleList = new ArrayList<ShadedTriangle>();

    protected GouraudShadingContext(PDShading shading, ColorModel colorModel, AffineTransform xform, Matrix matrix) throws IOException {
        super(shading, colorModel, xform, matrix);
    }

    final void setTriangleList(List<ShadedTriangle> triangleList) {
        this.triangleList = triangleList;
    }

    @Override
    protected Map<Point, Integer> calcPixelTable(Rectangle deviceBounds) throws IOException {
        HashMap<Point, Integer> map = new HashMap<Point, Integer>();
        super.calcPixelTable(this.triangleList, map, deviceBounds);
        return map;
    }

    @Override
    public void dispose() {
        this.triangleList = null;
        super.dispose();
    }

    @Override
    protected boolean isDataEmpty() {
        return this.triangleList.isEmpty();
    }
}

