/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyOnceMap<V extends Copiable<V>> {
    private static final Logger log = LoggerFactory.getLogger(CopyOnceMap.class);
    private boolean modified = false;
    private Map<String, V> map;
    private Set<String> modifiedEntries;

    public CopyOnceMap(Map<String, V> map) {
        this.map = map;
    }

    public void setAsModified() {
        if (this.modified) {
            return;
        }
        this.modified = true;
        this.map = new Object2ObjectOpenHashMap(this.map);
        this.modifiedEntries = new ObjectOpenHashSet();
    }

    public V getOrCopy(String key) {
        Copiable value = (Copiable)this.map.get(key);
        if (value == null) {
            log.trace("Attempted to get non-existent value for key [{}]", (Object)key);
            return null;
        }
        this.setAsModified();
        if (this.modifiedEntries.contains(key)) {
            return (V)((Copiable)this.map.get(key));
        }
        key = key.intern();
        this.modifiedEntries.add(key);
        Copiable newValue = (Copiable)value.copy();
        this.map.put(key, newValue);
        return (V)newValue;
    }

    public void put(String key, V value) {
        this.setAsModified();
        key = key.intern();
        this.modifiedEntries.add(key);
        this.map.put(key, value);
    }

    public void remove(String key) {
        this.setAsModified();
        this.modifiedEntries.remove(key);
        this.map.remove(key);
    }

    public boolean isModified() {
        return this.modified;
    }

    public void resetModified() {
        this.modified = false;
        this.modifiedEntries = null;
    }

    public Map<String, V> getUnderlyingMap() {
        return this.map;
    }
}

