/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.filter;

import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;

public class GroupFilter
extends Filter<TinyOwner> {
    public GroupFilter(String filter) {
        super(filter);
    }

    @Override
    public boolean matches(TinyOwner group) {
        return this.matches(group.getName());
    }
}

