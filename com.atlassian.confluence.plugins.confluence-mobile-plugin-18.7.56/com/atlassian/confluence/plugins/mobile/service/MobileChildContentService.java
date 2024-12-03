/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.pages.Page
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface MobileChildContentService {
    @Nonnull
    public List<Page> getPageChildren(long var1, LimitedRequest var3);

    @Nonnull
    public Map<Long, Integer> getPageChildrenCount(@Nonnull List<Long> var1);

    @Nonnull
    public PageResponse<CommentDto> getComments(@Nonnull ContentId var1, @Nonnull Expansions var2, @Nonnull Inclusions var3);
}

