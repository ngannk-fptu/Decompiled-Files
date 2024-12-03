/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.filter.Mask
 *  org.apache.batik.gvt.filter.MaskRable8Bit
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.MaskBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.MaskRable8Bit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGMaskElementBridge
extends AnimatableGenericSVGBridge
implements MaskBridge {
    @Override
    public String getLocalName() {
        return "mask";
    }

    @Override
    public Mask createMask(BridgeContext ctx, Element maskElement, Element maskedElement, GraphicsNode maskedNode) {
        Rectangle2D maskRegion = SVGUtilities.convertMaskRegion(maskElement, maskedElement, maskedNode, ctx);
        GVTBuilder builder = ctx.getGVTBuilder();
        CompositeGraphicsNode maskNode = new CompositeGraphicsNode();
        CompositeGraphicsNode maskNodeContent = new CompositeGraphicsNode();
        maskNode.getChildren().add(maskNodeContent);
        boolean hasChildren = false;
        for (Node node = maskElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            Element child;
            GraphicsNode gn;
            if (node.getNodeType() != 1 || (gn = builder.build(ctx, child = (Element)node)) == null) continue;
            hasChildren = true;
            maskNodeContent.getChildren().add(gn);
        }
        if (!hasChildren) {
            return null;
        }
        String s = maskElement.getAttributeNS(null, "transform");
        AffineTransform Tx = s.length() != 0 ? SVGUtilities.convertTransform(maskElement, "transform", s, ctx) : new AffineTransform();
        s = maskElement.getAttributeNS(null, "maskContentUnits");
        int coordSystemType = s.length() == 0 ? 1 : (int)SVGUtilities.parseCoordinateSystem(maskElement, "maskContentUnits", s, ctx);
        if (coordSystemType == 2) {
            Tx = SVGUtilities.toObjectBBox(Tx, maskedNode);
        }
        maskNodeContent.setTransform(Tx);
        Filter filter = maskedNode.getFilter();
        if (filter == null) {
            filter = maskedNode.getGraphicsNodeRable(true);
        }
        return new MaskRable8Bit(filter, (GraphicsNode)maskNode, maskRegion);
    }
}

