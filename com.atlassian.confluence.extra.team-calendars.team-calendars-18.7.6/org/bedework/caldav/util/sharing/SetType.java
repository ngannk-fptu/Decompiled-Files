/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import org.bedework.caldav.util.sharing.AccessType;
import org.bedework.util.misc.ToString;

public class SetType {
    private String href;
    private String commonName;
    private String summary;
    private AccessType access;

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    public void setCommonName(String val) {
        this.commonName = val;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public void setSummary(String val) {
        this.summary = val;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setAccess(AccessType val) {
        this.access = val;
    }

    public AccessType getAccess() {
        return this.access;
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
        ts.append("commonName", this.getCommonName());
        ts.append("summary", this.getSummary());
        ts.append(this.getAccess().toString());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

