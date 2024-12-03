/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ContextService {
    private final MobileSpaceConverter mobileSpaceConverter;
    private final MobileAbstractPageConverter abstractPageConverter;

    public ContextService(MobileSpaceConverter mobileSpaceConverter, MobileAbstractPageConverter abstractPageConverter) {
        this.mobileSpaceConverter = mobileSpaceConverter;
        this.abstractPageConverter = abstractPageConverter;
    }

    public abstract LocationDto getPageCreateLocation(Context var1);

    protected LocationDto getPageCreateLocation(Space locationSpace) {
        this.validateSpace(locationSpace);
        return this.getPageCreateLocation(locationSpace, locationSpace.getHomePage() != null ? Lists.newArrayList((Object[])new Page[]{locationSpace.getHomePage()}) : Collections.EMPTY_LIST);
    }

    protected LocationDto getPageCreateLocation(Space locationSpace, List<Page> ancestors) {
        this.validateSpace(locationSpace);
        return new LocationDto(this.mobileSpaceConverter.to(locationSpace), ancestors.stream().map(page -> this.abstractPageConverter.to((ContentEntityObject)page)).collect(Collectors.toList()));
    }

    private void validateSpace(Space locationSpace) {
        if (locationSpace == null) {
            throw new PermissionException("You don't have create permission");
        }
    }
}

