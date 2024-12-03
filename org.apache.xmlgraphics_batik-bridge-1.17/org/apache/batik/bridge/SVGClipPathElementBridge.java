/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMUseElement
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.ShapeNode
 */
package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ClipBridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGClipPathElementBridge
extends AnimatableGenericSVGBridge
implements ClipBridge {
    @Override
    public String getLocalName() {
        return "clipPath";
    }

    @Override
    public ClipRable createClip(BridgeContext ctx, Element clipElement, Element clipedElement, GraphicsNode clipedNode) {
        Filter filter;
        String s = clipElement.getAttributeNS(null, "transform");
        AffineTransform Tx = s.length() != 0 ? SVGUtilities.convertTransform(clipElement, "transform", s, ctx) : new AffineTransform();
        s = clipElement.getAttributeNS(null, "clipPathUnits");
        int coordSystemType = s.length() == 0 ? 1 : (int)SVGUtilities.parseCoordinateSystem(clipElement, "clipPathUnits", s, ctx);
        if (coordSystemType == 2) {
            Tx = SVGUtilities.toObjectBBox(Tx, clipedNode);
        }
        Area clipPath = new Area();
        GVTBuilder builder = ctx.getGVTBuilder();
        boolean hasChildren = false;
        for (Node node = clipElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            Node shadowChild;
            Element child;
            GraphicsNode clipNode;
            if (node.getNodeType() != 1 || (clipNode = builder.build(ctx, child = (Element)node)) == null) continue;
            hasChildren = true;
            if (child instanceof SVGOMUseElement && (shadowChild = ((SVGOMUseElement)child).getCSSFirstChild()) != null && shadowChild.getNodeType() == 1) {
                child = (Element)shadowChild;
            }
            int wr = CSSUtilities.convertClipRule(child);
            GeneralPath path = new GeneralPath(clipNode.getOutline());
            path.setWindingRule(wr);
            AffineTransform at = clipNode.getTransform();
            if (at == null) {
                at = Tx;
            } else {
                at.preConcatenate(Tx);
            }
            Shape outline = at.createTransformedShape(path);
            ShapeNode outlineNode = new ShapeNode();
            outlineNode.setShape(outline);
            ClipRable clip = CSSUtilities.convertClipPath(child, (GraphicsNode)outlineNode, ctx);
            if (clip != null) {
                Area area = new Area(outline);
                area.subtract(new Area(clip.getClipPath()));
                outline = area;
            }
            clipPath.add(new Area(outline));
        }
        if (!hasChildren) {
            return null;
        }
        ShapeNode clipPathNode = new ShapeNode();
        clipPathNode.setShape((Shape)clipPath);
        ClipRable clipElementClipPath = CSSUtilities.convertClipPath(clipElement, (GraphicsNode)clipPathNode, ctx);
        if (clipElementClipPath != null) {
            clipPath.subtract(new Area(clipElementClipPath.getClipPath()));
        }
        if ((filter = clipedNode.getFilter()) == null) {
            filter = clipedNode.getGraphicsNodeRable(true);
        }
        boolean useAA = false;
        RenderingHints hints = CSSUtilities.convertShapeRendering(clipElement, null);
        if (hints != null) {
            Object o = hints.get(RenderingHints.KEY_ANTIALIASING);
            useAA = o == RenderingHints.VALUE_ANTIALIAS_ON;
        }
        return new ClipRable8Bit(filter, (Shape)clipPath, useAA);
    }
}

