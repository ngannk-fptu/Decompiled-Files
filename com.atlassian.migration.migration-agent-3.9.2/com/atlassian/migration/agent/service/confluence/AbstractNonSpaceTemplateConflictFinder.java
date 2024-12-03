/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 */
package com.atlassian.migration.agent.service.confluence;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.catalogue.model.CloudPageTemplate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractNonSpaceTemplateConflictFinder {
    protected final GlobalEntityType applicableType;
    private final AbstractNonSpaceTemplateConflictFinder nextConflictFinder;

    public AbstractNonSpaceTemplateConflictFinder(GlobalEntityType applicableType) {
        this(applicableType, null);
    }

    public AbstractNonSpaceTemplateConflictFinder(GlobalEntityType applicableType, AbstractNonSpaceTemplateConflictFinder nextConflictFinder) {
        this.applicableType = applicableType;
        this.nextConflictFinder = nextConflictFinder;
    }

    public abstract String getCloudTemplateConflictIdentifierKey(CloudPageTemplate var1);

    public abstract String getServerTemplateConflictIdentifierKey(PageTemplate var1);

    public abstract Predicate<PageTemplate> getServerTemplateFilter();

    public abstract Predicate<CloudPageTemplate> getCloudTemplateFilter();

    protected boolean isApplicable(GlobalEntityType givenConflictType) {
        return givenConflictType == this.applicableType || givenConflictType == GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES;
    }

    public void populateNonSpaceConflicts(List<CloudPageTemplate> cloudTemplates, List<PageTemplate> serverTemplates, NonSpaceTemplateConflictsInfo nonSpaceTemplateConflictsInfo, GlobalEntityType givenConflictType) {
        if (this.isApplicable(givenConflictType)) {
            Map<String, PageTemplate> serverTemplateKeyMap = serverTemplates.stream().filter(this.getServerTemplateFilter()).collect(Collectors.toMap(this::getServerTemplateConflictIdentifierKey, x -> x));
            nonSpaceTemplateConflictsInfo.setTotalNumOfServerTemplates(this.applicableType, serverTemplateKeyMap.size());
            Map<String, CloudPageTemplate> cloudTemplateKeyMap = cloudTemplates.stream().filter(this.getCloudTemplateFilter()).collect(Collectors.toMap(this::getCloudTemplateConflictIdentifierKey, x -> x));
            serverTemplateKeyMap.entrySet().forEach(serverPageTemplateEntry -> {
                CloudPageTemplate cloudPageTemplateWithSameKey = (CloudPageTemplate)cloudTemplateKeyMap.get(serverPageTemplateEntry.getKey());
                if (Objects.nonNull(cloudPageTemplateWithSameKey)) {
                    nonSpaceTemplateConflictsInfo.addConflict(NonSpaceTemplateConflictsInfo.Conflict.builder().serverTemplateName(((PageTemplate)serverPageTemplateEntry.getValue()).getName()).serverTemplateId(String.valueOf(((PageTemplate)serverPageTemplateEntry.getValue()).getId())).cloudTemplateName(cloudPageTemplateWithSameKey.getName()).cloudTemplateId(cloudPageTemplateWithSameKey.getTemplateId()).type(this.applicableType).templateModuleKey(((PageTemplate)serverPageTemplateEntry.getValue()).getModuleKey()).templatePluginKey(((PageTemplate)serverPageTemplateEntry.getValue()).getPluginKey()).build());
                }
            });
        }
        if (Objects.nonNull(this.nextConflictFinder)) {
            this.nextConflictFinder.populateNonSpaceConflicts(cloudTemplates, serverTemplates, nonSpaceTemplateConflictsInfo, givenConflictType);
        }
    }
}

