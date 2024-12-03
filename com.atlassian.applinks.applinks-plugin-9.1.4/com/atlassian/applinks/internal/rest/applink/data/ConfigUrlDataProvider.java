/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.applinks.internal.rest.applink.data.AbstractSingleKeyRestApplinkDataProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigUrlDataProvider
extends AbstractSingleKeyRestApplinkDataProvider {
    public static final String CONFIG_URL = "configUrl";

    public ConfigUrlDataProvider() {
        super(CONFIG_URL);
    }

    @Override
    @Nullable
    public Object doProvide(@Nonnull ApplicationLink applink) throws ServiceException {
        if (ApplicationTypes.isAtlassian(applink.getType())) {
            return this.getAtlassianConfigUrl(applink);
        }
        return applink.getDisplayUrl();
    }

    private Object getAtlassianConfigUrl(ApplicationLink applink) {
        if (applink.getType() instanceof ConfluenceApplicationType) {
            return Uris.uncheckedConcatenate(applink.getDisplayUrl(), "/admin/listapplicationlinks.action");
        }
        return Uris.uncheckedConcatenate(applink.getDisplayUrl(), "/plugins/servlet/applinks/listApplicationLinks");
    }
}

