/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.ext.awt.MultipleGradientPaint$ColorSpaceEnum
 *  org.apache.batik.ext.awt.MultipleGradientPaint$CycleMethodEnum
 *  org.apache.batik.ext.awt.RadialGradientPaint
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
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;

public class SVGRadialGradientElementBridge
extends AbstractSVGGradientElementBridge {
    @Override
    public String getLocalName() {
        return "radialGradient";
    }

    @Override
    protected Paint buildGradient(Element paintElement, Element paintedElement, GraphicsNode paintedNode, MultipleGradientPaint.CycleMethodEnum spreadMethod, MultipleGradientPaint.ColorSpaceEnum colorSpace, AffineTransform transform, Color[] colors, float[] offsets, BridgeContext ctx) {
        UnitProcessor.Context uctx;
        float r;
        Rectangle2D bbox;
        String s;
        String fyStr;
        String fxStr;
        String rStr;
        String cyStr;
        String cxStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "cx", ctx);
        if (cxStr.length() == 0) {
            cxStr = "50%";
        }
        if ((cyStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "cy", ctx)).length() == 0) {
            cyStr = "50%";
        }
        if ((rStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "r", ctx)).length() == 0) {
            rStr = "50%";
        }
        if ((fxStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "fx", ctx)).length() == 0) {
            fxStr = cxStr;
        }
        if ((fyStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "fy", ctx)).length() == 0) {
            fyStr = cyStr;
        }
        short coordSystemType = (s = SVGUtilities.getChainableAttributeNS(paintElement, null, "gradientUnits", ctx)).length() == 0 ? (short)2 : (short)SVGUtilities.parseCoordinateSystem(paintElement, "gradientUnits", s, ctx);
        SVGContext bridge = BridgeContext.getSVGContext(paintedElement);
        if (coordSystemType == 2 && bridge instanceof AbstractGraphicsNodeBridge && (bbox = bridge.getBBox()) != null && (bbox.getWidth() == 0.0 || bbox.getHeight() == 0.0)) {
            return null;
        }
        if (coordSystemType == 2) {
            transform = SVGUtilities.toObjectBBox(transform, paintedNode);
        }
        if ((r = SVGUtilities.convertLength(rStr, "r", coordSystemType, uctx = UnitProcessor.createContext(ctx, paintElement))) == 0.0f) {
            return colors[colors.length - 1];
        }
        Point2D c = SVGUtilities.convertPoint(cxStr, "cx", cyStr, "cy", coordSystemType, uctx);
        Point2D f = SVGUtilities.convertPoint(fxStr, "fx", fyStr, "fy", coordSystemType, uctx);
        return new RadialGradientPaint(c, r, f, offsets, colors, spreadMethod, RadialGradientPaint.SRGB, transform);
    }
}

