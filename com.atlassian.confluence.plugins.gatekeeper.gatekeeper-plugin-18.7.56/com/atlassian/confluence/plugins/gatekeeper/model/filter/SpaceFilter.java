/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.filter;

import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;

public class SpaceFilter
extends Filter<TinySpace> {
    public SpaceFilter(String filter) {
        super(filter);
    }

    @Override
    public boolean matches(TinySpace space) {
        return this.matches(space.getKey()) || this.matches(space.getName());
    }
}

