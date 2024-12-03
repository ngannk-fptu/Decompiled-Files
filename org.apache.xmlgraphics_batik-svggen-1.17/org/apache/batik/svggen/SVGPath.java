/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.w3c.dom.Element;

public class SVGPath
extends SVGGraphicObjectConverter {
    public SVGPath(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public Element toSVG(Shape path) {
        String dAttr = SVGPath.toSVGPathData(path, this.generatorContext);
        if (dAttr == null || dAttr.length() == 0) {
            return null;
        }
        Element svgPath = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "path");
        svgPath.setAttributeNS(null, "d", dAttr);
        if (path.getPathIterator(null).getWindingRule() == 0) {
            svgPath.setAttributeNS(null, "fill-rule", "evenodd");
        }
        return svgPath;
    }

    public static String toSVGPathData(Shape path, SVGGeneratorContext gc) {
        StringBuffer d = new StringBuffer(40);
        PathIterator pi = path.getPathIterator(null);
        float[] seg = new float[6];
        int segType = 0;
        while (!pi.isDone()) {
            segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    d.append("M");
                    SVGPath.appendPoint(d, seg[0], seg[1], gc);
                    break;
                }
                case 1: {
                    d.append("L");
                    SVGPath.appendPoint(d, seg[0], seg[1], gc);
                    break;
                }
                case 4: {
                    d.append("Z");
                    break;
                }
                case 2: {
                    d.append("Q");
                    SVGPath.appendPoint(d, seg[0], seg[1], gc);
                    SVGPath.appendPoint(d, seg[2], seg[3], gc);
                    break;
                }
                case 3: {
                    d.append("C");
                    SVGPath.appendPoint(d, seg[0], seg[1], gc);
                    SVGPath.appendPoint(d, seg[2], seg[3], gc);
                    SVGPath.appendPoint(d, seg[4], seg[5], gc);
                    break;
                }
                default: {
                    throw new RuntimeException("invalid segmentType:" + segType);
                }
            }
            pi.next();
        }
        if (d.length() > 0) {
            return d.toString().trim();
        }
        return "";
    }

    private static void appendPoint(StringBuffer d, float x, float y, SVGGeneratorContext gc) {
        d.append(gc.doubleString(x));
        d.append(" ");
        d.append(gc.doubleString(y));
        d.append(" ");
    }
}

