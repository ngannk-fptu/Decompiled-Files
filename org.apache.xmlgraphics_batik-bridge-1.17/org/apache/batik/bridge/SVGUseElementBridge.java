/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedLength
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.anim.dom.SVGOMUseElement
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGOMUseShadowRoot
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.w3c.dom.svg.SVGTransformable
 *  org.w3c.dom.svg.SVGUseElement
 */
package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGOMUseShadowRoot;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.svg.SVGUseElement;

public class SVGUseElementBridge
extends AbstractGraphicsNodeBridge {
    protected ReferencedElementMutationListener l;
    protected BridgeContext subCtx;

    @Override
    public String getLocalName() {
        return "use";
    }

    @Override
    public Bridge getInstance() {
        return new SVGUseElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        this.associateSVGContext(ctx, e, (GraphicsNode)gn);
        return gn;
    }

    public CompositeGraphicsNode buildCompositeGraphicsNode(BridgeContext ctx, Element e, CompositeGraphicsNode gn) {
        NodeEventTarget target;
        Rectangle2D r;
        Element localRefElement;
        SVGOMUseElement ue = (SVGOMUseElement)e;
        String uri = ue.getHref().getAnimVal();
        if (uri.length() == 0) {
            throw new BridgeException(ctx, e, "attribute.missing", new Object[]{"xlink:href"});
        }
        Element refElement = ctx.getReferencedElement(e, uri);
        SVGOMDocument document = (SVGOMDocument)e.getOwnerDocument();
        SVGOMDocument refDocument = (SVGOMDocument)refElement.getOwnerDocument();
        boolean isLocal = refDocument == document;
        BridgeContext theCtx = ctx;
        this.subCtx = null;
        if (!isLocal) {
            theCtx = this.subCtx = (BridgeContext)refDocument.getCSSEngine().getCSSContext();
        }
        if ("symbol".equals((localRefElement = (Element)document.importNode((Node)refElement, true, true)).getLocalName())) {
            Element svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
            NamedNodeMap attrs = localRefElement.getAttributes();
            int len = attrs.getLength();
            for (int i = 0; i < len; ++i) {
                Attr attr = (Attr)attrs.item(i);
                svgElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
            }
            Node n = localRefElement.getFirstChild();
            while (n != null) {
                svgElement.appendChild(n);
                n = localRefElement.getFirstChild();
            }
            localRefElement = svgElement;
        }
        if ("svg".equals(localRefElement.getLocalName())) {
            try {
                SVGOMAnimatedLength al = (SVGOMAnimatedLength)ue.getWidth();
                if (al.isSpecified()) {
                    localRefElement.setAttributeNS(null, "width", al.getAnimVal().getValueAsString());
                }
                if ((al = (SVGOMAnimatedLength)ue.getHeight()).isSpecified()) {
                    localRefElement.setAttributeNS(null, "height", al.getAnimVal().getValueAsString());
                }
            }
            catch (LiveAttributeException ex) {
                throw new BridgeException(ctx, ex);
            }
        }
        SVGOMUseShadowRoot root = new SVGOMUseShadowRoot((AbstractDocument)document, e, isLocal);
        root.appendChild((Node)localRefElement);
        if (gn == null) {
            gn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, this.node);
        } else {
            int s = gn.size();
            for (int i = 0; i < s; ++i) {
                gn.remove(0);
            }
        }
        Node oldRoot = ue.getCSSFirstChild();
        if (oldRoot != null) {
            SVGUseElementBridge.disposeTree(oldRoot);
        }
        ue.setUseShadowTree(root);
        Element g = localRefElement;
        CSSUtilities.computeStyleAndURIs(refElement, localRefElement, uri);
        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode refNode = builder.build(ctx, g);
        gn.getChildren().add(refNode);
        gn.setTransform(this.computeTransform((SVGTransformable)e, ctx));
        gn.setVisible(CSSUtilities.convertVisibility(e));
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }
        if ((r = CSSUtilities.convertEnableBackground(e)) != null) {
            gn.setBackgroundEnable(r);
        }
        if (this.l != null) {
            target = this.l.target;
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", (EventListener)this.l, true);
            this.l = null;
        }
        if (isLocal && ctx.isDynamic()) {
            this.l = new ReferencedElementMutationListener();
            this.l.target = target = (NodeEventTarget)refElement;
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.l, true, null);
            theCtx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "DOMAttrModified", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)this.l, true, null);
            theCtx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.l, true, null);
            theCtx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", (EventListener)this.l, true, null);
            theCtx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.l, true);
        }
        return gn;
    }

    @Override
    public void dispose() {
        SVGOMUseElement ue;
        if (this.l != null) {
            NodeEventTarget target = this.l.target;
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", (EventListener)this.l, true);
            this.l = null;
        }
        if ((ue = (SVGOMUseElement)this.e) != null && ue.getCSSFirstChild() != null) {
            SVGUseElementBridge.disposeTree(ue.getCSSFirstChild());
        }
        super.dispose();
        this.subCtx = null;
    }

    @Override
    protected AffineTransform computeTransform(SVGTransformable e, BridgeContext ctx) {
        AffineTransform at = super.computeTransform(e, ctx);
        SVGUseElement ue = (SVGUseElement)e;
        try {
            AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)ue.getX();
            float x = _x.getCheckedValue();
            AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)ue.getY();
            float y = _y.getCheckedValue();
            AffineTransform xy = AffineTransform.getTranslateInstance(x, y);
            xy.preConcatenate(at);
            return xy;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        super.buildGraphicsNode(ctx, e, node);
        if (ctx.isInteractive()) {
            NodeEventTarget target = (NodeEventTarget)e;
            CursorMouseOverListener l = new CursorMouseOverListener(ctx);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", (EventListener)l, false, null);
            ctx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "mouseover", l, false);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        try {
            String ns = alav.getNamespaceURI();
            String ln = alav.getLocalName();
            if (ns == null) {
                if (ln.equals("x") || ln.equals("y") || ln.equals("transform")) {
                    this.node.setTransform(this.computeTransform((SVGTransformable)this.e, this.ctx));
                    this.handleGeometryChanged();
                } else if (ln.equals("width") || ln.equals("height")) {
                    this.buildCompositeGraphicsNode(this.ctx, this.e, (CompositeGraphicsNode)this.node);
                }
            } else if (ns.equals("http://www.w3.org/1999/xlink") && ln.equals("href")) {
                this.buildCompositeGraphicsNode(this.ctx, this.e, (CompositeGraphicsNode)this.node);
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(this.ctx, ex);
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    protected class ReferencedElementMutationListener
    implements EventListener {
        protected NodeEventTarget target;

        protected ReferencedElementMutationListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            SVGUseElementBridge.this.buildCompositeGraphicsNode(SVGUseElementBridge.this.ctx, SVGUseElementBridge.this.e, (CompositeGraphicsNode)SVGUseElementBridge.this.node);
        }
    }

    public static class CursorMouseOverListener
    implements EventListener {
        protected BridgeContext ctx;

        public CursorMouseOverListener(BridgeContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void handleEvent(Event evt) {
            Cursor cursor;
            Element currentTarget = (Element)((Object)evt.getCurrentTarget());
            if (!CSSUtilities.isAutoCursor(currentTarget) && (cursor = CSSUtilities.convertCursor(currentTarget, this.ctx)) != null) {
                this.ctx.getUserAgent().setSVGCursor(cursor);
            }
        }
    }
}

