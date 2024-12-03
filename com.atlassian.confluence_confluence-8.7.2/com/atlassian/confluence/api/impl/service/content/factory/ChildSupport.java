/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.finder.FinderPredicates;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupportProvider;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChildSupport {
    private final ContentTypeApiSupportProvider bindingProvider;
    private final NavigationService navigationService;
    private final ContentEntityManager contentEntityManager;

    public ChildSupport(ContentTypeApiSupportProvider bindingProvider, NavigationService navigationService, ContentEntityManager contentEntityManager) {
        this.bindingProvider = bindingProvider;
        this.navigationService = navigationService;
        this.contentEntityManager = contentEntityManager;
    }

    public Map<ContentType, PageResponse<Content>> getChildrenMap(ContentSelector parentSelector, LimitedRequest limitedRequest, Expansions childExpansions, Depth depth) {
        return this.getChildrenMap(parentSelector, limitedRequest, childExpansions, depth, (Collection<String>)Sets.newHashSet());
    }

    public Map<ContentType, PageResponse<Content>> getChildrenMap(ContentSelector parentSelector, LimitedRequest limitedRequest, Expansions childExpansions, Depth depth, Collection<String> location) {
        int parentVersion;
        long parentId = parentSelector.getId().asLong();
        ContentEntityObject contentConvertible = this.contentEntityManager.getById(parentId);
        if (contentConvertible == null) {
            throw new NotFoundException(String.format("No parent content with id : %s", parentId));
        }
        if (parentSelector.hasVersion() && (contentConvertible = this.contentEntityManager.getOtherVersion(contentConvertible, parentVersion = parentSelector.getVersion())) == null) {
            throw new NotFoundException("No parent content with id : " + parentId + " and version : " + parentVersion);
        }
        return this.getChildrenMap((ContentConvertible)((Object)contentConvertible), limitedRequest, childExpansions, depth, location);
    }

    public Map<ContentType, PageResponse<Content>> getChildrenMap(ContentConvertible parent, LimitedRequest limitedRequest, Expansions childExpansions, Depth depth, Collection<String> location) {
        HashMap childrenMap = Maps.newHashMap();
        if (limitedRequest == null) {
            limitedRequest = this.makeDefaultLimitedRequest(childExpansions);
        }
        ContentType parentType = (ContentType)Preconditions.checkNotNull((Object)parent.getContentTypeObject());
        Set<ContentType> availableChildrenTypes = this.getChildContentTypes(parentType);
        for (ContentType childType : availableChildrenTypes) {
            if (depth == Depth.ALL && (parentType == ContentType.PAGE && childType == ContentType.PAGE || parentType == ContentType.COMMENT && childType == ContentType.COMMENT)) continue;
            childrenMap.put(childType, this.getChildrenForExpansions(parent, childType, limitedRequest, childExpansions, depth, location));
        }
        Navigation.Builder navBuilder = this.navigationService.createNavigation().content(parent.getSelector()).children(depth);
        return ModelMapBuilder.newInstance((Map)childrenMap).navigable(navBuilder).build();
    }

    public PageResponse<Content> getChildrenList(ContentConvertible parent, ContentType childType, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Collection<String> location) {
        ContentTypeApiSupport typeBinding = this.bindingProvider.getForType(childType);
        ContentType parentType = parent.getContentTypeObject();
        Preconditions.checkState((boolean)typeBinding.supportsChildrenForParentType(parentType), (String)"Cannot find children of type %s for parent type %s", (Object)childType, (Object)parentType);
        Predicate<? super ContentEntityObject> predicate = null;
        if (!location.isEmpty()) {
            predicate = FinderPredicates.createCommentLocationPredicate(location);
        }
        return typeBinding.getFilteredChildren(parent, limitedRequest, expansions, depth, predicate);
    }

    private LimitedRequest makeDefaultLimitedRequest(Expansions childExpansions) {
        int limit = PaginationLimits.childMap((Expansions)childExpansions);
        SimplePageRequest pageRequest = new SimplePageRequest(0, limit);
        return LimitedRequestImpl.create((PageRequest)pageRequest, (int)limit);
    }

    private PageResponse<Content> getChildrenForExpansions(ContentConvertible parent, ContentType childType, LimitedRequest limitedRequest, Expansions childExpansions, Depth depth, Collection<String> location) {
        Navigation.Builder navBuilder = this.navigationService.createNavigation().content(parent.getSelector()).children(childType, depth);
        String childTypeStr = childType.getType();
        if (!childExpansions.canExpand(childTypeStr)) {
            return BuilderUtils.collapsedPageResponse((Navigation.Builder)navBuilder);
        }
        Expansions typeExpansions = childExpansions.getSubExpansions(childTypeStr);
        PageResponse<Content> children = this.getChildrenList(parent, childType, limitedRequest, typeExpansions, depth, location);
        RestPageRequest request = new RestPageRequest(navBuilder, limitedRequest.getStart(), limitedRequest.getLimit());
        return RestList.createRestList((PageRequest)request, children);
    }

    private Set<ContentType> getChildContentTypes(ContentType type) {
        ContentTypeApiSupport apiSupport = this.bindingProvider.getForType(type);
        return this.bindingProvider.getList().stream().filter(item -> item.supportsChildrenForParentType(type) && apiSupport.supportsChildrenOfType(item.getHandledType())).map(ContentTypeApiSupport::getHandledType).collect(Collectors.toSet());
    }
}

