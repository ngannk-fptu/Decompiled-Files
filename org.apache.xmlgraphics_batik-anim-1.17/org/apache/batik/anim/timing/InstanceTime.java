/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.timing;

import org.apache.batik.anim.timing.TimingSpecifier;

public class InstanceTime
implements Comparable {
    protected float time;
    protected TimingSpecifier creator;
    protected boolean clearOnReset;

    public InstanceTime(TimingSpecifier creator, float time, boolean clearOnReset) {
        this.creator = creator;
        this.time = time;
        this.clearOnReset = clearOnReset;
    }

    public boolean getClearOnReset() {
        return this.clearOnReset;
    }

    public float getTime() {
        return this.time;
    }

    float dependentUpdate(float newTime) {
        this.time = newTime;
        if (this.creator != null) {
            return this.creator.handleTimebaseUpdate(this, this.time);
        }
        return Float.POSITIVE_INFINITY;
    }

    public String toString() {
        return Float.toString(this.time);
    }

    public int compareTo(Object o) {
        InstanceTime it = (InstanceTime)o;
        if (this.time == it.time) {
            return 0;
        }
        if (this.time > it.time) {
            return 1;
        }
        return -1;
    }
}

