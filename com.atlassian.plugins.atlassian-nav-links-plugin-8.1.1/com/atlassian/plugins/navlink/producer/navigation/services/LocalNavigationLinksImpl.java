/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepositoryService;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalNavigationLinksImpl
implements LocalNavigationLinks {
    private final Logger logger = LoggerFactory.getLogger(LocalNavigationLinksImpl.class);
    private final NavigationLinkRepositoryService navigationLinkRepositoryService;

    public LocalNavigationLinksImpl(NavigationLinkRepositoryService navigationLinkRepositoryService) {
        this.navigationLinkRepositoryService = navigationLinkRepositoryService;
    }

    @Override
    @Nonnull
    public Set<RawNavigationLink> all() {
        List<NavigationLinkRepository> navigationLinkRepositoryList = this.navigationLinkRepositoryService.getAllNavigationLinkRepositories();
        List navigationLinkEntityList = Lists.transform(navigationLinkRepositoryList, this.getAllLocalNavigationLinks());
        return Sets.newHashSet((Iterable)Iterables.concat((Iterable)navigationLinkEntityList));
    }

    @Nonnull
    private Function<NavigationLinkRepository, Iterable<RawNavigationLink>> getAllLocalNavigationLinks() {
        return new Function<NavigationLinkRepository, Iterable<RawNavigationLink>>(){

            public Iterable<RawNavigationLink> apply(@Nullable NavigationLinkRepository input) {
                try {
                    if (input != null) {
                        return input.all();
                    }
                }
                catch (RuntimeException e) {
                    LocalNavigationLinksImpl.this.logger.warn("Failed to gather navigation links", (Throwable)e);
                }
                return Collections.emptyList();
            }
        };
    }
}

