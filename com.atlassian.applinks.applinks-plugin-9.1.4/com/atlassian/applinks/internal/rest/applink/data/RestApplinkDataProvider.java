/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RestApplinkDataProvider {
    @Nonnull
    public Set<String> getSupportedKeys();

    @Nullable
    public Object provide(@Nonnull String var1, @Nonnull ApplicationLink var2) throws ServiceException, IllegalArgumentException;
}

