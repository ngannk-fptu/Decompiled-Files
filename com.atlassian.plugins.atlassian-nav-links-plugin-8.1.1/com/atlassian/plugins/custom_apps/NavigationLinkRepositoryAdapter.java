/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.CustomAppService;
import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinkComparator;
import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLinkBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NavigationLinkRepositoryAdapter
implements NavigationLinkRepository {
    private static final String NAVIGATION_LINK_KEY = "custom-apps";
    private final CustomAppService customAppService;
    private final ApplicationTypeService applicationTypeService;

    public NavigationLinkRepositoryAdapter(@Nonnull CustomAppService customAppService, @Nonnull ApplicationTypeService applicationTypeService) {
        this.customAppService = Objects.requireNonNull(customAppService);
        this.applicationTypeService = Objects.requireNonNull(applicationTypeService);
    }

    @Override
    @Nonnull
    public Iterable<RawNavigationLink> all() {
        return this.matching((com.google.common.base.Predicate<RawNavigationLink>)null);
    }

    @Override
    @Deprecated
    @Nonnull
    public Iterable<RawNavigationLink> matching(@Nullable com.google.common.base.Predicate<RawNavigationLink> predicate) {
        return this.matching((Predicate<RawNavigationLink>)predicate);
    }

    @Override
    @Nonnull
    public List<RawNavigationLink> matching(@Nullable Predicate<RawNavigationLink> predicate) {
        return Collections.unmodifiableList(this.customAppService.getCustomApps().stream().map(customApp -> customApp != null ? this.createLocalNavigationLink((CustomApp)customApp) : null).filter(predicate != null ? predicate : o -> true).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Nonnull
    private RawNavigationLink createLocalNavigationLink(@Nonnull CustomApp customApp) {
        return ((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)new RawNavigationLinkBuilder().key(NAVIGATION_LINK_KEY)).labelKey(customApp.getDisplayName()).href(customApp.getUrl())).source(LinkSource.localDefault())).applicationType(this.applicationTypeService.get())).weight(NavigationLinkComparator.Weights.MAX.value())).build();
    }
}

