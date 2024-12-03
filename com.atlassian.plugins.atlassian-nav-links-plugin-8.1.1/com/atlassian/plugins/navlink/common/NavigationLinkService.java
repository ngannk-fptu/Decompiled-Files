/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.common;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.google.common.base.Predicate;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;

public interface NavigationLinkService {
    @Nonnull
    public Set<NavigationLink> all(@Nonnull Locale var1);

    @Deprecated
    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale var1, @Nonnull Predicate<NavigationLink> var2);

    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale var1, @Nonnull java.util.function.Predicate<NavigationLink> var2);
}

