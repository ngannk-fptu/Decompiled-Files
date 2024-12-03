/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

public abstract class AbstractFinder<T>
implements SingleFetcher<T> {
    protected final Expansion[] expansions;

    public AbstractFinder(Expansion ... expansions) {
        this.expansions = expansions;
    }

    public final T fetchOneOrNull() {
        return this.fetch().orElse(null);
    }

    protected Expansions getExpansions() {
        return new Expansions(this.expansions);
    }
}

