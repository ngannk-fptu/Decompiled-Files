/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.like;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.Like;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LikeManager {
    public Like addLike(ContentEntityObject var1, User var2);

    public void removeLike(ContentEntityObject var1, User var2);

    public void removeAllLikesOn(ContentEntityObject var1);

    @Deprecated
    public void removeAllLikesFor(String var1);

    public void removeAllLikesFor(@NonNull UserKey var1);

    public boolean hasLike(ContentEntityObject var1, User var2);

    @Transactional(readOnly=true)
    public List<Like> getLikes(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public Map<Long, List<Like>> getLikes(Collection<? extends ContentEntityObject> var1);

    @Transactional(readOnly=true)
    public Map<Searchable, Integer> countLikes(Collection<? extends Searchable> var1);

    @Transactional(readOnly=true)
    public int countLikes(Searchable var1);
}

