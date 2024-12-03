/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.AffineRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeOffsetElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feOffset";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        Filter in = SVGFeOffsetElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        float dx = SVGFeOffsetElementBridge.convertNumber(filterElement, "dx", 0.0f, ctx);
        float dy = SVGFeOffsetElementBridge.convertNumber(filterElement, "dy", 0.0f, ctx);
        AffineTransform at = AffineTransform.getTranslateInstance(dx, dy);
        PadRable8Bit pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        AffineRable8Bit filter = new AffineRable8Bit((Filter)pad, at);
        filter = new PadRable8Bit((Filter)filter, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeOffsetElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        SVGFeOffsetElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }
}

