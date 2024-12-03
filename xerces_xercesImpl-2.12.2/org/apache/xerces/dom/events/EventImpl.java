/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl
implements Event {
    public String type = null;
    public EventTarget target;
    public EventTarget currentTarget;
    public short eventPhase;
    public boolean initialized = false;
    public boolean bubbles = true;
    public boolean cancelable = false;
    public boolean stopPropagation = false;
    public boolean preventDefault = false;
    protected long timeStamp = System.currentTimeMillis();

    @Override
    public void initEvent(String string, boolean bl, boolean bl2) {
        this.type = string;
        this.bubbles = bl;
        this.cancelable = bl2;
        this.initialized = true;
    }

    @Override
    public boolean getBubbles() {
        return this.bubbles;
    }

    @Override
    public boolean getCancelable() {
        return this.cancelable;
    }

    @Override
    public EventTarget getCurrentTarget() {
        return this.currentTarget;
    }

    @Override
    public short getEventPhase() {
        return this.eventPhase;
    }

    @Override
    public EventTarget getTarget() {
        return this.target;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public void stopPropagation() {
        this.stopPropagation = true;
    }

    @Override
    public void preventDefault() {
        this.preventDefault = true;
    }
}

