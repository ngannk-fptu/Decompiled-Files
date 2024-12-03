/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.content.ChildContentService$ChildContentFinder
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.factory.ChildSupport;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ChildContentServiceImpl
implements ChildContentService {
    private final ChildSupport childSupport;
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final FinderProxyFactory finderProxyFactory;

    public ChildContentServiceImpl(ChildSupport childSupport, ContentEntityManager contentEntityManager, PermissionManager permissionManager, FinderProxyFactory finderProxyFactory) {
        this.childSupport = childSupport;
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.finderProxyFactory = finderProxyFactory;
    }

    public ChildContentService.ChildContentFinder findContent(ContentId parentId, Expansion ... expansions) throws NotFoundException {
        ChildContentFinderImpl finder = new ChildContentFinderImpl(parentId, expansions);
        return this.finderProxyFactory.createProxy(finder, ChildContentService.ChildContentFinder.class);
    }

    private class ChildContentFinderImpl
    implements ChildContentService.ChildContentFinder {
        private final Expansions expansions;
        private final ContentId parentId;
        private int parentVersion = 0;
        private Depth depth = Depth.ROOT;
        private Collection<String> location = Collections.emptyList();

        public ChildContentFinderImpl(ContentId parentId, Expansion ... expansions) throws NotFoundException {
            Preconditions.checkNotNull((Object)parentId);
            Preconditions.checkNotNull((Object)expansions);
            this.parentId = parentId;
            this.expansions = new Expansions(expansions);
        }

        public ChildContentService.ChildContentFinder withDepth(Depth depth) {
            this.depth = (Depth)Preconditions.checkNotNull((Object)depth);
            return this;
        }

        public ChildContentService.ChildContentFinder withParentVersion(int parentVersion) {
            this.parentVersion = parentVersion;
            return this;
        }

        public Map<ContentType, PageResponse<Content>> fetchMappedByType(PageRequest request) throws BadRequestException {
            Preconditions.checkNotNull((Object)request);
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.content((Expansions)this.expansions));
            return ChildContentServiceImpl.this.childSupport.getChildrenMap(this.fetchParent(), limitedRequest, this.expansions, this.depth, this.location);
        }

        public PageResponse<Content> fetchMany(ContentType type, PageRequest request) throws BadRequestException {
            Preconditions.checkNotNull((Object)request);
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.content((Expansions)this.expansions));
            return ChildContentServiceImpl.this.childSupport.getChildrenList(this.fetchParent(), type, limitedRequest, this.expansions, this.depth, this.location);
        }

        public ChildContentService.ChildContentFinder withLocation(Collection<String> location) {
            this.location = location;
            return this;
        }

        private ContentConvertible fetchParent() throws NotFoundException {
            ContentEntityObject parentEntity = ChildContentServiceImpl.this.contentEntityManager.getById(this.parentId.asLong());
            if (parentEntity == null || !ChildContentServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, parentEntity)) {
                throw new NotFoundException("No parent or not permitted to view content with id : " + this.parentId);
            }
            if (this.parentVersion != 0 && (parentEntity = ChildContentServiceImpl.this.contentEntityManager.getOtherVersion(parentEntity, this.parentVersion)) == null) {
                throw new NotFoundException("No parent content with id : " + this.parentId + " and version : " + this.parentVersion);
            }
            return (ContentConvertible)((Object)parentEntity);
        }
    }
}

