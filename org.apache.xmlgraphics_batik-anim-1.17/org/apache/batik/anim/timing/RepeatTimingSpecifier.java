/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.smil.TimeEvent
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.EventbaseTimingSpecifier;
import org.apache.batik.anim.timing.TimedElement;
import org.w3c.dom.events.Event;
import org.w3c.dom.smil.TimeEvent;

public class RepeatTimingSpecifier
extends EventbaseTimingSpecifier {
    protected int repeatIteration;
    protected boolean repeatIterationSpecified;

    public RepeatTimingSpecifier(TimedElement owner, boolean isBegin, float offset, String syncbaseID) {
        super(owner, isBegin, offset, syncbaseID, owner.getRoot().getRepeatEventName());
    }

    public RepeatTimingSpecifier(TimedElement owner, boolean isBegin, float offset, String syncbaseID, int repeatIteration) {
        super(owner, isBegin, offset, syncbaseID, owner.getRoot().getRepeatEventName());
        this.repeatIteration = repeatIteration;
        this.repeatIterationSpecified = true;
    }

    @Override
    public String toString() {
        return (this.eventbaseID == null ? "" : this.eventbaseID + ".") + "repeat" + (this.repeatIterationSpecified ? "(" + this.repeatIteration + ")" : "") + (this.offset != 0.0f ? super.toString() : "");
    }

    @Override
    public void handleEvent(Event e) {
        TimeEvent evt = (TimeEvent)e;
        if (!this.repeatIterationSpecified || evt.getDetail() == this.repeatIteration) {
            super.handleEvent(e);
        }
    }
}

