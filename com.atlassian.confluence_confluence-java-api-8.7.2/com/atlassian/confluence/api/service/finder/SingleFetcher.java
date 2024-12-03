/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.service.finder;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;

@ExperimentalApi
public interface SingleFetcher<T> {
    @Deprecated
    default public Option<T> fetchOne() {
        return FugueConversionUtil.toComOption(this.fetch());
    }

    public Optional<T> fetch();

    @Deprecated
    public T fetchOneOrNull();

    default public T fetchOrNull() {
        return this.fetch().orElse(null);
    }
}

