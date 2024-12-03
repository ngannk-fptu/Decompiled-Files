/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.Light
 *  org.apache.batik.ext.awt.image.renderable.DiffuseLightingRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGLightingElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.renderable.DiffuseLightingRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeDiffuseLightingElementBridge
extends AbstractSVGLightingElementBridge {
    @Override
    public String getLocalName() {
        return "feDiffuseLighting";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        float surfaceScale = SVGFeDiffuseLightingElementBridge.convertNumber(filterElement, "surfaceScale", 1.0f, ctx);
        float diffuseConstant = SVGFeDiffuseLightingElementBridge.convertNumber(filterElement, "diffuseConstant", 1.0f, ctx);
        Light light = SVGFeDiffuseLightingElementBridge.extractLight(filterElement, ctx);
        double[] kernelUnitLength = SVGFeDiffuseLightingElementBridge.convertKernelUnitLength(filterElement, ctx);
        Filter in = SVGFeDiffuseLightingElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        DiffuseLightingRable8Bit filter = new DiffuseLightingRable8Bit(in, primitiveRegion, light, (double)diffuseConstant, (double)surfaceScale, kernelUnitLength);
        SVGFeDiffuseLightingElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        SVGFeDiffuseLightingElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }
}

