/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.id.JsonContentPropertyId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$ContentPropertyFinder
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$ParameterContentPropertyFinder
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$SingleContentPropertyFetcher
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.finder.ManyFetcher
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.id.JsonContentPropertyId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderPermissionCheck;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyQueryFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class ContentPropertyFinderImpl
implements ContentPropertyService.ContentPropertyFinder,
SingleFetcher<JsonContentProperty>,
ManyFetcher<JsonContentProperty> {
    private final ContentService contentService;
    private final PaginationService paginationService;
    private final CustomContentManager customContentManager;
    private final PermissionManager permissionManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final DraftManager draftManager;
    private final Expansions expansions;
    private final ContentPropertyFinderPermissionCheck permissionCheck;
    private JsonContentPropertyId contentPropertyId;
    private ContentId contentId;
    private String key;
    private List<ContentId> contentIds;
    private List<String> keys;

    public ContentPropertyFinderImpl(ContentService contentService, PaginationService paginationService, CustomContentManager customContentManager, PermissionManager permissionManager, JsonPropertyFactory jsonPropertyFactory, ContentPropertyFinderPermissionCheck permissionCheck, DraftManager draftManager, Expansion ... expansions) {
        this.contentService = contentService;
        this.paginationService = paginationService;
        this.customContentManager = customContentManager;
        this.permissionManager = permissionManager;
        this.jsonPropertyFactory = jsonPropertyFactory;
        this.permissionCheck = permissionCheck;
        this.draftManager = draftManager;
        this.expansions = new Expansions(expansions);
    }

    private Optional<JsonContentProperty> findById() {
        CustomContentEntityObject result = this.customContentManager.getById(this.contentPropertyId.asLong());
        if (result == null || this.isPermissionCheckEnabled() && !this.permissionManager.hasPermission((User)this.getCurrentUser(), Permission.VIEW, (Object)result)) {
            return Optional.empty();
        }
        return Optional.of(this.jsonPropertyFactory.buildContentPropertyFrom(result, this.expansions));
    }

    private PageResponse<JsonContentProperty> findAllByContentId(PageRequest request) throws NotFoundException {
        Content content;
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        try {
            content = (Content)this.contentService.find(this.expansions.getSubExpansions("content").toArray()).withId(this.contentId).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("Cannot find content with id " + this.contentId)));
        }
        catch (NotFoundException e) {
            Draft draft = this.draftManager.getDraft(this.contentId.asLong());
            Content.ContentBuilder transientContent = Content.builder((ContentType)ContentType.valueOf((String)draft.getType()), (long)draft.getId()).title(draft.getTitle()).body(draft.getBodyAsString(), ContentRepresentation.STORAGE).extension("draft", (Object)draft);
            content = transientContent.build();
        }
        Function<CustomContentEntityObject, JsonContentProperty> modelConverter = this.jsonPropertyFactory.buildContentPropertyFromFunction(content, this.expansions);
        return this.paginationService.performPaginationListRequest(limitedRequest, from -> this.customContentManager.findByQuery(JsonPropertyQueryFactory.findAllByContentId(this.contentId.asLong()), from, this.hasViewPermission()), items -> this.stream((Iterable<CustomContentEntityObject>)items).map(modelConverter).collect(Collectors.toList()));
    }

    private PageResponse<JsonContentProperty> findAllByContentIdsAndKeys(PageRequest request) {
        List ids = this.contentIds != null && !this.contentIds.isEmpty() ? Lists.transform(this.contentIds, ContentId::asLong) : Lists.newArrayList((Object[])new Long[]{this.contentId.asLong()});
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        return this.paginationService.performPaginationListRequest(limitedRequest, from -> this.customContentManager.findByQuery(JsonPropertyQueryFactory.findAllByContentIdsAndKeys(ids, this.keys), from, this.hasViewPermission()), items -> this.stream((Iterable<CustomContentEntityObject>)items).map(this.jsonPropertyFactory::buildFrom).collect(Collectors.toList()));
    }

    private Optional<JsonContentProperty> findByContentIdAndKey() {
        CustomContentEntityObject result = (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(JsonPropertyQueryFactory.findByContentIdAndKey(this.contentId.asLong(), this.key));
        if (result == null || this.isPermissionCheckEnabled() && !this.permissionManager.hasPermission((User)this.getCurrentUser(), Permission.VIEW, (Object)result)) {
            return Optional.empty();
        }
        return Optional.of(this.jsonPropertyFactory.buildContentPropertyFrom(result, this.expansions));
    }

    public ContentPropertyService.ParameterContentPropertyFinder withContentId(ContentId contentId) {
        this.contentId = contentId;
        return this;
    }

    public ContentPropertyService.ParameterContentPropertyFinder withContentIds(List<ContentId> contentIds) {
        this.contentIds = contentIds;
        return this;
    }

    public ContentPropertyService.ParameterContentPropertyFinder withKey(String key) {
        return this.withPropertyKey(key);
    }

    public ContentPropertyService.ParameterContentPropertyFinder withPropertyKey(String key) {
        this.key = key;
        return this;
    }

    public ContentPropertyService.ParameterContentPropertyFinder withPropertyKeys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    public ContentPropertyService.SingleContentPropertyFetcher withId(JsonContentPropertyId contentPropertyId) {
        this.contentPropertyId = contentPropertyId;
        return this;
    }

    public PageResponse<JsonContentProperty> fetchMany(PageRequest request) throws NotFoundException {
        if (this.keys != null && (this.contentId != null || this.contentIds != null)) {
            return this.findAllByContentIdsAndKeys(request);
        }
        if (this.contentId != null) {
            return this.findAllByContentId(request);
        }
        throw new IllegalArgumentException("Must specify contentId, or contentIds and keys");
    }

    public Optional<JsonContentProperty> fetch() throws IllegalArgumentException {
        if (this.contentPropertyId != null) {
            return this.findById();
        }
        if (this.contentId != null && this.key != null) {
            return this.findByContentIdAndKey();
        }
        throw new IllegalArgumentException("Must specify either a contentPropertyId, or a contentId and a key");
    }

    public Iterator<String> fetchPropertyKeys() {
        if (this.contentId != null) {
            if (this.contentService.find(new Expansion[0]).withId(this.contentId).fetch().isEmpty()) {
                throw new NotFoundException("Cannot find content with id " + this.contentId);
            }
            Iterable<CustomContentEntityObject> iterable = () -> this.customContentManager.findAllContainedOfType(this.contentId.asLong(), "com.atlassian.confluence.plugins.confluence-content-property-storage:content-property");
            return this.stream(iterable).map(ContentEntityObject::getTitle).iterator();
        }
        throw new IllegalArgumentException("Must specify a contentId");
    }

    public JsonContentProperty fetchOneOrNull() {
        return this.fetch().orElse(null);
    }

    private ConfluenceUser getCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private Predicate<CustomContentEntityObject> hasViewPermission() {
        return target -> !this.isPermissionCheckEnabled() || this.permissionManager.hasPermission((User)this.getCurrentUser(), Permission.VIEW, target);
    }

    private Stream<CustomContentEntityObject> stream(Iterable<CustomContentEntityObject> items) {
        return StreamSupport.stream(items.spliterator(), false);
    }

    private boolean isPermissionCheckEnabled() {
        return this.permissionCheck.equals((Object)ContentPropertyFinderPermissionCheck.YES);
    }
}

