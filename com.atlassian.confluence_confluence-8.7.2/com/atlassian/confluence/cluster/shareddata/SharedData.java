/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.Mutable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.shareddata;

import com.atlassian.confluence.cluster.shareddata.SharedDataMutable;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.mutable.Mutable;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.2", forRemoval=true)
public interface SharedData {
    public <K extends Serializable, V extends Serializable> @NonNull Map<K, V> getMap();

    default public <K extends Serializable, V extends Serializable> Mutable<V> getMutable(K key, V defaultValue) {
        return new SharedDataMutable<K, V>(this.getMap(), key, defaultValue);
    }
}

