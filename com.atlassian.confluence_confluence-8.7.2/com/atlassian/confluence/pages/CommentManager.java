/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bonnie.Searchable
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.NewCommentDeduplicator;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface CommentManager
extends ContentEntityManager {
    public Comment getComment(long var1);

    @Deprecated
    public @NonNull Comment addCommentToPage(AbstractPage var1, @Nullable Comment var2, String var3);

    public @NonNull Comment addCommentToObject(ContentEntityObject var1, @Nullable Comment var2, String var3);

    public @NonNull Comment addCommentToObject(ContentEntityObject var1, @Nullable Comment var2, String var3, NewCommentDeduplicator var4);

    public void updateCommentContent(Comment var1, String var2);

    @Deprecated
    public void removeCommentFromPage(long var1);

    public void removeCommentFromObject(long var1);

    public @NonNull Iterator getRecentlyUpdatedComments(Space var1, int var2);

    @Deprecated
    public @NonNull List<Comment> getPageComments(long var1, Date var3);

    public @NonNull List<Comment> getPageComments(long var1, Date var3, String var4);

    @Deprecated
    public @NonNull List<Comment> getPageLevelComments(long var1, Date var3);

    public @NonNull Map<Searchable, Integer> countComments(Collection<? extends Searchable> var1);

    public int countComments(Searchable var1);

    public int countAllCommentVersions();

    public @NonNull Map<Long, Integer> countUnresolvedComments(Collection<Long> var1);
}

