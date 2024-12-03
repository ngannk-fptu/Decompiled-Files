/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.plugins.clustersupport;

import java.io.Serializable;

public class ClusterNotification
implements Serializable {
    public static final int FLUSH_KEY = 1;
    public static final int FLUSH_GROUP = 2;
    public static final int FLUSH_PATTERN = 3;
    public static final int FLUSH_CACHE = 4;
    protected Serializable data;
    protected int type;

    public ClusterNotification(int type, Serializable data) {
        this.type = type;
        this.data = data;
    }

    public Serializable getData() {
        return this.data;
    }

    public int getType() {
        return this.type;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("type=").append(this.type).append(", data=").append(this.data);
        return buf.toString();
    }
}

