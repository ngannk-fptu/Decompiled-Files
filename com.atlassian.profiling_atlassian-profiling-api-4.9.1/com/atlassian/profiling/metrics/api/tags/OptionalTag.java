/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.profiling.metrics.api.tags;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Immutable
public interface OptionalTag {
    public String getKey();

    public String getValue();
}

