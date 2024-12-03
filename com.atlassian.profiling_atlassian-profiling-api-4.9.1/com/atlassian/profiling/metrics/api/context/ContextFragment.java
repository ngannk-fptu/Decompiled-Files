/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.profiling.metrics.api.context;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface ContextFragment
extends AutoCloseable {
    @Override
    public void close();
}

