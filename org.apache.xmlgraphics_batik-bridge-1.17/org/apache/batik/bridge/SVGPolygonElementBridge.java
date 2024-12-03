/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedPoints
 *  org.apache.batik.anim.dom.SVGOMPolygonElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.parser.AWTPolygonProducer
 *  org.w3c.dom.svg.SVGPoint
 *  org.w3c.dom.svg.SVGPointList
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedPoints;
import org.apache.batik.anim.dom.SVGOMPolygonElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGDecoratedShapeElementBridge;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPolygonProducer;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SVGPolygonElementBridge
extends SVGDecoratedShapeElementBridge {
    protected static final Shape DEFAULT_SHAPE = new GeneralPath();

    @Override
    public String getLocalName() {
        return "polygon";
    }

    @Override
    public Bridge getInstance() {
        return new SVGPolygonElementBridge();
    }

    @Override
    protected void buildShape(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        SVGOMPolygonElement pe = (SVGOMPolygonElement)e;
        try {
            SVGOMAnimatedPoints _points = pe.getSVGOMAnimatedPoints();
            _points.check();
            SVGPointList pl = _points.getAnimatedPoints();
            int size = pl.getNumberOfItems();
            if (size == 0) {
                shapeNode.setShape(DEFAULT_SHAPE);
            } else {
                AWTPolygonProducer app = new AWTPolygonProducer();
                app.setWindingRule(CSSUtilities.convertFillRule(e));
                app.startPoints();
                for (int i = 0; i < size; ++i) {
                    SVGPoint p = pl.getItem(i);
                    app.point(p.getX(), p.getY());
                }
                app.endPoints();
                shapeNode.setShape(app.getShape());
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        String ln;
        if (alav.getNamespaceURI() == null && (ln = alav.getLocalName()).equals("points")) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
            return;
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    @Override
    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 17: {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
            }
        }
    }
}

