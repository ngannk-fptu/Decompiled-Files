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
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ChildSupport;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import java.util.Map;

public class ChildContentFactory {
    private final ChildSupport childSupport;
    private final NavigationService navigationService;

    public ChildContentFactory(ChildSupport childSupport, NavigationService navigationService) {
        this.childSupport = childSupport;
        this.navigationService = navigationService;
    }

    public Map<ContentType, PageResponse<Content>> buildFrom(ContentSelector parentSelector, Depth depth, Fauxpansions fauxpansions) {
        if (fauxpansions.canExpand()) {
            LimitedRequest limitedReq = LimitedRequestImpl.create((int)PaginationLimits.childMap((Expansions)fauxpansions.getSubExpansions()));
            return this.childSupport.getChildrenMap(parentSelector, limitedReq, fauxpansions.getSubExpansions(), depth);
        }
        Navigation.Builder builder = this.navigationService.createNavigation().content(parentSelector).children(depth);
        return BuilderUtils.collapsedMap((Navigation.Builder)builder);
    }
}

