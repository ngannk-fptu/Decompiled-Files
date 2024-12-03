/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.pages.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.pages.Comment;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CommentDao
extends ObjectDao {
    public Comment getById(long var1);

    public Iterator getRecentlyUpdatedComments(long var1, int var3);

    public List<Comment> getContainerComments(long var1, Date var3);

    public List<Comment> getContainerComments(long var1, Date var3, String var4);

    @Deprecated
    public PageResponse<Comment> getContainerComments(long var1, LimitedRequest var3, Depth var4, Predicate<? super Comment> ... var5);

    public Map<Searchable, Integer> countComments(Collection<? extends Searchable> var1);

    public int countComments(Searchable var1);

    public int countAllCommentVersions();

    @Deprecated
    public PageResponse<Comment> getChildren(Comment var1, LimitedRequest var2, Depth var3, Predicate<? super Comment> ... var4);

    public Map<Long, Integer> countUnresolvedComments(@NonNull Collection<Long> var1);
}

