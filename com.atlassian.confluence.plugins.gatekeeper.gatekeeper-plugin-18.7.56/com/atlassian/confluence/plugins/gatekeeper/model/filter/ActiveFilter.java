/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.filter;

import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;

public class ActiveFilter
extends Filter<TinyOwner> {
    @Override
    public boolean isEmptyFilter() {
        return false;
    }

    @Override
    public boolean matches(TinyOwner owner) {
        return owner.isActive();
    }
}

