/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMCircleElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGShapeElementBridge;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

public class SVGCircleElementBridge
extends SVGShapeElementBridge {
    @Override
    public String getLocalName() {
        return "circle";
    }

    @Override
    public Bridge getInstance() {
        return new SVGCircleElementBridge();
    }

    @Override
    protected void buildShape(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        try {
            SVGOMCircleElement ce = (SVGOMCircleElement)e;
            AbstractSVGAnimatedLength _cx = (AbstractSVGAnimatedLength)ce.getCx();
            float cx = _cx.getCheckedValue();
            AbstractSVGAnimatedLength _cy = (AbstractSVGAnimatedLength)ce.getCy();
            float cy = _cy.getCheckedValue();
            AbstractSVGAnimatedLength _r = (AbstractSVGAnimatedLength)ce.getR();
            float r = _r.getCheckedValue();
            float x = cx - r;
            float y = cy - r;
            float w = r * 2.0f;
            shapeNode.setShape((Shape)new Ellipse2D.Float(x, y, w, w));
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        String ln;
        if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("cx") || ln.equals("cy") || ln.equals("r"))) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
            return;
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    @Override
    protected ShapePainter createShapePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        Rectangle2D r2d = shapeNode.getShape().getBounds2D();
        if (r2d.getWidth() == 0.0 || r2d.getHeight() == 0.0) {
            return null;
        }
        return super.createShapePainter(ctx, e, shapeNode);
    }
}

