/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.dom;

import java.util.LinkedList;
import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AnimatedAttributeListener;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;

public abstract class AbstractSVGAnimatedValue
implements AnimatedLiveAttributeValue {
    protected AbstractElement element;
    protected String namespaceURI;
    protected String localName;
    protected boolean hasAnimVal;
    protected LinkedList listeners = new LinkedList();

    public AbstractSVGAnimatedValue(AbstractElement elt, String ns, String ln) {
        this.element = elt;
        this.namespaceURI = ns;
        this.localName = ln;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }

    public boolean isSpecified() {
        return this.hasAnimVal || this.element.hasAttributeNS(this.namespaceURI, this.localName);
    }

    protected abstract void updateAnimatedValue(AnimatableValue var1);

    @Override
    public void addAnimatedAttributeListener(AnimatedAttributeListener aal) {
        if (!this.listeners.contains(aal)) {
            this.listeners.add(aal);
        }
    }

    @Override
    public void removeAnimatedAttributeListener(AnimatedAttributeListener aal) {
        this.listeners.remove(aal);
    }

    protected void fireBaseAttributeListeners() {
        if (this.element instanceof SVGOMElement) {
            ((SVGOMElement)this.element).fireBaseAttributeListeners(this.namespaceURI, this.localName);
        }
    }

    protected void fireAnimatedAttributeListeners() {
        for (Object listener1 : this.listeners) {
            AnimatedAttributeListener listener = (AnimatedAttributeListener)listener1;
            listener.animatedAttributeChanged((Element)((Object)this.element), this);
        }
    }
}

