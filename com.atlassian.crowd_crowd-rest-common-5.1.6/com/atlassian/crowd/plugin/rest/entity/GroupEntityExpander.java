/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
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
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.crowd.plugin.rest.util.EntityExpansionUtil;
import com.atlassian.crowd.plugin.rest.util.GroupEntityUtil;
import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public class GroupEntityExpander
implements EntityExpander<GroupEntity> {
    public static final String ATTRIBUTES_FIELD_NAME = "attributes";
    private final ApplicationService applicationService;
    private final ApplicationManager applicationManager;

    public GroupEntityExpander(ApplicationService applicationService, ApplicationManager applicationManager) {
        this.applicationService = applicationService;
        this.applicationManager = applicationManager;
    }

    public GroupEntity expand(ExpandContext<GroupEntity> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
        GroupEntity expandedGroupEntity;
        Application application;
        GroupEntity groupEntity = (GroupEntity)context.getEntity();
        if (groupEntity.isExpanded()) {
            return groupEntity;
        }
        try {
            application = this.applicationManager.findByName(groupEntity.getApplicationName());
        }
        catch (ApplicationNotFoundException e) {
            throw new RuntimeException(e);
        }
        boolean expandAttributes = EntityExpansionUtil.shouldExpandField(GroupEntity.class, ATTRIBUTES_FIELD_NAME, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()));
        try {
            expandedGroupEntity = GroupEntityUtil.expandGroup(this.applicationService, application, groupEntity, expandAttributes);
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!context.getEntityExpandParameter().isEmpty()) {
            entityCrawler.crawl((Object)expandedGroupEntity, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()), expanderResolver);
        }
        return expandedGroupEntity;
    }
}

