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
import java.io.IOException;
import java.util.List;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;
import org.apache.pdfbox.pdmodel.graphics.shading.Vertex;
import org.apache.pdfbox.util.Matrix;

abstract class PDTriangleBasedShadingType
extends PDShading {
    private COSArray decode = null;
    private static final Log LOG = LogFactory.getLog(PDTriangleBasedShadingType.class);
    private int bitsPerCoordinate = -1;
    private int bitsPerColorComponent = -1;
    private int numberOfColorComponents = -1;

    PDTriangleBasedShadingType(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    public int getBitsPerComponent() {
        if (this.bitsPerColorComponent == -1) {
            this.bitsPerColorComponent = this.getCOSObject().getInt(COSName.BITS_PER_COMPONENT, -1);
            LOG.debug((Object)("bitsPerColorComponent: " + this.bitsPerColorComponent));
        }
        return this.bitsPerColorComponent;
    }

    public void setBitsPerComponent(int bitsPerComponent) {
        this.getCOSObject().setInt(COSName.BITS_PER_COMPONENT, bitsPerComponent);
        this.bitsPerColorComponent = bitsPerComponent;
    }

    public int getBitsPerCoordinate() {
        if (this.bitsPerCoordinate == -1) {
            this.bitsPerCoordinate = this.getCOSObject().getInt(COSName.BITS_PER_COORDINATE, -1);
            LOG.debug((Object)("bitsPerCoordinate: " + (Math.pow(2.0, this.bitsPerCoordinate) - 1.0)));
        }
        return this.bitsPerCoordinate;
    }

    public void setBitsPerCoordinate(int bitsPerCoordinate) {
        this.getCOSObject().setInt(COSName.BITS_PER_COORDINATE, bitsPerCoordinate);
        this.bitsPerCoordinate = bitsPerCoordinate;
    }

    public int getNumberOfColorComponents() throws IOException {
        if (this.numberOfColorComponents == -1) {
            this.numberOfColorComponents = this.getFunction() != null ? 1 : this.getColorSpace().getNumberOfComponents();
            LOG.debug((Object)("numberOfColorComponents: " + this.numberOfColorComponents));
        }
        return this.numberOfColorComponents;
    }

    private COSArray getDecodeValues() {
        if (this.decode == null) {
            this.decode = (COSArray)this.getCOSObject().getDictionaryObject(COSName.DECODE);
        }
        return this.decode;
    }

    public void setDecodeValues(COSArray decodeValues) {
        this.decode = decodeValues;
        this.getCOSObject().setItem(COSName.DECODE, (COSBase)decodeValues);
    }

    public PDRange getDecodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray decodeValues = this.getDecodeValues();
        if (decodeValues != null && decodeValues.size() >= paramNum * 2 + 1) {
            retval = new PDRange(decodeValues, paramNum);
        }
        return retval;
    }

    protected float interpolate(float src, long srcMax, float dstMin, float dstMax) {
        return dstMin + src * (dstMax - dstMin) / (float)srcMax;
    }

    protected Vertex readVertex(ImageInputStream input, long maxSrcCoord, long maxSrcColor, PDRange rangeX, PDRange rangeY, PDRange[] colRangeTab, Matrix matrix, AffineTransform xform) throws IOException {
        float[] colorComponentTab = new float[this.numberOfColorComponents];
        long x = input.readBits(this.bitsPerCoordinate);
        long y = input.readBits(this.bitsPerCoordinate);
        float dstX = this.interpolate(x, maxSrcCoord, rangeX.getMin(), rangeX.getMax());
        float dstY = this.interpolate(y, maxSrcCoord, rangeY.getMin(), rangeY.getMax());
        LOG.debug((Object)("coord: " + String.format("[%06X,%06X] -> [%f,%f]", x, y, Float.valueOf(dstX), Float.valueOf(dstY))));
        Point2D.Float p = matrix.transformPoint(dstX, dstY);
        xform.transform(p, p);
        for (int n = 0; n < this.numberOfColorComponents; ++n) {
            int color = (int)input.readBits(this.bitsPerColorComponent);
            colorComponentTab[n] = this.interpolate(color, maxSrcColor, colRangeTab[n].getMin(), colRangeTab[n].getMax());
            LOG.debug((Object)("color[" + n + "]: " + color + "/" + String.format("%02x", color) + "-> color[" + n + "]: " + colorComponentTab[n]));
        }
        int bitOffset = input.getBitOffset();
        if (bitOffset != 0) {
            input.readBits(8 - bitOffset);
        }
        return new Vertex(p, colorComponentTab);
    }

    abstract List<ShadedTriangle> collectTriangles(AffineTransform var1, Matrix var2) throws IOException;

    @Override
    public Rectangle2D getBounds(AffineTransform xform, Matrix matrix) throws IOException {
        Rectangle2D bounds = null;
        for (ShadedTriangle shadedTriangle : this.collectTriangles(xform, matrix)) {
            if (bounds == null) {
                bounds = new Rectangle2D.Double(shadedTriangle.corner[0].getX(), shadedTriangle.corner[0].getY(), 0.0, 0.0);
            }
            bounds.add(shadedTriangle.corner[0]);
            bounds.add(shadedTriangle.corner[1]);
            bounds.add(shadedTriangle.corner[2]);
        }
        if (bounds == null) {
            return new Rectangle2D.Float();
        }
        return bounds;
    }
}

