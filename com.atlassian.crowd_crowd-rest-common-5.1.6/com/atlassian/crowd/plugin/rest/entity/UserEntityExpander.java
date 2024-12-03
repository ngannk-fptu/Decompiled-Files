/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.plugins.rest.common.expand.EntityCrawler
 *  com.atlassian.plugins.rest.common.expand.EntityExpander
 *  com.atlassian.plugins.rest.common.expand.ExpandContext
 *  com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.plugin.rest.entity.UserEntity;
import com.atlassian.crowd.plugin.rest.util.EntityExpansionUtil;
import com.atlassian.crowd.plugin.rest.util.UserEntityUtil;
import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public class UserEntityExpander
implements EntityExpander<UserEntity> {
    private final ApplicationService applicationService;
    private final ApplicationManager applicationManager;

    public UserEntityExpander(ApplicationService applicationService, ApplicationManager applicationManager) {
        this.applicationService = applicationService;
        this.applicationManager = applicationManager;
    }

    public UserEntity expand(ExpandContext<UserEntity> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
        UserEntity expandedUserEntity;
        Application application;
        UserEntity userEntity = (UserEntity)context.getEntity();
        if (userEntity.isExpanded()) {
            return userEntity;
        }
        try {
            application = this.applicationManager.findByName(userEntity.getApplicationName());
        }
        catch (ApplicationNotFoundException e) {
            throw new RuntimeException(e);
        }
        boolean expandAttributes = EntityExpansionUtil.shouldExpandField(UserEntity.class, "attributes", context.getEntityExpandParameter().getExpandParameter(context.getExpandable()));
        try {
            expandedUserEntity = UserEntityUtil.expandUser(this.applicationService, application, userEntity, expandAttributes);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!context.getEntityExpandParameter().isEmpty()) {
            entityCrawler.crawl((Object)expandedUserEntity, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()), expanderResolver);
        }
        return expandedUserEntity;
    }
}

