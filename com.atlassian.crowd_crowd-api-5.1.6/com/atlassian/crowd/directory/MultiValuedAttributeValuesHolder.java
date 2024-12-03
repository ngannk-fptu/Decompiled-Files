/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Attributes;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;

public class MultiValuedAttributeValuesHolder
implements Attributes {
    private final Map<String, Set<String>> attributes;

    public MultiValuedAttributeValuesHolder(Map<String, Set<String>> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getValues(String name) {
        return this.attributes.get(name);
    }

    public String getValue(String name) {
        Set<String> values = this.getValues(name);
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        return values.iterator().next();
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }
}

