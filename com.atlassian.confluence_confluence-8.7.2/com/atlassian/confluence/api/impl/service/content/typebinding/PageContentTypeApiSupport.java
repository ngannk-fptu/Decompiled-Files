/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Position
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Position;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.event.events.content.page.PageCreateFromTemplateEvent;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;

public class PageContentTypeApiSupport
extends BaseContentTypeApiSupport<Page> {
    private static final String SOURCE_TEMPLATE_ID_PROPERTY = "sourceTemplateId";
    private final ContentFactory contentFactory;
    private final PageManagerInternal pageManager;
    private final SpaceManager spaceManager;
    private final PaginationService paginationService;
    private final ContentCreator contentCreator;
    private final EventPublisher eventPublisher;
    private final PageTemplateManager pageTemplateManager;

    public PageContentTypeApiSupport(ContentFactory contentFactory, PageManagerInternal pageManager, SpaceManager spaceManager, PaginationService paginationService, ApiSupportProvider apiSupportProvider, ContentCreator contentCreator, EventPublisher eventPublisher, PageTemplateManager pageTemplateManager) {
        super(apiSupportProvider);
        this.contentFactory = contentFactory;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.paginationService = paginationService;
        this.contentCreator = contentCreator;
        this.eventPublisher = eventPublisher;
        this.pageTemplateManager = pageTemplateManager;
    }

    @Override
    public ContentType getHandledType() {
        return ContentType.PAGE;
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(Page content, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        long id = content.getId();
        Page page = this.pageManager.getPage(id);
        PaginationBatch fetchPage = nextRequest -> this.pageManager.getChildren(page, (LimitedRequest)nextRequest, depth);
        Function<Iterable, Iterable> modelConverter = items -> this.contentFactory.buildFrom(items, expansions);
        return this.paginationService.performPaginationListRequest(limitedRequest, fetchPage, modelConverter);
    }

    @Override
    public boolean supportsChildrenOfType(ContentType parentType) {
        return !parentType.equals((Object)ContentType.BLOG_POST);
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    public boolean supportsChildrenForParentType(ContentType parentType) {
        return parentType.equals((Object)ContentType.PAGE);
    }

    @Override
    public Class<Page> getEntityClass() {
        return Page.class;
    }

    @Override
    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Page> pages, Expansions expansions) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (Page page : pages) {
            Map extensions = ModelMapBuilder.newInstance().put((Object)"position", (Object)Position.of((Integer)page.getPosition())).build();
            mapBuilder.put((Object)page.getContentId(), (Object)extensions);
        }
        return mapBuilder.build();
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        Maybe<ValidationResult> validationResult = this.validateSourceTemplateId(newContent);
        if (validationResult.isDefined()) {
            return (ValidationResult)validationResult.get();
        }
        return this.contentCreator.validateCreate(AuthenticatedUserThreadLocal.get(), newContent, this.getEntityClass());
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, Page sceo) {
        Maybe<ValidationResult> validationResult = this.validateSourceTemplateId(updatedContent);
        if (validationResult.isDefined()) {
            return (ValidationResult)validationResult.get();
        }
        return this.contentCreator.validateUpdate(AuthenticatedUserThreadLocal.get(), updatedContent, sceo);
    }

    @VisibleForTesting
    Maybe<ValidationResult> validateSourceTemplateId(Content updatedContent) {
        String sourceTemplateId = (String)updatedContent.getExtension(SOURCE_TEMPLATE_ID_PROPERTY);
        if (sourceTemplateId == null) {
            return Option.none();
        }
        try {
            Long templateId = Long.valueOf(sourceTemplateId);
            if (this.pageTemplateManager.getPageTemplate(templateId) == null) {
                return Option.some((Object)SimpleValidationResult.builder().addError("Could not find template with id: " + sourceTemplateId, new Object[0]).build());
            }
            return Option.none();
        }
        catch (NumberFormatException ex) {
            return Option.some((Object)SimpleValidationResult.builder().addError("sourceTemplateId is not a long: " + sourceTemplateId, new Object[0]).build());
        }
    }

    @Override
    public Page create(Content newContent) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Page page = new Page();
        this.contentCreator.setCommonPropertiesForCreate(newContent, page, user);
        ContentId parentId = newContent.getParentId();
        if (parentId.isSet()) {
            Page potentialParent = this.pageManager.getPage(parentId.asLong());
            if (potentialParent == null) {
                throw new BadRequestException("The parent ID specified (" + parentId + ") does not exist?");
            }
            if (page.isDraft()) {
                page.setParentPage(potentialParent);
                page.getAncestors().addAll(potentialParent.getAncestors());
                page.getAncestors().add(potentialParent);
            } else {
                potentialParent.addChild(page);
            }
        }
        Draft draft = (Draft)newContent.getExtension("draft");
        Page savedPage = this.contentCreator.saveNewContent(page, newContent.getVersion(), draft);
        this.maybePublishCreateFromTemplateEvent(newContent, savedPage);
        return savedPage;
    }

    @Override
    public Page update(Content updatedContent, Page page) {
        Page originalPage = this.contentCreator.cloneForUpdate(page);
        boolean pagePropertiesChanged = this.contentCreator.setCommonPropertiesForUpdate(updatedContent, page);
        boolean pageMetadataChanged = this.contentCreator.setCommonMetadata(updatedContent, page);
        boolean spaceWasUpdated = this.maybeUpdateSpace(updatedContent, page);
        ContentId parentId = updatedContent.getParentId();
        Page existingParent = page.getParent();
        Page potentialParent = parentId.isSet() ? this.pageManager.getPage(parentId.asLong()) : existingParent;
        boolean parentPageWasUpdated = this.maybeUpdatePageParent(page, potentialParent, existingParent, this.isDraftBeingPublished(updatedContent, originalPage));
        boolean changed = pagePropertiesChanged;
        changed |= pageMetadataChanged;
        changed |= spaceWasUpdated;
        changed |= parentPageWasUpdated;
        if (this.isDraftBeingPublished(updatedContent, originalPage)) {
            this.maybePublishCreateFromTemplateEvent(updatedContent, page);
        }
        if (changed) {
            return this.contentCreator.update(page, originalPage, updatedContent.getVersion());
        }
        return page;
    }

    private boolean maybeUpdateSpace(Content updatedContent, Page page) {
        if (updatedContent.getSpace() != null && page.getSpace() != null && !updatedContent.getSpace().getKey().equals(page.getSpace().getKey())) {
            page.setSpace(this.spaceManager.getSpace(updatedContent.getSpace().getKey()));
            return true;
        }
        return false;
    }

    private boolean maybeUpdatePageParent(Page page, Page potentialParent, Page existingParent, boolean isDraftBeingPublished) {
        boolean storeRequired = false;
        if (potentialParent == null) {
            if (existingParent != null) {
                existingParent.removeChild(page);
                page.setParentPage(null);
                storeRequired = true;
            }
        } else if (existingParent == null) {
            potentialParent.addChild(page);
            storeRequired = true;
        } else if (existingParent.getId() != potentialParent.getId() || isDraftBeingPublished && !potentialParent.getChildren().contains(page)) {
            existingParent.removeChild(page);
            potentialParent.addChild(page);
            storeRequired = true;
        }
        return storeRequired;
    }

    private void maybePublishCreateFromTemplateEvent(Content updatedContent, Page page) {
        String sourceTemplateId = (String)updatedContent.getExtension(SOURCE_TEMPLATE_ID_PROPERTY);
        if (sourceTemplateId != null) {
            this.eventPublisher.publish((Object)new PageCreateFromTemplateEvent((Object)this, page, sourceTemplateId));
        }
    }

    private boolean isDraftBeingPublished(Content updatedContent, Page originalPage) {
        return originalPage.isDraft() && ContentStatus.CURRENT.equals((Object)updatedContent.getStatus());
    }
}

