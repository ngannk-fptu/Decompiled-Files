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
import com.atlassian.applinks.internal.rest.applink.data.AbstractRestApplinkDataProvider;
import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProvider;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractSingleKeyRestApplinkDataProvider
extends AbstractRestApplinkDataProvider
implements RestApplinkDataProvider {
    protected AbstractSingleKeyRestApplinkDataProvider(@Nonnull String supportedKey) {
        super(Collections.singleton(Objects.requireNonNull(supportedKey, "supportedKey")));
    }

    @Override
    @Nullable
    public Object provide(@Nonnull String key, @Nonnull ApplicationLink applink) throws ServiceException, IllegalArgumentException {
        if (!this.supportedKeys.contains(key)) {
            throw new IllegalArgumentException(String.format("Unsupported key: '%s'", key));
        }
        return this.doProvide(applink);
    }

    @Nullable
    protected abstract Object doProvide(@Nonnull ApplicationLink var1) throws ServiceException;
}

