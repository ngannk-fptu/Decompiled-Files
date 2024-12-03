/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 */
package com.atlassian.crowd.support;

import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.google.common.collect.Ordering;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SupportInformationBuilder {
    private final Map<String, String> map = new LinkedHashMap<String, String>();
    private String prefix = "";

    @Deprecated
    public void addField(String name, Object value) {
        this.field(name, value);
    }

    public SupportInformationBuilder field(String name, Object value) {
        this.map.put(this.prefix + "." + name, value == null ? "null" : value.toString());
        return this;
    }

    @Deprecated
    public void addAttributes(String name, Map<String, String> attributesToSet) {
        this.attributes(name, attributesToSet);
    }

    public SupportInformationBuilder attributes(String name, Map<String, String> attributes) {
        for (String key : Ordering.natural().sortedCopy(attributes.keySet())) {
            this.map.put(this.prefix + "." + name + "." + key, DirectoryImpl.PASSWORD_ATTRIBUTES.contains(key) ? "********" : attributes.get(key));
        }
        return this;
    }

    public Map<String, String> getMap() {
        return this.map;
    }

    @Deprecated
    public void setPrefix(String prefixToSet) {
        this.prefix(prefixToSet);
    }

    public SupportInformationBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}

