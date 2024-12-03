/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.restricted.CollectionData;
import com.google.template.soy.data.restricted.StringData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class SoyMapData
extends CollectionData
implements SoyDict {
    private final Map<String, SoyData> map;

    public SoyMapData() {
        this.map = Maps.newLinkedHashMap();
    }

    public SoyMapData(Map<String, ?> data) {
        this.map = new LinkedHashMap<String, SoyData>(data.size());
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String key;
            try {
                key = entry.getKey();
            }
            catch (ClassCastException cce) {
                throw new SoyDataException("Attempting to convert a map with non-string key to Soy data (key type " + entry.getKey().getClass().getName() + ").");
            }
            Object value = entry.getValue();
            try {
                this.map.put(key, SoyData.createFromExistingData(value));
            }
            catch (SoyDataException sde) {
                sde.prependKeyToDataPath(key);
                throw sde;
            }
        }
    }

    public SoyMapData(Object ... data) {
        this();
        this.put(data);
    }

    public Map<String, SoyData> asMap() {
        return Collections.unmodifiableMap(this.map);
    }

    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    @Override
    public String toString() {
        return this.toStringHelper(this.map);
    }

    protected String toStringHelper(Map<String, SoyData> map) {
        StringBuilder mapStr = new StringBuilder();
        mapStr.append('{');
        boolean isFirst = true;
        for (Map.Entry<String, SoyData> entry : map.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                mapStr.append(", ");
            }
            mapStr.append(entry.getKey()).append(": ").append(entry.getValue().coerceToString());
        }
        mapStr.append('}');
        return mapStr.toString();
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public void putSingle(String key, SoyData value) {
        this.map.put(key, value);
    }

    @Override
    public void removeSingle(String key) {
        this.map.remove(key);
    }

    @Override
    public SoyData getSingle(String key) {
        return this.map.get(key);
    }

    @Override
    @Nonnull
    public Map<String, ? extends SoyValueProvider> asJavaStringMap() {
        return this.asMap();
    }

    @Override
    @Nonnull
    public Map<String, ? extends SoyValue> asResolvedJavaStringMap() {
        return this.asMap();
    }

    @Override
    public boolean hasField(String name) {
        return this.getSingle(name) != null;
    }

    @Override
    public SoyValue getField(String name) {
        return this.getSingle(name);
    }

    @Override
    public SoyValueProvider getFieldProvider(String name) {
        return this.getSingle(name);
    }

    @Override
    public int getItemCnt() {
        return this.getKeys().size();
    }

    @Nonnull
    public Iterable<StringData> getItemKeys() {
        Set<String> internalKeys = this.getKeys();
        ArrayList keys = Lists.newArrayListWithCapacity((int)internalKeys.size());
        for (String internalKey : internalKeys) {
            keys.add(StringData.forValue(internalKey));
        }
        return keys;
    }

    @Override
    public boolean hasItem(SoyValue key) {
        return this.getSingle(this.getStringKey(key)) != null;
    }

    @Override
    public SoyValue getItem(SoyValue key) {
        return this.getSingle(this.getStringKey(key));
    }

    @Override
    public SoyValueProvider getItemProvider(SoyValue key) {
        return this.getSingle(this.getStringKey(key));
    }

    private String getStringKey(SoyValue key) {
        try {
            return ((StringData)key).getValue();
        }
        catch (ClassCastException e) {
            throw new SoyDataException("SoyDict accessed with non-string key (got key type " + key.getClass().getName() + ").");
        }
    }
}

