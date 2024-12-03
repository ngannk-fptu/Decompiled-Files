/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.GaussianBlurRable8Bit
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
import org.apache.batik.ext.awt.image.renderable.GaussianBlurRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeGaussianBlurElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feGaussianBlur";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        float[] stdDeviationXY = SVGFeGaussianBlurElementBridge.convertStdDeviation(filterElement, ctx);
        if (stdDeviationXY[0] < 0.0f || stdDeviationXY[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"stdDeviation", String.valueOf(stdDeviationXY[0]) + stdDeviationXY[1]});
        }
        Filter in = SVGFeGaussianBlurElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        PadRable8Bit pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        GaussianBlurRable8Bit blur = new GaussianBlurRable8Bit((Filter)pad, (double)stdDeviationXY[0], (double)stdDeviationXY[1]);
        SVGFeGaussianBlurElementBridge.handleColorInterpolationFilters((Filter)blur, filterElement);
        PadRable8Bit filter = new PadRable8Bit((Filter)blur, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeGaussianBlurElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static float[] convertStdDeviation(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "stdDeviation");
        if (s.length() == 0) {
            return new float[]{0.0f, 0.0f};
        }
        float[] stdDevs = new float[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            stdDevs[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            stdDevs[1] = tokens.hasMoreTokens() ? SVGUtilities.convertSVGNumber(tokens.nextToken()) : stdDevs[0];
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"stdDeviation", s, nfEx});
        }
        if (tokens.hasMoreTokens()) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"stdDeviation", s});
        }
        return stdDevs;
    }
}

