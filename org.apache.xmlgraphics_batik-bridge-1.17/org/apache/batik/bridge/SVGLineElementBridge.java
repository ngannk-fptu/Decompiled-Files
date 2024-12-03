/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMLineElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Line2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.bridge.SVGDecoratedShapeElementBridge;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

public class SVGLineElementBridge
extends SVGDecoratedShapeElementBridge {
    @Override
    public String getLocalName() {
        return "line";
    }

    @Override
    public Bridge getInstance() {
        return new SVGLineElementBridge();
    }

    @Override
    protected ShapePainter createFillStrokePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        return PaintServer.convertStrokePainter(e, shapeNode, ctx);
    }

    @Override
    protected void buildShape(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        try {
            SVGOMLineElement le = (SVGOMLineElement)e;
            AbstractSVGAnimatedLength _x1 = (AbstractSVGAnimatedLength)le.getX1();
            float x1 = _x1.getCheckedValue();
            AbstractSVGAnimatedLength _y1 = (AbstractSVGAnimatedLength)le.getY1();
            float y1 = _y1.getCheckedValue();
            AbstractSVGAnimatedLength _x2 = (AbstractSVGAnimatedLength)le.getX2();
            float x2 = _x2.getCheckedValue();
            AbstractSVGAnimatedLength _y2 = (AbstractSVGAnimatedLength)le.getY2();
            float y2 = _y2.getCheckedValue();
            shapeNode.setShape((Shape)new Line2D.Float(x1, y1, x2, y2));
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        String ln;
        if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("x1") || ln.equals("y1") || ln.equals("x2") || ln.equals("y2"))) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
            return;
        }
        super.handleAnimatedAttributeChanged(alav);
    }
}

