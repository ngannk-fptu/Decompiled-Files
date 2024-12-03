/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.api.services;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import javax.annotation.Nonnull;

public interface FileCommentService {
    @Nonnull
    public FileComment getCommentById(long var1, int var3, long var4);

    @Nonnull
    public FileComment getCommentById(long var1, long var3);

    @Nonnull
    public PageResponse<FileComment> getComments(long var1, int var3, @Nonnull PageRequest var4);

    @Nonnull
    public PageResponse<FileComment> getComments(long var1, @Nonnull PageRequest var3);

    @Nonnull
    public FileComment createComment(long var1, int var3, @Nonnull FileCommentInput var4);

    @Nonnull
    public FileComment createComment(long var1, @Nonnull FileCommentInput var3);

    public void deleteComment(long var1, int var3, long var4);

    public void deleteComment(long var1, long var3);

    @Nonnull
    public FileComment updateComment(long var1, int var3, long var4, @Nonnull FileCommentInput var6);

    @Nonnull
    public FileComment updateComment(long var1, long var3, @Nonnull FileCommentInput var5);
}

