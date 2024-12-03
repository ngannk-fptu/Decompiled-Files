/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import org.bedework.util.misc.ToString;

public class RemoveType {
    private String href;

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

