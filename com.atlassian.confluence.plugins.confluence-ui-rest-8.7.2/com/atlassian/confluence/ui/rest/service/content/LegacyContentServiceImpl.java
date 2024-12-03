/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.AbstractLabelableEntityObject
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.core.service.ServiceCommand
 *  com.atlassian.confluence.labels.service.AddLabelsCommand
 *  com.atlassian.confluence.labels.service.LabelsService
 *  com.atlassian.confluence.labels.service.RemoveLabelCommand
 *  com.atlassian.confluence.labels.service.ValidateLabelsCommand
 *  com.atlassian.confluence.legacyapi.NotFoundException
 *  com.atlassian.confluence.legacyapi.NotPermittedException
 *  com.atlassian.confluence.legacyapi.model.PartialList
 *  com.atlassian.confluence.legacyapi.model.content.Content
 *  com.atlassian.confluence.legacyapi.model.content.ContentBody
 *  com.atlassian.confluence.legacyapi.model.content.ContentRepresentation
 *  com.atlassian.confluence.legacyapi.model.content.ContentTree
 *  com.atlassian.confluence.legacyapi.model.content.ContentType
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.atlassian.confluence.legacyapi.model.content.locator.ContentLocator
 *  com.atlassian.confluence.legacyapi.service.Expansion
 *  com.atlassian.confluence.legacyapi.service.Expansions
 *  com.atlassian.confluence.legacyapi.service.content.ContentService
 *  com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.themes.CustomLayoutManager
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  org.joda.time.LocalTime
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.service.content;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.labels.service.ValidateLabelsCommand;
import com.atlassian.confluence.legacyapi.NotFoundException;
import com.atlassian.confluence.legacyapi.NotPermittedException;
import com.atlassian.confluence.legacyapi.model.PartialList;
import com.atlassian.confluence.legacyapi.model.content.Content;
import com.atlassian.confluence.legacyapi.model.content.ContentBody;
import com.atlassian.confluence.legacyapi.model.content.ContentRepresentation;
import com.atlassian.confluence.legacyapi.model.content.ContentTree;
import com.atlassian.confluence.legacyapi.model.content.ContentType;
import com.atlassian.confluence.legacyapi.model.content.Label;
import com.atlassian.confluence.legacyapi.model.content.locator.ContentLocator;
import com.atlassian.confluence.legacyapi.service.Expansion;
import com.atlassian.confluence.legacyapi.service.Expansions;
import com.atlassian.confluence.legacyapi.service.content.ContentService;
import com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.themes.CustomLayoutManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.ui.rest.builder.LegacyContentBodyBuilder;
import com.atlassian.confluence.ui.rest.builder.LegacyContentBuilder;
import com.atlassian.confluence.ui.rest.service.content.LegacyLabelHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Deprecated
@Component(value="localContentService")
public class LegacyContentServiceImpl
implements ContentService {
    private final PageManager pageManager;
    private final CommentManager commentManager;
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final LegacyContentBuilder contentBuilder;
    private final LegacyContentBodyBuilder contentBodyBuilder;
    private final FormatConverter formatConverter;
    private final ThemeManager themeManager;
    private final CustomLayoutManager customLayoutManager;
    private final LabelsService labelsService;

    @Autowired
    public LegacyContentServiceImpl(@ComponentImport PageManager pageManager, @ComponentImport CommentManager commentManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport PermissionManager permissionManager, LegacyContentBuilder contentBuilder, LegacyContentBodyBuilder contentBodyBuilder, @ComponentImport FormatConverter formatConverter, @ComponentImport ThemeManager themeManager, @ComponentImport CustomLayoutManager customLayoutManager, @ComponentImport LabelsService labelsService) {
        this.pageManager = pageManager;
        this.commentManager = commentManager;
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.contentBuilder = contentBuilder;
        this.contentBodyBuilder = contentBodyBuilder;
        this.formatConverter = formatConverter;
        this.themeManager = themeManager;
        this.customLayoutManager = customLayoutManager;
        this.labelsService = labelsService;
    }

    public Option<Content> findById(long id, Expansion ... expansions) {
        ContentEntityObject entity = this.contentEntityManager.getById(id);
        return this.buildContentOption(entity, expansions);
    }

    public Option<Content> findNextVersion(long id, Expansion ... expansions) {
        ContentEntityObject entity = this.contentEntityManager.getById(id);
        ContentEntityObject nextEntity = null;
        if (entity != null) {
            nextEntity = this.contentEntityManager.getNextVersion(entity);
        }
        return this.buildContentOption(nextEntity, expansions);
    }

    public Option<Content> findPreviousVersion(long id, Expansion ... expansions) {
        ContentEntityObject entity = this.contentEntityManager.getById(id);
        ContentEntityObject previousEntity = null;
        if (entity != null) {
            previousEntity = this.contentEntityManager.getPreviousVersion(entity);
        }
        return this.buildContentOption(previousEntity, expansions);
    }

    public Option<Content> findCurrentVersion(long id, Expansion ... expansions) {
        ContentEntityObject entity = this.contentEntityManager.getById(id);
        ContentEntityObject latestEntity = null;
        if (entity != null) {
            latestEntity = this.contentEntityManager.getById(((ContentEntityObject)entity.getLatestVersion()).getId());
        }
        return this.buildContentOption(latestEntity, expansions);
    }

    public Option<Content> find(ContentLocator locator, Expansion ... expansions) {
        ContentEntityObject entity = this.findContentEntity(locator);
        return this.buildContentOption(entity, expansions);
    }

    private boolean canView(ContentEntityObject entity) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)entity);
    }

    private boolean canEdit(ContentEntityObject entity) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)entity);
    }

    private Option<Content> buildContentOption(ContentEntityObject entity, Expansion ... expansions) {
        if (entity != null && this.canView(entity)) {
            return Option.some((Object)this.buildContent(entity, new Expansions(expansions)));
        }
        return Option.none();
    }

    public PartialList<Content> findSubContent(long id, ContentType subContentType, int offset, int count, Expansion ... expansions) {
        ContentEntityObject content = this.contentEntityManager.getById(id);
        if (content != null && this.canView(content) && ContentType.COMMENT.equals(subContentType)) {
            ArrayList<Content> comments = new ArrayList<Content>();
            List commentEntities = content.getComments();
            for (int i = offset; i < commentEntities.size() && i - offset < count; ++i) {
                comments.add(this.buildContent((ContentEntityObject)commentEntities.get(i), new Expansions(expansions)));
            }
            return new PartialList(commentEntities.size(), offset, comments);
        }
        return PartialList.empty();
    }

    public PartialList<ContentTree> findSubContentTree(long parentId, ContentType subContentType, Expansion ... expansions) {
        ContentEntityObject content = this.contentEntityManager.getById(parentId);
        if (content != null && this.canView(content) && ContentType.COMMENT.equals(subContentType)) {
            return this.makeCommentTree(content.getComments(), 0L, new Expansions(expansions));
        }
        return PartialList.empty();
    }

    private PartialList<ContentTree> makeCommentTree(List<Comment> comments, long parentId, Expansions expansions) {
        return PartialList.forAll((Iterable)Iterables.transform(this.getDirectChildComments(comments, parentId), comment -> new ContentTree(this.makeCommentTree(comments, comment.getId(), expansions), this.buildContent((ContentEntityObject)comment, expansions))));
    }

    private Iterable<Comment> getDirectChildComments(List<Comment> comments, long parentId) {
        return Iterables.filter(comments, comment -> parentId == 0L ? comment.getParent() == null : comment.getParent() != null && comment.getParent().getId() == parentId);
    }

    public ContentBody getContentBody(long contentId, ContentRepresentation contentRepresentation) throws NotFoundException, InvalidRepresentationException {
        ContentEntityObject object = this.contentEntityManager.getById(contentId);
        if (object == null || !this.canView(object)) {
            throw new NotFoundException("Content with id " + contentId + " is either missing or not visible to this user");
        }
        return this.contentBodyBuilder.build(object.getBodyContent(), contentRepresentation);
    }

    public ContentBody updateContentBody(long contentId, ContentRepresentation contentRepresentation, ContentBody body) throws NotFoundException, InvalidRepresentationException, NotPermittedException {
        ContentEntityObject entity = this.getContentIfViewable(contentId);
        if (!this.canEdit(entity)) {
            throw new NotPermittedException("User " + AuthenticatedUserThreadLocal.get() + " does not have permission to edit " + entity);
        }
        ContentEntityObject previousVersion = (ContentEntityObject)entity.clone();
        entity.setBodyAsString(this.getNewBodyString(contentRepresentation, body, entity));
        this.getRelevantManager(entity).saveContentEntity(entity, previousVersion, DefaultSaveContext.DEFAULT);
        return body;
    }

    public Iterable<Label> getLabels(long contentId, Collection<Label.Prefix> prefixes) throws NotFoundException {
        return LegacyLabelHelper.extractViewableLabels((AbstractLabelableEntityObject)this.getContentIfViewable(contentId), prefixes, (User)AuthenticatedUserThreadLocal.get());
    }

    public Iterable<Label> addLabels(long contentId, Iterable<Label> labels) throws IllegalArgumentException {
        ContentEntityObject labelable = this.getContentIfViewable(contentId);
        String labelsString = LegacyLabelHelper.concatentateLabels(labels);
        AddLabelsCommand command = this.labelsService.newAddLabelCommand(labelsString, (User)AuthenticatedUserThreadLocal.get(), contentId, labelable.getType());
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
        return this.getLabels(contentId, Arrays.asList(Label.Prefix.values()));
    }

    public void removeLabel(long contentId, long labelId) throws IllegalArgumentException {
        RemoveLabelCommand command = this.labelsService.newRemoveLabelCommand(labelId, (User)AuthenticatedUserThreadLocal.get(), contentId);
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
    }

    public Iterable<Label> validateLabels(Iterable<Label> labels) throws IllegalArgumentException {
        String labelsString = LegacyLabelHelper.concatentateLabels(labels);
        ValidateLabelsCommand command = this.labelsService.newValidateLabelCommand(labelsString, (User)AuthenticatedUserThreadLocal.get());
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        return labels;
    }

    private ContentEntityObject getContentIfViewable(long contentId) {
        ContentEntityObject object = this.contentEntityManager.getById(contentId);
        if (object == null || !this.canView(object)) {
            throw new NotFoundException("Content with id " + contentId + " is either missing or not visible to this user");
        }
        return object;
    }

    private String getNewBodyString(ContentRepresentation contentRepresentation, ContentBody body, ContentEntityObject entity) {
        if (contentRepresentation.equals((Object)ContentRepresentation.EDITOR)) {
            try {
                return this.formatConverter.convertToStorageFormat(body.getValue(), (RenderContext)entity.toPageContext());
            }
            catch (XhtmlException e) {
                throw new RuntimeException("Unable to convert to storage format: " + e, e);
            }
        }
        if (contentRepresentation.equals((Object)ContentRepresentation.RAW) || contentRepresentation.equals((Object)ContentRepresentation.STORAGE)) {
            return body.getValue();
        }
        throw new InvalidRepresentationException(contentRepresentation, new ContentRepresentation[]{ContentRepresentation.STORAGE, ContentRepresentation.EDITOR});
    }

    private ContentEntityManager getRelevantManager(ContentEntityObject entity) {
        if (entity instanceof AbstractPage) {
            return this.pageManager;
        }
        if (entity instanceof Comment) {
            return this.commentManager;
        }
        return this.contentEntityManager;
    }

    private Content buildContent(ContentEntityObject entity, Expansions expansions) {
        return this.contentBuilder.buildFrom(entity, expansions);
    }

    private ContentEntityObject findContentEntity(ContentLocator locator) {
        Page entity = null;
        if (locator.isForContent(ContentType.PAGE)) {
            entity = this.pageManager.getPage(locator.getSpaceKey(), locator.getTitle());
        } else if (locator.isForContent(ContentType.BLOG_POST)) {
            entity = this.pageManager.getBlogPost(locator.getSpaceKey(), locator.getTitle(), locator.getPostingDay().toDateTime(LocalTime.MIDNIGHT).toCalendar(Locale.getDefault()));
        }
        return entity;
    }

    public String getThemeKey(long contentId) {
        ContentEntityObject object = this.contentEntityManager.getById(contentId);
        if (object instanceof SpaceContentEntityObject) {
            SpaceContentEntityObject spaceObject = (SpaceContentEntityObject)object;
            return this.themeManager.getSpaceThemeKey(spaceObject.getSpaceKey());
        }
        return null;
    }

    public boolean hasCustomLayout(String spaceKey) {
        return this.customLayoutManager.usesCustomLayout(spaceKey);
    }
}

