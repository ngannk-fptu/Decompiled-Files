/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributeUtil
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.attribute.AttributeUtil;
import com.atlassian.crowd.embedded.api.Attributes;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class EntityWithAttributes
implements Attributes {
    private final ImmutableSetMultimap<String, String> attributes;

    public EntityWithAttributes(Multimap<String, String> attributes) {
        this.attributes = ImmutableSetMultimap.copyOf(attributes);
    }

    public EntityWithAttributes(Map<String, Set<String>> attributes) {
        this((Multimap<String, String>)AttributeUtil.toMultimap(attributes));
    }

    public Set<String> getValues(String name) {
        return this.attributes.get((Object)name);
    }

    public String getValue(String name) {
        return (String)Iterables.getFirst(this.getValues(name), null);
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("attributes", this.attributes).toString();
    }
}

