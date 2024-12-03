/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Exceptions;
import groovy.json.internal.LazyMap;
import groovy.json.internal.MapItemValue;
import groovy.json.internal.Sys;
import groovy.json.internal.Value;
import groovy.json.internal.ValueList;
import groovy.json.internal.ValueMap;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LazyValueMap
extends AbstractMap<String, Object>
implements ValueMap<String, Object> {
    private Map<String, Object> map = null;
    private Map.Entry<String, Value>[] items;
    private int len = 0;
    private final boolean lazyChop;
    boolean mapChopped = false;

    public LazyValueMap(boolean lazyChop) {
        this.items = new Map.Entry[5];
        this.lazyChop = lazyChop;
    }

    public LazyValueMap(boolean lazyChop, int initialSize) {
        this.items = new Map.Entry[initialSize];
        this.lazyChop = lazyChop;
    }

    @Override
    public final void add(MapItemValue miv) {
        if (this.len >= this.items.length) {
            this.items = LazyMap.grow(this.items);
        }
        this.items[this.len] = miv;
        ++this.len;
    }

    @Override
    public final Object get(Object key) {
        Object object = null;
        if (this.map == null) {
            this.buildMap();
        }
        object = this.map.get(key);
        this.lazyChopIfNeeded(object);
        return object;
    }

    private void lazyChopIfNeeded(Object object) {
        if (this.lazyChop) {
            if (object instanceof LazyValueMap) {
                LazyValueMap m = (LazyValueMap)object;
                m.chopMap();
            } else if (object instanceof ValueList) {
                ValueList list = (ValueList)object;
                list.chopList();
            }
        }
    }

    public final void chopMap() {
        if (this.mapChopped) {
            return;
        }
        this.mapChopped = true;
        if (this.map == null) {
            for (int index = 0; index < this.len; ++index) {
                MapItemValue entry = (MapItemValue)this.items[index];
                Value value = entry.getValue();
                if (value == null) continue;
                if (value.isContainer()) {
                    LazyValueMap.chopContainer(value);
                    continue;
                }
                value.chop();
            }
        } else {
            for (Map.Entry<String, Object> entry : this.map.entrySet()) {
                Object object = entry.getValue();
                if (object instanceof Value) {
                    Value value = (Value)object;
                    if (value.isContainer()) {
                        LazyValueMap.chopContainer(value);
                        continue;
                    }
                    value.chop();
                    continue;
                }
                if (object instanceof LazyValueMap) {
                    LazyValueMap m = (LazyValueMap)object;
                    m.chopMap();
                    continue;
                }
                if (!(object instanceof ValueList)) continue;
                ValueList list = (ValueList)object;
                list.chopList();
            }
        }
    }

    private static void chopContainer(Value value) {
        Object obj = value.toValue();
        if (obj instanceof LazyValueMap) {
            LazyValueMap map = (LazyValueMap)obj;
            map.chopMap();
        } else if (obj instanceof ValueList) {
            ValueList list = (ValueList)obj;
            list.chopList();
        }
    }

    @Override
    public Value put(String key, Object value) {
        Exceptions.die("Not that kind of map");
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.map == null) {
            this.buildMap();
        }
        return this.map.entrySet();
    }

    private void buildMap() {
        this.map = Sys.is1_8OrLater() || Sys.is1_7() && LazyMap.JDK_MAP_ALTHASHING_SYSPROP != null ? new HashMap<String, Object>(this.items.length) : new TreeMap<String, Object>();
        for (Map.Entry<String, Value> miv : this.items) {
            if (miv == null) break;
            this.map.put(miv.getKey(), miv.getValue().toValue());
        }
        this.len = 0;
        this.items = null;
    }

    @Override
    public Collection<Object> values() {
        if (this.map == null) {
            this.buildMap();
        }
        return this.map.values();
    }

    @Override
    public int size() {
        if (this.map == null) {
            this.buildMap();
        }
        return this.map.size();
    }

    @Override
    public String toString() {
        if (this.map == null) {
            this.buildMap();
        }
        return this.map.toString();
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
}

