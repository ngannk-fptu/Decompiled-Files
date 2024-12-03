/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimingSpecifier;

public class MediaMarkerTimingSpecifier
extends TimingSpecifier {
    protected String syncbaseID;
    protected TimedElement mediaElement;
    protected String markerName;
    protected InstanceTime instance;

    public MediaMarkerTimingSpecifier(TimedElement owner, boolean isBegin, String syncbaseID, String markerName) {
        super(owner, isBegin);
        this.syncbaseID = syncbaseID;
        this.markerName = markerName;
        this.mediaElement = owner.getTimedElementById(syncbaseID);
    }

    public String toString() {
        return this.syncbaseID + ".marker(" + this.markerName + ")";
    }

    @Override
    public boolean isEventCondition() {
        return false;
    }
}

