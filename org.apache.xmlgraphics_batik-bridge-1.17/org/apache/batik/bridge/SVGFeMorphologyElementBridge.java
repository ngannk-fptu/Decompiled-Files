/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.MorphologyRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.MorphologyRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeMorphologyElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feMorphology";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        float[] radii = SVGFeMorphologyElementBridge.convertRadius(filterElement, ctx);
        if (radii[0] == 0.0f || radii[1] == 0.0f) {
            return null;
        }
        boolean isDilate = SVGFeMorphologyElementBridge.convertOperator(filterElement, ctx);
        Filter in = SVGFeMorphologyElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        PadRable8Bit pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        MorphologyRable8Bit morphology = new MorphologyRable8Bit((Filter)pad, (double)radii[0], (double)radii[1], isDilate);
        SVGFeMorphologyElementBridge.handleColorInterpolationFilters((Filter)morphology, filterElement);
        PadRable8Bit filter = new PadRable8Bit((Filter)morphology, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeMorphologyElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static float[] convertRadius(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "radius");
        if (s.length() == 0) {
            return new float[]{0.0f, 0.0f};
        }
        float[] radii = new float[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            radii[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            radii[1] = tokens.hasMoreTokens() ? SVGUtilities.convertSVGNumber(tokens.nextToken()) : radii[0];
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"radius", s, nfEx});
        }
        if (tokens.hasMoreTokens() || radii[0] < 0.0f || radii[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"radius", s});
        }
        return radii;
    }

    protected static boolean convertOperator(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "operator");
        if (s.length() == 0) {
            return false;
        }
        if ("erode".equals(s)) {
            return false;
        }
        if ("dilate".equals(s)) {
            return true;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"operator", s});
    }
}

