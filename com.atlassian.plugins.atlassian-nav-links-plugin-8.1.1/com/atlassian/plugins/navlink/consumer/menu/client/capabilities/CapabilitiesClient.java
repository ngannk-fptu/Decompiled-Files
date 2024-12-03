/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.plugins.navlink.consumer.menu.client.capabilities;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.google.common.util.concurrent.ListenableFuture;

public interface CapabilitiesClient {
    public ListenableFuture<ExpiringValue<RemoteApplicationWithCapabilities>> getCapabilities(ReadOnlyApplicationLink var1);
}

