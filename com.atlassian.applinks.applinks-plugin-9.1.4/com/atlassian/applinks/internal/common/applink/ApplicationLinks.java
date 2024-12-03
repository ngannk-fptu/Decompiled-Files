/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import java.net.URI;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public final class ApplicationLinks {
    private ApplicationLinks() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> isSystemLink() {
        return link -> link != null && link.isSystem();
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> isAtlassianLink() {
        return link -> link != null && ApplicationTypes.isAtlassian(link.getType());
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> withId(@Nonnull ApplicationId applicationId) {
        Objects.requireNonNull(applicationId, "applicationId");
        return link -> link != null && applicationId.equals((Object)link.getId());
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> withName(@Nonnull String name) {
        Objects.requireNonNull(name, "name");
        return link -> link != null && name.equals(link.getName());
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> withRpcUrl(@Nonnull URI rpcUrl) {
        Objects.requireNonNull(rpcUrl, "rpcUrl");
        return link -> link != null && rpcUrl.equals(link.getRpcUrl());
    }

    @Nonnull
    public static Predicate<ReadOnlyApplicationLink> withDisplayUrl(@Nonnull URI displayUrl) {
        Objects.requireNonNull(displayUrl, "displayUrl");
        return link -> link != null && displayUrl.equals(link.getDisplayUrl());
    }
}

