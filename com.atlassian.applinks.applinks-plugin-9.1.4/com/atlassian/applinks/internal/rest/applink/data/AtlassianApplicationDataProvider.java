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
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.AbstractSingleKeyRestApplinkDataProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AtlassianApplicationDataProvider
extends AbstractSingleKeyRestApplinkDataProvider {
    public static final String ATLASSIAN = "atlassian";

    public AtlassianApplicationDataProvider() {
        super(ATLASSIAN);
    }

    @Override
    @Nullable
    public Object doProvide(@Nonnull ApplicationLink applink) throws ServiceException {
        return ApplicationTypes.isAtlassian(applink.getType());
    }
}

