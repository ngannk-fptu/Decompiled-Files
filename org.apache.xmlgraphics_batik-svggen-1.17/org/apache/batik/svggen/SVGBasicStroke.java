/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.BasicStroke;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGStrokeDescriptor;

public class SVGBasicStroke
extends AbstractSVGConverter {
    public SVGBasicStroke(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        if (gc.getStroke() instanceof BasicStroke) {
            return this.toSVG((BasicStroke)gc.getStroke());
        }
        return null;
    }

    public final SVGStrokeDescriptor toSVG(BasicStroke stroke) {
        String strokeWidth = this.doubleString(stroke.getLineWidth());
        String capStyle = SVGBasicStroke.endCapToSVG(stroke.getEndCap());
        String joinStyle = SVGBasicStroke.joinToSVG(stroke.getLineJoin());
        String miterLimit = this.doubleString(stroke.getMiterLimit());
        float[] array = stroke.getDashArray();
        String dashArray = null;
        dashArray = array != null ? this.dashArrayToSVG(array) : "none";
        String dashOffset = this.doubleString(stroke.getDashPhase());
        return new SVGStrokeDescriptor(strokeWidth, capStyle, joinStyle, miterLimit, dashArray, dashOffset);
    }

    private final String dashArrayToSVG(float[] dashArray) {
        StringBuffer dashArrayBuf = new StringBuffer(dashArray.length * 8);
        if (dashArray.length > 0) {
            dashArrayBuf.append(this.doubleString(dashArray[0]));
        }
        for (int i = 1; i < dashArray.length; ++i) {
            dashArrayBuf.append(",");
            dashArrayBuf.append(this.doubleString(dashArray[i]));
        }
        return dashArrayBuf.toString();
    }

    private static String joinToSVG(int lineJoin) {
        switch (lineJoin) {
            case 2: {
                return "bevel";
            }
            case 1: {
                return "round";
            }
        }
        return "miter";
    }

    private static String endCapToSVG(int endCap) {
        switch (endCap) {
            case 0: {
                return "butt";
            }
            case 1: {
                return "round";
            }
        }
        return "square";
    }
}

