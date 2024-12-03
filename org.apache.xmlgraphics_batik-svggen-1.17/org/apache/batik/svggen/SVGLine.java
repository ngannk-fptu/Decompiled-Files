/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.geom.Line2D;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicObjectConverter;
import org.w3c.dom.Element;

public class SVGLine
extends SVGGraphicObjectConverter {
    public SVGLine(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public Element toSVG(Line2D line) {
        Element svgLine = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "line");
        svgLine.setAttributeNS(null, "x1", this.doubleString(line.getX1()));
        svgLine.setAttributeNS(null, "y1", this.doubleString(line.getY1()));
        svgLine.setAttributeNS(null, "x2", this.doubleString(line.getX2()));
        svgLine.setAttributeNS(null, "y2", this.doubleString(line.getY2()));
        return svgLine;
    }
}

