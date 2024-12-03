/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.FavouriteDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface MobileContentService {
    @Nonnull
    public ContentDto getContent(long var1);

    public Boolean favourite(Long var1);

    public Boolean removeFavourite(Long var1);

    @Nonnull
    @Deprecated
    public List<FavouriteDto> getFavourites(PageRequest var1);

    @Nonnull
    public ContentMetadataDto getContentMetadata(ContentId var1);

    @Nonnull
    public ContentMetadataDto getCreationContentMetadata(Context var1);

    @Nonnull
    public PageResponse<ContentDto> getSavedList(PageRequest var1);

    @Nonnull
    public Map<String, PageResponse> getRelationContent(long var1, Expansions var3, Inclusions var4, PageRequest var5);

    public static enum RelationContentType {
        ANCESTOR("ancestor"),
        PARENT("parent"),
        SIBLING("sibling"),
        CHILD("child");

        private String value;

        private RelationContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static RelationContentType getEnum(String value) {
            for (RelationContentType type : RelationContentType.values()) {
                if (!value.equalsIgnoreCase(type.toString())) continue;
                return type;
            }
            throw new NotFoundException("Cannot find relation content type of this value: " + value);
        }
    }
}

