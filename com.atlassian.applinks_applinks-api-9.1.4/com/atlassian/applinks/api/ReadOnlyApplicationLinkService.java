/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import javax.annotation.Nullable;

@ExperimentalApi
public interface ReadOnlyApplicationLinkService {
    public Iterable<ReadOnlyApplicationLink> getApplicationLinks();

    @Nullable
    public ReadOnlyApplicationLink getApplicationLink(ApplicationId var1);

    public Iterable<ReadOnlyApplicationLink> getApplicationLinks(Class<? extends ApplicationType> var1);

    @Nullable
    public ReadOnlyApplicationLink getPrimaryApplicationLink(Class<? extends ApplicationType> var1);
}

