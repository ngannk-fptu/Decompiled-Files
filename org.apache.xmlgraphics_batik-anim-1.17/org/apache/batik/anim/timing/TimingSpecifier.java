/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.Interval;
import org.apache.batik.anim.timing.TimedElement;

public abstract class TimingSpecifier {
    protected TimedElement owner;
    protected boolean isBegin;

    protected TimingSpecifier(TimedElement owner, boolean isBegin) {
        this.owner = owner;
        this.isBegin = isBegin;
    }

    public TimedElement getOwner() {
        return this.owner;
    }

    public boolean isBegin() {
        return this.isBegin;
    }

    public void initialize() {
    }

    public void deinitialize() {
    }

    public abstract boolean isEventCondition();

    float newInterval(Interval interval) {
        return Float.POSITIVE_INFINITY;
    }

    float removeInterval(Interval interval) {
        return Float.POSITIVE_INFINITY;
    }

    float handleTimebaseUpdate(InstanceTime instanceTime, float newTime) {
        return Float.POSITIVE_INFINITY;
    }
}

