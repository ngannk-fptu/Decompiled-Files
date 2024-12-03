/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.spaces.Space;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MobileSpaceService {
    @Nonnull
    public PageResponse<SpaceDto> getSpaces(@Nullable String var1, @Nonnull Expansions var2, @Nonnull Inclusions var3, @Nonnull PageRequest var4);

    @Nonnull
    public ContentDto getHomePage(@Nonnull String var1, @Nonnull Expansions var2);

    @Nullable
    public Space getSuggestionSpace();
}

