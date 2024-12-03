/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.ext.awt.LinearGradientPaint
 *  org.apache.batik.ext.awt.MultipleGradientPaint$ColorSpaceEnum
 *  org.apache.batik.ext.awt.MultipleGradientPaint$CycleMethodEnum
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.parser.UnitProcessor$Context
 */
package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.AbstractSVGGradientElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;

public class SVGLinearGradientElementBridge
extends AbstractSVGGradientElementBridge {
    @Override
    public String getLocalName() {
        return "linearGradient";
    }

    @Override
    protected Paint buildGradient(Element paintElement, Element paintedElement, GraphicsNode paintedNode, MultipleGradientPaint.CycleMethodEnum spreadMethod, MultipleGradientPaint.ColorSpaceEnum colorSpace, AffineTransform transform, Color[] colors, float[] offsets, BridgeContext ctx) {
        Rectangle2D bbox;
        String s;
        String y2Str;
        String x2Str;
        String y1Str;
        String x1Str = SVGUtilities.getChainableAttributeNS(paintElement, null, "x1", ctx);
        if (x1Str.length() == 0) {
            x1Str = "0%";
        }
        if ((y1Str = SVGUtilities.getChainableAttributeNS(paintElement, null, "y1", ctx)).length() == 0) {
            y1Str = "0%";
        }
        if ((x2Str = SVGUtilities.getChainableAttributeNS(paintElement, null, "x2", ctx)).length() == 0) {
            x2Str = "100%";
        }
        if ((y2Str = SVGUtilities.getChainableAttributeNS(paintElement, null, "y2", ctx)).length() == 0) {
            y2Str = "0%";
        }
        short coordSystemType = (s = SVGUtilities.getChainableAttributeNS(paintElement, null, "gradientUnits", ctx)).length() == 0 ? (short)2 : (short)SVGUtilities.parseCoordinateSystem(paintElement, "gradientUnits", s, ctx);
        SVGContext bridge = BridgeContext.getSVGContext(paintedElement);
        if (coordSystemType == 2 && bridge instanceof AbstractGraphicsNodeBridge && (bbox = bridge.getBBox()) != null && (bbox.getWidth() == 0.0 || bbox.getHeight() == 0.0)) {
            return null;
        }
        if (coordSystemType == 2) {
            transform = SVGUtilities.toObjectBBox(transform, paintedNode);
        }
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintElement);
        Point2D p1 = SVGUtilities.convertPoint(x1Str, "x1", y1Str, "y1", coordSystemType, uctx);
        Point2D p2 = SVGUtilities.convertPoint(x2Str, "x2", y2Str, "y2", coordSystemType, uctx);
        if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
            return colors[colors.length - 1];
        }
        return new LinearGradientPaint(p1, p2, offsets, colors, spreadMethod, colorSpace, transform);
    }
}

