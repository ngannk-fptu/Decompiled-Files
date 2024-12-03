/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintSanitiserManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultContentBlueprintSanitiserManager
implements ContentBlueprintSanitiserManager {
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private static final String CONTEXT_SPACE_KEY = "spaceKey";
    private static final String CONTEXT_SPACE_ID = "spaceId";
    private static final Logger log = LoggerFactory.getLogger(DefaultContentBlueprintSanitiserManager.class);

    @Autowired
    public DefaultContentBlueprintSanitiserManager(@ComponentImport UserAccessor userAccessor, @ComponentImport SpaceManager spaceManager, ContentBlueprintManager contentBlueprintManager) {
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public CreateBlueprintPageEntity sanitise(CreateBlueprintPageEntity entity) {
        return new CreateBlueprintPageRestEntity.Builder(entity).spaceId(entity.getSpaceId() == 0L ? this.mapSpaceKeyToSpaceId(entity.getSpaceKey()) : entity.getSpaceId()).spaceKey("").context(this.cleanupContext(entity.getContext(), entity.getContentBlueprintId())).viewPermissionsUsers(this.cleanupUserIdentifiers(entity.getViewPermissionsUsers())).build();
    }

    @Override
    public CreateBlueprintPageEntity unsanitise(CreateBlueprintPageEntity sanitisedEntity) {
        String sanitisedSpaceKey = sanitisedEntity.getSpaceKey();
        String spaceKey = StringUtils.isEmpty((CharSequence)sanitisedSpaceKey) ? this.mapSpaceIdToSpaceKey(sanitisedEntity.getSpaceId()) : sanitisedSpaceKey;
        return new CreateBlueprintPageRestEntity.Builder(sanitisedEntity).spaceKey(spaceKey).spaceId(sanitisedEntity.getSpaceId()).context(this.unsanitiseContext(sanitisedEntity.getContext())).build();
    }

    @VisibleForTesting
    Map<String, Object> cleanupContext(Map<String, Object> context, String contentBlueprintId) {
        Object viewPermissionsUsersObject;
        if (context.containsKey("viewPermissionsUsers") && (viewPermissionsUsersObject = context.get("viewPermissionsUsers")) instanceof String) {
            context.put("viewPermissionsUsers", this.cleanupUserIdentifiers((String)viewPermissionsUsersObject));
        }
        if (context.containsKey(CONTEXT_SPACE_KEY)) {
            Object spaceKey = context.get(CONTEXT_SPACE_KEY);
            if (spaceKey instanceof String) {
                context.remove(CONTEXT_SPACE_KEY);
                context.put(CONTEXT_SPACE_ID, "" + this.mapSpaceKeyToSpaceId((String)spaceKey));
            } else {
                log.warn("The spaceKey found in the context '{}' was not the appropriate type (String). Skipping...", spaceKey);
            }
        }
        if (contentBlueprintId != null) {
            UUID uuid = UUID.fromString(contentBlueprintId);
            ContentBlueprint blueprint = (ContentBlueprint)this.contentBlueprintManager.getById(uuid);
            if (blueprint != null) {
                log.debug("Found blueprint {}", (Object)blueprint.getIndexKey());
                switch (blueprint.getIndexKey()) {
                    case "daci-decision": {
                        context = this.sanitiseStorageFormatContextEntries(context, "daciDecisionTemplateXML");
                        return this.sanitiseUserIdentifierContextEntries(context, "driver", "approver");
                    }
                    case "health-monitor": {
                        context = this.sanitiseStorageFormatContextEntries(context, "projectMonitorTemplateXML", "leadershipMonitorTemplateXML", "serviceMonitorTemplateXML");
                        return this.sanitiseUserIdentifierContextEntries(context, "sponsor", "owner");
                    }
                    case "requirements": {
                        return this.sanitiseStorageFormatContextEntries(context, "documentOwner");
                    }
                    case "retrospective": {
                        context = this.sanitiseStorageFormatContextEntries(context, "participants", "participantList", "participantTable");
                        return this.sanitiseUserIdentifierContextEntries(context, "retro-participants");
                    }
                    case "decisions": {
                        context = this.sanitiseStorageFormatContextEntries(context, "owner", "mentions");
                        return this.sanitiseUserIdentifierContextEntries(context, "stakeholders");
                    }
                    case "meeting-notes": {
                        return this.sanitiseStorageFormatContextEntries(context, "documentOwner");
                    }
                    case "kb-how-to-article": {
                        return this.sanitiseStorageFormatContextEntries(context, "contentbylabelMacro", "jiraIssuesMacro");
                    }
                    case "kb-troubleshooting-article": {
                        return this.sanitiseStorageFormatContextEntries(context, "contentbylabelMacro", "jiraIssuesMacro");
                    }
                    case "project-poster": {
                        return this.sanitiseStorageFormatContextEntries(context, "templateXML");
                    }
                    case "experience-canvas": {
                        return this.sanitiseStorageFormatContextEntries(context, "templateXML");
                    }
                }
                log.debug("This blueprint was not recognised as needing context cleanup. Skipping...");
            } else {
                log.warn("Unable to identify the source blueprint for this entity. The entity's context will not be sanitised.");
            }
        }
        return context;
    }

    @VisibleForTesting
    Map<String, Object> unsanitiseContext(Map<String, Object> sanitisedContext) {
        if (sanitisedContext.containsKey(CONTEXT_SPACE_ID)) {
            Object spaceIdObject = sanitisedContext.get(CONTEXT_SPACE_ID);
            try {
                if (spaceIdObject instanceof String) {
                    sanitisedContext.remove(CONTEXT_SPACE_ID);
                    sanitisedContext.put(CONTEXT_SPACE_KEY, this.mapSpaceIdToSpaceKey(Long.parseLong((String)spaceIdObject)));
                } else {
                    log.warn("The spaceId found in the context '{}' was not the appropriate type (String). Skipping...", spaceIdObject);
                }
            }
            catch (NumberFormatException e) {
                log.error("Could not convert the stored spaceId entry '{}' back into a spaceKey", spaceIdObject);
            }
        }
        return sanitisedContext;
    }

    private Map<String, Object> sanitiseUserIdentifierContextEntries(Map<String, Object> context, String ... keys) {
        for (String key : keys) {
            if (!context.containsKey(key)) continue;
            context.put(key, this.cleanupUserIdentifiers((String)context.get(key)));
        }
        return context;
    }

    private Map<String, Object> sanitiseStorageFormatContextEntries(Map<String, Object> context, String ... keys) {
        for (String key : keys) {
            if (!context.containsKey(key)) continue;
            context.put(key, "");
        }
        return context;
    }

    @VisibleForTesting
    String cleanupUserIdentifiers(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return inputString;
        }
        return Arrays.stream(GeneralUtil.splitCommaDelimitedString((String)inputString)).map(this::mapUserIdentifierToUserKey).filter(Objects::nonNull).collect(Collectors.joining(","));
    }

    private String mapUserIdentifierToUserKey(String userIdentifier) {
        if (this.userAccessor.getUserByKey(new UserKey(userIdentifier)) != null) {
            return userIdentifier;
        }
        ConfluenceUser potentialUser = this.userAccessor.getUserByName(userIdentifier);
        if (potentialUser != null) {
            return potentialUser.getKey().getStringValue();
        }
        return null;
    }

    private long mapSpaceKeyToSpaceId(String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        return space != null ? space.getId() : 0L;
    }

    private String mapSpaceIdToSpaceKey(long spaceId) {
        if (spaceId == 0L) {
            return "";
        }
        Space space = this.spaceManager.getSpace(spaceId);
        return space != null ? space.getKey() : "";
    }
}

