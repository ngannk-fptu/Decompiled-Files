/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType4;
import org.apache.pdfbox.pdmodel.graphics.shading.Patch;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.util.Matrix;

abstract class PDMeshBasedShadingType
extends PDShadingType4 {
    private static final Log LOG = LogFactory.getLog(PDMeshBasedShadingType.class);

    PDMeshBasedShadingType(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final List<Patch> collectPatches(AffineTransform xform, Matrix matrix, int controlPoints) throws IOException {
        COSDictionary dict = this.getCOSObject();
        if (!(dict instanceof COSStream)) {
            return Collections.emptyList();
        }
        PDRange rangeX = this.getDecodeForParameter(0);
        PDRange rangeY = this.getDecodeForParameter(1);
        if (rangeX == null || rangeY == null || Float.compare(rangeX.getMin(), rangeX.getMax()) == 0 || Float.compare(rangeY.getMin(), rangeY.getMax()) == 0) {
            return Collections.emptyList();
        }
        int bitsPerFlag = this.getBitsPerFlag();
        PDRange[] colRange = new PDRange[this.getNumberOfColorComponents()];
        for (int i = 0; i < colRange.length; ++i) {
            colRange[i] = this.getDecodeForParameter(2 + i);
            if (colRange[i] != null) continue;
            throw new IOException("Range missing in shading /Decode entry");
        }
        ArrayList<Patch> list = new ArrayList<Patch>();
        long maxSrcCoord = (long)Math.pow(2.0, this.getBitsPerCoordinate()) - 1L;
        long maxSrcColor = (long)Math.pow(2.0, this.getBitsPerComponent()) - 1L;
        COSStream cosStream = (COSStream)dict;
        MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(cosStream.createInputStream());
        try {
            Point2D[] implicitEdge = new Point2D[4];
            float[][] implicitCornerColor = new float[2][colRange.length];
            byte flag = 0;
            try {
                flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
            }
            catch (EOFException ex) {
                LOG.error((Object)ex);
                ArrayList<Patch> arrayList = list;
                mciis.close();
                return arrayList;
            }
            boolean eof = false;
            block15: while (!eof) {
                try {
                    boolean isFree = flag == 0;
                    Patch current = this.readPatch(mciis, isFree, implicitEdge, implicitCornerColor, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform, controlPoints);
                    if (current == null) {
                        break;
                    }
                    list.add(current);
                    flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
                    switch (flag) {
                        case 0: {
                            continue block15;
                        }
                        case 1: {
                            implicitEdge = current.getFlag1Edge();
                            implicitCornerColor = current.getFlag1Color();
                            continue block15;
                        }
                        case 2: {
                            implicitEdge = current.getFlag2Edge();
                            implicitCornerColor = current.getFlag2Color();
                            continue block15;
                        }
                        case 3: {
                            implicitEdge = current.getFlag3Edge();
                            implicitCornerColor = current.getFlag3Color();
                            continue block15;
                        }
                    }
                    LOG.warn((Object)("bad flag: " + flag));
                }
                catch (EOFException ex) {
                    eof = true;
                }
            }
        }
        finally {
            mciis.close();
        }
        return list;
    }

    protected Patch readPatch(ImageInputStream input, boolean isFree, Point2D[] implicitEdge, float[][] implicitCornerColor, long maxSrcCoord, long maxSrcColor, PDRange rangeX, PDRange rangeY, PDRange[] colRange, Matrix matrix, AffineTransform xform, int controlPoints) throws IOException {
        int i;
        int numberOfColorComponents = this.getNumberOfColorComponents();
        float[][] color = new float[4][numberOfColorComponents];
        Point2D[] points = new Point2D[controlPoints];
        int pStart = 4;
        int cStart = 2;
        if (isFree) {
            pStart = 0;
            cStart = 0;
        } else {
            points[0] = implicitEdge[0];
            points[1] = implicitEdge[1];
            points[2] = implicitEdge[2];
            points[3] = implicitEdge[3];
            for (i = 0; i < numberOfColorComponents; ++i) {
                color[0][i] = implicitCornerColor[0][i];
                color[1][i] = implicitCornerColor[1][i];
            }
        }
        try {
            for (i = pStart; i < controlPoints; ++i) {
                long x = input.readBits(this.getBitsPerCoordinate());
                long y = input.readBits(this.getBitsPerCoordinate());
                float px = this.interpolate(x, maxSrcCoord, rangeX.getMin(), rangeX.getMax());
                float py = this.interpolate(y, maxSrcCoord, rangeY.getMin(), rangeY.getMax());
                Point2D.Float p = matrix.transformPoint(px, py);
                xform.transform(p, p);
                points[i] = p;
            }
            for (i = cStart; i < 4; ++i) {
                for (int j = 0; j < numberOfColorComponents; ++j) {
                    long c = input.readBits(this.getBitsPerComponent());
                    color[i][j] = this.interpolate(c, maxSrcColor, colRange[j].getMin(), colRange[j].getMax());
                }
            }
        }
        catch (EOFException ex) {
            LOG.debug((Object)"EOF", (Throwable)ex);
            return null;
        }
        return this.generatePatch(points, color);
    }

    abstract Patch generatePatch(Point2D[] var1, float[][] var2);

    @Override
    public abstract Rectangle2D getBounds(AffineTransform var1, Matrix var2) throws IOException;

    Rectangle2D getBounds(AffineTransform xform, Matrix matrix, int controlPoints) throws IOException {
        Rectangle2D bounds = null;
        for (Patch patch : this.collectPatches(xform, matrix, controlPoints)) {
            for (ShadedTriangle shadedTriangle : patch.listOfTriangles) {
                if (bounds == null) {
                    bounds = new Rectangle2D.Double(shadedTriangle.corner[0].getX(), shadedTriangle.corner[0].getY(), 0.0, 0.0);
                }
                bounds.add(shadedTriangle.corner[0]);
                bounds.add(shadedTriangle.corner[1]);
                bounds.add(shadedTriangle.corner[2]);
            }
        }
        return bounds;
    }
}

