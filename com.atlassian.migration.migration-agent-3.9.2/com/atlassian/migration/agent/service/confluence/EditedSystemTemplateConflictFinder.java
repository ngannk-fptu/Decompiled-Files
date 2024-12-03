/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.migration.agent.service.confluence;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.catalogue.model.CloudPageTemplate;
import com.atlassian.migration.agent.service.confluence.AbstractNonSpaceTemplateConflictFinder;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class EditedSystemTemplateConflictFinder
extends AbstractNonSpaceTemplateConflictFinder {
    private static final String SYSTEM_TEMPLATE_PLUGIN_AND_MODULE_KEY_COMBINATION_FORMAT = "%s # %s";
    private static final Set<String> ATLASSIAN_SYSTEM_TEMPLATE_PLUGIN_KEYS = ImmutableSet.of((Object)"com.atlassian.confluence.plugins.confluence-default-space-content-plugin", (Object)"com.atlassian.confluence.plugins.system-templates");
    private static final Set<String> ATLASSIAN_SYSTEM_TEMPLATE_MODULE_KEYS = ImmutableSet.of((Object)"spacecontent-personal-custom-sample-page", (Object)"welcome-message", (Object)"spacecontent-global", (Object)"spacecontent-personal");
    private static final Predicate<PageTemplate> SERVER_PAGE_TEMPLATE_PREDICATE = serverPageTemplate -> Objects.nonNull(serverPageTemplate.getPluginKey()) && ATLASSIAN_SYSTEM_TEMPLATE_PLUGIN_KEYS.contains(serverPageTemplate.getPluginKey()) && Objects.nonNull(serverPageTemplate.getModuleKey()) && ATLASSIAN_SYSTEM_TEMPLATE_MODULE_KEYS.contains(serverPageTemplate.getModuleKey());
    private static final Predicate<CloudPageTemplate> CLOUD_PAGE_TEMPLATE_PREDICATE = cloudPageTemplate -> Objects.nonNull(cloudPageTemplate.getOriginalTemplate()) && Objects.nonNull(cloudPageTemplate.getOriginalTemplate().getPluginKey()) && ATLASSIAN_SYSTEM_TEMPLATE_PLUGIN_KEYS.contains(cloudPageTemplate.getOriginalTemplate().getPluginKey()) && Objects.nonNull(cloudPageTemplate.getOriginalTemplate().getModuleKey()) && ATLASSIAN_SYSTEM_TEMPLATE_MODULE_KEYS.contains(cloudPageTemplate.getOriginalTemplate().getModuleKey());

    public EditedSystemTemplateConflictFinder(GlobalEntityType conflictTypeApplicable) {
        super(conflictTypeApplicable);
    }

    public EditedSystemTemplateConflictFinder(GlobalEntityType conflictTypeApplicable, AbstractNonSpaceTemplateConflictFinder nextConflictFinder) {
        super(conflictTypeApplicable, nextConflictFinder);
    }

    @Override
    public String getCloudTemplateConflictIdentifierKey(CloudPageTemplate cloudPageTemplate) {
        return String.format(SYSTEM_TEMPLATE_PLUGIN_AND_MODULE_KEY_COMBINATION_FORMAT, cloudPageTemplate.getOriginalTemplate().getPluginKey(), cloudPageTemplate.getOriginalTemplate().getModuleKey());
    }

    @Override
    public String getServerTemplateConflictIdentifierKey(PageTemplate serverPageTemplate) {
        return String.format(SYSTEM_TEMPLATE_PLUGIN_AND_MODULE_KEY_COMBINATION_FORMAT, serverPageTemplate.getPluginKey(), serverPageTemplate.getModuleKey());
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

