/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.api;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClusterNode {
    private final String inetAddress;
    private final String id;
    private final String name;

    public ClusterNode(String id, @Nullable String inetAddress, @Nullable String name) {
        this.id = Objects.requireNonNull(id);
        this.inetAddress = inetAddress;
        this.name = name;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    public Optional<String> getInetAddress() {
        return Optional.ofNullable(this.inetAddress);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }
}

