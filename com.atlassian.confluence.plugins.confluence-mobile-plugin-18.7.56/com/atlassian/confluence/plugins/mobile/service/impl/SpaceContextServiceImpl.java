/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.ContextService;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceContextServiceImpl
extends ContextService {
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final MobileSpaceService mobileSpaceService;

    @Autowired
    public SpaceContextServiceImpl(@ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, MobileSpaceService mobileSpaceService, MobileSpaceConverter mobileSpaceConverter, MobileAbstractPageConverter abstractPageConverter) {
        super(mobileSpaceConverter, abstractPageConverter);
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.mobileSpaceService = mobileSpaceService;
    }

    @Override
    public LocationDto getPageCreateLocation(Context context) {
        if (StringUtils.isBlank((CharSequence)context.getSpaceKey())) {
            throw new BadRequestException("Required space key value");
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(context.getSpaceKey());
        boolean hasCreatPermission = space != null && this.permissionManager.hasCreatePermission((User)loginUser, (Object)space, Page.class);
        Space locationSpace = hasCreatPermission ? space : this.mobileSpaceService.getSuggestionSpace();
        return this.getPageCreateLocation(locationSpace);
    }
}

