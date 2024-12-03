/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  com.atlassian.profiling.metrics.api.tags.TagFactory
 *  com.atlassian.profiling.metrics.api.tags.TagFactoryFactory
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.tags;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.profiling.metrics.api.tags.TagFactory;
import com.atlassian.profiling.metrics.api.tags.TagFactoryFactory;
import com.atlassian.profiling.metrics.tags.PrefixedTagFactoryAdaptor;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Internal
public class TagFactoryFactoryImpl
implements TagFactoryFactory {
    public TagFactory prefixedTagFactory(String keyPrefix) {
        Objects.requireNonNull(keyPrefix, "keyPrefix");
        return new PrefixedTagFactoryAdaptor(keyPrefix);
    }
}

