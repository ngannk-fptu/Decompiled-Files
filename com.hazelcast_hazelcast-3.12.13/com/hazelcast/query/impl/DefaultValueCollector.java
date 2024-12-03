/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.impl.getters.MultiResult;
import java.util.ArrayList;
import java.util.List;

public class DefaultValueCollector
extends ValueCollector {
    private Object value;
    private List<Object> values;

    public void addObject(Object valueToCollect) {
        if (this.values != null) {
            this.values.add(valueToCollect);
        } else if (this.value == null) {
            this.value = valueToCollect;
        } else {
            this.values = new ArrayList<Object>();
            this.values.add(this.value);
            this.values.add(valueToCollect);
            this.value = null;
        }
    }

    public Object getResult() {
        if (this.value != null) {
            return this.value;
        }
        if (this.values != null) {
            return new MultiResult<Object>(this.values);
        }
        return null;
    }
}

