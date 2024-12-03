/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.request.QueryParameter
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.webhooks.request.QueryParameter;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InternalQueryParameter
implements QueryParameter {
    private final String name;
    private final String value;

    public InternalQueryParameter(@Nonnull String name, @Nullable String value) {
        this.name = Objects.requireNonNull(name, "name");
        this.value = value;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public Optional<String> getValue() {
        return Optional.ofNullable(this.value);
    }
}

