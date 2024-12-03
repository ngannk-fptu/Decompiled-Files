/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentTypeAware
 *  com.atlassian.confluence.core.Versioned
 *  com.atlassian.confluence.legacyapi.model.SpaceSummary
 *  com.atlassian.confluence.legacyapi.model.content.Content
 *  com.atlassian.confluence.legacyapi.model.content.ContentLink
 *  com.atlassian.confluence.legacyapi.model.content.ContentType
 *  com.atlassian.confluence.legacyapi.model.content.EditSummary
 *  com.atlassian.confluence.legacyapi.model.content.HistorySummary
 *  com.atlassian.confluence.legacyapi.model.content.Permission
 *  com.atlassian.confluence.legacyapi.model.people.Person
 *  com.atlassian.confluence.legacyapi.service.Expansions
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.TinyUrl
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.builder;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentTypeAware;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.legacyapi.model.SpaceSummary;
import com.atlassian.confluence.legacyapi.model.content.Content;
import com.atlassian.confluence.legacyapi.model.content.ContentLink;
import com.atlassian.confluence.legacyapi.model.content.ContentType;
import com.atlassian.confluence.legacyapi.model.content.EditSummary;
import com.atlassian.confluence.legacyapi.model.content.HistorySummary;
import com.atlassian.confluence.legacyapi.model.people.Person;
import com.atlassian.confluence.legacyapi.service.Expansions;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.ui.rest.builder.LegacyContentBodyBuilder;
import com.atlassian.confluence.ui.rest.builder.LegacyPersonBuilder;
import com.atlassian.confluence.ui.rest.builder.LegacySpaceSummaryBuilder;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LegacyContentBuilder {
    private final LegacyPersonBuilder personBuilder;
    private final LegacyContentBodyBuilder contentBodyBuilder;
    private final LegacySpaceSummaryBuilder spaceSummaryBuilder;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;

    @Autowired
    public LegacyContentBuilder(LegacyPersonBuilder personBuilder, LegacyContentBodyBuilder contentBodyBuilder, LegacySpaceSummaryBuilder spaceSummaryBuilder, @ComponentImport PermissionManager permissionManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.personBuilder = personBuilder;
        this.contentBodyBuilder = contentBodyBuilder;
        this.spaceSummaryBuilder = spaceSummaryBuilder;
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
    }

    public Content buildFrom(ContentEntityObject entity, Expansions expansions) {
        return new Content(Long.valueOf(entity.getId()), this.makeOptionalSpaceSummary((Versioned)entity, expansions), this.makeContentType((ContentTypeAware)entity), entity.getDisplayTitle(), entity.getUrlPath(), this.makeOptionalTinyUrl(entity), this.makeHistorySummary(entity, expansions.getSubExpansions("history")), this.makeAncestry((Versioned)entity), this.contentBodyBuilder.makeContentBodies(entity.getBodyContent(), expansions.getSubExpansions("body")), this.makePermissions(entity));
    }

    private Option<String> makeOptionalTinyUrl(Object entity) {
        if (entity instanceof AbstractPage) {
            TinyUrl tinyUrl = new TinyUrl((AbstractPage)entity);
            return Option.some((Object)("/x/" + tinyUrl.getIdentifier()));
        }
        return Option.none(String.class);
    }

    private Map<com.atlassian.confluence.legacyapi.model.content.Permission, Boolean> makePermissions(Object entity) {
        HashMap<com.atlassian.confluence.legacyapi.model.content.Permission, Boolean> permissions = new HashMap<com.atlassian.confluence.legacyapi.model.content.Permission, Boolean>();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.EDIT, this.permissionManager.hasPermission((User)user, Permission.EDIT, entity));
        permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.REMOVE, this.permissionManager.hasPermission((User)user, Permission.REMOVE, entity));
        if (entity instanceof Page) {
            permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.EXPORT, this.permissionManager.hasPermission((User)user, Permission.EXPORT, entity));
        }
        if (entity instanceof Page) {
            permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.SET_PERMISSIONS, this.permissionManager.hasPermission((User)user, Permission.SET_PERMISSIONS, entity));
        }
        permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.ADD_COMMENT, this.permissionManager.hasCreatePermission((User)user, entity, Comment.class));
        permissions.put(com.atlassian.confluence.legacyapi.model.content.Permission.ADD_ATTACHMENT, this.permissionManager.hasCreatePermission((User)user, entity, Attachment.class));
        return permissions;
    }

    private Iterable<ContentLink> makeAncestry(Versioned entity) {
        ContentEntityObject latestVersion = (ContentEntityObject)entity.getLatestVersion();
        if (latestVersion instanceof Page) {
            Page page = (Page)latestVersion;
            List ancestors = page.getAncestors();
            return Iterables.transform((Iterable)ancestors, page1 -> new ContentLink(Long.valueOf(page1.getId()), this.makeOptionalSpaceSummary((Versioned)page1, Expansions.EMPTY), ContentType.PAGE, page1.getTitle(), page1.getUrlPath()));
        }
        return Collections.emptyList();
    }

    private Option<SpaceSummary> makeOptionalSpaceSummary(Versioned entity, Expansions expansions) {
        Space space = null;
        if (entity instanceof Spaced) {
            space = ((Spaced)entity.getLatestVersion()).getSpace();
        }
        return space == null ? Option.none() : Option.some((Object)this.spaceSummaryBuilder.buildFrom(space, expansions.getSubExpansions("space")));
    }

    private HistorySummary makeHistorySummary(ContentEntityObject entity, Expansions expansions) {
        ContentEntityObject thisEntity = null;
        ContentEntityObject previousEntity = null;
        ContentEntityObject nextEntity = null;
        ContentEntityObject latestEntity = (ContentEntityObject)entity.getLatestVersion();
        Person createdBy = this.personBuilder.forUsername(latestEntity.getCreatorName());
        Date creationDate = latestEntity.getCreationDate();
        if (expansions.canExpand("previous")) {
            previousEntity = this.contentEntityManager.getPreviousVersion(entity);
        }
        if (expansions.canExpand("current")) {
            thisEntity = entity;
        }
        if (expansions.canExpand("next")) {
            nextEntity = this.contentEntityManager.getNextVersion(entity);
        }
        if (!expansions.canExpand("latest")) {
            latestEntity = null;
        }
        Option<EditSummary> previousVersion = this.makeEditSummary(previousEntity);
        Option<EditSummary> currentVersion = this.makeEditSummary(thisEntity);
        Option<EditSummary> nextVersion = this.makeEditSummary(nextEntity);
        Option<EditSummary> latestVersion = this.makeEditSummary(latestEntity);
        return new HistorySummary(currentVersion, previousVersion, nextVersion, latestVersion, entity.isLatestVersion(), createdBy, creationDate);
    }

    private Option<EditSummary> makeEditSummary(ContentEntityObject entity) {
        if (entity == null) {
            return Option.none();
        }
        EditSummary summary = new EditSummary(this.personBuilder.forUsername(entity.getLastModifierName()), entity.getLastModificationDate(), entity.getVersionComment(), entity.getVersion(), entity.getId());
        return Option.some((Object)summary);
    }

    private ContentType makeContentType(ContentTypeAware entity) {
        if (entity instanceof CustomContentEntityObject) {
            return ContentType.forName((String)((CustomContentEntityObject)entity).getPluginModuleKey());
        }
        return ContentType.forName((String)entity.getType());
    }
}

