/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Attributes;
import java.util.Set;

public abstract class AbstractDelegatingEntityWithAttributes
implements Attributes {
    private final Attributes attributes;

    public AbstractDelegatingEntityWithAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Set<String> getValues(String key) {
        return this.attributes.getValues(key);
    }

    @Override
    public String getValue(String key) {
        return this.attributes.getValue(key);
    }

    @Override
    public Set<String> getKeys() {
        return this.attributes.getKeys();
    }

    @Override
    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }
}

