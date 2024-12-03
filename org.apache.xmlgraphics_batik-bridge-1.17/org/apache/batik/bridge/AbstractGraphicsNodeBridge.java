/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedTransformList
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.css.engine.SVGCSSEngine
 *  org.apache.batik.dom.events.AbstractEvent
 *  org.apache.batik.dom.svg.AbstractSVGTransformList
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.svg.SVGMotionAnimatableElement
 *  org.apache.batik.ext.awt.geom.SegmentList
 *  org.apache.batik.gvt.CanvasGraphicsNode
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.w3c.dom.svg.SVGFitToViewBox
 *  org.w3c.dom.svg.SVGTransformable
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.AnimatableSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.SVGSwitchElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.svg.AbstractSVGTransformList;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGMotionAnimatableElement;
import org.apache.batik.ext.awt.geom.SegmentList;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGTransformable;

public abstract class AbstractGraphicsNodeBridge
extends AnimatableSVGBridge
implements SVGContext,
BridgeUpdateHandler,
GraphicsNodeBridge,
ErrorConstants {
    protected GraphicsNode node;
    protected boolean isSVG12;
    protected UnitProcessor.Context unitContext;
    protected SoftReference bboxShape = null;
    protected Rectangle2D bbox = null;

    protected AbstractGraphicsNodeBridge() {
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        GraphicsNode node = this.instantiateGraphicsNode();
        this.setTransform(node, e, ctx);
        node.setVisible(CSSUtilities.convertVisibility(e));
        this.associateSVGContext(ctx, e, node);
        return node;
    }

    protected abstract GraphicsNode instantiateGraphicsNode();

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
    }

    @Override
    public boolean getDisplay(Element e) {
        return CSSUtilities.convertDisplay(e);
    }

    protected AffineTransform computeTransform(SVGTransformable te, BridgeContext ctx) {
        try {
            SVGMotionAnimatableElement mae;
            AffineTransform mat;
            AffineTransform at = new AffineTransform();
            SVGOMAnimatedTransformList atl = (SVGOMAnimatedTransformList)te.getTransform();
            if (atl.isSpecified()) {
                atl.check();
                AbstractSVGTransformList tl = (AbstractSVGTransformList)te.getTransform().getAnimVal();
                at.concatenate(tl.getAffineTransform());
            }
            if (this.e instanceof SVGMotionAnimatableElement && (mat = (mae = (SVGMotionAnimatableElement)this.e).getMotionTransform()) != null) {
                at.concatenate(mat);
            }
            return at;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    protected void setTransform(GraphicsNode n, Element e, BridgeContext ctx) {
        n.setTransform(this.computeTransform((SVGTransformable)e, ctx));
    }

    protected void associateSVGContext(BridgeContext ctx, Element e, GraphicsNode node) {
        this.e = e;
        this.node = node;
        this.ctx = ctx;
        this.unitContext = UnitProcessor.createContext(ctx, e);
        this.isSVG12 = ctx.isSVG12();
        ((SVGOMElement)e).setSVGContext((SVGContext)this);
    }

    protected void initializeDynamicSupport(BridgeContext ctx, Element e, GraphicsNode node) {
        if (ctx.isInteractive()) {
            ctx.bind(e, node);
        }
    }

    @Override
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
    }

    protected void handleGeometryChanged() {
        this.node.setFilter(CSSUtilities.convertFilter(this.e, this.node, this.ctx));
        this.node.setMask(CSSUtilities.convertMask(this.e, this.node, this.ctx));
        this.node.setClip(CSSUtilities.convertClipPath(this.e, this.node, this.ctx));
        if (this.isSVG12) {
            if (!"use".equals(this.e.getLocalName())) {
                this.fireShapeChangeEvent();
            }
            this.fireBBoxChangeEvent();
        }
    }

    protected void fireShapeChangeEvent() {
        DocumentEvent d = (DocumentEvent)((Object)this.e.getOwnerDocument());
        AbstractEvent evt = (AbstractEvent)d.createEvent("SVGEvents");
        evt.initEventNS("http://www.w3.org/2000/svg", "shapechange", true, false);
        try {
            ((EventTarget)((Object)this.e)).dispatchEvent((Event)evt);
        }
        catch (RuntimeException ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        Element e2;
        Bridge b;
        if (evt.getTarget() instanceof Element && (b = this.ctx.getBridge(e2 = (Element)((Object)evt.getTarget()))) instanceof GenericBridge) {
            ((GenericBridge)b).handleElement(this.ctx, e2);
        }
    }

    @Override
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        SVGContext bridge;
        Node parent = this.e.getParentNode();
        if (parent instanceof SVGOMElement && (bridge = ((SVGOMElement)parent).getSVGContext()) instanceof SVGSwitchElementBridge) {
            ((SVGSwitchElementBridge)bridge).handleChildElementRemoved(this.e);
            return;
        }
        CompositeGraphicsNode gn = this.node.getParent();
        gn.remove((Object)this.node);
        AbstractGraphicsNodeBridge.disposeTree(this.e);
    }

    @Override
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    @Override
    public void dispose() {
        SVGOMElement elt = (SVGOMElement)this.e;
        elt.setSVGContext(null);
        this.ctx.unbind(this.e);
        this.bboxShape = null;
    }

    protected static void disposeTree(Node node) {
        AbstractGraphicsNodeBridge.disposeTree(node, true);
    }

    protected static void disposeTree(Node node, boolean removeContext) {
        SVGOMElement elt;
        SVGContext ctx;
        if (node instanceof SVGOMElement && (ctx = (elt = (SVGOMElement)node).getSVGContext()) instanceof BridgeUpdateHandler) {
            BridgeUpdateHandler h = (BridgeUpdateHandler)ctx;
            if (removeContext) {
                elt.setSVGContext(null);
            }
            h.dispose();
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            AbstractGraphicsNodeBridge.disposeTree(n, removeContext);
        }
    }

    @Override
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
        try {
            int[] properties;
            SVGCSSEngine eng = (SVGCSSEngine)evt.getSource();
            for (int idx : properties = evt.getProperties()) {
                this.handleCSSPropertyChanged(idx);
                String pn = eng.getPropertyName(idx);
                this.fireBaseAttributeListeners(pn);
            }
        }
        catch (Exception ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }

    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 57: {
                this.node.setVisible(CSSUtilities.convertVisibility(this.e));
                break;
            }
            case 38: {
                this.node.setComposite(CSSUtilities.convertOpacity(this.e));
                break;
            }
            case 18: {
                this.node.setFilter(CSSUtilities.convertFilter(this.e, this.node, this.ctx));
                break;
            }
            case 37: {
                this.node.setMask(CSSUtilities.convertMask(this.e, this.node, this.ctx));
                break;
            }
            case 3: {
                this.node.setClip(CSSUtilities.convertClipPath(this.e, this.node, this.ctx));
                break;
            }
            case 40: {
                this.node.setPointerEventType(CSSUtilities.convertPointerEvents(this.e));
                break;
            }
            case 12: {
                if (this.getDisplay(this.e)) break;
                CompositeGraphicsNode parent = this.node.getParent();
                parent.remove((Object)this.node);
                AbstractGraphicsNodeBridge.disposeTree(this.e, false);
            }
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null && alav.getLocalName().equals("transform")) {
            this.setTransform(this.node, this.e, this.ctx);
            this.handleGeometryChanged();
        }
    }

    @Override
    public void handleOtherAnimationChanged(String type) {
        if (type.equals("motion")) {
            this.setTransform(this.node, this.e, this.ctx);
            this.handleGeometryChanged();
        }
    }

    protected void checkBBoxChange() {
        if (this.e != null) {
            this.fireBBoxChangeEvent();
        }
    }

    protected void fireBBoxChangeEvent() {
        DocumentEvent d = (DocumentEvent)((Object)this.e.getOwnerDocument());
        AbstractEvent evt = (AbstractEvent)d.createEvent("SVGEvents");
        evt.initEventNS("http://www.w3.org/2000/svg", "RenderedBBoxChange", true, false);
        try {
            ((EventTarget)((Object)this.e)).dispatchEvent((Event)evt);
        }
        catch (RuntimeException ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }

    public float getPixelUnitToMillimeter() {
        return this.ctx.getUserAgent().getPixelUnitToMillimeter();
    }

    public float getPixelToMM() {
        return this.getPixelUnitToMillimeter();
    }

    public Rectangle2D getBBox() {
        if (this.node == null) {
            return null;
        }
        Shape s = this.node.getOutline();
        if (this.bboxShape != null && s == this.bboxShape.get()) {
            return this.bbox;
        }
        this.bboxShape = new SoftReference<Shape>(s);
        this.bbox = null;
        if (s == null) {
            return this.bbox;
        }
        SegmentList sl = new SegmentList(s);
        this.bbox = sl.getBounds2D();
        return this.bbox;
    }

    public AffineTransform getCTM() {
        GraphicsNode gn = this.node;
        AffineTransform ctm = new AffineTransform();
        Element elt = this.e;
        while (elt != null) {
            AffineTransform at;
            if (elt instanceof SVGFitToViewBox) {
                at = gn instanceof CanvasGraphicsNode ? ((CanvasGraphicsNode)gn).getViewingTransform() : gn.getTransform();
                if (at == null) break;
                ctm.preConcatenate(at);
                break;
            }
            at = gn.getTransform();
            if (at != null) {
                ctm.preConcatenate(at);
            }
            elt = SVGCSSEngine.getParentCSSStylableElement((Element)elt);
            gn = gn.getParent();
        }
        return ctm;
    }

    public AffineTransform getScreenTransform() {
        return this.ctx.getUserAgent().getTransform();
    }

    public void setScreenTransform(AffineTransform at) {
        this.ctx.getUserAgent().setTransform(at);
    }

    public AffineTransform getGlobalTransform() {
        return this.node.getGlobalTransform();
    }

    public float getViewportWidth() {
        return this.ctx.getBlockWidth(this.e);
    }

    public float getViewportHeight() {
        return this.ctx.getBlockHeight(this.e);
    }

    public float getFontSize() {
        return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
    }
}

