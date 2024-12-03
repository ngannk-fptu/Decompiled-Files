/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.core.spi.service.BaseUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.core.spi.service.BaseUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import javax.annotation.Nonnull;

public class ConfluenceBaseUrlProvider
implements BaseUrlProvider {
    private final ApplicationProperties applicationProperties;

    public ConfluenceBaseUrlProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Nonnull
    public String currentBaseUrl() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
    }
}

