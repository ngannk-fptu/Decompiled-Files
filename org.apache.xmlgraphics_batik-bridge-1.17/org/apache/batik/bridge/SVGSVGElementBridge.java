/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedRect
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.anim.dom.SVGOMSVGElement
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.svg.SVGSVGContext
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.CanvasGraphicsNode
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.ShapeNode
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGGElementBridge;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGSVGContext;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGRect;

public class SVGSVGElementBridge
extends SVGGElementBridge
implements SVGSVGContext {
    @Override
    public String getLocalName() {
        return "svg";
    }

    @Override
    public Bridge getInstance() {
        return new SVGSVGElementBridge();
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new CanvasGraphicsNode();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        CanvasGraphicsNode cgn = (CanvasGraphicsNode)this.instantiateGraphicsNode();
        this.associateSVGContext(ctx, e, (GraphicsNode)cgn);
        try {
            Rectangle2D r;
            SVGDocument doc = (SVGDocument)e.getOwnerDocument();
            SVGOMSVGElement se = (SVGOMSVGElement)e;
            boolean isOutermost = doc.getRootElement() == e;
            float x = 0.0f;
            float y = 0.0f;
            if (!isOutermost) {
                AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)se.getX();
                x = _x.getCheckedValue();
                AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)se.getY();
                y = _y.getCheckedValue();
            }
            AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)se.getWidth();
            float w = _width.getCheckedValue();
            AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)se.getHeight();
            float h = _height.getCheckedValue();
            cgn.setVisible(CSSUtilities.convertVisibility(e));
            SVGOMAnimatedRect vb = (SVGOMAnimatedRect)se.getViewBox();
            SVGAnimatedPreserveAspectRatio par = se.getPreserveAspectRatio();
            AffineTransform viewingTransform = ViewBox.getPreserveAspectRatioTransform(e, (SVGAnimatedRect)vb, par, w, h, ctx);
            float actualWidth = w;
            float actualHeight = h;
            try {
                AffineTransform vtInv = viewingTransform.createInverse();
                actualWidth = (float)((double)w * vtInv.getScaleX());
                actualHeight = (float)((double)h * vtInv.getScaleY());
            }
            catch (NoninvertibleTransformException vtInv) {
                // empty catch block
            }
            AffineTransform positionTransform = AffineTransform.getTranslateInstance(x, y);
            if (!isOutermost) {
                cgn.setPositionTransform(positionTransform);
            } else if (doc == ctx.getDocument()) {
                final double dw = w;
                final double dh = h;
                ctx.setDocumentSize(new Dimension2D(){
                    double w;
                    double h;
                    {
                        this.w = dw;
                        this.h = dh;
                    }

                    @Override
                    public double getWidth() {
                        return this.w;
                    }

                    @Override
                    public double getHeight() {
                        return this.h;
                    }

                    @Override
                    public void setSize(double w, double h) {
                        this.w = w;
                        this.h = h;
                    }
                });
            }
            cgn.setViewingTransform(viewingTransform);
            Shape clip = null;
            if (CSSUtilities.convertOverflow(e)) {
                float[] offsets = CSSUtilities.convertClip(e);
                clip = offsets == null ? new Rectangle2D.Float(x, y, w, h) : new Rectangle2D.Float(x + offsets[3], y + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
            }
            if (clip != null) {
                try {
                    AffineTransform at = new AffineTransform(positionTransform);
                    at.concatenate(viewingTransform);
                    at = at.createInverse();
                    clip = at.createTransformedShape(clip);
                    Filter filter = cgn.getGraphicsNodeRable(true);
                    cgn.setClip((ClipRable)new ClipRable8Bit(filter, clip));
                }
                catch (NoninvertibleTransformException at) {
                    // empty catch block
                }
            }
            RenderingHints hints = null;
            if ((hints = CSSUtilities.convertColorRendering(e, hints)) != null) {
                cgn.setRenderingHints(hints);
            }
            if ((r = CSSUtilities.convertEnableBackground(e)) != null) {
                cgn.setBackgroundEnable(r);
            }
            if (vb.isSpecified()) {
                SVGRect vbr = vb.getAnimVal();
                actualWidth = vbr.getWidth();
                actualHeight = vbr.getHeight();
            }
            ctx.openViewport(e, new SVGSVGElementViewport(actualWidth, actualHeight));
            return cgn;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
        ctx.closeViewport(e);
    }

    @Override
    public void dispose() {
        this.ctx.removeViewport(this.e);
        super.dispose();
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        block16: {
            try {
                SVGDocument doc;
                boolean rebuild = false;
                if (alav.getNamespaceURI() != null) break block16;
                String ln = alav.getLocalName();
                if (ln.equals("width") || ln.equals("height")) {
                    rebuild = true;
                } else if (ln.equals("x") || ln.equals("y")) {
                    boolean isOutermost;
                    doc = (SVGDocument)this.e.getOwnerDocument();
                    SVGOMSVGElement se = (SVGOMSVGElement)this.e;
                    boolean bl = isOutermost = doc.getRootElement() == this.e;
                    if (!isOutermost) {
                        AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)se.getX();
                        float x = _x.getCheckedValue();
                        AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)se.getY();
                        float y = _y.getCheckedValue();
                        AffineTransform positionTransform = AffineTransform.getTranslateInstance(x, y);
                        CanvasGraphicsNode cgn = (CanvasGraphicsNode)this.node;
                        cgn.setPositionTransform(positionTransform);
                        return;
                    }
                } else if (ln.equals("viewBox") || ln.equals("preserveAspectRatio")) {
                    doc = (SVGDocument)this.e.getOwnerDocument();
                    SVGOMSVGElement se = (SVGOMSVGElement)this.e;
                    boolean isOutermost = doc.getRootElement() == this.e;
                    float x = 0.0f;
                    float y = 0.0f;
                    if (!isOutermost) {
                        AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)se.getX();
                        x = _x.getCheckedValue();
                        AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)se.getY();
                        y = _y.getCheckedValue();
                    }
                    AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)se.getWidth();
                    float w = _width.getCheckedValue();
                    AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)se.getHeight();
                    float h = _height.getCheckedValue();
                    CanvasGraphicsNode cgn = (CanvasGraphicsNode)this.node;
                    SVGOMAnimatedRect vb = (SVGOMAnimatedRect)se.getViewBox();
                    SVGAnimatedPreserveAspectRatio par = se.getPreserveAspectRatio();
                    AffineTransform newVT = ViewBox.getPreserveAspectRatioTransform(this.e, (SVGAnimatedRect)vb, par, w, h, this.ctx);
                    AffineTransform oldVT = cgn.getViewingTransform();
                    if (newVT.getScaleX() != oldVT.getScaleX() || newVT.getScaleY() != oldVT.getScaleY() || newVT.getShearX() != oldVT.getShearX() || newVT.getShearY() != oldVT.getShearY()) {
                        rebuild = true;
                    } else {
                        cgn.setViewingTransform(newVT);
                        Shape clip = null;
                        if (CSSUtilities.convertOverflow(this.e)) {
                            float[] offsets = CSSUtilities.convertClip(this.e);
                            clip = offsets == null ? new Rectangle2D.Float(x, y, w, h) : new Rectangle2D.Float(x + offsets[3], y + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
                        }
                        if (clip != null) {
                            try {
                                AffineTransform at = cgn.getPositionTransform();
                                at = at == null ? new AffineTransform() : new AffineTransform(at);
                                at.concatenate(newVT);
                                at = at.createInverse();
                                clip = at.createTransformedShape(clip);
                                Filter filter = cgn.getGraphicsNodeRable(true);
                                cgn.setClip((ClipRable)new ClipRable8Bit(filter, clip));
                            }
                            catch (NoninvertibleTransformException noninvertibleTransformException) {
                                // empty catch block
                            }
                        }
                    }
                }
                if (rebuild) {
                    CompositeGraphicsNode gn = this.node.getParent();
                    gn.remove((Object)this.node);
                    SVGSVGElementBridge.disposeTree(this.e, false);
                    this.handleElementAdded(gn, this.e.getParentNode(), this.e);
                    return;
                }
            }
            catch (LiveAttributeException ex) {
                throw new BridgeException(this.ctx, ex);
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    public List getIntersectionList(SVGRect svgRect, Element end) {
        Node next;
        ArrayList<Element> ret = new ArrayList<Element>();
        Rectangle2D.Float rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return ret;
        }
        Rectangle2D svgBounds = svgGN.getSensitiveBounds();
        if (svgBounds == null) {
            return ret;
        }
        if (!rect.intersects(svgBounds)) {
            return ret;
        }
        Element base = this.e;
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        for (next = base.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
        }
        if (next == null) {
            return ret;
        }
        Element curr = (Element)next;
        Set ancestors = null;
        if (end != null && (ancestors = this.getAncestors(end, base)) == null) {
            end = null;
        }
        while (curr != null) {
            String nsURI = curr.getNamespaceURI();
            String tag = curr.getLocalName();
            boolean isGroup = "http://www.w3.org/2000/svg".equals(nsURI) && ("g".equals(tag) || "svg".equals(tag) || "a".equals(tag));
            GraphicsNode gn = this.ctx.getGraphicsNode(curr);
            if (gn == null) {
                if (ancestors != null && ancestors.contains(curr)) break;
                curr = this.getNext(curr, base, end);
                continue;
            }
            AffineTransform at = gn.getGlobalTransform();
            Rectangle2D gnBounds = gn.getSensitiveBounds();
            at.preConcatenate(ati);
            if (gnBounds != null) {
                gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
            }
            if (gnBounds == null || !rect.intersects(gnBounds)) {
                if (ancestors != null && ancestors.contains(curr)) break;
                curr = this.getNext(curr, base, end);
                continue;
            }
            if (isGroup) {
                for (next = curr.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
                }
                if (next != null) {
                    curr = (Element)next;
                    continue;
                }
            } else {
                if (curr == end) break;
                if ("http://www.w3.org/2000/svg".equals(nsURI) && "use".equals(tag) && rect.contains(gnBounds)) {
                    ret.add(curr);
                }
                if (gn instanceof ShapeNode) {
                    ShapeNode sn = (ShapeNode)gn;
                    Shape sensitive = sn.getSensitiveArea();
                    if (sensitive != null && (sensitive = at.createTransformedShape(sensitive)).intersects(rect)) {
                        ret.add(curr);
                    }
                } else if (gn instanceof TextNode) {
                    SVGOMElement svgElem = (SVGOMElement)curr;
                    SVGTextElementBridge txtBridge = (SVGTextElementBridge)svgElem.getSVGContext();
                    Set elems = txtBridge.getTextIntersectionSet(at, rect);
                    if (ancestors != null && ancestors.contains(curr)) {
                        this.filterChildren(curr, end, elems, ret);
                    } else {
                        ret.addAll(elems);
                    }
                } else {
                    ret.add(curr);
                }
            }
            curr = this.getNext(curr, base, end);
        }
        return ret;
    }

    public List getEnclosureList(SVGRect svgRect, Element end) {
        Node next;
        ArrayList<Element> ret = new ArrayList<Element>();
        Rectangle2D.Float rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return ret;
        }
        Rectangle2D svgBounds = svgGN.getSensitiveBounds();
        if (svgBounds == null) {
            return ret;
        }
        if (!rect.intersects(svgBounds)) {
            return ret;
        }
        Element base = this.e;
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        for (next = base.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
        }
        if (next == null) {
            return ret;
        }
        Element curr = (Element)next;
        Set ancestors = null;
        if (end != null && (ancestors = this.getAncestors(end, base)) == null) {
            end = null;
        }
        while (curr != null) {
            String nsURI = curr.getNamespaceURI();
            String tag = curr.getLocalName();
            boolean isGroup = "http://www.w3.org/2000/svg".equals(nsURI) && ("g".equals(tag) || "svg".equals(tag) || "a".equals(tag));
            GraphicsNode gn = this.ctx.getGraphicsNode(curr);
            if (gn == null) {
                if (ancestors != null && ancestors.contains(curr)) break;
                curr = this.getNext(curr, base, end);
                continue;
            }
            AffineTransform at = gn.getGlobalTransform();
            Rectangle2D gnBounds = gn.getSensitiveBounds();
            at.preConcatenate(ati);
            if (gnBounds != null) {
                gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
            }
            if (gnBounds == null || !rect.intersects(gnBounds)) {
                if (ancestors != null && ancestors.contains(curr)) break;
                curr = this.getNext(curr, base, end);
                continue;
            }
            if (isGroup) {
                for (next = curr.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
                }
                if (next != null) {
                    curr = (Element)next;
                    continue;
                }
            } else {
                if (curr == end) break;
                if ("http://www.w3.org/2000/svg".equals(nsURI) && "use".equals(tag)) {
                    if (rect.contains(gnBounds)) {
                        ret.add(curr);
                    }
                } else if (gn instanceof TextNode) {
                    SVGOMElement svgElem = (SVGOMElement)curr;
                    SVGTextElementBridge txtBridge = (SVGTextElementBridge)svgElem.getSVGContext();
                    Set elems = txtBridge.getTextEnclosureSet(at, rect);
                    if (ancestors != null && ancestors.contains(curr)) {
                        this.filterChildren(curr, end, elems, ret);
                    } else {
                        ret.addAll(elems);
                    }
                } else if (rect.contains(gnBounds)) {
                    ret.add(curr);
                }
            }
            curr = this.getNext(curr, base, end);
        }
        return ret;
    }

    public boolean checkIntersection(Element element, SVGRect svgRect) {
        GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return false;
        }
        Rectangle2D.Float rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        SVGContext svgctx = null;
        if (element instanceof SVGOMElement && ((svgctx = ((SVGOMElement)element).getSVGContext()) instanceof SVGTextElementBridge || svgctx instanceof SVGTextElementBridge.AbstractTextChildSVGContext)) {
            return SVGTextElementBridge.getTextIntersection(this.ctx, element, ati, rect, true);
        }
        Rectangle2D gnBounds = null;
        GraphicsNode gn = this.ctx.getGraphicsNode(element);
        if (gn != null) {
            gnBounds = gn.getSensitiveBounds();
        }
        if (gnBounds == null) {
            return false;
        }
        AffineTransform at = gn.getGlobalTransform();
        at.preConcatenate(ati);
        gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
        if (!rect.intersects(gnBounds)) {
            return false;
        }
        if (!(gn instanceof ShapeNode)) {
            return true;
        }
        ShapeNode sn = (ShapeNode)gn;
        Shape sensitive = sn.getSensitiveArea();
        if (sensitive == null) {
            return false;
        }
        return (sensitive = at.createTransformedShape(sensitive)).intersects(rect);
    }

    public boolean checkEnclosure(Element element, SVGRect svgRect) {
        GraphicsNode gn = this.ctx.getGraphicsNode(element);
        Rectangle2D gnBounds = null;
        SVGContext svgctx = null;
        if (element instanceof SVGOMElement) {
            svgctx = ((SVGOMElement)element).getSVGContext();
            if (svgctx instanceof SVGTextElementBridge || svgctx instanceof SVGTextElementBridge.AbstractTextChildSVGContext) {
                gnBounds = SVGTextElementBridge.getTextBounds(this.ctx, element, true);
                for (Element p = (Element)element.getParentNode(); p != null && gn == null; p = (Element)p.getParentNode()) {
                    gn = this.ctx.getGraphicsNode(p);
                }
            } else if (gn != null) {
                gnBounds = gn.getSensitiveBounds();
            }
        } else if (gn != null) {
            gnBounds = gn.getSensitiveBounds();
        }
        if (gnBounds == null) {
            return false;
        }
        GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return false;
        }
        Rectangle2D.Float rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        AffineTransform at = gn.getGlobalTransform();
        at.preConcatenate(ati);
        gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
        return rect.contains(gnBounds);
    }

    public boolean filterChildren(Element curr, Element end, Set elems, List ret) {
        for (Node child = curr.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Element) || !this.filterChildren((Element)child, end, elems, ret)) continue;
            return true;
        }
        if (curr == end) {
            return true;
        }
        if (elems.contains(curr)) {
            ret.add(curr);
        }
        return false;
    }

    protected Set getAncestors(Element end, Element base) {
        HashSet<Element> ret = new HashSet<Element>();
        Element p = end;
        do {
            ret.add(p);
        } while ((p = (Element)p.getParentNode()) != null && p != base);
        if (p == null) {
            return null;
        }
        return ret;
    }

    protected Element getNext(Element curr, Element base, Element end) {
        Node next;
        for (next = curr.getNextSibling(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
        }
        while (next == null) {
            if ((curr = (Element)curr.getParentNode()) == end || curr == base) {
                next = null;
                break;
            }
            for (next = curr.getNextSibling(); next != null && !(next instanceof Element); next = next.getNextSibling()) {
            }
        }
        return (Element)next;
    }

    public void deselectAll() {
        this.ctx.getUserAgent().deselectAll();
    }

    public int suspendRedraw(int max_wait_milliseconds) {
        UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            return um.addRedrawSuspension(max_wait_milliseconds);
        }
        return -1;
    }

    public boolean unsuspendRedraw(int suspend_handle_id) {
        UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            return um.releaseRedrawSuspension(suspend_handle_id);
        }
        return false;
    }

    public void unsuspendRedrawAll() {
        UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.releaseAllRedrawSuspension();
        }
    }

    public void forceRedraw() {
        UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.forceRepaint();
        }
    }

    public void pauseAnimations() {
        this.ctx.getAnimationEngine().pause();
    }

    public void unpauseAnimations() {
        this.ctx.getAnimationEngine().unpause();
    }

    public boolean animationsPaused() {
        return this.ctx.getAnimationEngine().isPaused();
    }

    public float getCurrentTime() {
        return this.ctx.getAnimationEngine().getCurrentTime();
    }

    public void setCurrentTime(float t) {
        this.ctx.getAnimationEngine().setCurrentTime(t);
    }

    public static class SVGSVGElementViewport
    implements Viewport {
        private float width;
        private float height;

        public SVGSVGElementViewport(float w, float h) {
            this.width = w;
            this.height = h;
        }

        @Override
        public float getWidth() {
            return this.width;
        }

        @Override
        public float getHeight() {
            return this.height;
        }
    }
}

