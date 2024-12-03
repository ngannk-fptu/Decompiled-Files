/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.RootGraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.DocumentBridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;

public class SVGDocumentBridge
implements DocumentBridge,
BridgeUpdateHandler,
SVGContext {
    protected Document document;
    protected RootGraphicsNode node;
    protected BridgeContext ctx;

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public Bridge getInstance() {
        return new SVGDocumentBridge();
    }

    @Override
    public RootGraphicsNode createGraphicsNode(BridgeContext ctx, Document doc) {
        RootGraphicsNode gn = new RootGraphicsNode();
        this.document = doc;
        this.node = gn;
        this.ctx = ctx;
        ((SVGOMDocument)doc).setSVGContext((SVGContext)this);
        return gn;
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Document doc, RootGraphicsNode node) {
        if (ctx.isDynamic()) {
            ctx.bind(doc, (GraphicsNode)node);
        }
    }

    @Override
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            Element childElt = (Element)((Object)evt.getTarget());
            GVTBuilder builder = this.ctx.getGVTBuilder();
            GraphicsNode childNode = builder.build(this.ctx, childElt);
            if (childNode == null) {
                return;
            }
            this.node.add((Object)childNode);
        }
    }

    @Override
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
    }

    @Override
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    @Override
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
    }

    @Override
    public void handleOtherAnimationChanged(String type) {
    }

    @Override
    public void dispose() {
        ((SVGOMDocument)this.document).setSVGContext(null);
        this.ctx.unbind(this.document);
    }

    public float getPixelUnitToMillimeter() {
        return this.ctx.getUserAgent().getPixelUnitToMillimeter();
    }

    public float getPixelToMM() {
        return this.getPixelUnitToMillimeter();
    }

    public Rectangle2D getBBox() {
        return null;
    }

    public AffineTransform getScreenTransform() {
        return this.ctx.getUserAgent().getTransform();
    }

    public void setScreenTransform(AffineTransform at) {
        this.ctx.getUserAgent().setTransform(at);
    }

    public AffineTransform getCTM() {
        return null;
    }

    public AffineTransform getGlobalTransform() {
        return null;
    }

    public float getViewportWidth() {
        return 0.0f;
    }

    public float getViewportHeight() {
        return 0.0f;
    }

    public float getFontSize() {
        return 0.0f;
    }
}

