/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId$ContentBlueprintIdWithId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId$ContentBlueprintIdWithKeys
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintSpec
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithKeys
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintSpec;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintPage;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentBlueprintInstanceAdapter {
    private final ContentService contentService;
    private final DraftsTransitionHelper draftsTransitionHelper;

    @Autowired
    public ContentBlueprintInstanceAdapter(@ComponentImport ContentService contentService, @ComponentImport DraftsTransitionHelper draftsTransitionHelper) {
        this.contentService = contentService;
        this.draftsTransitionHelper = draftsTransitionHelper;
    }

    CreateBlueprintPageEntity convertToEntity(ContentBlueprintInstance cbi) {
        Content contentIn = cbi.getContent();
        ContentBlueprintSpec spec = cbi.getContentBlueprintSpec();
        Space space = contentIn.getSpace();
        String spaceKey = space != null ? space.getKey() : null;
        String contentBlueprintId = this.getContentBlueprintUuid(spec.getBlueprintId());
        String moduleCompleteKey = this.getContentBlueprintKey(spec.getBlueprintId());
        String contentTemplateId = this.getContentTemplateId(spec.contentTemplateId());
        String contentTemplateKey = this.getContentTemplateKey(spec.contentTemplateId());
        String viewPermissionUsers = this.getViewPermissionUsers(spec.getContext());
        long parentPageId = this.getParentPageId(contentIn);
        long spaceId = space != null ? space.getId() : 0L;
        return new CreateBlueprintPageRestEntity(spaceKey, contentBlueprintId, contentTemplateId, contentTemplateKey, contentIn.getTitle(), viewPermissionUsers, parentPageId, moduleCompleteKey, spec.getContext(), spaceId);
    }

    ContentBlueprintInstance convertToInstance(ContentEntityObject draft, ContentBlueprintInstance contentBlueprintInstance, Expansion[] expansions) {
        Content createdContent = this.draftsTransitionHelper.isSharedDraftsFeatureEnabled(contentBlueprintInstance.getContent().getSpace().getKey()) ? (Content)this.contentService.find(expansions).withStatus(new ContentStatus[]{ContentStatus.DRAFT}).withId(draft.getContentId()).fetchOneOrNull() : Content.builder().id(ContentId.deserialise((String)draft.getIdAsString())).status(ContentStatus.DRAFT).title(draft.getTitle()).build();
        return ContentBlueprintInstance.builder().content(createdContent).contentBlueprintSpec(contentBlueprintInstance.getContentBlueprintSpec()).build();
    }

    ContentBlueprintInstance convertToInstance(BlueprintPage page, ContentBlueprintInstance contentBlueprintInstance, Expansion[] expansions) {
        Content createdContent = (Content)this.contentService.find(expansions).withId(page.getPage().getContentId()).fetchOneOrNull();
        return ContentBlueprintInstance.builder().content(createdContent).contentBlueprintSpec(contentBlueprintInstance.getContentBlueprintSpec()).build();
    }

    private long getParentPageId(Content contentIn) {
        ContentId parentPageId = contentIn.getParentId();
        return parentPageId.isSet() ? parentPageId.asLong() : 0L;
    }

    private String getViewPermissionUsers(Map<String, Object> context) {
        return (String)context.get("viewPermissionUsers");
    }

    private String getContentTemplateId(Optional<ContentTemplateId> contentTemplateId) {
        if (!contentTemplateId.isPresent()) {
            return null;
        }
        if (contentTemplateId.get() instanceof ContentTemplateId.ContentTemplateIdWithId) {
            long id = ((ContentTemplateId.ContentTemplateIdWithId)contentTemplateId.get()).getId();
            return String.valueOf(id);
        }
        return null;
    }

    private String getContentTemplateKey(Optional<ContentTemplateId> contentTemplateId) {
        if (!contentTemplateId.isPresent()) {
            return null;
        }
        if (contentTemplateId.get() instanceof ContentTemplateId.ContentTemplateIdWithKeys) {
            return ((ContentTemplateId.ContentTemplateIdWithKeys)contentTemplateId.get()).getModuleCompleteKey();
        }
        return null;
    }

    private String getContentBlueprintKey(ContentBlueprintId blueprintId) {
        if (blueprintId instanceof ContentBlueprintId.ContentBlueprintIdWithKeys) {
            return ((ContentBlueprintId.ContentBlueprintIdWithKeys)blueprintId).getModuleCompleteKey();
        }
        return null;
    }

    private String getContentBlueprintUuid(ContentBlueprintId blueprintId) {
        if (blueprintId instanceof ContentBlueprintId.ContentBlueprintIdWithId) {
            return ((ContentBlueprintId.ContentBlueprintIdWithId)blueprintId).getId();
        }
        return null;
    }
}

