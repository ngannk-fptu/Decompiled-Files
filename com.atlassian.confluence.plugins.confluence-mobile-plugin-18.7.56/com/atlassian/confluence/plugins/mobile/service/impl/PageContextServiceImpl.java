/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.ContextService;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageContextServiceImpl
extends ContextService {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final MobileSpaceService mobileSpaceService;

    @Autowired
    public PageContextServiceImpl(@ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, MobileSpaceService mobileSpaceService, MobileSpaceConverter mobileSpaceConverter, MobileAbstractPageConverter abstractPageConverter) {
        super(mobileSpaceConverter, abstractPageConverter);
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.mobileSpaceService = mobileSpaceService;
    }

    @Override
    public LocationDto getPageCreateLocation(Context context) {
        Space currentSpace;
        Long pageId = context.getContentId();
        if (pageId == null || pageId <= 0L) {
            throw new BadRequestException("Required valid id value");
        }
        Page page = this.pageManager.getPage(pageId.longValue());
        if (page == null) {
            throw new BadRequestException("Cannot find page with id: " + pageId);
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        if (this.permissionManager.hasPermission((User)loginUser, Permission.VIEW, (Object)page) && (currentSpace = page.getSpace()) != null && this.permissionManager.hasCreatePermission((User)loginUser, (Object)currentSpace, Page.class)) {
            ArrayList ancestors = Lists.newArrayList((Iterable)page.getAncestors());
            ancestors.add(page);
            return this.getPageCreateLocation(currentSpace, ancestors);
        }
        return this.getPageCreateLocation(this.mobileSpaceService.getSuggestionSpace());
    }
}

