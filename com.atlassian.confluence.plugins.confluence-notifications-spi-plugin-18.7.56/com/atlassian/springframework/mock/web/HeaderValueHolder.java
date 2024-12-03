/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.springframework.mock.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

class HeaderValueHolder {
    private final List<Object> values = new LinkedList<Object>();

    HeaderValueHolder() {
    }

    public static HeaderValueHolder getByName(Map<String, HeaderValueHolder> headers, String name) {
        Assert.notNull((Object)name, (String)"Header name must not be null");
        for (String headerName : headers.keySet()) {
            if (!headerName.equalsIgnoreCase(name)) continue;
            return headers.get(headerName);
        }
        return null;
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public void addValues(Collection<?> values) {
        this.values.addAll(values);
    }

    public void addValueArray(Object values) {
        CollectionUtils.mergeArrayIntoCollection((Object)values, this.values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    public List<String> getStringValues() {
        ArrayList<String> stringList = new ArrayList<String>(this.values.size());
        for (Object value : this.values) {
            stringList.add(value.toString());
        }
        return Collections.unmodifiableList(stringList);
    }

    public Object getValue() {
        return !this.values.isEmpty() ? this.values.get(0) : null;
    }

    public void setValue(Object value) {
        this.values.clear();
        this.values.add(value);
    }

    public String getStringValue() {
        return !this.values.isEmpty() ? this.values.get(0).toString() : null;
    }
}

