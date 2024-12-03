/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.plugins.navlink.consumer.menu.client.navigation;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.navigation.ApplicationNavigationLinks;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Locale;

public interface NavigationClient {
    public ListenableFuture<ExpiringValue<ApplicationNavigationLinks>> getNavigationLinks(RemoteApplicationWithCapabilities var1, Locale var2);
}

