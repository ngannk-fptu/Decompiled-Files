/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.FilterChainRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.FloodRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FloodRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGFilterElementBridge
extends AnimatableGenericSVGBridge
implements FilterBridge,
ErrorConstants {
    protected static final Color TRANSPARENT_BLACK = new Color(0, true);

    @Override
    public String getLocalName() {
        return "filter";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode) {
        Rectangle2D filterRegion = SVGUtilities.convertFilterChainRegion(filterElement, filteredElement, filteredNode, ctx);
        if (filterRegion == null) {
            return null;
        }
        Filter sourceGraphic = filteredNode.getGraphicsNodeRable(true);
        sourceGraphic = new PadRable8Bit(sourceGraphic, filterRegion, PadMode.ZERO_PAD);
        FilterChainRable8Bit filterChain = new FilterChainRable8Bit(sourceGraphic, filterRegion);
        float[] filterRes = SVGUtilities.convertFilterRes(filterElement, ctx);
        filterChain.setFilterResolutionX((int)filterRes[0]);
        filterChain.setFilterResolutionY((int)filterRes[1]);
        HashMap<String, Filter> filterNodeMap = new HashMap<String, Filter>(11);
        filterNodeMap.put("SourceGraphic", sourceGraphic);
        Filter in = SVGFilterElementBridge.buildFilterPrimitives(filterElement, filterRegion, filteredElement, filteredNode, sourceGraphic, filterNodeMap, ctx);
        if (in == null) {
            return null;
        }
        if (in == sourceGraphic) {
            in = SVGFilterElementBridge.createEmptyFilter(filterElement, filterRegion, filteredElement, filteredNode, ctx);
        }
        filterChain.setSource(in);
        return filterChain;
    }

    protected static Filter createEmptyFilter(Element filterElement, Rectangle2D filterRegion, Element filteredElement, GraphicsNode filteredNode, BridgeContext ctx) {
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(null, filterElement, filteredElement, filteredNode, filterRegion, filterRegion, ctx);
        return new FloodRable8Bit(primitiveRegion, (Paint)TRANSPARENT_BLACK);
    }

    protected static Filter buildFilterPrimitives(Element filterElement, Rectangle2D filterRegion, Element filteredElement, GraphicsNode filteredNode, Filter in, Map filterNodeMap, BridgeContext ctx) {
        LinkedList<ParsedURL> refs = new LinkedList<ParsedURL>();
        Filter newIn;
        while ((newIn = SVGFilterElementBridge.buildLocalFilterPrimitives(filterElement, filterRegion, filteredElement, filteredNode, in, filterNodeMap, ctx)) == in) {
            String uri = XLinkSupport.getXLinkHref((Element)filterElement);
            if (uri.length() == 0) {
                return in;
            }
            SVGOMDocument doc = (SVGOMDocument)filterElement.getOwnerDocument();
            ParsedURL url = new ParsedURL(doc.getURLObject(), uri);
            if (refs.contains(url)) {
                throw new BridgeException(ctx, filterElement, "xlink.href.circularDependencies", new Object[]{uri});
            }
            refs.add(url);
            filterElement = ctx.getReferencedElement(filterElement, uri);
        }
        return newIn;
    }

    protected static Filter buildLocalFilterPrimitives(Element filterElement, Rectangle2D filterRegion, Element filteredElement, GraphicsNode filteredNode, Filter in, Map filterNodeMap, BridgeContext ctx) {
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element e;
            Bridge bridge;
            if (n.getNodeType() != 1 || (bridge = ctx.getBridge(e = (Element)n)) == null || !(bridge instanceof FilterPrimitiveBridge)) continue;
            FilterPrimitiveBridge filterBridge = (FilterPrimitiveBridge)bridge;
            Filter filterNode = filterBridge.createFilter(ctx, e, filteredElement, filteredNode, in, filterRegion, filterNodeMap);
            if (filterNode == null) {
                return null;
            }
            in = filterNode;
        }
        return in;
    }
}

