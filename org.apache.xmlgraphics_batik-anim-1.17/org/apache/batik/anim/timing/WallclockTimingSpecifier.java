/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import java.util.Calendar;
import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimingSpecifier;

public class WallclockTimingSpecifier
extends TimingSpecifier {
    protected Calendar time;
    protected InstanceTime instance;

    public WallclockTimingSpecifier(TimedElement owner, boolean isBegin, Calendar time) {
        super(owner, isBegin);
        this.time = time;
    }

    public String toString() {
        return "wallclock(" + this.time.toString() + ")";
    }

    @Override
    public void initialize() {
        float t = this.owner.getRoot().convertWallclockTime(this.time);
        this.instance = new InstanceTime(this, t, false);
        this.owner.addInstanceTime(this.instance, this.isBegin);
    }

    @Override
    public boolean isEventCondition() {
        return false;
    }
}

