/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.AttachmentUpload;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import java.util.Collection;

@ExperimentalApi
public interface AttachmentService {
    public static final String COMMENT_METADATA_KEY = "comment";
    public static final String MEDIA_TYPE_METADATA_KEY = "mediaType";
    public static final String LABELS_METADATA_KEY = "labels";
    public static final String FILE_SIZE = "fileSize";

    public PageResponse<Content> addAttachments(ContentId var1, Collection<AttachmentUpload> var2) throws ServiceException;

    public PageResponse<Content> addAttachments(ContentId var1, ContentStatus var2, Collection<AttachmentUpload> var3) throws ServiceException;

    public PageResponse<Content> addAttachments(ContentId var1, ContentStatus var2, Collection<AttachmentUpload> var3, boolean var4, Expansions var5) throws ServiceException;

    public AttachmentFinder find(Expansion ... var1);

    public Content update(Content var1) throws ServiceException;

    public Content updateData(ContentId var1, AttachmentUpload var2) throws ServiceException;

    public Validator validator();

    public void delete(Content var1) throws ServiceException;

    public static interface Validator {
        public ValidationResult validateDelete(Content var1);

        public boolean canCreateAttachments(ContentId var1) throws NotFoundException;

        public boolean canCreateAttachments(ContentId var1, ContentStatus var2) throws NotFoundException;
    }

    public static interface AttachmentFinder
    extends SingleFetcher<Content>,
    ManyFetcher<Content> {
        public SingleFetcher<Content> withId(ContentId var1);

        public AttachmentFinder withContainerId(ContentId var1);

        public AttachmentFinder withFilename(String var1);

        public AttachmentFinder withMediaType(String var1);
    }
}

