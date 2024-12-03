/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.api.services;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.files.entities.ConfluenceFileEntity;
import com.atlassian.confluence.plugins.files.entities.FileContentEntity;
import com.atlassian.confluence.plugins.files.entities.FileVersionSummaryEntity;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface ConfluenceFileService {
    @Nonnull
    public ConfluenceFileEntity getFileById(long var1);

    @Nonnull
    public ConfluenceFileEntity getFileById(long var1, int var3);

    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFiles(long var1, @Nonnull PageRequest var3);

    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFilesByIds(@Nonnull List<Long> var1);

    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFilesMinusAttachmentId(long var1, @Nonnull List<Long> var3, @Nonnull PageRequest var4);

    public int getUnresolvedCommentCountByAttachmentId(long var1);

    public int getUnresolvedCommentCountByAttachmentId(long var1, int var3);

    @Nonnull
    public PageResponse<FileVersionSummaryEntity> getVersionSummaries(long var1, @Nonnull PageRequest var3);

    @Nonnull
    public Map<ConversionType, FileContentEntity> getPreviewMap(@Nonnull Attachment var1);
}

