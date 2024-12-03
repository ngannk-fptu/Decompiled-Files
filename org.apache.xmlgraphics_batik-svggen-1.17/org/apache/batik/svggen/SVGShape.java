/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import org.apache.batik.svggen.SVGArc;
import org.apache.batik.svggen.SVGEllipse;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.apache.batik.svggen.SVGLine;
import org.apache.batik.svggen.SVGPath;
import org.apache.batik.svggen.SVGPolygon;
import org.apache.batik.svggen.SVGRectangle;
import org.w3c.dom.Element;

public class SVGShape
extends SVGGraphicObjectConverter {
    private SVGArc svgArc;
    private SVGEllipse svgEllipse;
    private SVGLine svgLine;
    private SVGPath svgPath;
    private SVGPolygon svgPolygon;
    private SVGRectangle svgRectangle;

    public SVGShape(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.svgArc = new SVGArc(generatorContext);
        this.svgEllipse = new SVGEllipse(generatorContext);
        this.svgLine = new SVGLine(generatorContext);
        this.svgPath = new SVGPath(generatorContext);
        this.svgPolygon = new SVGPolygon(generatorContext);
        this.svgRectangle = new SVGRectangle(generatorContext);
    }

    public Element toSVG(Shape shape) {
        if (shape instanceof Polygon) {
            return this.svgPolygon.toSVG((Polygon)shape);
        }
        if (shape instanceof Rectangle2D) {
            return this.svgRectangle.toSVG((Rectangle2D)shape);
        }
        if (shape instanceof RoundRectangle2D) {
            return this.svgRectangle.toSVG((RoundRectangle2D)shape);
        }
        if (shape instanceof Ellipse2D) {
            return this.svgEllipse.toSVG((Ellipse2D)shape);
        }
        if (shape instanceof Line2D) {
            return this.svgLine.toSVG((Line2D)shape);
        }
        if (shape instanceof Arc2D) {
            return this.svgArc.toSVG((Arc2D)shape);
        }
        return this.svgPath.toSVG(shape);
    }
}

