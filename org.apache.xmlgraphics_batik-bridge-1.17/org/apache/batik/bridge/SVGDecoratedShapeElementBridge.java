/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeShapePainter
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.bridge.SVGShapeElementBridge;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

public abstract class SVGDecoratedShapeElementBridge
extends SVGShapeElementBridge {
    protected SVGDecoratedShapeElementBridge() {
    }

    ShapePainter createFillStrokePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        return super.createShapePainter(ctx, e, shapeNode);
    }

    ShapePainter createMarkerPainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        return PaintServer.convertMarkers(e, shapeNode, ctx);
    }

    @Override
    protected ShapePainter createShapePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        ShapePainter painter;
        ShapePainter fillAndStroke = this.createFillStrokePainter(ctx, e, shapeNode);
        ShapePainter markerPainter = this.createMarkerPainter(ctx, e, shapeNode);
        Shape shape = shapeNode.getShape();
        if (markerPainter != null) {
            if (fillAndStroke != null) {
                CompositeShapePainter cp = new CompositeShapePainter(shape);
                cp.addShapePainter(fillAndStroke);
                cp.addShapePainter(markerPainter);
                painter = cp;
            } else {
                painter = markerPainter;
            }
        } else {
            painter = fillAndStroke;
        }
        return painter;
    }

    @Override
    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 34: 
            case 35: 
            case 36: {
                if (this.hasNewShapePainter) break;
                this.hasNewShapePainter = true;
                ShapeNode shapeNode = (ShapeNode)this.node;
                shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
            }
        }
    }
}

