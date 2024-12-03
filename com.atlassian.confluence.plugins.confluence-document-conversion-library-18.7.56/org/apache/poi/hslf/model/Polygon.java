/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model;

import java.awt.geom.Point2D;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public final class Polygon
extends HSLFAutoShape {
    protected Polygon(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public Polygon(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super((EscherContainerRecord)null, parent);
        this.createSpContainer(ShapeType.NOT_PRIMITIVE, parent instanceof HSLFGroupShape);
    }

    public Polygon() {
        this((ShapeContainer<HSLFShape, HSLFTextParagraph>)null);
    }

    public void setPoints(float[] xPoints, float[] yPoints) {
        float right = this.findBiggest(xPoints);
        float bottom = this.findBiggest(yPoints);
        float left = this.findSmallest(xPoints);
        float top = this.findSmallest(yPoints);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__RIGHT, Units.pointsToMaster(right - left)));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__BOTTOM, Units.pointsToMaster(bottom - top)));
        int i = 0;
        while (i < xPoints.length) {
            int n = i;
            xPoints[n] = xPoints[n] + -left;
            int n2 = i++;
            yPoints[n2] = yPoints[n2] + -top;
        }
        int numpoints = xPoints.length;
        EscherArrayProperty verticesProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__VERTICES, false, 0);
        verticesProp.setNumberOfElementsInArray(numpoints + 1);
        verticesProp.setNumberOfElementsInMemory(numpoints + 1);
        verticesProp.setSizeOfElements(65520);
        for (int i2 = 0; i2 < numpoints; ++i2) {
            byte[] data = new byte[4];
            LittleEndian.putShort(data, 0, (short)Units.pointsToMaster(xPoints[i2]));
            LittleEndian.putShort(data, 2, (short)Units.pointsToMaster(yPoints[i2]));
            verticesProp.setElement(i2, data);
        }
        byte[] data = new byte[4];
        LittleEndian.putShort(data, 0, (short)Units.pointsToMaster(xPoints[0]));
        LittleEndian.putShort(data, 2, (short)Units.pointsToMaster(yPoints[0]));
        verticesProp.setElement(numpoints, data);
        opt.addEscherProperty(verticesProp);
        EscherArrayProperty segmentsProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__SEGMENTINFO, false, 0);
        segmentsProp.setSizeOfElements(2);
        segmentsProp.setNumberOfElementsInArray(numpoints * 2 + 4);
        segmentsProp.setNumberOfElementsInMemory(numpoints * 2 + 4);
        segmentsProp.setElement(0, new byte[]{0, 64});
        segmentsProp.setElement(1, new byte[]{0, -84});
        for (int i3 = 0; i3 < numpoints; ++i3) {
            segmentsProp.setElement(2 + i3 * 2, new byte[]{1, 0});
            segmentsProp.setElement(3 + i3 * 2, new byte[]{0, -84});
        }
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 2, new byte[]{1, 96});
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 1, new byte[]{0, -128});
        opt.addEscherProperty(segmentsProp);
        opt.sortProperties();
    }

    public void setPoints(Point2D[] points) {
        float[] xpoints = new float[points.length];
        float[] ypoints = new float[points.length];
        for (int i = 0; i < points.length; ++i) {
            xpoints[i] = (float)points[i].getX();
            ypoints[i] = (float)points[i].getY();
        }
        this.setPoints(xpoints, ypoints);
    }

    private float findBiggest(float[] values) {
        float result = Float.MIN_VALUE;
        for (float value : values) {
            if (!(value > result)) continue;
            result = value;
        }
        return result;
    }

    private float findSmallest(float[] values) {
        float result = Float.MAX_VALUE;
        for (float value : values) {
            if (!(value < result)) continue;
            result = value;
        }
        return result;
    }
}

