/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.nio.serialization.Data;

public class MapRecordKey {
    final String mapName;
    final Data key;

    public MapRecordKey(String mapName, Data key) {
        this.mapName = mapName;
        this.key = key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MapRecordKey that = (MapRecordKey)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.mapName != null ? !this.mapName.equals(that.mapName) : that.mapName != null);
    }

    public int hashCode() {
        int result = this.mapName != null ? this.mapName.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }
}

