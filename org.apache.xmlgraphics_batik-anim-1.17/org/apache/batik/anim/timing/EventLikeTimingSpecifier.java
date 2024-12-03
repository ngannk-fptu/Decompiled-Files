/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.OffsetTimingSpecifier;
import org.apache.batik.anim.timing.TimedElement;
import org.w3c.dom.events.Event;

public abstract class EventLikeTimingSpecifier
extends OffsetTimingSpecifier {
    public EventLikeTimingSpecifier(TimedElement owner, boolean isBegin, float offset) {
        super(owner, isBegin, offset);
    }

    @Override
    public boolean isEventCondition() {
        return true;
    }

    public abstract void resolve(Event var1);
}

