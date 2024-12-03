/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.apache.batik.svggen.SVGLine;
import org.w3c.dom.Element;

public class SVGEllipse
extends SVGGraphicObjectConverter {
    private SVGLine svgLine;

    public SVGEllipse(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public Element toSVG(Ellipse2D ellipse) {
        if (ellipse.getWidth() < 0.0 || ellipse.getHeight() < 0.0) {
            return null;
        }
        if (ellipse.getWidth() == ellipse.getHeight()) {
            return this.toSVGCircle(ellipse);
        }
        return this.toSVGEllipse(ellipse);
    }

    private Element toSVGCircle(Ellipse2D ellipse) {
        Element svgCircle = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "circle");
        svgCircle.setAttributeNS(null, "cx", this.doubleString(ellipse.getX() + ellipse.getWidth() / 2.0));
        svgCircle.setAttributeNS(null, "cy", this.doubleString(ellipse.getY() + ellipse.getHeight() / 2.0));
        svgCircle.setAttributeNS(null, "r", this.doubleString(ellipse.getWidth() / 2.0));
        return svgCircle;
    }

    private Element toSVGEllipse(Ellipse2D ellipse) {
        if (ellipse.getWidth() > 0.0 && ellipse.getHeight() > 0.0) {
            Element svgCircle = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "ellipse");
            svgCircle.setAttributeNS(null, "cx", this.doubleString(ellipse.getX() + ellipse.getWidth() / 2.0));
            svgCircle.setAttributeNS(null, "cy", this.doubleString(ellipse.getY() + ellipse.getHeight() / 2.0));
            svgCircle.setAttributeNS(null, "rx", this.doubleString(ellipse.getWidth() / 2.0));
            svgCircle.setAttributeNS(null, "ry", this.doubleString(ellipse.getHeight() / 2.0));
            return svgCircle;
        }
        if (ellipse.getWidth() == 0.0 && ellipse.getHeight() > 0.0) {
            Line2D.Double line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX(), ellipse.getY() + ellipse.getHeight());
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        if (ellipse.getWidth() > 0.0 && ellipse.getHeight() == 0.0) {
            Line2D.Double line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX() + ellipse.getWidth(), ellipse.getY());
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        return null;
    }
}

