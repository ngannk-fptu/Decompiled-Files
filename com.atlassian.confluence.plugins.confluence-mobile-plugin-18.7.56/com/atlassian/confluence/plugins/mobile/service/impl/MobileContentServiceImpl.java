/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptors
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.analytic.MobileSimpleAnalyticEvent;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.FavouriteDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.helper.ContentHelper;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.plugins.mobile.service.MobileChildContentService;
import com.atlassian.confluence.plugins.mobile.service.MobileContentService;
import com.atlassian.confluence.plugins.mobile.service.factory.ContentMetadataFactory;
import com.atlassian.confluence.plugins.mobile.service.factory.FavouriteFactory;
import com.atlassian.confluence.plugins.mobile.service.factory.MobileContentFactory;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileContentServiceImpl
implements MobileContentService {
    private static final Set<ContentStatus> RESTRICTED_CONTENT = ImmutableSet.of((Object)ContentStatus.DRAFT, (Object)ContentStatus.TRASHED);
    private static final String MOBILE_PAGE_VIEW_EVENT_NAME = "page.view";
    private final MobileContentFactory mobileContentFactory;
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;
    private final FavouriteFactory favouriteFactory;
    private final CQLSearchService searchService;
    private final ContentMetadataFactory contentMetadataFactory;
    private final ContentEntityManager contentEntityManager;
    private final MobileChildContentService childContentService;
    private final RelationService relationService;
    private final PersonService personService;

    @Autowired
    public MobileContentServiceImpl(MobileContentFactory mobileContentFactory, @ComponentImport PermissionManager permissionManager, @ComponentImport EventPublisher eventPublisher, FavouriteFactory favouriteFactory, @ComponentImport CQLSearchService searchService, ContentMetadataFactory contentMetadataFactory, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, MobileChildContentService childContentService, @ComponentImport RelationService relationService, @ComponentImport PersonService personService) {
        this.mobileContentFactory = mobileContentFactory;
        this.permissionManager = permissionManager;
        this.eventPublisher = eventPublisher;
        this.favouriteFactory = favouriteFactory;
        this.searchService = searchService;
        this.contentMetadataFactory = contentMetadataFactory;
        this.contentEntityManager = contentEntityManager;
        this.childContentService = childContentService;
        this.relationService = relationService;
        this.personService = personService;
    }

    @Override
    public ContentDto getContent(long id) {
        ContentEntityObject ceo = this.getContentById(id);
        if (ContentHelper.isPageOrBlog(ceo)) {
            ContentDto contentDto = this.mobileContentFactory.getContent(ceo);
            this.publishViewEvent(ceo);
            this.eventPublisher.publish((Object)new MobileSimpleAnalyticEvent(MOBILE_PAGE_VIEW_EVENT_NAME));
            return contentDto;
        }
        throw new NotImplementedServiceException(ceo.getType() + " is not yet supported");
    }

    private void publishViewEvent(ContentEntityObject ceo) {
        PageViewEvent event = null;
        if (ceo instanceof Page) {
            event = new PageViewEvent((Object)this, (Page)ceo, null);
        } else if (ceo instanceof BlogPost) {
            event = new BlogPostViewEvent((Object)this, (BlogPost)ceo, null);
        }
        if (event != null) {
            this.eventPublisher.publish((Object)event);
        }
    }

    @Override
    public Boolean favourite(Long id) {
        this.relationService.create(this.buildFavouriteRelation(id));
        return true;
    }

    @Override
    public Boolean removeFavourite(Long id) {
        this.relationService.delete(this.buildFavouriteRelation(id));
        return true;
    }

    @Override
    public List<FavouriteDto> getFavourites(PageRequest pageRequest) {
        String cql = "favourite=currentUser() order by favourite desc";
        Expansion[] expansions = ExpansionsParser.parse((String)"body.storage,history,metadata.currentuser.favourited");
        PageResponse contents = this.searchService.searchContent(cql, pageRequest, expansions);
        return contents.getResults().stream().filter(this::isFavourite).map(this.favouriteFactory::convertToFavouriteDto).collect(Collectors.toList());
    }

    @Override
    public ContentMetadataDto getContentMetadata(ContentId id) {
        return this.contentMetadataFactory.buildMetadata(this.getContentById(id.asLong()));
    }

    @Override
    public ContentMetadataDto getCreationContentMetadata(Context context) {
        return this.contentMetadataFactory.buildMetadata(context);
    }

    @Override
    public PageResponse<ContentDto> getSavedList(PageRequest pageRequest) {
        String cql = "favourite=currentUser() order by favourite desc";
        Expansion[] expansions = ExpansionsParser.parse((String)"body.storage,history,metadata.currentuser.favourited");
        PageResponse contents = this.searchService.searchContent(cql, pageRequest, expansions);
        boolean hasMore = contents.size() == pageRequest.getLimit();
        return PageResponseImpl.from(this.mobileContentFactory.convert(contents.getResults(), MobileContentFactory.Type.SAVE_CONTENT), (boolean)hasMore).pageRequest(pageRequest).hasMore(hasMore).build();
    }

    @Override
    public Map<String, PageResponse> getRelationContent(long contentId, Expansions expansions, Inclusions inclusions, PageRequest pageRequest) {
        ContentEntityObject ceo = this.getContentById(contentId);
        if (ceo instanceof Page) {
            return this.getPageRelation((Page)ceo, expansions, inclusions, pageRequest);
        }
        throw new NotImplementedServiceException(ceo.getType() + " is not yet supported.");
    }

    private RelationInstance buildFavouriteRelation(Long id) {
        com.atlassian.confluence.api.model.people.User source = (com.atlassian.confluence.api.model.people.User)this.personService.getCurrentUser(new Expansion[0]);
        Content target = Content.builder().id(ContentId.valueOf((String)id.toString())).build();
        RelationDescriptor descriptor = RelationDescriptors.lookupBuiltinOrCreate(com.atlassian.confluence.api.model.people.User.class, (String)"favourite", Content.class);
        return RelationInstance.builder((Relatable)source, (RelationDescriptor)descriptor, (Relatable)target).build();
    }

    private Map<String, PageResponse> getPageRelation(Page page, Expansions expansions, Inclusions inclusions, PageRequest pageRequest) {
        HashMap<String, PageResponse> relationMap = new HashMap<String, PageResponse>();
        if (inclusions.isInclude(MobileContentService.RelationContentType.PARENT.getValue())) {
            List<Page> parents = page.getParent() == null ? Collections.emptyList() : Collections.singletonList(page.getParent());
            relationMap.put(MobileContentService.RelationContentType.PARENT.getValue(), (PageResponse)PageResponseImpl.from(this.mobileContentFactory.convert(parents, expansions.getSubExpansions(MobileContentService.RelationContentType.PARENT.getValue())), (boolean)false).build());
        }
        if (inclusions.isInclude(MobileContentService.RelationContentType.SIBLING.getValue())) {
            List<Object> sibling;
            Page parent = page.getParent();
            if (parent == null) {
                sibling = Collections.emptyList();
            } else {
                int limit = pageRequest.getLimit() + 1;
                LimitedRequest limitedRequest = LimitedRequestImpl.create((int)pageRequest.getStart(), (int)limit, (int)limit);
                List<Page> children = this.childContentService.getPageChildren(parent.getId(), limitedRequest).stream().filter(content -> content.getId() != page.getId()).limit(pageRequest.getLimit()).collect(Collectors.toList());
                sibling = this.mobileContentFactory.convert(children, expansions.getSubExpansions(MobileContentService.RelationContentType.SIBLING.getValue()));
            }
            relationMap.put(MobileContentService.RelationContentType.SIBLING.getValue(), (PageResponse)PageResponseImpl.from(sibling, (pageRequest.getLimit() == sibling.size() ? 1 : 0) != 0).build());
        }
        if (inclusions.isInclude(MobileContentService.RelationContentType.CHILD.getValue())) {
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)pageRequest.getLimit());
            List<ContentDto> children = this.mobileContentFactory.convert(this.childContentService.getPageChildren(page.getId(), limitedRequest), expansions.getSubExpansions(MobileContentService.RelationContentType.CHILD.getValue()));
            relationMap.put(MobileContentService.RelationContentType.CHILD.getValue(), (PageResponse)PageResponseImpl.from(children, (pageRequest.getLimit() == children.size() ? 1 : 0) != 0).build());
        }
        return relationMap;
    }

    private boolean isFavourite(Content content) {
        Map currentUser = (Map)content.getMetadata().get("currentuser");
        return currentUser != null && currentUser.containsKey("favourited");
    }

    private ContentEntityObject getContentById(long id) {
        ContentEntityObject ceo = this.contentEntityManager.getById(id);
        if (ceo == null || RESTRICTED_CONTENT.contains(ceo.getContentStatusObject()) || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)ceo)) {
            throw new NotFoundException("Cannot find content with id: " + id);
        }
        return ceo;
    }
}

