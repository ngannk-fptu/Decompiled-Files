/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.like.Like
 */
package com.atlassian.confluence.plugins.like;

import com.atlassian.confluence.like.Like;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LikesSorter {
    public List<Like> sort(Collection<Like> likes, Set<String> followees) {
        if (likes == null || likes.isEmpty()) {
            return Collections.emptyList();
        }
        if (followees == null || followees.isEmpty()) {
            return new ArrayList<Like>(likes);
        }
        LinkedList<Like> networkLikes = new LinkedList<Like>();
        LinkedList<Like> otherLikes = new LinkedList<Like>();
        for (Like like : likes) {
            if (followees.contains(like.getUsername())) {
                networkLikes.add(like);
                continue;
            }
            otherLikes.add(like);
        }
        networkLikes.addAll(otherLikes);
        return networkLikes;
    }
}

