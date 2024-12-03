/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.oauth.Consumer
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.oauth.Consumer;
import javax.annotation.Nullable;

@Internal
public interface ServiceProviderStoreService {
    public void addConsumer(Consumer var1, ApplicationLink var2) throws IllegalStateException;

    public void removeConsumer(ApplicationLink var1);

    @Nullable
    public Consumer getConsumer(ApplicationLink var1);
}

