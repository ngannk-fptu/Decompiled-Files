/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.util.collections.GuavaConversionUtil
 */
package com.atlassian.confluence.internal.pages.persistence;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.persistence.dao.CommentDao;
import com.atlassian.confluence.util.collections.GuavaConversionUtil;
import java.util.function.Predicate;

public interface CommentDaoInternal
extends CommentDao,
ObjectDaoInternal<Comment> {
    default public PageResponse<Comment> getFilteredChildren(Comment comment, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.getChildren(comment, pageRequest, depth, GuavaConversionUtil.toGuavaPredicates((Predicate[])predicates));
    }

    default public PageResponse<Comment> getFilteredContainerComments(long containerId, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.getContainerComments(containerId, pageRequest, depth, GuavaConversionUtil.toGuavaPredicates((Predicate[])predicates));
    }
}

