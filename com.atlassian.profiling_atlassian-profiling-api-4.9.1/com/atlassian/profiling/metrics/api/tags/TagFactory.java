/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.api.tags;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.profiling.metrics.api.tags.OptionalTag;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface TagFactory {
    public OptionalTag createOptionalTag(String var1, String var2);
}

