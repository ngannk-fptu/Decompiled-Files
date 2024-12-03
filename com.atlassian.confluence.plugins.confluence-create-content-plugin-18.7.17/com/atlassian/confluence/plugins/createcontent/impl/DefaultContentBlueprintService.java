/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.service.content.ContentBlueprintService
 *  com.atlassian.confluence.api.service.content.ContentDraftService
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.PagePermissionsActionHelper
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.service.content.ContentBlueprintService;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PagePermissionsActionHelper;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprintInstanceAdapter;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.RequestResolver;
import com.atlassian.confluence.plugins.createcontent.services.RequestStorage;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintPage;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ExportAsService(value={com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintService.class, ContentBlueprintService.class})
@Component
public class DefaultContentBlueprintService
implements com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintService,
ContentBlueprintService {
    private final ContentBlueprintManager contentBlueprintManager;
    private final BlueprintManager legacyManager;
    private final PageManager pageManager;
    private final DraftManager draftManager;
    private final BlueprintContentGenerator contentGenerator;
    private final LabelManager labelManager;
    private final RequestResolver requestResolver;
    private final EventPublisher eventPublisher;
    private final ContentPermissionManager contentPermissionManager;
    private final RequestStorage requestStorage;
    private final DraftsTransitionHelper draftsTransitionHelper;
    private final UserBlueprintConfigManager userBlueprintConfigManager;
    private final ContentDraftService contentDraftService;
    private final ContentEntityManager contentEntityManager;
    private final SpaceManager spaceManager;
    private final ContentBlueprintInstanceAdapter contentBlueprintInstanceAdapter;
    private final UserAccessor userAccessor;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final PageTemplateManager pageTemplateManager;
    private final SpacePermissionManager spacePermissionManager;
    @VisibleForTesting
    static final String VIEW_PERMISSIONS_USERS = "viewPermissionsUsers";

    @Autowired
    public DefaultContentBlueprintService(ContentBlueprintManager contentBlueprintManager, BlueprintManager blueprintManager, @ComponentImport PageManager pageManager, @ComponentImport DraftManager draftManager, BlueprintContentGenerator contentGenerator, @ComponentImport LabelManager labelManager, RequestResolver requestResolver, @ComponentImport EventPublisher eventPublisher, @ComponentImport ContentPermissionManager contentPermissionManager, RequestStorage requestStorage, @ComponentImport DraftsTransitionHelper draftsTransitionHelper, UserBlueprintConfigManager userBlueprintConfigManager, @ComponentImport ContentDraftService contentDraftService, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport SpaceManager spaceManager, ContentBlueprintInstanceAdapter contentBlueprintInstanceAdapter, @ComponentImport UserAccessor userAccessor, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.contentBlueprintManager = contentBlueprintManager;
        this.legacyManager = blueprintManager;
        this.pageManager = pageManager;
        this.draftManager = draftManager;
        this.contentGenerator = contentGenerator;
        this.labelManager = labelManager;
        this.requestResolver = requestResolver;
        this.eventPublisher = eventPublisher;
        this.contentPermissionManager = contentPermissionManager;
        this.requestStorage = requestStorage;
        this.draftsTransitionHelper = draftsTransitionHelper;
        this.userBlueprintConfigManager = userBlueprintConfigManager;
        this.contentDraftService = contentDraftService;
        this.contentEntityManager = contentEntityManager;
        this.spaceManager = spaceManager;
        this.contentBlueprintInstanceAdapter = contentBlueprintInstanceAdapter;
        this.userAccessor = userAccessor;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.pageTemplateManager = pageTemplateManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    public ContentBlueprintInstance createInstance(ContentBlueprintInstance contentBlueprintInstance, Expansion ... expansions) {
        ContentStatus status = contentBlueprintInstance.getContent().getStatus();
        if (!ImmutableList.of((Object)ContentStatus.DRAFT, (Object)ContentStatus.CURRENT).contains((Object)status)) {
            throw new BadRequestException("Status of content must be DRAFT or CURRENT, supplied value: " + status);
        }
        CreateBlueprintPageEntity entity = this.contentBlueprintInstanceAdapter.convertToEntity(contentBlueprintInstance);
        try {
            ConfluenceUser creator = AuthenticatedUserThreadLocal.get();
            if (status.equals((Object)ContentStatus.DRAFT)) {
                ContentEntityObject created = this.createContentDraft(entity, creator);
                return this.contentBlueprintInstanceAdapter.convertToInstance(created, contentBlueprintInstance, expansions);
            }
            BlueprintPage created = this.createPage(entity, creator);
            return this.contentBlueprintInstanceAdapter.convertToInstance(created, contentBlueprintInstance, expansions);
        }
        catch (BlueprintIllegalArgumentException e) {
            throw new BadRequestException("It's bad, real bad", (Throwable)e);
        }
    }

    public Content publishInstance(Content content, Expansion ... expansions) {
        if (content.getId() == null) {
            throw new BadRequestException("Require draft id");
        }
        if (!content.getStatus().equals((Object)ContentStatus.CURRENT)) {
            throw new BadRequestException("Status is not supported: " + content.getStatus().toString());
        }
        ContentEntityObject draft = this.getDraft(content.getId());
        if (draft == null) {
            throw new BadRequestException("Could not find draft to publish with id: " + content.getId().asLong());
        }
        CreateBlueprintPageEntity createBlueprintPageEntity = this.getCreateBlueprintPageEntity(draft);
        ContentBlueprint blueprint = this.getBlueprint(createBlueprintPageEntity);
        if (blueprint != null) {
            content = this.createIndexPageAndSetAsParent(content, blueprint);
        }
        this.requestStorage.clear(draft);
        Content createdContent = this.draftsTransitionHelper.isSharedDraftsFeatureEnabled(content.getSpace().getKey()) ? this.contentDraftService.publishEditDraft(content, ContentDraftService.ConflictPolicy.ABORT) : this.contentDraftService.publishNewDraft(content, expansions);
        if (blueprint != null) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            Page createdPage = this.pageManager.getPage(createdContent.getId().asLong());
            if (user != null && this.userBlueprintConfigManager.isFirstBlueprintOfTypeForUser(blueprint.getId(), user)) {
                String webUILink = ((Link)createdContent.getLinks().get(LinkType.WEB_UI)).getPath();
                String url = this.decorateReturnedUrlWithFlashScopeInfo(blueprint, user, webUILink);
                createdContent = Content.builder((Content)createdContent).addLink(LinkType.WEB_UI, url).build();
            }
            Map<String, Object> emptyContext = createBlueprintPageEntity.getContext();
            this.eventPublisher.publish((Object)new BlueprintPageCreateEvent(this, createdPage, blueprint, user, emptyContext));
        }
        return createdContent;
    }

    @Override
    public BlueprintPage createPage(CreateBlueprintPageEntity entity, ConfluenceUser creator) throws BlueprintIllegalArgumentException {
        CreateBlueprintPageRequest createRequest = this.requestResolver.resolve(entity, creator);
        ContentBlueprint blueprint = createRequest.getContentBlueprint();
        this.validateBlueprintCreateResult(blueprint);
        this.validatePageTitleIsUnique(createRequest);
        this.validatePageRestrictionPerm(createRequest);
        if (!blueprint.isIndexDisabled()) {
            this.validatePageTitleDifferentToItsIndexPage(createRequest);
        }
        Page blueprintPage = this.contentGenerator.generateBlueprintPageObject(createRequest);
        this.addLabels(createRequest, (Labelable)blueprintPage);
        Page indexPage = this.linkWithParentOrIndexPage(createRequest, blueprint, blueprintPage);
        this.pageManager.saveContentEntity((ContentEntityObject)blueprintPage, DefaultSaveContext.DEFAULT);
        this.addPermissions(createRequest, (ContentEntityObject)blueprintPage);
        this.eventPublisher.publish((Object)new BlueprintPageCreateEvent(this, blueprintPage, blueprint, creator, createRequest.getContext()));
        return new BlueprintPage(blueprintPage, indexPage);
    }

    @Override
    public Draft createDraft(CreateBlueprintPageEntity entity, ConfluenceUser creator) throws BlueprintIllegalArgumentException {
        if (this.draftsTransitionHelper.isSharedDraftsFeatureEnabled(entity.getSpaceKey())) {
            throw new UnsupportedOperationException("Cannot create legacy drafts with shared-drafts dark feature enabled. Please use createContentDraft instead.");
        }
        return (Draft)this.createContentDraft(entity, creator);
    }

    @Override
    public ContentEntityObject createContentDraft(CreateBlueprintPageEntity entity, ConfluenceUser creator) throws BlueprintIllegalArgumentException {
        CreateBlueprintPageRequest createRequest = this.requestResolver.resolve(entity, creator);
        this.validatePageRestrictionPerm(createRequest);
        Page blueprintPage = this.contentGenerator.generateBlueprintPageObject(createRequest);
        ContentEntityObject contentDraft = this.saveContentDraft(createRequest, blueprintPage);
        this.addLabels(createRequest, (Labelable)contentDraft);
        this.addPermissions(createRequest, contentDraft);
        this.requestStorage.storeCreateRequest(entity, contentDraft);
        return contentDraft;
    }

    @Override
    public void deleteContentBlueprintsForSpace(@Nonnull String spaceKey) {
        List<ContentBlueprint> contentBlueprints = this.contentBlueprintManager.getAllBySpaceKey(spaceKey);
        contentBlueprints.forEach(contentBlueprint -> {
            ContentTemplateRef indexPageTemplateRef = contentBlueprint.getIndexPageTemplateRef();
            this.removePageTemplateSilently(indexPageTemplateRef.getTemplateId());
            this.contentTemplateRefManager.delete(indexPageTemplateRef.getId());
            contentBlueprint.getContentTemplateRefs().forEach(contentTemplateRef -> {
                this.removePageTemplateSilently(contentTemplateRef.getTemplateId());
                this.contentTemplateRefManager.delete(contentTemplateRef.getId());
            });
            this.contentBlueprintManager.delete(contentBlueprint.getId());
        });
    }

    private void addPermissions(CreateBlueprintPageRequest createRequest, ContentEntityObject ceo) {
        String viewPermissionsUsers = createRequest.getViewPermissionsUsers();
        if (StringUtils.isNotBlank((CharSequence)viewPermissionsUsers)) {
            PagePermissionsActionHelper permissionHelper = new PagePermissionsActionHelper(createRequest.getCreator(), this.userAccessor);
            List viewPermissions = permissionHelper.createPermissions("View", null, viewPermissionsUsers);
            this.contentPermissionManager.setContentPermissions((Collection)viewPermissions, ceo, "View");
        }
    }

    private void addLabels(CreateBlueprintPageRequest createRequest, Labelable labelable) {
        String labelsString = (String)createRequest.getContext().get("labelsString");
        if (StringUtils.isNotBlank((CharSequence)labelsString)) {
            String[] labels;
            for (String label : labels = labelsString.split(" ")) {
                this.labelManager.addLabel(labelable, new Label(label));
            }
        }
        this.labelManager.addLabel(labelable, new Label(createRequest.getContentBlueprint().getIndexKey()));
    }

    @VisibleForTesting
    ContentEntityObject saveContentDraft(CreateBlueprintPageRequest createRequest, Page blueprintPage) {
        ContentEntityObject contentDraft = createRequest.getParentPage() != null ? this.draftsTransitionHelper.createDraft("page", createRequest.getSpace().getKey(), createRequest.getParentPage().getId()) : this.draftsTransitionHelper.createDraft("page", createRequest.getSpace().getKey());
        contentDraft.setTitle(blueprintPage.getTitle());
        contentDraft.setBodyAsString(blueprintPage.getBodyAsString());
        if (DraftsTransitionHelper.isLegacyDraft((ContentEntityObject)contentDraft)) {
            this.draftManager.saveDraft((Draft)contentDraft);
        } else {
            this.pageManager.saveContentEntity(contentDraft, DefaultSaveContext.DRAFT);
        }
        return contentDraft;
    }

    private Page linkWithParentOrIndexPage(CreateBlueprintPageRequest createRequest, ContentBlueprint blueprint, Page blueprintPage) {
        Page indexPage = this.legacyManager.createAndPinIndexPage(blueprint, createRequest.getSpace());
        Page parentPage = createRequest.getParentPage();
        if (parentPage == null) {
            parentPage = indexPage;
        }
        if (parentPage != null) {
            parentPage.addChild(blueprintPage);
        }
        return indexPage;
    }

    private void validatePageTitleDifferentToItsIndexPage(CreateBlueprintPageRequest createRequest) throws BlueprintIllegalArgumentException {
        String title = createRequest.getTitle();
        String indexPageTitle = this.legacyManager.getIndexPageTitle(createRequest.getContentBlueprint());
        if (indexPageTitle.equalsIgnoreCase(title)) {
            throw new BlueprintIllegalArgumentException("Attempted to create a page with the same title as this Blueprint's Index page.", ResourceErrorType.DUPLICATED_TITLE_INDEX, (Object)title);
        }
    }

    private void validatePageTitleIsUnique(CreateBlueprintPageRequest createRequest) throws BlueprintIllegalArgumentException {
        String title = createRequest.getTitle();
        if (this.pageManager.getPage(createRequest.getSpace().getKey(), title) != null) {
            throw new BlueprintIllegalArgumentException("Attempted to create a page with the same title as an existing page.", ResourceErrorType.DUPLICATED_TITLE, (Object)title);
        }
    }

    private void validateBlueprintCreateResult(ContentBlueprint blueprint) throws BlueprintIllegalArgumentException {
        String createResult = blueprint.getCreateResult();
        if (!"view".equals(createResult)) {
            throw new BlueprintIllegalArgumentException("Attempted to create a page that needs to go to the editor.", ResourceErrorType.INVALID_CREATE_RESULT_BLUEPRINT, (Object)createResult);
        }
    }

    private void validatePageRestrictionPerm(CreateBlueprintPageRequest createRequest) {
        String viewPermissionsUsers = createRequest.getViewPermissionsUsers();
        if (StringUtils.isNotBlank((CharSequence)viewPermissionsUsers) && !this.spacePermissionManager.hasPermission("SETPAGEPERMISSIONS", createRequest.getSpace(), (User)AuthenticatedUserThreadLocal.get())) {
            throw new ResourceException("Can't set page permissions in this space", Response.Status.FORBIDDEN, ResourceErrorType.PERMISSION_USER_SET_PAGE_RESTRICTIONS);
        }
    }

    private ContentEntityObject getDraft(ContentId draftId) {
        ContentEntityObject content = this.contentEntityManager.getById(draftId.asLong());
        return content != null && content.isDraft() ? content : null;
    }

    private Content createIndexPageAndSetAsParent(Content content, ContentBlueprint blueprint) {
        Space space = this.spaceManager.getSpace(content.getSpace().getKey());
        Page indexPage = this.legacyManager.createAndPinIndexPage(blueprint, space);
        if (content.getAncestors().isEmpty() && indexPage != null) {
            Content parentContent = Content.builder((ContentType)ContentType.PAGE, (long)indexPage.getId()).build();
            content = Content.builder((Content)content).parent(parentContent).build();
        }
        return content;
    }

    private ContentBlueprint getBlueprint(CreateBlueprintPageEntity createBlueprintPageEntity) {
        if (createBlueprintPageEntity == null) {
            return null;
        }
        if (StringUtils.isNotBlank((CharSequence)createBlueprintPageEntity.getContentBlueprintId())) {
            return (ContentBlueprint)this.contentBlueprintManager.getById(UUID.fromString(createBlueprintPageEntity.getContentBlueprintId()));
        }
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(createBlueprintPageEntity.getModuleCompleteKey());
        return this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleCompleteKey, createBlueprintPageEntity.getSpaceKey());
    }

    private CreateBlueprintPageEntity getCreateBlueprintPageEntity(ContentEntityObject draft) {
        CreateBlueprintPageEntity createBlueprintPageEntity;
        try {
            createBlueprintPageEntity = this.requestStorage.retrieveRequest(draft);
        }
        catch (IllegalStateException ex) {
            createBlueprintPageEntity = null;
        }
        return createBlueprintPageEntity;
    }

    private String decorateReturnedUrlWithFlashScopeInfo(ContentBlueprint blueprint, ConfluenceUser user, String baseUrl) {
        this.userBlueprintConfigManager.setBlueprintCreatedByUser(blueprint.getId(), user);
        FlashScope.put((String)"firstBlueprintForUser", (Object)blueprint.getId());
        FlashScope.put((String)"com.atlassian.confluence.plugins.confluence-create-content-plugin.blueprint-index-disabled", (Object)blueprint.isIndexDisabled());
        String flashId = FlashScope.persist();
        return FlashScope.getFlashScopeUrl((String)baseUrl, (String)flashId);
    }

    private void removePageTemplateSilently(long pageTemplateId) {
        if (pageTemplateId == 0L) {
            return;
        }
        PageTemplate pageTemplate = this.pageTemplateManager.getPageTemplate(pageTemplateId);
        if (pageTemplate != null) {
            this.pageTemplateManager.removePageTemplate(pageTemplate);
        }
    }
}

