/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.TurbulenceRable8Bit
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
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeTurbulenceElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feTurbulence";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        Filter in = SVGFeTurbulenceElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = filterRegion;
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        float[] baseFrequency = SVGFeTurbulenceElementBridge.convertBaseFrenquency(filterElement, ctx);
        int numOctaves = SVGFeTurbulenceElementBridge.convertInteger(filterElement, "numOctaves", 1, ctx);
        int seed = SVGFeTurbulenceElementBridge.convertInteger(filterElement, "seed", 0, ctx);
        boolean stitchTiles = SVGFeTurbulenceElementBridge.convertStitchTiles(filterElement, ctx);
        boolean isFractalNoise = SVGFeTurbulenceElementBridge.convertType(filterElement, ctx);
        TurbulenceRable8Bit turbulenceRable = new TurbulenceRable8Bit(primitiveRegion);
        turbulenceRable.setBaseFrequencyX((double)baseFrequency[0]);
        turbulenceRable.setBaseFrequencyY((double)baseFrequency[1]);
        turbulenceRable.setNumOctaves(numOctaves);
        turbulenceRable.setSeed(seed);
        turbulenceRable.setStitched(stitchTiles);
        turbulenceRable.setFractalNoise(isFractalNoise);
        SVGFeTurbulenceElementBridge.handleColorInterpolationFilters((Filter)turbulenceRable, filterElement);
        SVGFeTurbulenceElementBridge.updateFilterMap(filterElement, (Filter)turbulenceRable, filterMap);
        return turbulenceRable;
    }

    protected static float[] convertBaseFrenquency(Element e, BridgeContext ctx) {
        String s = e.getAttributeNS(null, "baseFrequency");
        if (s.length() == 0) {
            return new float[]{0.001f, 0.001f};
        }
        float[] v = new float[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            v[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            v[1] = tokens.hasMoreTokens() ? SVGUtilities.convertSVGNumber(tokens.nextToken()) : v[0];
            if (tokens.hasMoreTokens()) {
                throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"baseFrequency", s});
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[]{"baseFrequency", s});
        }
        if (v[0] < 0.0f || v[1] < 0.0f) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"baseFrequency", s});
        }
        return v;
    }

    protected static boolean convertStitchTiles(Element e, BridgeContext ctx) {
        String s = e.getAttributeNS(null, "stitchTiles");
        if (s.length() == 0) {
            return false;
        }
        if ("stitch".equals(s)) {
            return true;
        }
        if ("noStitch".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"stitchTiles", s});
    }

    protected static boolean convertType(Element e, BridgeContext ctx) {
        String s = e.getAttributeNS(null, "type");
        if (s.length() == 0) {
            return false;
        }
        if ("fractalNoise".equals(s)) {
            return true;
        }
        if ("turbulence".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"type", s});
    }
}

