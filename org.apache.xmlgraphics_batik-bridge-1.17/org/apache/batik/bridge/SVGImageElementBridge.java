/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.DOMMouseEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.spi.BrokenLinkProvider
 *  org.apache.batik.ext.awt.image.spi.ImageTagRegistry
 *  org.apache.batik.gvt.CanvasGraphicsNode
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.ImageNode
 *  org.apache.batik.gvt.RasterImageNode
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.util.HaltingThread
 *  org.apache.batik.util.MimeTypeConstants
 *  org.apache.batik.util.ParsedURL
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 *  org.apache.xmlgraphics.java2d.color.RenderingIntent
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGImageElement
 *  org.w3c.dom.svg.SVGSVGElement
 */
package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.bridge.Messages;
import org.apache.batik.bridge.SVGBrokenLinkProvider;
import org.apache.batik.bridge.SVGColorProfileElementBridge;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.util.HaltingThread;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.util.ParsedURL;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGImageElementBridge
extends AbstractGraphicsNodeBridge {
    protected SVGDocument imgDocument;
    protected EventListener listener = null;
    protected BridgeContext subCtx = null;
    protected boolean hitCheckChildren = false;
    static SVGBrokenLinkProvider brokenLinkProvider = new SVGBrokenLinkProvider();

    @Override
    public String getLocalName() {
        return "image";
    }

    @Override
    public Bridge getInstance() {
        return new SVGImageElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        ImageNode imageNode = (ImageNode)super.createGraphicsNode(ctx, e);
        if (imageNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, (GraphicsNode)imageNode);
        this.hitCheckChildren = false;
        GraphicsNode node = this.buildImageGraphicsNode(ctx, e);
        if (node == null) {
            SVGImageElement ie = (SVGImageElement)e;
            String uriStr = ie.getHref().getAnimVal();
            throw new BridgeException(ctx, e, "uri.image.invalid", new Object[]{uriStr});
        }
        imageNode.setImage(node);
        imageNode.setHitCheckChildren(this.hitCheckChildren);
        RenderingHints hints = null;
        hints = CSSUtilities.convertImageRendering(e, hints);
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            imageNode.setRenderingHints(hints);
        }
        return imageNode;
    }

    protected GraphicsNode buildImageGraphicsNode(BridgeContext ctx, Element e) {
        SVGImageElement ie = (SVGImageElement)e;
        String uriStr = ie.getHref().getAnimVal();
        if (uriStr.length() == 0) {
            throw new BridgeException(ctx, e, "attribute.missing", new Object[]{"xlink:href"});
        }
        if (uriStr.indexOf(35) != -1) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"xlink:href", uriStr});
        }
        String baseURI = AbstractNode.getBaseURI((Node)e);
        ParsedURL purl = baseURI == null ? new ParsedURL(uriStr) : new ParsedURL(baseURI, uriStr);
        this.checkLoadExternalResource(ctx, e, purl);
        return this.createImageGraphicsNode(ctx, e, purl);
    }

    private void checkLoadExternalResource(BridgeContext ctx, Element e, ParsedURL purl) {
        SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
        String docURL = svgDoc.getURL();
        ParsedURL pDocURL = null;
        if (docURL != null) {
            pDocURL = new ParsedURL(docURL);
        }
        UserAgent userAgent = ctx.getUserAgent();
        try {
            userAgent.checkLoadExternalResource(purl, pDocURL);
        }
        catch (SecurityException secEx) {
            throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[]{purl});
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected GraphicsNode createImageGraphicsNode(BridgeContext ctx, Element e, ParsedURL purl) {
        Rectangle2D bounds = SVGImageElementBridge.getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            ShapeNode sn = new ShapeNode();
            sn.setShape((Shape)bounds);
            return sn;
        }
        DocumentLoader loader = ctx.getDocumentLoader();
        ImageTagRegistry reg = ImageTagRegistry.getRegistry();
        ICCColorSpaceWithIntent colorspace = SVGImageElementBridge.extractColorSpace(e, ctx);
        try {
            Document doc = loader.checkCache(purl.toString());
            if (doc != null) {
                this.imgDocument = (SVGDocument)doc;
                return this.createSVGImageNode(ctx, e, this.imgDocument);
            }
        }
        catch (BridgeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            // empty catch block
        }
        Filter img = reg.checkCache(purl, colorspace);
        if (img != null) {
            return this.createRasterImageNode(ctx, e, img, purl);
        }
        ProtectedStream reference = null;
        try {
            reference = this.openStream(e, purl);
        }
        catch (SecurityException secEx) {
            throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[]{purl});
        }
        catch (IOException ioe) {
            return this.createBrokenImageNode(ctx, e, purl.toString(), ioe.getLocalizedMessage());
        }
        Filter img2 = reg.readURL((InputStream)reference, purl, colorspace, false, false);
        if (img2 != null) {
            try {
                reference.tie();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return this.createRasterImageNode(ctx, e, img2, purl);
        }
        try {
            reference.retry();
        }
        catch (IOException ioe) {
            reference.release();
            reference = null;
            try {
                reference = this.openStream(e, purl);
            }
            catch (IOException ioe2) {
                return this.createBrokenImageNode(ctx, e, purl.toString(), ioe2.getLocalizedMessage());
            }
        }
        try {
            Document doc = loader.loadDocument(purl.toString(), reference);
            reference.release();
            this.imgDocument = (SVGDocument)doc;
            return this.createSVGImageNode(ctx, e, this.imgDocument);
        }
        catch (BridgeException ex) {
            reference.release();
            throw ex;
        }
        catch (SecurityException secEx) {
            reference.release();
            throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[]{purl});
        }
        catch (InterruptedIOException iioe) {
            reference.release();
            if (HaltingThread.hasBeenHalted()) {
                throw new InterruptedBridgeException();
            }
        }
        catch (InterruptedBridgeException ibe) {
            reference.release();
            throw ibe;
        }
        catch (Exception ibe) {
            // empty catch block
        }
        try {
            reference.retry();
        }
        catch (IOException ioe) {
            reference.release();
            reference = null;
            try {
                reference = this.openStream(e, purl);
            }
            catch (IOException ioe2) {
                return this.createBrokenImageNode(ctx, e, purl.toString(), ioe2.getLocalizedMessage());
            }
        }
        try {
            img = reg.readURL((InputStream)reference, purl, colorspace, true, true);
            if (img != null) {
                GraphicsNode graphicsNode = this.createRasterImageNode(ctx, e, img, purl);
                return graphicsNode;
            }
        }
        finally {
            reference.release();
        }
        return null;
    }

    protected ProtectedStream openStream(Element e, ParsedURL purl) throws IOException {
        ArrayList mimeTypes = new ArrayList(ImageTagRegistry.getRegistry().getRegisteredMimeTypes());
        mimeTypes.addAll(MimeTypeConstants.MIME_TYPES_SVG_LIST);
        InputStream reference = purl.openStream(mimeTypes.iterator());
        return new ProtectedStream(reference);
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new ImageNode();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    protected void initializeDynamicSupport(BridgeContext ctx, Element e, GraphicsNode node) {
        if (!ctx.isInteractive()) {
            return;
        }
        ctx.bind(e, node);
        if (ctx.isDynamic()) {
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext((SVGContext)this);
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        try {
            String ns = alav.getNamespaceURI();
            String ln = alav.getLocalName();
            if (ns == null) {
                if (ln.equals("x") || ln.equals("y")) {
                    this.updateImageBounds();
                    return;
                }
                if (ln.equals("width") || ln.equals("height")) {
                    SVGImageElement ie = (SVGImageElement)this.e;
                    ImageNode imageNode = (ImageNode)this.node;
                    AbstractSVGAnimatedLength _attr = ln.charAt(0) == 'w' ? (AbstractSVGAnimatedLength)ie.getWidth() : (AbstractSVGAnimatedLength)ie.getHeight();
                    float val = _attr.getCheckedValue();
                    if (val == 0.0f || imageNode.getImage() instanceof ShapeNode) {
                        this.rebuildImageNode();
                    } else {
                        this.updateImageBounds();
                    }
                    return;
                }
                if (ln.equals("preserveAspectRatio")) {
                    this.updateImageBounds();
                    return;
                }
            } else if (ns.equals("http://www.w3.org/1999/xlink") && ln.equals("href")) {
                this.rebuildImageNode();
                return;
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(this.ctx, ex);
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    protected void updateImageBounds() {
        Rectangle2D bounds = SVGImageElementBridge.getImageBounds(this.ctx, this.e);
        GraphicsNode imageNode = ((ImageNode)this.node).getImage();
        float[] vb = null;
        if (imageNode instanceof RasterImageNode) {
            Rectangle2D imgBounds = ((RasterImageNode)imageNode).getImageBounds();
            vb = new float[]{0.0f, 0.0f, (float)imgBounds.getWidth(), (float)imgBounds.getHeight()};
        } else if (this.imgDocument != null) {
            SVGSVGElement svgElement = this.imgDocument.getRootElement();
            String viewBox = svgElement.getAttributeNS(null, "viewBox");
            vb = ViewBox.parseViewBoxAttribute(this.e, viewBox, this.ctx);
        }
        if (imageNode != null) {
            SVGImageElementBridge.initializeViewport(this.ctx, this.e, imageNode, vb, bounds);
        }
    }

    protected void rebuildImageNode() {
        if (this.imgDocument != null && this.listener != null) {
            NodeEventTarget tgt = (NodeEventTarget)this.imgDocument.getRootElement();
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
            this.listener = null;
        }
        if (this.imgDocument != null) {
            SVGSVGElement svgElement = this.imgDocument.getRootElement();
            SVGImageElementBridge.disposeTree((Node)svgElement);
        }
        this.imgDocument = null;
        this.subCtx = null;
        GraphicsNode inode = this.buildImageGraphicsNode(this.ctx, this.e);
        ImageNode imgNode = (ImageNode)this.node;
        imgNode.setImage(inode);
        if (inode == null) {
            SVGImageElement ie = (SVGImageElement)this.e;
            String uriStr = ie.getHref().getAnimVal();
            throw new BridgeException(this.ctx, this.e, "uri.image.invalid", new Object[]{uriStr});
        }
    }

    @Override
    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 6: 
            case 30: {
                RenderingHints hints = CSSUtilities.convertImageRendering(this.e, null);
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

    protected GraphicsNode createRasterImageNode(BridgeContext ctx, Element e, Filter img, ParsedURL purl) {
        Rectangle2D bounds = SVGImageElementBridge.getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            ShapeNode sn = new ShapeNode();
            sn.setShape((Shape)bounds);
            return sn;
        }
        if (BrokenLinkProvider.hasBrokenLinkProperty((Filter)img)) {
            Object o = img.getProperty("org.apache.batik.BrokenLinkImage");
            String msg = "unknown";
            if (o instanceof String) {
                msg = (String)o;
            }
            SVGDocument doc = ctx.getUserAgent().getBrokenLinkDocument(e, purl.toString(), msg);
            return this.createSVGImageNode(ctx, e, doc);
        }
        RasterImageNode node = new RasterImageNode();
        node.setImage(img);
        Rectangle2D imgBounds = img.getBounds2D();
        float[] vb = new float[]{0.0f, 0.0f, (float)imgBounds.getWidth(), (float)imgBounds.getHeight()};
        SVGImageElementBridge.initializeViewport(ctx, e, (GraphicsNode)node, vb, bounds);
        return node;
    }

    protected GraphicsNode createSVGImageNode(BridgeContext ctx, Element e, SVGDocument imgDocument) {
        CSSEngine eng = ((SVGOMDocument)imgDocument).getCSSEngine();
        this.subCtx = ctx.createSubBridgeContext((SVGOMDocument)imgDocument);
        CompositeGraphicsNode result = new CompositeGraphicsNode();
        Rectangle2D bounds = SVGImageElementBridge.getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            ShapeNode sn = new ShapeNode();
            sn.setShape((Shape)bounds);
            result.getChildren().add(sn);
            return result;
        }
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            result.setBackgroundEnable(r);
        }
        SVGSVGElement svgElement = imgDocument.getRootElement();
        CanvasGraphicsNode node = (CanvasGraphicsNode)this.subCtx.getGVTBuilder().build(this.subCtx, (Element)svgElement);
        if (eng == null && ctx.isInteractive()) {
            this.subCtx.addUIEventListeners((Document)imgDocument);
        }
        node.setClip(null);
        node.setViewingTransform(new AffineTransform());
        result.getChildren().add(node);
        String viewBox = svgElement.getAttributeNS(null, "viewBox");
        float[] vb = ViewBox.parseViewBoxAttribute(e, viewBox, ctx);
        SVGImageElementBridge.initializeViewport(ctx, e, (GraphicsNode)result, vb, bounds);
        if (ctx.isInteractive()) {
            this.listener = new ForwardEventListener((Element)svgElement, e);
            NodeEventTarget tgt = (NodeEventTarget)svgElement;
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false, null);
            this.subCtx.storeEventListenerNS((EventTarget)tgt, "http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
        }
        return result;
    }

    @Override
    public void dispose() {
        if (this.imgDocument != null && this.listener != null) {
            NodeEventTarget tgt = (NodeEventTarget)this.imgDocument.getRootElement();
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
            this.listener = null;
        }
        if (this.imgDocument != null) {
            SVGSVGElement svgElement = this.imgDocument.getRootElement();
            SVGImageElementBridge.disposeTree((Node)svgElement);
            this.imgDocument = null;
            this.subCtx = null;
        }
        super.dispose();
    }

    protected static void initializeViewport(BridgeContext ctx, Element e, GraphicsNode node, float[] vb, Rectangle2D bounds) {
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        try {
            SVGImageElement ie = (SVGImageElement)e;
            SVGOMAnimatedPreserveAspectRatio _par = (SVGOMAnimatedPreserveAspectRatio)ie.getPreserveAspectRatio();
            _par.check();
            AffineTransform at = ViewBox.getPreserveAspectRatioTransform(e, vb, w, h, (SVGAnimatedPreserveAspectRatio)_par, ctx);
            at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
            node.setTransform(at);
            Shape clip = null;
            if (CSSUtilities.convertOverflow(e)) {
                float[] offsets = CSSUtilities.convertClip(e);
                clip = offsets == null ? new Rectangle2D.Float(x, y, w, h) : new Rectangle2D.Float(x + offsets[3], y + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
            }
            if (clip != null) {
                try {
                    at = at.createInverse();
                    Filter filter = node.getGraphicsNodeRable(true);
                    clip = at.createTransformedShape(clip);
                    node.setClip((ClipRable)new ClipRable8Bit(filter, clip));
                }
                catch (NoninvertibleTransformException noninvertibleTransformException) {}
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    protected static ICCColorSpaceWithIntent extractColorSpace(Element element, BridgeContext ctx) {
        SVGColorProfileElementBridge profileBridge;
        String colorProfileProperty = CSSUtilities.getComputedStyle(element, 8).getStringValue();
        ICCColorSpaceWithIntent colorSpace = null;
        if ("srgb".equalsIgnoreCase(colorProfileProperty)) {
            colorSpace = new ICCColorSpaceWithIntent(ICC_Profile.getInstance(1000), RenderingIntent.AUTO, "sRGB", null);
        } else if (!"auto".equalsIgnoreCase(colorProfileProperty) && !"".equalsIgnoreCase(colorProfileProperty) && (profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile")) != null) {
            colorSpace = profileBridge.createICCColorSpaceWithIntent(ctx, element, colorProfileProperty);
        }
        return colorSpace;
    }

    protected static Rectangle2D getImageBounds(BridgeContext ctx, Element element) {
        try {
            SVGImageElement ie = (SVGImageElement)element;
            AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)ie.getX();
            float x = _x.getCheckedValue();
            AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)ie.getY();
            float y = _y.getCheckedValue();
            AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)ie.getWidth();
            float w = _width.getCheckedValue();
            AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)ie.getHeight();
            float h = _height.getCheckedValue();
            return new Rectangle2D.Float(x, y, w, h);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    GraphicsNode createBrokenImageNode(BridgeContext ctx, Element e, String uri, String message) {
        SVGDocument doc = ctx.getUserAgent().getBrokenLinkDocument(e, uri, Messages.formatMessage("uri.image.error", new Object[]{message}));
        return this.createSVGImageNode(ctx, e, doc);
    }

    static {
        ImageTagRegistry.setBrokenLinkProvider((BrokenLinkProvider)brokenLinkProvider);
    }

    protected static class ForwardEventListener
    implements EventListener {
        protected Element svgElement;
        protected Element imgElement;

        public ForwardEventListener(Element svgElement, Element imgElement) {
            this.svgElement = svgElement;
            this.imgElement = imgElement;
        }

        @Override
        public void handleEvent(Event e) {
            DOMMouseEvent evt = (DOMMouseEvent)e;
            DOMMouseEvent newMouseEvent = (DOMMouseEvent)((DocumentEvent)((Object)this.imgElement.getOwnerDocument())).createEvent("MouseEvents");
            newMouseEvent.initMouseEventNS("http://www.w3.org/2001/xml-events", evt.getType(), evt.getBubbles(), evt.getCancelable(), evt.getView(), evt.getDetail(), evt.getScreenX(), evt.getScreenY(), evt.getClientX(), evt.getClientY(), evt.getButton(), (EventTarget)((Object)this.imgElement), evt.getModifiersString());
            ((EventTarget)((Object)this.imgElement)).dispatchEvent((Event)newMouseEvent);
        }
    }

    public static class ProtectedStream
    extends BufferedInputStream {
        static final int BUFFER_SIZE = 8192;
        boolean wasClosed = false;
        boolean isTied = false;

        ProtectedStream(InputStream is) {
            super(is, 8192);
            super.mark(8192);
        }

        ProtectedStream(InputStream is, int size) {
            super(is, size);
            super.mark(size);
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void mark(int sz) {
        }

        @Override
        public void reset() throws IOException {
            throw new IOException("Reset unsupported");
        }

        public synchronized void retry() throws IOException {
            super.reset();
            this.wasClosed = false;
            this.isTied = false;
        }

        @Override
        public synchronized void close() throws IOException {
            this.wasClosed = true;
            if (this.isTied) {
                super.close();
            }
        }

        public synchronized void tie() throws IOException {
            this.isTied = true;
            if (this.wasClosed) {
                super.close();
            }
        }

        public void release() {
            try {
                super.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

