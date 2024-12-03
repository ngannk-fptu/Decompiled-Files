/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.content.ContentVersionService$ParameterVersionFinder
 *  com.atlassian.confluence.api.service.content.ContentVersionService$VersionFinder
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.ContentVersionServiceImpl;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.VersionFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.content.ContentVersionService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import java.util.Optional;

public class VersionFinderFactory {
    private final FinderProxyFactory finderProxyFactory;
    private final ContentEntityManagerInternal contentEntityManagerInternal;
    private final VersionFactory versionFactory;
    private final ContentFactory contentFactory;

    public VersionFinderFactory(FinderProxyFactory finderProxyFactory, ContentEntityManagerInternal contentEntityManagerInternal, VersionFactory versionFactory, ContentFactory contentFactory) {
        this.finderProxyFactory = finderProxyFactory;
        this.contentEntityManagerInternal = contentEntityManagerInternal;
        this.versionFactory = versionFactory;
        this.contentFactory = contentFactory;
    }

    public ContentVersionService.VersionFinder createVersionFinder(ContentVersionServiceImpl contentVersionService, Expansion ... expansions) {
        VersionFinderImpl versionFinder = new VersionFinderImpl(contentVersionService, expansions);
        return this.finderProxyFactory.createProxy(versionFinder, ContentVersionService.VersionFinder.class);
    }

    private class VersionFinderImpl
    extends AbstractFinder<Version>
    implements ContentVersionService.VersionFinder {
        private final ContentVersionServiceImpl contentVersionService;
        private ContentId currentContentId;
        private int versionNumber;

        public VersionFinderImpl(ContentVersionServiceImpl contentVersionService, Expansion ... expansions) {
            super(expansions);
            this.contentVersionService = contentVersionService;
        }

        public SingleFetcher<Version> withIdAndVersion(ContentId contentId, int versionNumber) {
            this.currentContentId = contentId;
            this.versionNumber = versionNumber;
            return this;
        }

        public ContentVersionService.ParameterVersionFinder withId(ContentId contentId) {
            this.currentContentId = contentId;
            return this;
        }

        public PageResponse<Version> fetchMany(PageRequest request) {
            return this.getVersions(this.currentContentId, request);
        }

        public Optional<Version> fetch() {
            return Optional.of(this.getVersion(this.currentContentId, this.versionNumber));
        }

        private PageResponse<Version> getVersions(ContentId contentId, PageRequest pageRequest) {
            this.contentVersionService.validator().validateGet(contentId).throwIfNotSuccessful();
            Expansions localExpansions = this.getExpansions();
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)PaginationLimits.version((Expansions)localExpansions));
            PageResponse<VersionHistorySummary> vhs = VersionFinderFactory.this.contentEntityManagerInternal.getVersionHistorySummaries(contentId, limitedRequest);
            return PageResponseImpl.transformResponse(vhs, m -> VersionFinderFactory.this.versionFactory.build(VersionFinderFactory.this.contentEntityManagerInternal.getById(m.getId()), localExpansions, VersionFinderFactory.this.contentFactory));
        }

        private Version getVersion(ContentId contentId, int versionNumber) {
            this.contentVersionService.validator().validateGet(contentId).throwIfNotSuccessful();
            ContentEntityObject ceo = VersionFinderFactory.this.contentEntityManagerInternal.getById(contentId);
            if (ceo == null) {
                throw new NotFoundException("Cannot find content: " + contentId);
            }
            ContentEntityObject otherVersionCeo = VersionFinderFactory.this.contentEntityManagerInternal.getOtherVersion(ceo, versionNumber);
            if (otherVersionCeo == null) {
                throw new NotFoundException("Cannot find content version: " + contentId + ", " + versionNumber);
            }
            return VersionFinderFactory.this.versionFactory.build(otherVersionCeo, this.getExpansions(), VersionFinderFactory.this.contentFactory);
        }
    }
}

