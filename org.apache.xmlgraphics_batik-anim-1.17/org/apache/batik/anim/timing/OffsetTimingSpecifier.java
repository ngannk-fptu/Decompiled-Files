/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimingSpecifier;

public class OffsetTimingSpecifier
extends TimingSpecifier {
    protected float offset;

    public OffsetTimingSpecifier(TimedElement owner, boolean isBegin, float offset) {
        super(owner, isBegin);
        this.offset = offset;
    }

    public String toString() {
        return (this.offset >= 0.0f ? "+" : "") + this.offset;
    }

    @Override
    public void initialize() {
        InstanceTime instance = new InstanceTime(this, this.offset, false);
        this.owner.addInstanceTime(instance, this.isBegin);
    }

    @Override
    public boolean isEventCondition() {
        return false;
    }
}

