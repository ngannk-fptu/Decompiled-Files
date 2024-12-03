/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import java.util.LinkedList;
import java.util.List;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.timing.TimedElement;

public abstract class TimeContainer
extends TimedElement {
    protected List children = new LinkedList();

    public void addChild(TimedElement e) {
        if (e == this) {
            throw new IllegalArgumentException("recursive datastructure not allowed here!");
        }
        this.children.add(e);
        e.parent = this;
        this.setRoot(e, this.root);
        this.root.fireElementAdded(e);
        this.root.currentIntervalWillUpdate();
    }

    protected void setRoot(TimedElement e, TimedDocumentRoot root) {
        e.root = root;
        if (e instanceof TimeContainer) {
            TimeContainer c = (TimeContainer)e;
            for (Object aChildren : c.children) {
                TimedElement te = (TimedElement)aChildren;
                this.setRoot(te, root);
            }
        }
    }

    public void removeChild(TimedElement e) {
        this.children.remove(e);
        e.parent = null;
        this.setRoot(e, null);
        this.root.fireElementRemoved(e);
        this.root.currentIntervalWillUpdate();
    }

    public TimedElement[] getChildren() {
        return this.children.toArray(new TimedElement[this.children.size()]);
    }

    @Override
    protected float sampleAt(float parentSimpleTime, boolean hyperlinking) {
        super.sampleAt(parentSimpleTime, hyperlinking);
        return this.sampleChildren(parentSimpleTime, hyperlinking);
    }

    protected float sampleChildren(float parentSimpleTime, boolean hyperlinking) {
        float mint = Float.POSITIVE_INFINITY;
        for (Object aChildren : this.children) {
            TimedElement e = (TimedElement)aChildren;
            float t = e.sampleAt(parentSimpleTime, hyperlinking);
            if (!(t < mint)) continue;
            mint = t;
        }
        return mint;
    }

    @Override
    protected void reset(boolean clearCurrentBegin) {
        super.reset(clearCurrentBegin);
        for (Object aChildren : this.children) {
            TimedElement e = (TimedElement)aChildren;
            e.reset(clearCurrentBegin);
        }
    }

    @Override
    protected boolean isConstantAnimation() {
        return false;
    }

    public abstract float getDefaultBegin(TimedElement var1);
}

