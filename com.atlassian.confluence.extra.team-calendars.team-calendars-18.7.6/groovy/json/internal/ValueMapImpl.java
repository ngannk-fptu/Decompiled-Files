/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Exceptions;
import groovy.json.internal.LazyMap;
import groovy.json.internal.MapItemValue;
import groovy.json.internal.Value;
import groovy.json.internal.ValueMap;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValueMapImpl
extends AbstractMap<String, Value>
implements ValueMap<String, Value> {
    private Map<String, Value> map = null;
    private Map.Entry<String, Value>[] items = new Map.Entry[20];
    private int len = 0;

    @Override
    public void add(MapItemValue miv) {
        if (this.len >= this.items.length) {
            this.items = LazyMap.grow(this.items);
        }
        this.items[this.len] = miv;
        ++this.len;
    }

    @Override
    public int len() {
        return this.len;
    }

    @Override
    public boolean hydrated() {
        return this.map != null;
    }

    @Override
    public Map.Entry<String, Value>[] items() {
        return this.items;
    }

    @Override
    public Value get(Object key) {
        if (this.map == null && this.items.length < 20) {
            for (Map.Entry<String, Value> item : this.items) {
                MapItemValue miv = (MapItemValue)item;
                if (!key.equals(miv.name.toValue())) continue;
                return miv.value;
            }
            return null;
        }
        if (this.map == null) {
            this.buildIfNeededMap();
        }
        return this.map.get(key);
    }

    @Override
    public Value put(String key, Value value) {
        Exceptions.die("Not that kind of map");
        return null;
    }

    @Override
    public Set<Map.Entry<String, Value>> entrySet() {
        this.buildIfNeededMap();
        return this.map.entrySet();
    }

    private void buildIfNeededMap() {
        if (this.map == null) {
            this.map = new HashMap<String, Value>(this.items.length);
            for (Map.Entry<String, Value> miv : this.items) {
                if (miv == null) break;
                this.map.put(miv.getKey(), miv.getValue());
            }
        }
    }

    @Override
    public Collection<Value> values() {
        this.buildIfNeededMap();
        return this.map.values();
    }

    @Override
    public int size() {
        this.buildIfNeededMap();
        return this.map.size();
    }
}

