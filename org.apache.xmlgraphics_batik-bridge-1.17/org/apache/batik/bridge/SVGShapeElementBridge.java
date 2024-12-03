/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 */
package org.apache.batik.bridge;

import java.awt.RenderingHints;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

public abstract class SVGShapeElementBridge
extends AbstractGraphicsNodeBridge {
    protected boolean hasNewShapePainter;

    protected SVGShapeElementBridge() {
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        ShapeNode shapeNode = (ShapeNode)super.createGraphicsNode(ctx, e);
        if (shapeNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, (GraphicsNode)shapeNode);
        this.buildShape(ctx, e, shapeNode);
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        hints = CSSUtilities.convertShapeRendering(e, hints);
        if (hints != null) {
            shapeNode.setRenderingHints(hints);
        }
        return shapeNode;
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new ShapeNode();
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        ShapeNode shapeNode = (ShapeNode)node;
        shapeNode.setShapePainter(this.createShapePainter(ctx, e, shapeNode));
        super.buildGraphicsNode(ctx, e, node);
    }

    protected ShapePainter createShapePainter(BridgeContext ctx, Element e, ShapeNode shapeNode) {
        return PaintServer.convertFillAndStroke(e, shapeNode, ctx);
    }

    protected abstract void buildShape(BridgeContext var1, Element var2, ShapeNode var3);

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    protected void handleGeometryChanged() {
        super.handleGeometryChanged();
        ShapeNode shapeNode = (ShapeNode)this.node;
        shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
    }

    @Override
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
        this.hasNewShapePainter = false;
        super.handleCSSEngineEvent(evt);
    }

    @Override
    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 15: 
            case 16: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: {
                if (this.hasNewShapePainter) break;
                this.hasNewShapePainter = true;
                ShapeNode shapeNode = (ShapeNode)this.node;
                shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
                break;
            }
            case 42: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertShapeRendering(this.e, hints);
                if (hints == null) break;
                this.node.setRenderingHints(hints);
                break;
            }
            case 9: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertColorRendering(this.e, hints);
                if (hints == null) break;
                this.node.setRenderingHints(hints);
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
            }
        }
    }
}

