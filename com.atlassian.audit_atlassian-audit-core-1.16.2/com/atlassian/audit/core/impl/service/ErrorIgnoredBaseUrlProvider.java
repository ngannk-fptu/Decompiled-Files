/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.core.spi.service.BaseUrlProvider;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredBaseUrlProvider
implements BaseUrlProvider {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredBaseUrlProvider.class);
    private final BaseUrlProvider delegate;

    public ErrorIgnoredBaseUrlProvider(BaseUrlProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public String currentBaseUrl() {
        try {
            return this.delegate.currentBaseUrl();
        }
        catch (RuntimeException e) {
            log.error("Fail to get base url.", (Throwable)e);
            return null;
        }
    }
}

