/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.TypeId
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.producer.contentlinks.services;

import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.navlink.consumer.projectshortcuts.rest.UnauthenticatedRemoteApplication;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.ContentLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import io.atlassian.fugue.Pair;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ContentLinksService {
    @Nonnull
    public List<ContentLinkModuleDescriptor> getAllLocalContentLinks(@Nonnull Map<String, Object> var1, @Nullable TypeId var2);

    @Nonnull
    public List<ContentLinkEntity> getAllRemoteContentLinks(@Nonnull String var1, @Nonnull TypeId var2);

    @Nonnull
    public Pair<Iterable<ContentLinkEntity>, Iterable<UnauthenticatedRemoteApplication>> getAllRemoteContentLinksAndUnauthedApps(@Nonnull String var1, @Nonnull TypeId var2);
}

