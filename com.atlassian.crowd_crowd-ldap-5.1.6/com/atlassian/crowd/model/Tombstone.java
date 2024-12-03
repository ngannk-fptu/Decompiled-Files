/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model;

public class Tombstone {
    private final String objectGUID;
    private final Long usnChanged;

    public Tombstone(String objectGUID, String usnChanged) {
        this.objectGUID = objectGUID;
        this.usnChanged = Long.parseLong(usnChanged);
    }

    public String getObjectGUID() {
        return this.objectGUID;
    }

    public Long getUsnChanged() {
        return this.usnChanged;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Tombstone");
        sb.append("{objectGUID='").append(this.objectGUID).append('\'');
        sb.append(", usnChanged='").append(this.usnChanged).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

