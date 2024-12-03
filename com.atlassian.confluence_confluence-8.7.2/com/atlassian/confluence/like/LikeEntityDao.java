/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.like;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface LikeEntityDao {
    public @NonNull LikeEntity addLike(ContentEntityObject var1, User var2);

    public void removeLike(ContentEntityObject var1, User var2);

    public void removeAllLikesOn(ContentEntityObject var1);

    @Deprecated
    public void removeAllLikesFor(String var1);

    public void removeAllLikesFor(@NonNull UserKey var1);

    public boolean hasLike(ContentEntityObject var1, User var2);

    public @NonNull List<LikeEntity> getLikeEntities(Collection<? extends ContentEntityObject> var1);

    public int countLikes(Searchable var1);

    public @NonNull Map<Searchable, Integer> countLikes(Collection<? extends Searchable> var1);
}

