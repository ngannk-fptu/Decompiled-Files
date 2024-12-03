/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.events.DOMKeyEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.w3c.dom.events.KeyboardEvent
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.EventLikeTimingSpecifier;
import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.dom.events.DOMKeyEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.w3c.dom.events.KeyboardEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class AccesskeyTimingSpecifier
extends EventLikeTimingSpecifier
implements EventListener {
    protected char accesskey;
    protected boolean isSVG12AccessKey;
    protected String keyName;

    public AccesskeyTimingSpecifier(TimedElement owner, boolean isBegin, float offset, char accesskey) {
        super(owner, isBegin, offset);
        this.accesskey = accesskey;
    }

    public AccesskeyTimingSpecifier(TimedElement owner, boolean isBegin, float offset, String keyName) {
        super(owner, isBegin, offset);
        this.isSVG12AccessKey = true;
        this.keyName = keyName;
    }

    @Override
    public String toString() {
        if (this.isSVG12AccessKey) {
            return "accessKey(" + this.keyName + ")" + (this.offset != 0.0f ? super.toString() : "");
        }
        return "accesskey(" + this.accesskey + ")" + (this.offset != 0.0f ? super.toString() : "");
    }

    @Override
    public void initialize() {
        if (this.isSVG12AccessKey) {
            NodeEventTarget eventTarget = (NodeEventTarget)this.owner.getRootEventTarget();
            eventTarget.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", (EventListener)this, false, null);
        } else {
            EventTarget eventTarget = this.owner.getRootEventTarget();
            eventTarget.addEventListener("keypress", this, false);
        }
    }

    @Override
    public void deinitialize() {
        if (this.isSVG12AccessKey) {
            NodeEventTarget eventTarget = (NodeEventTarget)this.owner.getRootEventTarget();
            eventTarget.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", (EventListener)this, false);
        } else {
            EventTarget eventTarget = this.owner.getRootEventTarget();
            eventTarget.removeEventListener("keypress", this, false);
        }
    }

    @Override
    public void handleEvent(Event e) {
        boolean matched;
        if (e.getType().charAt(3) == 'p') {
            DOMKeyEvent evt = (DOMKeyEvent)e;
            matched = evt.getCharCode() == this.accesskey;
        } else {
            KeyboardEvent evt = (KeyboardEvent)e;
            matched = evt.getKeyIdentifier().equals(this.keyName);
        }
        if (matched) {
            this.owner.eventOccurred(this, e);
        }
    }

    @Override
    public void resolve(Event e) {
        float time = this.owner.getRoot().convertEpochTime(e.getTimeStamp());
        InstanceTime instance = new InstanceTime(this, time + this.offset, true);
        this.owner.addInstanceTime(instance, this.isBegin);
    }
}

