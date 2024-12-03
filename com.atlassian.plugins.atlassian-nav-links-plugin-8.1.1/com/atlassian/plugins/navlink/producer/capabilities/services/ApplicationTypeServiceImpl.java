/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.capabilities.services;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationTypeServiceImpl
implements ApplicationTypeService {
    private final Logger logger = LoggerFactory.getLogger(ApplicationTypeServiceImpl.class);
    private final InternalHostApplication internalHostApplication;

    public ApplicationTypeServiceImpl(InternalHostApplication internalHostApplication) {
        this.internalHostApplication = internalHostApplication;
    }

    @Override
    public String get() {
        ApplicationType type = this.internalHostApplication.getType();
        try {
            return TypeId.getTypeId((ApplicationType)type).get();
        }
        catch (RuntimeException e) {
            this.logger.warn("Failed to retrieve application type; returning default '{}'. Error message: {}", new Object[]{DEFAULT_APPLICATION_TYPE, e.getMessage()});
            this.logger.debug("Stacktrace:", (Throwable)e);
            return DEFAULT_APPLICATION_TYPE;
        }
    }
}

