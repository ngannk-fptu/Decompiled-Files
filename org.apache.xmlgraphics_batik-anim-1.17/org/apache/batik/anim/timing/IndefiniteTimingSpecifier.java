/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimingSpecifier;

public class IndefiniteTimingSpecifier
extends TimingSpecifier {
    public IndefiniteTimingSpecifier(TimedElement owner, boolean isBegin) {
        super(owner, isBegin);
    }

    public String toString() {
        return "indefinite";
    }

    @Override
    public void initialize() {
        if (!this.isBegin) {
            InstanceTime instance = new InstanceTime(this, Float.POSITIVE_INFINITY, false);
            this.owner.addInstanceTime(instance, this.isBegin);
        }
    }

    @Override
    public boolean isEventCondition() {
        return false;
    }
}

