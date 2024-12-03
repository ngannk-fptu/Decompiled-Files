/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.producer.navigation.services;

import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import java.util.Set;
import javax.annotation.Nonnull;

public interface LocalNavigationLinks {
    @Nonnull
    public Set<RawNavigationLink> all();
}

