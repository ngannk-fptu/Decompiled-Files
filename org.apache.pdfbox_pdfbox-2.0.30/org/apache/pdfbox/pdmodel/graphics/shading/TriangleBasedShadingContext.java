/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.graphics.shading.IntPoint;
import org.apache.pdfbox.pdmodel.graphics.shading.Line;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext;
import org.apache.pdfbox.util.Matrix;

abstract class TriangleBasedShadingContext
extends ShadingContext
implements PaintContext {
    private Map<Point, Integer> pixelTable;

    TriangleBasedShadingContext(PDShading shading, ColorModel cm, AffineTransform xform, Matrix matrix) throws IOException {
        super(shading, cm, xform, matrix);
    }

    protected final void createPixelTable(Rectangle deviceBounds) throws IOException {
        this.pixelTable = this.calcPixelTable(deviceBounds);
    }

    abstract Map<Point, Integer> calcPixelTable(Rectangle var1) throws IOException;

    protected void calcPixelTable(List<ShadedTriangle> triangleList, Map<Point, Integer> map, Rectangle deviceBounds) throws IOException {
        for (ShadedTriangle tri : triangleList) {
            int degree = tri.getDeg();
            if (degree == 2) {
                Line line = tri.getLine();
                for (Point p : line.linePoints) {
                    map.put(p, this.evalFunctionAndConvertToRGB(line.calcColor(p)));
                }
                continue;
            }
            int[] boundary = tri.getBoundary();
            boundary[0] = Math.max(boundary[0], deviceBounds.x);
            boundary[1] = Math.min(boundary[1], deviceBounds.x + deviceBounds.width);
            boundary[2] = Math.max(boundary[2], deviceBounds.y);
            boundary[3] = Math.min(boundary[3], deviceBounds.y + deviceBounds.height);
            for (int x = boundary[0]; x <= boundary[1]; ++x) {
                for (int y = boundary[2]; y <= boundary[3]; ++y) {
                    IntPoint p = new IntPoint(x, y);
                    if (!tri.contains(p)) continue;
                    map.put(p, this.evalFunctionAndConvertToRGB(tri.calcColor(p)));
                }
            }
            IntPoint p0 = new IntPoint((int)Math.round(tri.corner[0].getX()), (int)Math.round(tri.corner[0].getY()));
            IntPoint p1 = new IntPoint((int)Math.round(tri.corner[1].getX()), (int)Math.round(tri.corner[1].getY()));
            IntPoint p2 = new IntPoint((int)Math.round(tri.corner[2].getX()), (int)Math.round(tri.corner[2].getY()));
            Line l1 = new Line(p0, p1, tri.color[0], tri.color[1]);
            Line l2 = new Line(p1, p2, tri.color[1], tri.color[2]);
            Line l3 = new Line(p2, p0, tri.color[2], tri.color[0]);
            for (Point p : l1.linePoints) {
                map.put(p, this.evalFunctionAndConvertToRGB(l1.calcColor(p)));
            }
            for (Point p : l2.linePoints) {
                map.put(p, this.evalFunctionAndConvertToRGB(l2.calcColor(p)));
            }
            for (Point p : l3.linePoints) {
                map.put(p, this.evalFunctionAndConvertToRGB(l3.calcColor(p)));
            }
        }
    }

    private int evalFunctionAndConvertToRGB(float[] values) throws IOException {
        if (this.getShading().getFunction() != null) {
            values = this.getShading().evalFunction(values);
        }
        return this.convertToRGB(values);
    }

    abstract boolean isDataEmpty();

    @Override
    public final ColorModel getColorModel() {
        return super.getColorModel();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public final Raster getRaster(int x, int y, int w, int h) {
        WritableRaster raster = this.getColorModel().createCompatibleWritableRaster(w, h);
        int[] data = new int[w * h * 4];
        if (!this.isDataEmpty() || this.getBackground() != null) {
            for (int row = 0; row < h; ++row) {
                for (int col = 0; col < w; ++col) {
                    int value;
                    IntPoint p = new IntPoint(x + col, y + row);
                    Integer v = this.pixelTable.get(p);
                    if (v != null) {
                        value = v;
                    } else {
                        if (this.getBackground() == null) continue;
                        value = this.getRgbBackground();
                    }
                    int index = (row * w + col) * 4;
                    data[index] = value & 0xFF;
                    data[index + 1] = (value >>= 8) & 0xFF;
                    data[index + 2] = (value >>= 8) & 0xFF;
                    data[index + 3] = 255;
                }
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }
}

