/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedPathData
 *  org.apache.batik.anim.dom.SVGOMPathElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGAnimatedPathDataSupport
 *  org.apache.batik.dom.svg.SVGPathContext
 *  org.apache.batik.ext.awt.geom.PathLength
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.parser.AWTPathProducer
 *  org.apache.batik.parser.PathHandler
 *  org.w3c.dom.svg.SVGPathSegList
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedPathData;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGDecoratedShapeElementBridge;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.dom.svg.SVGPathContext;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.PathHandler;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPathSegList;

public class SVGPathElementBridge
extends SVGDecoratedShapeElementBridge
implements SVGPathContext {
    protected static final Shape DEFAULT_SHAPE = new GeneralPath();
    protected Shape pathLengthShape;
    protected PathLength pathLength;

    @Override
    public String getLocalName() {
        return "path";
    }

    @Override
    public Bridge getInstance() {
        return new SVGPathElementBridge();
    }

    @Override
    protected void buildShape(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        SVGOMPathElement pe = (SVGOMPathElement)e;
        AWTPathProducer app = new AWTPathProducer();
        try {
            SVGOMAnimatedPathData _d = pe.getAnimatedPathData();
            _d.check();
            SVGPathSegList p = _d.getAnimatedPathSegList();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            SVGAnimatedPathDataSupport.handlePathSegList((SVGPathSegList)p, (PathHandler)app);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
        finally {
            shapeNode.setShape(app.getShape());
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null && alav.getLocalName().equals("d")) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
        } else {
            super.handleAnimatedAttributeChanged(alav);
        }
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

    protected PathLength getPathLengthObj() {
        Shape s = ((ShapeNode)this.node).getShape();
        if (this.pathLengthShape != s) {
            this.pathLength = new PathLength(s);
            this.pathLengthShape = s;
        }
        return this.pathLength;
    }

    public float getTotalLength() {
        PathLength pl = this.getPathLengthObj();
        return pl.lengthOfPath();
    }

    public Point2D getPointAtLength(float distance) {
        PathLength pl = this.getPathLengthObj();
        return pl.pointAtLength(distance);
    }

    public int getPathSegAtLength(float distance) {
        PathLength pl = this.getPathLengthObj();
        return pl.segmentAtLength(distance);
    }
}

