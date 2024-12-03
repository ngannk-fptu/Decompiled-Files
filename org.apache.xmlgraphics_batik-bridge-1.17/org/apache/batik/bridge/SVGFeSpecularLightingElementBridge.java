/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.Light
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.SpecularLightingRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGLightingElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.SpecularLightingRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeSpecularLightingElementBridge
extends AbstractSVGLightingElementBridge {
    @Override
    public String getLocalName() {
        return "feSpecularLighting";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        float surfaceScale = SVGFeSpecularLightingElementBridge.convertNumber(filterElement, "surfaceScale", 1.0f, ctx);
        float specularConstant = SVGFeSpecularLightingElementBridge.convertNumber(filterElement, "specularConstant", 1.0f, ctx);
        float specularExponent = SVGFeSpecularLightingElementBridge.convertSpecularExponent(filterElement, ctx);
        Light light = SVGFeSpecularLightingElementBridge.extractLight(filterElement, ctx);
        double[] kernelUnitLength = SVGFeSpecularLightingElementBridge.convertKernelUnitLength(filterElement, ctx);
        Filter in = SVGFeSpecularLightingElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        SpecularLightingRable8Bit filter = new SpecularLightingRable8Bit(in, primitiveRegion, light, (double)specularConstant, (double)specularExponent, (double)surfaceScale, kernelUnitLength);
        SVGFeSpecularLightingElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        SVGFeSpecularLightingElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static float convertSpecularExponent(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "specularExponent");
        if (s.length() == 0) {
            return 1.0f;
        }
        try {
            float v = SVGUtilities.convertSVGNumber(s);
            if (v < 1.0f || v > 128.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"specularConstant", s});
            }
            return v;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"specularConstant", s, nfEx});
        }
    }
}

