/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMRectElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGShapeElementBridge;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

public class SVGRectElementBridge
extends SVGShapeElementBridge {
    @Override
    public String getLocalName() {
        return "rect";
    }

    @Override
    public Bridge getInstance() {
        return new SVGRectElementBridge();
    }

    @Override
    protected void buildShape(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        try {
            AbstractSVGAnimatedLength _ry;
            float ry;
            SVGOMRectElement re = (SVGOMRectElement)e;
            AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)re.getX();
            float x = _x.getCheckedValue();
            AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)re.getY();
            float y = _y.getCheckedValue();
            AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)re.getWidth();
            float w = _width.getCheckedValue();
            AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)re.getHeight();
            float h = _height.getCheckedValue();
            AbstractSVGAnimatedLength _rx = (AbstractSVGAnimatedLength)re.getRx();
            float rx = _rx.getCheckedValue();
            if (rx > w / 2.0f) {
                rx = w / 2.0f;
            }
            if ((ry = (_ry = (AbstractSVGAnimatedLength)re.getRy()).getCheckedValue()) > h / 2.0f) {
                ry = h / 2.0f;
            }
            RectangularShape shape = rx == 0.0f || ry == 0.0f ? new Rectangle2D.Float(x, y, w, h) : new RoundRectangle2D.Float(x, y, w, h, rx * 2.0f, ry * 2.0f);
            shapeNode.setShape((Shape)shape);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        String ln;
        if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("x") || ln.equals("y") || ln.equals("width") || ln.equals("height") || ln.equals("rx") || ln.equals("ry"))) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
            return;
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    @Override
    protected ShapePainter createShapePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        Shape shape = shapeNode.getShape();
        Rectangle2D r2d = shape.getBounds2D();
        if (r2d.getWidth() == 0.0 || r2d.getHeight() == 0.0) {
            return null;
        }
        return super.createShapePainter(ctx, e, shapeNode);
    }
}

