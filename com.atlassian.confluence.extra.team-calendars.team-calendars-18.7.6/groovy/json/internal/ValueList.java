/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.LazyValueMap;
import groovy.json.internal.Value;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValueList
extends AbstractList<Object> {
    List<Object> list = new ArrayList<Object>(5);
    private final boolean lazyChop;
    boolean converted = false;

    public ValueList(boolean lazyChop) {
        this.lazyChop = lazyChop;
    }

    @Override
    public Object get(int index) {
        Object obj = this.list.get(index);
        if (obj instanceof Value) {
            obj = ValueList.convert((Value)obj);
            this.list.set(index, obj);
        }
        this.chopIfNeeded(obj);
        return obj;
    }

    private static Object convert(Value value) {
        return value.toValue();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<Object> iterator() {
        this.convertAllIfNeeded();
        return this.list.iterator();
    }

    private void convertAllIfNeeded() {
        if (!this.converted) {
            this.converted = true;
            for (int index = 0; index < this.list.size(); ++index) {
                this.get(index);
            }
        }
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean add(Object obj) {
        return this.list.add(obj);
    }

    public void chopList() {
        for (Object obj : this.list) {
            if (obj == null || !(obj instanceof Value)) continue;
            Value value = (Value)obj;
            if (value.isContainer()) {
                ValueList.chopContainer(value);
                continue;
            }
            value.chop();
        }
    }

    private void chopIfNeeded(Object object) {
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

    static void chopContainer(Value value) {
        Object obj = value.toValue();
        if (obj instanceof LazyValueMap) {
            LazyValueMap map = (LazyValueMap)obj;
            map.chopMap();
        } else if (obj instanceof ValueList) {
            ValueList list = (ValueList)obj;
            list.chopList();
        }
    }

    public List<Object> list() {
        return this.list;
    }
}

