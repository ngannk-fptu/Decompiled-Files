/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

public class Metadata {
    private Object keyMeta;
    private Object valueMeta;

    public void setKeyMetadata(Object metadata) {
        this.keyMeta = metadata;
    }

    public void setValueMetadata(Object metadata) {
        this.valueMeta = metadata;
    }

    public Object getKeyMetadata() {
        return this.keyMeta;
    }

    public Object getValueMetadata() {
        return this.valueMeta;
    }
}

