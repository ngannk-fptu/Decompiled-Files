/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.google.common.base.Predicate;
import java.util.List;

public interface NavigationLinkRepository {
    @Deprecated
    public Iterable<RawNavigationLink> matching(Predicate<RawNavigationLink> var1);

    public List<RawNavigationLink> matching(java.util.function.Predicate<RawNavigationLink> var1);

    public Iterable<RawNavigationLink> all();
}

