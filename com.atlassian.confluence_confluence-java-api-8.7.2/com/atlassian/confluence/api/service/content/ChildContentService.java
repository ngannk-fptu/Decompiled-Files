/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import java.util.Collection;
import java.util.Map;

@ExperimentalApi
public interface ChildContentService {
    public ChildContentFinder findContent(ContentId var1, Expansion ... var2) throws NotFoundException;

    public static interface ChildContentFinder {
        public ChildContentFinder withDepth(Depth var1);

        public ChildContentFinder withParentVersion(int var1);

        public Map<ContentType, PageResponse<Content>> fetchMappedByType(PageRequest var1) throws BadRequestException;

        public PageResponse<Content> fetchMany(ContentType var1, PageRequest var2) throws BadRequestException;

        public ChildContentFinder withLocation(Collection<String> var1);
    }
}

