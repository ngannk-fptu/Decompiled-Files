/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.LabelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceMetadataFactory;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultSpaceMetadataFactory
implements SpaceMetadataFactory {
    private static final String LABELS_EXPAND = "labels";
    private final LabelFactory labelFactory;
    private final PaginationService paginationService;

    public DefaultSpaceMetadataFactory(LabelFactory labelFactory, PaginationService paginationService) {
        this.labelFactory = labelFactory;
        this.paginationService = paginationService;
    }

    @Override
    public Map<String, Object> makeMetadata(Space space, Fauxpansions fauxpansions) {
        if (!fauxpansions.canExpand()) {
            return BuilderUtils.collapsedMap();
        }
        ModelMapBuilder metadata = ModelMapBuilder.newExpandedInstance();
        if (!fauxpansions.getSubExpansions().canExpand(LABELS_EXPAND)) {
            metadata.addCollapsedEntry((Object)LABELS_EXPAND);
        } else {
            LimitedRequest limitedRequest = LimitedRequestImpl.create((int)PaginationLimits.labels());
            List<Label> labels = space.getDescription().getVisibleLabels(AuthenticatedUserThreadLocal.get());
            Collections.sort(labels);
            Function<Iterable, Iterable> modelConverter = items -> this.labelFactory.buildFrom(items, fauxpansions.getSubExpansions().getSubExpansions(LABELS_EXPAND));
            PageResponse response = this.paginationService.performPaginationListRequest(limitedRequest, request -> PageResponseImpl.from((Iterable)labels, (labels.size() > limitedRequest.getLimit() ? 1 : 0) != 0).pageRequest(limitedRequest).build(), modelConverter);
            metadata.put((Object)LABELS_EXPAND, (Object)response);
        }
        return metadata.build();
    }
}

