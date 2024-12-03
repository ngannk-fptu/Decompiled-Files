/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.dom.svg.SVGContext
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.AnimatableSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;

public abstract class AnimatableGenericSVGBridge
extends AnimatableSVGBridge
implements GenericBridge,
BridgeUpdateHandler,
SVGContext {
    @Override
    public void handleElement(BridgeContext ctx, Element e) {
        if (ctx.isDynamic()) {
            this.e = e;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext((SVGContext)this);
        }
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

    @Override
    public void dispose() {
        ((SVGOMElement)this.e).setSVGContext(null);
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
    }

    @Override
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    @Override
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        this.dispose();
    }

    @Override
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
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
}

