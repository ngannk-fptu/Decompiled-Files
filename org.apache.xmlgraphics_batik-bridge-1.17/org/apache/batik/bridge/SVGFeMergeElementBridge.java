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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGFeMergeElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feMerge";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        List srcs = SVGFeMergeElementBridge.extractFeMergeNode(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (srcs == null) {
            return null;
        }
        if (srcs.size() == 0) {
            return null;
        }
        Iterator iter = srcs.iterator();
        Rectangle2D defaultRegion = (Rectangle2D)((Filter)iter.next()).getBounds2D().clone();
        while (iter.hasNext()) {
            defaultRegion.add(((Filter)iter.next()).getBounds2D());
        }
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        CompositeRable8Bit filter = new CompositeRable8Bit(srcs, CompositeRule.OVER, true);
        SVGFeMergeElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        filter = new PadRable8Bit((Filter)filter, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeMergeElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static List extractFeMergeNode(Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Map filterMap, BridgeContext ctx) {
        LinkedList<Filter> srcs = null;
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Filter filter;
            Element e;
            Bridge bridge;
            if (n.getNodeType() != 1 || (bridge = ctx.getBridge(e = (Element)n)) == null || !(bridge instanceof SVGFeMergeNodeElementBridge) || (filter = ((SVGFeMergeNodeElementBridge)bridge).createFilter(ctx, e, filteredElement, filteredNode, inputFilter, filterMap)) == null) continue;
            if (srcs == null) {
                srcs = new LinkedList<Filter>();
            }
            srcs.add(filter);
        }
        return srcs;
    }

    public static class SVGFeMergeNodeElementBridge
    extends AnimatableGenericSVGBridge {
        @Override
        public String getLocalName() {
            return "feMergeNode";
        }

        public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Map filterMap) {
            return AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        }
    }
}

