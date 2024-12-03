/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.ARGBChannel
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.DisplacementMapRable8Bit
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
import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeDisplacementMapElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feDisplacementMap";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        float scale = SVGFeDisplacementMapElementBridge.convertNumber(filterElement, "scale", 0.0f, ctx);
        ARGBChannel xChannelSelector = SVGFeDisplacementMapElementBridge.convertChannelSelector(filterElement, "xChannelSelector", ARGBChannel.A, ctx);
        ARGBChannel yChannelSelector = SVGFeDisplacementMapElementBridge.convertChannelSelector(filterElement, "yChannelSelector", ARGBChannel.A, ctx);
        Filter in = SVGFeDisplacementMapElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Filter in2 = SVGFeDisplacementMapElementBridge.getIn2(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in2 == null) {
            return null;
        }
        Rectangle2D defaultRegion = (Rectangle2D)in.getBounds2D().clone();
        defaultRegion.add(in2.getBounds2D());
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        PadRable8Bit pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        ArrayList<Object> srcs = new ArrayList<Object>(2);
        srcs.add(pad);
        srcs.add(in2);
        DisplacementMapRable8Bit displacementMap = new DisplacementMapRable8Bit(srcs, (double)scale, xChannelSelector, yChannelSelector);
        SVGFeDisplacementMapElementBridge.handleColorInterpolationFilters((Filter)displacementMap, filterElement);
        PadRable8Bit filter = new PadRable8Bit((Filter)displacementMap, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeDisplacementMapElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static ARGBChannel convertChannelSelector(Element filterElement, String attrName, ARGBChannel defaultChannel, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultChannel;
        }
        if ("A".equals(s)) {
            return ARGBChannel.A;
        }
        if ("R".equals(s)) {
            return ARGBChannel.R;
        }
        if ("G".equals(s)) {
            return ARGBChannel.G;
        }
        if ("B".equals(s)) {
            return ARGBChannel.B;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{attrName, s});
    }
}

