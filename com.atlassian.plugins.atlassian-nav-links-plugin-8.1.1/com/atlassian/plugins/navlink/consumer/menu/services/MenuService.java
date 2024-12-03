/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MenuService {
    @Nonnull
    public Iterable<NavigationLink> getMenuItems(@Nonnull String var1, String var2, Locale var3);

    @Nonnull
    public Iterable<NavigationLink> getAppSwitcherItems(String var1);

    public boolean isAppSwitcherVisibleForUser(@Nullable String var1);

    public void setUserData(String var1, String var2);

    public String getUserData(String var1);
}

