/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.dom.AnimationTargetListener
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.anim.timing.TimedElement
 *  org.apache.batik.anim.values.AnimatableValue
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.dom.svg.SVGAnimationContext
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.w3c.dom.svg.SVGElement
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.AnimationSupport;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.bridge.SVGAnimationEngine;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.SVGAnimationContext;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.util.XLinkSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGElement;

public abstract class SVGAnimationElementBridge
extends AbstractSVGBridge
implements GenericBridge,
BridgeUpdateHandler,
SVGAnimationContext,
AnimatableElement {
    protected SVGOMElement element;
    protected BridgeContext ctx;
    protected SVGAnimationEngine eng;
    protected TimedElement timedElement;
    protected AbstractAnimation animation;
    protected String attributeNamespaceURI;
    protected String attributeLocalName;
    protected short animationType;
    protected SVGOMElement targetElement;
    protected AnimationTarget animationTarget;

    public TimedElement getTimedElement() {
        return this.timedElement;
    }

    public AnimatableValue getUnderlyingValue() {
        if (this.animationType == 0) {
            return this.animationTarget.getUnderlyingValue(this.attributeNamespaceURI, this.attributeLocalName);
        }
        return this.eng.getUnderlyingCSSValue((Element)this.element, this.animationTarget, this.attributeLocalName);
    }

    @Override
    public void handleElement(BridgeContext ctx, Element e) {
        if (ctx.isDynamic() && BridgeContext.getSVGContext(e) == null) {
            SVGAnimationElementBridge b = (SVGAnimationElementBridge)this.getInstance();
            b.element = (SVGOMElement)e;
            b.ctx = ctx;
            b.eng = ctx.getAnimationEngine();
            b.element.setSVGContext((SVGContext)b);
            if (b.eng.hasStarted()) {
                b.initializeAnimation();
                b.initializeTimedElement();
            } else {
                b.eng.addInitialBridge(b);
            }
        }
    }

    protected void initializeAnimation() {
        Node t;
        String uri = XLinkSupport.getXLinkHref((Element)this.element);
        if (uri.length() == 0) {
            t = this.element.getParentNode();
        } else {
            t = this.ctx.getReferencedElement((Element)this.element, uri);
            if (t.getOwnerDocument() != this.element.getOwnerDocument()) {
                throw new BridgeException(this.ctx, (Element)this.element, "uri.badTarget", new Object[]{uri});
            }
        }
        this.animationTarget = null;
        if (t instanceof SVGOMElement) {
            this.targetElement = (SVGOMElement)t;
            this.animationTarget = this.targetElement;
        }
        if (this.animationTarget == null) {
            throw new BridgeException(this.ctx, (Element)this.element, "uri.badTarget", new Object[]{uri});
        }
        String an = this.element.getAttributeNS(null, "attributeName");
        int ci = an.indexOf(58);
        if (ci == -1) {
            if (this.element.hasProperty(an)) {
                this.animationType = 1;
                this.attributeLocalName = an;
            } else {
                this.animationType = 0;
                this.attributeLocalName = an;
            }
        } else {
            this.animationType = 0;
            String prefix = an.substring(0, ci);
            this.attributeNamespaceURI = this.element.lookupNamespaceURI(prefix);
            this.attributeLocalName = an.substring(ci + 1);
        }
        if (this.animationType == 1 && !this.targetElement.isPropertyAnimatable(this.attributeLocalName) || this.animationType == 0 && !this.targetElement.isAttributeAnimatable(this.attributeNamespaceURI, this.attributeLocalName)) {
            throw new BridgeException(this.ctx, (Element)this.element, "attribute.not.animatable", new Object[]{this.targetElement.getNodeName(), an});
        }
        int type = this.animationType == 1 ? this.targetElement.getPropertyType(this.attributeLocalName) : this.targetElement.getAttributeType(this.attributeNamespaceURI, this.attributeLocalName);
        if (!this.canAnimateType(type)) {
            throw new BridgeException(this.ctx, (Element)this.element, "type.not.animatable", new Object[]{this.targetElement.getNodeName(), an, this.element.getNodeName()});
        }
        this.timedElement = this.createTimedElement();
        this.animation = this.createAnimation(this.animationTarget);
        this.eng.addAnimation(this.animationTarget, this.animationType, this.attributeNamespaceURI, this.attributeLocalName, this.animation);
    }

    protected abstract boolean canAnimateType(int var1);

    protected boolean checkValueType(AnimatableValue v) {
        return true;
    }

    protected void initializeTimedElement() {
        this.initializeTimedElement(this.timedElement);
        this.timedElement.initialize();
    }

    protected TimedElement createTimedElement() {
        return new SVGTimedElement();
    }

    protected abstract AbstractAnimation createAnimation(AnimationTarget var1);

    protected AnimatableValue parseAnimatableValue(String an) {
        String s;
        if (!this.element.hasAttributeNS(null, an)) {
            return null;
        }
        AnimatableValue val = this.eng.parseAnimatableValue((Element)this.element, this.animationTarget, this.attributeNamespaceURI, this.attributeLocalName, this.animationType == 1, s = this.element.getAttributeNS(null, an));
        if (!this.checkValueType(val)) {
            throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{an, s});
        }
        return val;
    }

    protected void initializeTimedElement(TimedElement timedElement) {
        timedElement.parseAttributes(this.element.getAttributeNS(null, "begin"), this.element.getAttributeNS(null, "dur"), this.element.getAttributeNS(null, "end"), this.element.getAttributeNS(null, "min"), this.element.getAttributeNS(null, "max"), this.element.getAttributeNS(null, "repeatCount"), this.element.getAttributeNS(null, "repeatDur"), this.element.getAttributeNS(null, "fill"), this.element.getAttributeNS(null, "restart"));
    }

    @Override
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
    }

    @Override
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        this.element.setSVGContext(null);
        this.dispose();
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
        if (this.element.getSVGContext() == null) {
            this.eng.removeAnimation(this.animation);
            this.timedElement.deinitialize();
            this.timedElement = null;
            this.element = null;
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
        return this.ctx.getBlockWidth((Element)this.element);
    }

    public float getViewportHeight() {
        return this.ctx.getBlockHeight((Element)this.element);
    }

    public float getFontSize() {
        return 0.0f;
    }

    public float svgToUserSpace(float v, int type, int pcInterp) {
        return 0.0f;
    }

    public void addTargetListener(String pn, AnimationTargetListener l) {
    }

    public void removeTargetListener(String pn, AnimationTargetListener l) {
    }

    public SVGElement getTargetElement() {
        return this.targetElement;
    }

    public float getStartTime() {
        return this.timedElement.getCurrentBeginTime();
    }

    public float getCurrentTime() {
        return this.timedElement.getLastSampleTime();
    }

    public float getSimpleDuration() {
        return this.timedElement.getSimpleDur();
    }

    public float getHyperlinkBeginTime() {
        return this.timedElement.getHyperlinkBeginTime();
    }

    public boolean beginElement() throws DOMException {
        this.timedElement.beginElement();
        return this.timedElement.canBegin();
    }

    public boolean beginElementAt(float offset) throws DOMException {
        this.timedElement.beginElement(offset);
        return true;
    }

    public boolean endElement() throws DOMException {
        this.timedElement.endElement();
        return this.timedElement.canEnd();
    }

    public boolean endElementAt(float offset) throws DOMException {
        this.timedElement.endElement(offset);
        return true;
    }

    protected boolean isConstantAnimation() {
        return false;
    }

    protected class SVGTimedElement
    extends TimedElement {
        protected SVGTimedElement() {
        }

        public Element getElement() {
            return SVGAnimationElementBridge.this.element;
        }

        protected void fireTimeEvent(String eventType, Calendar time, int detail) {
            AnimationSupport.fireTimeEvent((EventTarget)SVGAnimationElementBridge.this.element, eventType, time, detail);
        }

        protected void toActive(float begin) {
            SVGAnimationElementBridge.this.eng.toActive(SVGAnimationElementBridge.this.animation, begin);
        }

        protected void toInactive(boolean stillActive, boolean isFrozen) {
            SVGAnimationElementBridge.this.eng.toInactive(SVGAnimationElementBridge.this.animation, isFrozen);
        }

        protected void removeFill() {
            SVGAnimationElementBridge.this.eng.removeFill(SVGAnimationElementBridge.this.animation);
        }

        protected void sampledAt(float simpleTime, float simpleDur, int repeatIteration) {
            SVGAnimationElementBridge.this.eng.sampledAt(SVGAnimationElementBridge.this.animation, simpleTime, simpleDur, repeatIteration);
        }

        protected void sampledLastValue(int repeatIteration) {
            SVGAnimationElementBridge.this.eng.sampledLastValue(SVGAnimationElementBridge.this.animation, repeatIteration);
        }

        protected TimedElement getTimedElementById(String id) {
            return AnimationSupport.getTimedElementById(id, (Node)SVGAnimationElementBridge.this.element);
        }

        protected EventTarget getEventTargetById(String id) {
            return AnimationSupport.getEventTargetById(id, (Node)SVGAnimationElementBridge.this.element);
        }

        protected EventTarget getRootEventTarget() {
            return (EventTarget)((Object)SVGAnimationElementBridge.this.element.getOwnerDocument());
        }

        protected EventTarget getAnimationEventTarget() {
            return SVGAnimationElementBridge.this.targetElement;
        }

        public boolean isBefore(TimedElement other) {
            Element e = other.getElement();
            short pos = SVGAnimationElementBridge.this.element.compareDocumentPosition((Node)e);
            return (pos & 2) != 0;
        }

        public String toString() {
            String id;
            if (SVGAnimationElementBridge.this.element != null && (id = SVGAnimationElementBridge.this.element.getAttributeNS(null, "id")).length() != 0) {
                return id;
            }
            return super.toString();
        }

        protected boolean isConstantAnimation() {
            return SVGAnimationElementBridge.this.isConstantAnimation();
        }
    }
}

