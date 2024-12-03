/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import java.util.HashMap;
import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.Interval;
import org.apache.batik.anim.timing.OffsetTimingSpecifier;
import org.apache.batik.anim.timing.TimedElement;

public class SyncbaseTimingSpecifier
extends OffsetTimingSpecifier {
    protected String syncbaseID;
    protected TimedElement syncbaseElement;
    protected boolean syncBegin;
    protected HashMap instances = new HashMap();

    public SyncbaseTimingSpecifier(TimedElement owner, boolean isBegin, float offset, String syncbaseID, boolean syncBegin) {
        super(owner, isBegin, offset);
        this.syncbaseID = syncbaseID;
        this.syncBegin = syncBegin;
        this.syncbaseElement = owner.getTimedElementById(syncbaseID);
        this.syncbaseElement.addDependent(this, syncBegin);
    }

    @Override
    public String toString() {
        return this.syncbaseID + "." + (this.syncBegin ? "begin" : "end") + (this.offset != 0.0f ? super.toString() : "");
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean isEventCondition() {
        return false;
    }

    @Override
    float newInterval(Interval interval) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        InstanceTime instance = new InstanceTime(this, (this.syncBegin ? interval.getBegin() : interval.getEnd()) + this.offset, true);
        this.instances.put(interval, instance);
        interval.addDependent(instance, this.syncBegin);
        return this.owner.addInstanceTime(instance, this.isBegin);
    }

    @Override
    float removeInterval(Interval interval) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        InstanceTime instance = (InstanceTime)this.instances.get(interval);
        interval.removeDependent(instance, this.syncBegin);
        return this.owner.removeInstanceTime(instance, this.isBegin);
    }

    @Override
    float handleTimebaseUpdate(InstanceTime instanceTime, float newTime) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        return this.owner.instanceTimeChanged(instanceTime, this.isBegin);
    }
}

