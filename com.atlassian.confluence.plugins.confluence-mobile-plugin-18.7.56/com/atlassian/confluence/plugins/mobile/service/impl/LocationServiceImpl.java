/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.LocationService;
import com.atlassian.confluence.plugins.mobile.service.factory.ContextServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationServiceImpl
implements LocationService {
    private final ContextServiceFactory contextServiceFactory;

    @Autowired
    public LocationServiceImpl(ContextServiceFactory contextServiceFactory) {
        this.contextServiceFactory = contextServiceFactory;
    }

    @Override
    public LocationDto getPageCreateLocation(Context context) {
        return this.contextServiceFactory.getContextService(context.getType()).getPageCreateLocation(context);
    }
}

