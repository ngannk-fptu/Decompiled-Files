/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;

public class CapabilitiesTruncatedType {
    protected boolean any;
    protected List<Integer> years;
    protected boolean untruncated;

    public boolean getAny() {
        return this.any;
    }

    public void setAny(boolean value) {
        this.any = value;
    }

    public List<Integer> getYears() {
        if (this.years == null) {
            this.years = new ArrayList<Integer>();
        }
        return this.years;
    }

    public boolean getUntruncated() {
        return this.untruncated;
    }

    public void setUntruncated(boolean value) {
        this.untruncated = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("any", this.getAny());
        ts.append("years", this.getYears(), false);
        ts.append("untruncated", this.getUntruncated());
        return ts.toString();
    }
}

