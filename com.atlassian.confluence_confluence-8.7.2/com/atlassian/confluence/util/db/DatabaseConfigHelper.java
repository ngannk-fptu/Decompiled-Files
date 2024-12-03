/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 */
package com.atlassian.confluence.util.db;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Internal
public interface DatabaseConfigHelper {
    public Optional<Integer> getConnectionPoolSize();

    public Optional<String> getProductName();
}

