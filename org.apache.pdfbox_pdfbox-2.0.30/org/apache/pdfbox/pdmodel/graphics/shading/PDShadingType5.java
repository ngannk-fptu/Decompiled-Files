/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.pdmodel.graphics.shading.Type5ShadingPaint;
import org.apache.pdfbox.pdmodel.graphics.shading.Vertex;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType5
extends PDTriangleBasedShadingType {
    public PDShadingType5(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 5;
    }

    public int getVerticesPerRow() {
        return this.getCOSObject().getInt(COSName.VERTICES_PER_ROW, -1);
    }

    public void setVerticesPerRow(int verticesPerRow) {
        this.getCOSObject().setInt(COSName.VERTICES_PER_ROW, verticesPerRow);
    }

    @Override
    public Paint toPaint(Matrix matrix) {
        return new Type5ShadingPaint(this, matrix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    List<ShadedTriangle> collectTriangles(AffineTransform xform, Matrix matrix) throws IOException {
        COSDictionary dict = this.getCOSObject();
        if (!(dict instanceof COSStream)) {
            return Collections.emptyList();
        }
        PDRange rangeX = this.getDecodeForParameter(0);
        PDRange rangeY = this.getDecodeForParameter(1);
        if (rangeX == null || rangeY == null || Float.compare(rangeX.getMin(), rangeX.getMax()) == 0 || Float.compare(rangeY.getMin(), rangeY.getMax()) == 0) {
            return Collections.emptyList();
        }
        int numPerRow = this.getVerticesPerRow();
        PDRange[] colRange = new PDRange[this.getNumberOfColorComponents()];
        for (int i = 0; i < colRange.length; ++i) {
            colRange[i] = this.getDecodeForParameter(2 + i);
            if (colRange[i] != null) continue;
            throw new IOException("Range missing in shading /Decode entry");
        }
        ArrayList<Vertex> vlist = new ArrayList<Vertex>();
        long maxSrcCoord = (long)Math.pow(2.0, this.getBitsPerCoordinate()) - 1L;
        long maxSrcColor = (long)Math.pow(2.0, this.getBitsPerComponent()) - 1L;
        COSStream cosStream = (COSStream)dict;
        MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(cosStream.createInputStream());
        try {
            boolean eof = false;
            while (!eof) {
                try {
                    Vertex p = this.readVertex(mciis, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                    vlist.add(p);
                }
                catch (EOFException ex) {
                    eof = true;
                }
            }
        }
        finally {
            mciis.close();
        }
        int rowNum = vlist.size() / numPerRow;
        if (rowNum < 2) {
            return Collections.emptyList();
        }
        Vertex[][] latticeArray = new Vertex[rowNum][numPerRow];
        for (int i = 0; i < rowNum; ++i) {
            for (int j = 0; j < numPerRow; ++j) {
                latticeArray[i][j] = (Vertex)vlist.get(i * numPerRow + j);
            }
        }
        return this.createShadedTriangleList(rowNum, numPerRow, latticeArray);
    }

    private List<ShadedTriangle> createShadedTriangleList(int rowNum, int numPerRow, Vertex[][] latticeArray) {
        Point2D[] ps = new Point2D[3];
        float[][] cs = new float[3][];
        ArrayList<ShadedTriangle> list = new ArrayList<ShadedTriangle>();
        for (int i = 0; i < rowNum - 1; ++i) {
            for (int j = 0; j < numPerRow - 1; ++j) {
                ps[0] = latticeArray[i][j].point;
                ps[1] = latticeArray[i][j + 1].point;
                ps[2] = latticeArray[i + 1][j].point;
                cs[0] = latticeArray[i][j].color;
                cs[1] = latticeArray[i][j + 1].color;
                cs[2] = latticeArray[i + 1][j].color;
                list.add(new ShadedTriangle(ps, cs));
                ps[0] = latticeArray[i][j + 1].point;
                ps[1] = latticeArray[i + 1][j].point;
                ps[2] = latticeArray[i + 1][j + 1].point;
                cs[0] = latticeArray[i][j + 1].color;
                cs[1] = latticeArray[i + 1][j].color;
                cs[2] = latticeArray[i + 1][j + 1].color;
                list.add(new ShadedTriangle(ps, cs));
            }
        }
        return list;
    }
}

