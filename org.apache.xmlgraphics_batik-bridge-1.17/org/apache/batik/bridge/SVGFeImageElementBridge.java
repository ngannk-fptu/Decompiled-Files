/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.AffineRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.ext.awt.image.spi.ImageTagRegistry
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGFeImageElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feImage";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        String uriStr = XLinkSupport.getXLinkHref((Element)filterElement);
        if (uriStr.length() == 0) {
            throw new BridgeException(ctx, filterElement, "attribute.missing", new Object[]{"xlink:href"});
        }
        Document document = filterElement.getOwnerDocument();
        boolean isUse = uriStr.indexOf(35) != -1;
        Element contentElement = null;
        contentElement = isUse ? document.createElementNS("http://www.w3.org/2000/svg", "use") : document.createElementNS("http://www.w3.org/2000/svg", "image");
        contentElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", uriStr);
        Element proxyElement = document.createElementNS("http://www.w3.org/2000/svg", "g");
        proxyElement.appendChild(contentElement);
        Rectangle2D defaultRegion = filterRegion;
        Element filterDefElement = (Element)filterElement.getParentNode();
        Rectangle2D primitiveRegion = SVGUtilities.getBaseFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, ctx);
        contentElement.setAttributeNS(null, "x", String.valueOf(primitiveRegion.getX()));
        contentElement.setAttributeNS(null, "y", String.valueOf(primitiveRegion.getY()));
        contentElement.setAttributeNS(null, "width", String.valueOf(primitiveRegion.getWidth()));
        contentElement.setAttributeNS(null, "height", String.valueOf(primitiveRegion.getHeight()));
        GraphicsNode node = ctx.getGVTBuilder().build(ctx, proxyElement);
        Filter filter = node.getGraphicsNodeRable(true);
        String s = SVGUtilities.getChainableAttributeNS(filterDefElement, null, "primitiveUnits", ctx);
        short coordSystemType = s.length() == 0 ? (short)1 : SVGUtilities.parseCoordinateSystem(filterDefElement, "primitiveUnits", s, ctx);
        AffineTransform at = new AffineTransform();
        if (coordSystemType == 2) {
            at = SVGUtilities.toObjectBBox(at, filteredNode);
        }
        filter = new AffineRable8Bit(filter, at);
        SVGFeImageElementBridge.handleColorInterpolationFilters(filter, filterElement);
        Rectangle2D primitiveRegionUserSpace = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        filter = new PadRable8Bit(filter, primitiveRegionUserSpace, PadMode.ZERO_PAD);
        SVGFeImageElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }

    protected static Filter createSVGFeImage(BridgeContext ctx, Rectangle2D primitiveRegion, Element refElement, boolean toBBoxNeeded, Element filterElement, GraphicsNode filteredNode) {
        GraphicsNode node = ctx.getGVTBuilder().build(ctx, refElement);
        Filter filter = node.getGraphicsNodeRable(true);
        AffineTransform at = new AffineTransform();
        if (toBBoxNeeded) {
            Element filterDefElement = (Element)filterElement.getParentNode();
            String s = SVGUtilities.getChainableAttributeNS(filterDefElement, null, "primitiveUnits", ctx);
            int coordSystemType = s.length() == 0 ? 1 : (int)SVGUtilities.parseCoordinateSystem(filterDefElement, "primitiveUnits", s, ctx);
            if (coordSystemType == 2) {
                at = SVGUtilities.toObjectBBox(at, filteredNode);
            }
            Rectangle2D bounds = filteredNode.getGeometryBounds();
            at.preConcatenate(AffineTransform.getTranslateInstance(primitiveRegion.getX() - bounds.getX(), primitiveRegion.getY() - bounds.getY()));
        } else {
            at.translate(primitiveRegion.getX(), primitiveRegion.getY());
        }
        return new AffineRable8Bit(filter, at);
    }

    protected static Filter createRasterFeImage(BridgeContext ctx, Rectangle2D primitiveRegion, ParsedURL purl) {
        Filter filter = ImageTagRegistry.getRegistry().readURL(purl);
        Rectangle2D bounds = filter.getBounds2D();
        AffineTransform scale = new AffineTransform();
        scale.translate(primitiveRegion.getX(), primitiveRegion.getY());
        scale.scale(primitiveRegion.getWidth() / (bounds.getWidth() - 1.0), primitiveRegion.getHeight() / (bounds.getHeight() - 1.0));
        scale.translate(-bounds.getX(), -bounds.getY());
        return new AffineRable8Bit(filter, scale);
    }
}

