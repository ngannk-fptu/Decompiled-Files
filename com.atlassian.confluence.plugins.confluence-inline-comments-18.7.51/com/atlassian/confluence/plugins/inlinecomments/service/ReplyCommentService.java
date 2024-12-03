/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentResult;
import com.atlassian.confluence.plugins.inlinecomments.entities.Reply;
import java.util.List;

public interface ReplyCommentService {
    public InlineCommentResult<List<Reply>> getReplies(long var1);

    public InlineCommentResult<Reply> createReply(Reply var1, Long var2);

    public InlineCommentResult deleteReply(Long var1);

    public InlineCommentResult<Reply> updateReply(Reply var1);
}

