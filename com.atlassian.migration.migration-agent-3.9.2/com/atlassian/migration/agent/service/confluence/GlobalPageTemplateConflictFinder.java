/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 */
package com.atlassian.migration.agent.service.confluence;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.catalogue.model.CloudPageTemplate;
import com.atlassian.migration.agent.service.confluence.AbstractNonSpaceTemplateConflictFinder;
import java.util.Objects;
import java.util.function.Predicate;

public class GlobalPageTemplateConflictFinder
extends AbstractNonSpaceTemplateConflictFinder {
    private static final Predicate<PageTemplate> SERVER_PAGE_TEMPLATE_PREDICATE = serverPageTemplate -> Objects.isNull(serverPageTemplate.getPluginKey()) && Objects.isNull(serverPageTemplate.getModuleKey());
    private static final Predicate<CloudPageTemplate> CLOUD_PAGE_TEMPLATE_PREDICATE = cloudPageTemplate -> Objects.isNull(cloudPageTemplate.getOriginalTemplate());

    public GlobalPageTemplateConflictFinder(GlobalEntityType conflictTypeApplicable) {
        super(conflictTypeApplicable);
    }

    public GlobalPageTemplateConflictFinder(GlobalEntityType conflictTypeApplicable, AbstractNonSpaceTemplateConflictFinder nextConflictFinder) {
        super(conflictTypeApplicable, nextConflictFinder);
    }

    @Override
    public String getCloudTemplateConflictIdentifierKey(CloudPageTemplate cloudPageTemplate) {
        return cloudPageTemplate.getName();
    }

    @Override
    public String getServerTemplateConflictIdentifierKey(PageTemplate serverPageTemplate) {
        return serverPageTemplate.getName();
    }

    @Override
    public Predicate<PageTemplate> getServerTemplateFilter() {
        return SERVER_PAGE_TEMPLATE_PREDICATE;
    }

    @Override
    public Predicate<CloudPageTemplate> getCloudTemplateFilter() {
        return CLOUD_PAGE_TEMPLATE_PREDICATE;
    }
}

