/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.content.SpacePropertyService$SpacePropertyFinder
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.plugins.contentproperty.spaceproperty;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyQueryFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Optional;
import java.util.function.Predicate;

public class SpacePropertyFinderImpl
implements SpacePropertyService.SpacePropertyFinder {
    private final CustomContentManager customContentManager;
    private final PermissionManager permissionManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final SpaceService spaceService;
    private final PaginationService paginationService;
    private final Expansions expansions;
    private String spaceKey;
    private String key;

    public SpacePropertyFinderImpl(CustomContentManager customContentManager, PermissionManager permissionManager, JsonPropertyFactory jsonPropertyFactory, SpaceService spaceService, PaginationService paginationService, Expansions expansions) {
        this.customContentManager = customContentManager;
        this.permissionManager = permissionManager;
        this.jsonPropertyFactory = jsonPropertyFactory;
        this.spaceService = spaceService;
        this.paginationService = paginationService;
        this.expansions = expansions;
    }

    public SpacePropertyService.SpacePropertyFinder withSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
        return this;
    }

    public SpacePropertyService.SpacePropertyFinder withPropertyKey(String key) {
        this.key = key;
        return this;
    }

    public PageResponse<JsonSpaceProperty> fetchMany(PageRequest request) {
        if (this.spaceKey == null) {
            throw new NotImplementedServiceException("Must provide a non null spaceKey");
        }
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        Space space = (Space)this.spaceService.find(this.expansions.getSubExpansions("space").toArray()).withKeys(new String[]{this.spaceKey}).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("Cannot find space with key " + this.spaceKey)));
        Function<CustomContentEntityObject, JsonSpaceProperty> modelConverter = this.jsonPropertyFactory.buildSpacePropertyFromFunction(space, this.expansions);
        return this.paginationService.performPaginationListRequest(limitedRequest, from -> this.customContentManager.findByQuery(JsonPropertyQueryFactory.findAllBySpaceKey(this.spaceKey), from, this.hasViewPermission()), items -> Iterables.transform((Iterable)items, (Function)modelConverter));
    }

    public Optional<JsonSpaceProperty> fetch() {
        if (this.key == null) {
            throw new NotImplementedServiceException("Must provide a non null key");
        }
        if (this.spaceKey == null) {
            throw new NotImplementedServiceException("Must provide a non null spaceKey");
        }
        CustomContentEntityObject result = (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(JsonPropertyQueryFactory.findBySpaceKeyAndKey(this.spaceKey, this.key));
        if (result == null || !this.hasViewPermission().test(result)) {
            return Optional.empty();
        }
        return Optional.of(this.jsonPropertyFactory.buildSpacePropertyFrom(result, this.expansions));
    }

    public JsonSpaceProperty fetchOneOrNull() {
        return this.fetch().orElse(null);
    }

    private Predicate<CustomContentEntityObject> hasViewPermission() {
        return target -> this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, target);
    }
}

