/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import java.util.ArrayList;
import java.util.List;
import org.apache.batik.dom.xbl.OriginalEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public abstract class AbstractEvent
implements Event,
OriginalEvent,
Cloneable {
    protected String type;
    protected boolean isBubbling;
    protected boolean cancelable;
    protected EventTarget currentTarget;
    protected EventTarget target;
    protected short eventPhase;
    protected long timeStamp = System.currentTimeMillis();
    protected boolean stopPropagation = false;
    protected boolean stopImmediatePropagation = false;
    protected boolean preventDefault = false;
    protected String namespaceURI;
    protected Event originalEvent;
    protected List defaultActions;
    protected int bubbleLimit = 0;

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public EventTarget getCurrentTarget() {
        return this.currentTarget;
    }

    @Override
    public EventTarget getTarget() {
        return this.target;
    }

    @Override
    public short getEventPhase() {
        return this.eventPhase;
    }

    @Override
    public boolean getBubbles() {
        return this.isBubbling;
    }

    @Override
    public boolean getCancelable() {
        return this.cancelable;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public Event getOriginalEvent() {
        return this.originalEvent;
    }

    @Override
    public void stopPropagation() {
        this.stopPropagation = true;
    }

    @Override
    public void preventDefault() {
        this.preventDefault = true;
    }

    public boolean getDefaultPrevented() {
        return this.preventDefault;
    }

    public List getDefaultActions() {
        return this.defaultActions;
    }

    public void addDefaultAction(Runnable rable) {
        if (this.defaultActions == null) {
            this.defaultActions = new ArrayList();
        }
        this.defaultActions.add(rable);
    }

    public void stopImmediatePropagation() {
        this.stopImmediatePropagation = true;
    }

    @Override
    public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
        this.type = eventTypeArg;
        this.isBubbling = canBubbleArg;
        this.cancelable = cancelableArg;
    }

    public void initEventNS(String namespaceURIArg, String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
        if (this.namespaceURI != null && this.namespaceURI.length() == 0) {
            this.namespaceURI = null;
        }
        this.namespaceURI = namespaceURIArg;
        this.type = eventTypeArg;
        this.isBubbling = canBubbleArg;
        this.cancelable = cancelableArg;
    }

    boolean getStopPropagation() {
        return this.stopPropagation;
    }

    boolean getStopImmediatePropagation() {
        return this.stopImmediatePropagation;
    }

    void setEventPhase(short eventPhase) {
        this.eventPhase = eventPhase;
    }

    void stopPropagation(boolean state) {
        this.stopPropagation = state;
    }

    void stopImmediatePropagation(boolean state) {
        this.stopImmediatePropagation = state;
    }

    void preventDefault(boolean state) {
        this.preventDefault = state;
    }

    void setCurrentTarget(EventTarget currentTarget) {
        this.currentTarget = currentTarget;
    }

    void setTarget(EventTarget target) {
        this.target = target;
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractEvent newEvent = (AbstractEvent)super.clone();
        newEvent.timeStamp = System.currentTimeMillis();
        return newEvent;
    }

    public AbstractEvent cloneEvent() {
        try {
            AbstractEvent newEvent = (AbstractEvent)this.clone();
            newEvent.originalEvent = this;
            return newEvent;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getBubbleLimit() {
        return this.bubbleLimit;
    }

    public void setBubbleLimit(int n) {
        this.bubbleLimit = n;
    }
}

