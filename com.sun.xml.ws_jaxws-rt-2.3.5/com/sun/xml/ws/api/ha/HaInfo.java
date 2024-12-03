/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.ha;

public class HaInfo {
    private final String replicaInstance;
    private final String key;
    private final boolean failOver;

    public HaInfo(String key, String replicaInstance, boolean failOver) {
        this.key = key;
        this.replicaInstance = replicaInstance;
        this.failOver = failOver;
    }

    public String getReplicaInstance() {
        return this.replicaInstance;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isFailOver() {
        return this.failOver;
    }
}

