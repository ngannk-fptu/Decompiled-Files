/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  com.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.manager;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.files.api.SavableContent;
import com.atlassian.confluence.plugins.files.entities.FileContentEntity;
import com.atlassian.confluence.plugins.files.entities.FileVersionSummaryEntity;
import com.atlassian.fugue.Option;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface ConfluenceFileManager {
    @Nonnull
    public Option<Attachment> getPreviewContent(@Nonnull Attachment var1, @Nonnull ConversionType var2);

    @Nonnull
    public Map<ConversionType, FileContentEntity> getPreviewMap(@Nonnull Attachment var1);

    @Nonnull
    public PageResponse<FileVersionSummaryEntity> getVersionSummaries(long var1, @Nonnull PageRequest var3);

    public int getUnresolvedCommentCountByAttachmentId(long var1);

    public int getUnresolvedCommentCountByAttachmentId(long var1, int var3);

    @Nonnull
    public PageResponse<Attachment> getFilesMinusAttachmentId(long var1, List<Long> var3, @Nonnull PageRequest var4);

    @Nonnull
    public PageResponse<Attachment> getFilesForContent(long var1, @Nonnull PageRequest var3);

    public void savePreview(@Nonnull Attachment var1, @Nonnull SavableContent var2, @Nonnull ConversionType var3);
}

