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
import com.atlassian.confluence.plugins.mobile.service.ContextService;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalContextServiceImpl
extends ContextService {
    private final MobileSpaceService mobileSpaceService;

    @Autowired
    public GlobalContextServiceImpl(MobileSpaceService mobileSpaceService, MobileSpaceConverter mobileSpaceConverter, MobileAbstractPageConverter abstractPageConverter) {
        super(mobileSpaceConverter, abstractPageConverter);
        this.mobileSpaceService = mobileSpaceService;
    }

    @Override
    public LocationDto getPageCreateLocation(Context context) {
        return this.getPageCreateLocation(this.mobileSpaceService.getSuggestionSpace());
    }
}

