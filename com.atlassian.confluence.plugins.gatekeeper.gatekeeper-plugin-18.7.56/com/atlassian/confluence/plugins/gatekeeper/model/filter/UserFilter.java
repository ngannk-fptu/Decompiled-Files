/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.filter;

import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;

public class UserFilter
extends Filter<TinyOwner> {
    public UserFilter(String filter) {
        super(filter);
    }

    @Override
    public boolean matches(TinyOwner user) {
        return this.matches(user.getName()) || this.matches(user.getDisplayName());
    }
}

