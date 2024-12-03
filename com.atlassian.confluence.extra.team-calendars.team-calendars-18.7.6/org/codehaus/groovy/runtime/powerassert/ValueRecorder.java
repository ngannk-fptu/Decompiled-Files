/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.powerassert.Value;

public class ValueRecorder {
    private final List<Value> values = new ArrayList<Value>();

    public void clear() {
        this.values.clear();
    }

    public Object record(Object value, int anchor) {
        this.values.add(new Value(value, anchor));
        return value;
    }

    public List<Value> getValues() {
        return this.values;
    }
}

