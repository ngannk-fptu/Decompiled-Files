/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;

public interface NewCommentDeduplicator {
    @Deprecated
    default public Option<Comment> findDuplicateComment(Iterable<Comment> existingComments) {
        return FugueConversionUtil.toComOption(this.getDuplicateComment(existingComments));
    }

    public Optional<Comment> getDuplicateComment(Iterable<Comment> var1);

    public void newCommentSaved(Comment var1);
}

