/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.graphics.shading.PDMeshBasedShadingType;
import org.apache.pdfbox.pdmodel.graphics.shading.Patch;
import org.apache.pdfbox.pdmodel.graphics.shading.TriangleBasedShadingContext;
import org.apache.pdfbox.util.Matrix;

abstract class PatchMeshesShadingContext
extends TriangleBasedShadingContext {
    private List<Patch> patchList;

    protected PatchMeshesShadingContext(PDMeshBasedShadingType shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds, int controlPoints) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.patchList = shading.collectPatches(xform, matrix, controlPoints);
        this.createPixelTable(deviceBounds);
    }

    @Override
    protected Map<Point, Integer> calcPixelTable(Rectangle deviceBounds) throws IOException {
        HashMap<Point, Integer> map = new HashMap<Point, Integer>();
        for (Patch it : this.patchList) {
            super.calcPixelTable(it.listOfTriangles, map, deviceBounds);
        }
        return map;
    }

    @Override
    public void dispose() {
        this.patchList = null;
        super.dispose();
    }

    @Override
    protected boolean isDataEmpty() {
        return this.patchList.isEmpty();
    }
}

