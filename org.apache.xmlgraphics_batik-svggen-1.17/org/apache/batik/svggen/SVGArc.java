/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.apache.batik.svggen.SVGEllipse;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.apache.batik.svggen.SVGLine;
import org.w3c.dom.Element;

public class SVGArc
extends SVGGraphicObjectConverter {
    private SVGLine svgLine;
    private SVGEllipse svgEllipse;

    public SVGArc(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public Element toSVG(Arc2D arc) {
        double ext = arc.getAngleExtent();
        double width = arc.getWidth();
        double height = arc.getHeight();
        if (width == 0.0 || height == 0.0) {
            Line2D.Double line = new Line2D.Double(arc.getX(), arc.getY(), arc.getX() + width, arc.getY() + height);
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        if (ext >= 360.0 || ext <= -360.0) {
            Ellipse2D.Double ellipse = new Ellipse2D.Double(arc.getX(), arc.getY(), width, height);
            if (this.svgEllipse == null) {
                this.svgEllipse = new SVGEllipse(this.generatorContext);
            }
            return this.svgEllipse.toSVG(ellipse);
        }
        Element svgPath = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "path");
        StringBuffer d = new StringBuffer(64);
        Point2D startPt = arc.getStartPoint();
        Point2D endPt = arc.getEndPoint();
        int type = arc.getArcType();
        d.append("M");
        d.append(this.doubleString(startPt.getX()));
        d.append(" ");
        d.append(this.doubleString(startPt.getY()));
        d.append(" ");
        d.append("A");
        d.append(this.doubleString(width / 2.0));
        d.append(" ");
        d.append(this.doubleString(height / 2.0));
        d.append(" ");
        d.append('0');
        d.append(" ");
        if (ext > 0.0) {
            if (ext > 180.0) {
                d.append('1');
            } else {
                d.append('0');
            }
            d.append(" ");
            d.append('0');
        } else {
            if (ext < -180.0) {
                d.append('1');
            } else {
                d.append('0');
            }
            d.append(" ");
            d.append('1');
        }
        d.append(" ");
        d.append(this.doubleString(endPt.getX()));
        d.append(" ");
        d.append(this.doubleString(endPt.getY()));
        if (type == 1) {
            d.append("Z");
        } else if (type == 2) {
            double cx = arc.getX() + width / 2.0;
            double cy = arc.getY() + height / 2.0;
            d.append("L");
            d.append(" ");
            d.append(this.doubleString(cx));
            d.append(" ");
            d.append(this.doubleString(cy));
            d.append(" ");
            d.append("Z");
        }
        svgPath.setAttributeNS(null, "d", d.toString());
        return svgPath;
    }
}

