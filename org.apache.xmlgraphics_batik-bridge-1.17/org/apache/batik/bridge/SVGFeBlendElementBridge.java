/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.CompositeRule
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeBlendElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feBlend";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        CompositeRule rule = SVGFeBlendElementBridge.convertMode(filterElement, ctx);
        Filter in = SVGFeBlendElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Filter in2 = SVGFeBlendElementBridge.getIn2(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in2 == null) {
            return null;
        }
        Rectangle2D defaultRegion = (Rectangle2D)in.getBounds2D().clone();
        defaultRegion.add(in2.getBounds2D());
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        ArrayList<Filter> srcs = new ArrayList<Filter>(2);
        srcs.add(in2);
        srcs.add(in);
        CompositeRable8Bit filter = new CompositeRable8Bit(srcs, rule, true);
        SVGFeBlendElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        filter = new PadRable8Bit((Filter)filter, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeBlendElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static CompositeRule convertMode(Element filterElement, BridgeContext ctx) {
        String rule = filterElement.getAttributeNS(null, "mode");
        if (rule.length() == 0) {
            return CompositeRule.OVER;
        }
        if ("normal".equals(rule)) {
            return CompositeRule.OVER;
        }
        if ("multiply".equals(rule)) {
            return CompositeRule.MULTIPLY;
        }
        if ("screen".equals(rule)) {
            return CompositeRule.SCREEN;
        }
        if ("darken".equals(rule)) {
            return CompositeRule.DARKEN;
        }
        if ("lighten".equals(rule)) {
            return CompositeRule.LIGHTEN;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"mode", rule});
    }
}

