/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.ContextService;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogpostContextServiceImpl
extends ContextService {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final MobileSpaceService mobileSpaceService;

    @Autowired
    public BlogpostContextServiceImpl(@ComponentImport PageManager pageManager, MobileSpaceService mobileSpaceService, MobileSpaceConverter mobileSpaceConverter, MobileAbstractPageConverter abstractPageConverter, @ComponentImport PermissionManager permissionManager) {
        super(mobileSpaceConverter, abstractPageConverter);
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.mobileSpaceService = mobileSpaceService;
    }

    @Override
    public LocationDto getPageCreateLocation(Context context) {
        Long blogPostId = context.getContentId();
        if (blogPostId == null || blogPostId <= 0L) {
            throw new BadRequestException("Required valid id value");
        }
        BlogPost blogPost = this.pageManager.getBlogPost(blogPostId.longValue());
        if (blogPost == null) {
            throw new BadRequestException("Cannot find blogpost with id: " + blogPostId);
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        boolean hasCreatePermission = this.permissionManager.hasCreatePermission((User)loginUser, (Object)blogPost.getSpace(), Page.class);
        return this.getPageCreateLocation(hasCreatePermission ? blogPost.getSpace() : this.mobileSpaceService.getSuggestionSpace());
    }
}

