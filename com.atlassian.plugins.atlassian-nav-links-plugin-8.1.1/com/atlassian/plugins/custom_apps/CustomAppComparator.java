/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinkComparator;
import java.util.Comparator;

public class CustomAppComparator
implements Comparator<CustomApp> {
    public static final CustomAppComparator INSTANCE = new CustomAppComparator();

    @Override
    public int compare(CustomApp o1, CustomApp o2) {
        return NavigationLinkComparator.INSTANCE.compare(o1.getNavigationLink(), o2.getNavigationLink());
    }
}

