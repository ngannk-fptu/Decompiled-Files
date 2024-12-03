/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Polygon;
import java.awt.geom.PathIterator;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.w3c.dom.Element;

public class SVGPolygon
extends SVGGraphicObjectConverter {
    public SVGPolygon(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public Element toSVG(Polygon polygon) {
        Element svgPolygon = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "polygon");
        StringBuffer points = new StringBuffer(" ");
        PathIterator pi = polygon.getPathIterator(null);
        float[] seg = new float[6];
        while (!pi.isDone()) {
            int segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    this.appendPoint(points, seg[0], seg[1]);
                    break;
                }
                case 1: {
                    this.appendPoint(points, seg[0], seg[1]);
                    break;
                }
                case 4: {
                    break;
                }
                default: {
                    throw new RuntimeException("invalid segmentType:" + segType);
                }
            }
            pi.next();
        }
        svgPolygon.setAttributeNS(null, "points", points.substring(0, points.length() - 1));
        return svgPolygon;
    }

    private void appendPoint(StringBuffer points, float x, float y) {
        points.append(this.doubleString(x));
        points.append(" ");
        points.append(this.doubleString(y));
        points.append(" ");
    }
}

