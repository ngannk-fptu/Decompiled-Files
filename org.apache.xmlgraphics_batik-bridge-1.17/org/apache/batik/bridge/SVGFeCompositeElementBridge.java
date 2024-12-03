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

public class SVGFeCompositeElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feComposite";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        CompositeRule rule = SVGFeCompositeElementBridge.convertOperator(filterElement, ctx);
        Filter in = SVGFeCompositeElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Filter in2 = SVGFeCompositeElementBridge.getIn2(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
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
        SVGFeCompositeElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        filter = new PadRable8Bit((Filter)filter, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeCompositeElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static CompositeRule convertOperator(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "operator");
        if (s.length() == 0) {
            return CompositeRule.OVER;
        }
        if ("atop".equals(s)) {
            return CompositeRule.ATOP;
        }
        if ("in".equals(s)) {
            return CompositeRule.IN;
        }
        if ("over".equals(s)) {
            return CompositeRule.OVER;
        }
        if ("out".equals(s)) {
            return CompositeRule.OUT;
        }
        if ("xor".equals(s)) {
            return CompositeRule.XOR;
        }
        if ("arithmetic".equals(s)) {
            float k1 = SVGFeCompositeElementBridge.convertNumber(filterElement, "k1", 0.0f, ctx);
            float k2 = SVGFeCompositeElementBridge.convertNumber(filterElement, "k2", 0.0f, ctx);
            float k3 = SVGFeCompositeElementBridge.convertNumber(filterElement, "k3", 0.0f, ctx);
            float k4 = SVGFeCompositeElementBridge.convertNumber(filterElement, "k4", 0.0f, ctx);
            return CompositeRule.ARITHMETIC((float)k1, (float)k2, (float)k3, (float)k4);
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"operator", s});
    }
}

