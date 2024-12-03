/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import java.util.List;
import javax.annotation.Nonnull;

public interface NavigationLinkRepositoryService {
    @Nonnull
    public List<NavigationLinkRepository> getAllNavigationLinkRepositories();
}

