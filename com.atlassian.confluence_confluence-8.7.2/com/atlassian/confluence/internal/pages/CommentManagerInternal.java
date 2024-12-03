/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.pages;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface CommentManagerInternal
extends CommentManager {
    @Deprecated
    default public @NonNull PageResponse<Comment> getChildren(Comment comment, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.getFilteredChildren(comment, pageRequest, depth, (java.util.function.Predicate<? super Comment>[])predicates);
    }

    public @NonNull PageResponse<Comment> getFilteredChildren(Comment var1, LimitedRequest var2, Depth var3, java.util.function.Predicate<? super Comment> ... var4);

    @Deprecated
    default public @NonNull PageResponse<Comment> getContainerComments(long containerId, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.getFilteredContainerComments(containerId, pageRequest, depth, (java.util.function.Predicate<? super Comment>[])predicates);
    }

    public @NonNull PageResponse<Comment> getFilteredContainerComments(long var1, LimitedRequest var3, Depth var4, java.util.function.Predicate<? super Comment> ... var5);
}

