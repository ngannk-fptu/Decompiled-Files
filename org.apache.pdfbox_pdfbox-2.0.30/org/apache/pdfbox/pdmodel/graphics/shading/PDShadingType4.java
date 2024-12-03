/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.pdmodel.graphics.shading.Type4ShadingPaint;
import org.apache.pdfbox.pdmodel.graphics.shading.Vertex;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType4
extends PDTriangleBasedShadingType {
    private static final Log LOG = LogFactory.getLog(PDShadingType4.class);

    public PDShadingType4(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 4;
    }

    public int getBitsPerFlag() {
        return this.getCOSObject().getInt(COSName.BITS_PER_FLAG, -1);
    }

    public void setBitsPerFlag(int bitsPerFlag) {
        this.getCOSObject().setInt(COSName.BITS_PER_FLAG, bitsPerFlag);
    }

    @Override
    public Paint toPaint(Matrix matrix) {
        return new Type4ShadingPaint(this, matrix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    List<ShadedTriangle> collectTriangles(AffineTransform xform, Matrix matrix) throws IOException {
        int bitsPerFlag = this.getBitsPerFlag();
        COSDictionary dict = this.getCOSObject();
        if (!(dict instanceof COSStream)) {
            return Collections.emptyList();
        }
        PDRange rangeX = this.getDecodeForParameter(0);
        PDRange rangeY = this.getDecodeForParameter(1);
        if (rangeX == null || rangeY == null || Float.compare(rangeX.getMin(), rangeX.getMax()) == 0 || Float.compare(rangeY.getMin(), rangeY.getMax()) == 0) {
            return Collections.emptyList();
        }
        PDRange[] colRange = new PDRange[this.getNumberOfColorComponents()];
        for (int i = 0; i < colRange.length; ++i) {
            colRange[i] = this.getDecodeForParameter(2 + i);
            if (colRange[i] != null) continue;
            throw new IOException("Range missing in shading /Decode entry");
        }
        ArrayList<ShadedTriangle> list = new ArrayList<ShadedTriangle>();
        long maxSrcCoord = (long)Math.pow(2.0, this.getBitsPerCoordinate()) - 1L;
        long maxSrcColor = (long)Math.pow(2.0, this.getBitsPerComponent()) - 1L;
        COSStream stream = (COSStream)dict;
        MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(stream.createInputStream());
        try {
            byte flag = 0;
            try {
                flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
            }
            catch (EOFException ex) {
                LOG.error((Object)ex);
            }
            boolean eof = false;
            block12: while (!eof) {
                try {
                    switch (flag) {
                        case 0: {
                            Vertex p0 = this.readVertex(mciis, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
                            if (flag != 0) {
                                LOG.error((Object)("bad triangle: " + flag));
                            }
                            Vertex p1 = this.readVertex(mciis, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            mciis.readBits(bitsPerFlag);
                            if (flag != 0) {
                                LOG.error((Object)("bad triangle: " + flag));
                            }
                            Vertex p2 = this.readVertex(mciis, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            Point2D[] ps = new Point2D[]{p0.point, p1.point, p2.point};
                            float[][] cs = new float[][]{p0.color, p1.color, p2.color};
                            list.add(new ShadedTriangle(ps, cs));
                            flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
                            continue block12;
                        }
                        case 1: 
                        case 2: {
                            int lastIndex = list.size() - 1;
                            if (lastIndex < 0) {
                                LOG.error((Object)("broken data stream: " + list.size()));
                                continue block12;
                            }
                            ShadedTriangle preTri = (ShadedTriangle)list.get(lastIndex);
                            Vertex p2 = this.readVertex(mciis, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            Point2D[] ps = new Point2D[]{flag == 1 ? preTri.corner[1] : preTri.corner[0], preTri.corner[2], p2.point};
                            float[][] cs = new float[][]{flag == 1 ? preTri.color[1] : preTri.color[0], preTri.color[2], p2.color};
                            list.add(new ShadedTriangle(ps, cs));
                            flag = (byte)(mciis.readBits(bitsPerFlag) & 3L);
                            continue block12;
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
}

