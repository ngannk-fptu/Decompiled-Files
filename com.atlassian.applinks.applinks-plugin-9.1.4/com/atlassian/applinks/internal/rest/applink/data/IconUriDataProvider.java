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
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.AbstractSingleKeyRestApplinkDataProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IconUriDataProvider
extends AbstractSingleKeyRestApplinkDataProvider {
    public static final String ICON_URI = "iconUri";

    public IconUriDataProvider() {
        super(ICON_URI);
    }

    @Override
    @Nullable
    protected Object doProvide(@Nonnull ApplicationLink applink) throws ServiceException {
        return IconUriResolver.resolveIconUri(applink.getType());
    }
}

