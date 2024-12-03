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
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.SpaceStatus;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import java.util.Map;

@ExperimentalApi
public interface SpaceService {
    public Space create(Space var1, boolean var2) throws ServiceException;

    public Space update(Space var1) throws ServiceException;

    public SpaceFinder find(Expansion ... var1);

    public SpaceContentFinder findContent(Space var1, Expansion ... var2) throws NotFoundException;

    public LongTaskSubmission delete(Space var1) throws ServiceException;

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateCreate(Space var1, boolean var2);

        public ValidationResult validateUpdate(Space var1);

        public ValidationResult validateDelete(Space var1);
    }

    public static interface SpaceContentFinder {
        public SpaceContentFinder withDepth(Depth var1);

        public Map<ContentType, PageResponse<Content>> fetchMappedByType(PageRequest var1) throws BadRequestException, NotFoundException;

        public PageResponse<Content> fetchMany(ContentType var1, PageRequest var2) throws BadRequestException, NotFoundException;
    }

    public static interface SingleSpaceFetcher
    extends SingleFetcher<Space> {
    }

    public static interface SpaceFinder
    extends SingleSpaceFetcher,
    ManyFetcher<Space> {
        public SpaceFinder withKeys(String ... var1);

        public SpaceFinder withType(SpaceType var1);

        public SpaceFinder withStatus(SpaceStatus var1);

        public SpaceFinder withLabels(Label ... var1);

        public SpaceFinder withIsFavourited(boolean var1);

        public SpaceFinder withHasRetentionPolicy(boolean var1);
    }
}

