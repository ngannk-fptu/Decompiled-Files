/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.request.Header
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.webhooks.request.Header;
import java.util.Objects;
import javax.annotation.Nonnull;

public class InternalHeader
implements Header {
    private final String name;
    private final String value;

    public InternalHeader(@Nonnull String name, @Nonnull String value) {
        this.name = Objects.requireNonNull(name, "name");
        this.value = Objects.requireNonNull(value, "value");
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }
}

