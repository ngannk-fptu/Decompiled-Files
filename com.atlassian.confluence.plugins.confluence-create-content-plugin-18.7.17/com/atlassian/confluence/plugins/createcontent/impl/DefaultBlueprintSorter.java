/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintSorter;
import com.atlassian.confluence.plugins.createcontent.services.PromotedBlueprintService;
import com.atlassian.confluence.plugins.createcontent.services.PromotedTemplateService;
import com.atlassian.confluence.plugins.createcontent.template.PageTemplateGrouper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBlueprintSorter
implements BlueprintSorter {
    private static final Comparator<CreateDialogWebItemEntity> WEB_ITEM_COMPARATOR = (o1, o2) -> {
        boolean isO2Blueprint;
        boolean isO1Blueprint = o1.getContentBlueprintId() != null;
        boolean bl = isO2Blueprint = o2.getContentBlueprintId() != null;
        if (isO1Blueprint && !isO2Blueprint) {
            return -1;
        }
        if (!isO1Blueprint && isO2Blueprint) {
            return 1;
        }
        return o1.getName().compareToIgnoreCase(o2.getName());
    };
    private final PageTemplateGrouper pageTemplateGrouper;
    private final PromotedBlueprintService promotedBlueprintService;
    private final PromotedTemplateService promotedTemplateService;

    @Autowired
    public DefaultBlueprintSorter(PageTemplateGrouper pageTemplateGrouper, PromotedBlueprintService promotedBlueprintService, PromotedTemplateService promotedTemplateService) {
        this.pageTemplateGrouper = pageTemplateGrouper;
        this.promotedBlueprintService = promotedBlueprintService;
        this.promotedTemplateService = promotedTemplateService;
    }

    @Override
    public List<CreateDialogWebItemEntity> sortContentBlueprintItems(@Nonnull Collection<CreateDialogWebItemEntity> items, @Nonnull Space space, @Nonnull ConfluenceUser user) {
        CreateDialogWebItemEntity blankPageItem = this.removeFromItems(items, "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-page");
        CreateDialogWebItemEntity blogPostItem = this.removeFromItems(items, "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blog-post");
        List<CreateDialogWebItemEntity> sortedItems = this.sortWebItems(items);
        if (blogPostItem != null) {
            sortedItems.add(0, blogPostItem);
        }
        if (blankPageItem != null) {
            sortedItems.add(0, blankPageItem);
        }
        return this.promoteItemsForSpace(space, sortedItems);
    }

    @Override
    public List<CreateDialogWebItemEntity> sortSpaceBlueprintItems(@Nonnull List<CreateDialogWebItemEntity> items, @Nonnull ConfluenceUser user) {
        CreateDialogWebItemEntity blankSpaceItem = this.removeFromItems(items, "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-space-item");
        CreateDialogWebItemEntity personalSpaceItem = this.removeFromItems(items, "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-personal-space-item");
        List<CreateDialogWebItemEntity> sortedItems = this.sortWebItems(items);
        ArrayList enabledSystemBPs = Lists.newArrayList();
        if (blankSpaceItem != null) {
            enabledSystemBPs.add(blankSpaceItem);
        }
        if (personalSpaceItem != null) {
            enabledSystemBPs.add(personalSpaceItem);
        }
        if (enabledSystemBPs.size() > 0) {
            sortedItems.addAll(0, enabledSystemBPs);
        }
        return sortedItems;
    }

    private CreateDialogWebItemEntity removeFromItems(@Nonnull Collection<CreateDialogWebItemEntity> items, @Nonnull String itemModuleKey) {
        for (CreateDialogWebItemEntity item : items) {
            if (!itemModuleKey.equals(item.getItemModuleCompleteKey())) continue;
            items.remove(item);
            return item;
        }
        return null;
    }

    private List<CreateDialogWebItemEntity> sortWebItems(@Nonnull Collection<CreateDialogWebItemEntity> pluginItems) {
        ArrayList sortedItems = Lists.newArrayList(pluginItems);
        sortedItems.sort(WEB_ITEM_COMPARATOR);
        return sortedItems;
    }

    private List<CreateDialogWebItemEntity> promoteItemsForSpace(@Nonnull Space space, @Nonnull List<CreateDialogWebItemEntity> webItemsEntities) {
        Collection<ContentBlueprint> blueprintsInSpace = this.pageTemplateGrouper.getSpaceContentBlueprints(space);
        Collection<ContentBlueprint> promotedBps = this.promotedBlueprintService.getPromotedBlueprints(blueprintsInSpace, space);
        Collection<Long> promotedTemplates = this.promotedTemplateService.getPromotedTemplates(space);
        if (promotedBps.isEmpty() && promotedTemplates.isEmpty()) {
            return webItemsEntities;
        }
        Collection<UUID> promotedBpsUuids = this.convertBlueprintsToUuid(promotedBps);
        for (int j = webItemsEntities.size() - 1; j >= 0; --j) {
            boolean isPromotedTemplate;
            CreateDialogWebItemEntity webItem = webItemsEntities.get(j);
            UUID contentBlueprintId = webItem.getContentBlueprintId();
            String templateId = webItem.getTemplateId();
            boolean isPromotedBlueprint = contentBlueprintId != null && promotedBpsUuids.contains(contentBlueprintId);
            boolean bl = isPromotedTemplate = templateId != null && promotedTemplates.contains(Long.parseLong(templateId));
            if (webItem.isPromoted() || !isPromotedBlueprint && !isPromotedTemplate) continue;
            webItem.setPromoted(true);
            webItemsEntities.remove(j++);
            webItemsEntities.add(0, webItem);
        }
        return webItemsEntities;
    }

    private Collection<UUID> convertBlueprintsToUuid(@Nonnull Collection<ContentBlueprint> blueprints) {
        ArrayList ids = Lists.newArrayList();
        for (ContentBlueprint blueprint : blueprints) {
            ids.add(blueprint.getId());
        }
        return ids;
    }
}

