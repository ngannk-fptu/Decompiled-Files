/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.dashboard.macros.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentMacroNames {
    private final long contentId;
    private final Long commentParentContentId;
    private final Collection<String> macroNames;
    private final List<ContentMacroNames> comments;

    public ContentMacroNames(long contentId, Long commentParentContentId, Collection<String> macroNames, List<ContentMacroNames> comments) {
        this.contentId = contentId;
        this.commentParentContentId = commentParentContentId;
        this.macroNames = macroNames != null ? Collections.unmodifiableCollection(macroNames) : null;
        this.comments = comments != null ? Collections.unmodifiableList(comments) : null;
    }

    public ContentMacroNames(ContentMacroNames prototype, Collection<String> macroNames, List<ContentMacroNames> comments) {
        this(prototype.getContentId(), prototype.getCommentParentContentId(), macroNames, comments);
    }

    public long getContentId() {
        return this.contentId;
    }

    public Long getCommentParentContentId() {
        return this.commentParentContentId;
    }

    public boolean isComment() {
        return this.commentParentContentId != null;
    }

    public Collection<String> getMacroNames() {
        return this.macroNames;
    }

    public List<ContentMacroNames> getComments() {
        return this.comments;
    }

    public Collection<String> getMacroNamesIncludingComments() {
        Collection<String> page = this.getMacroNames();
        if (page == null) {
            return null;
        }
        Collection<String> comment = this.getCommentMacroNames();
        if (comment == null) {
            return null;
        }
        HashSet<String> allMacroNames = new HashSet<String>();
        allMacroNames.addAll(page);
        allMacroNames.addAll(comment);
        return Collections.unmodifiableCollection(allMacroNames);
    }

    public Collection<String> getCommentMacroNames() {
        HashSet<String> set = new HashSet<String>();
        for (ContentMacroNames comment : this.getComments()) {
            if (comment.getMacroNames() == null) {
                return null;
            }
            set.addAll(comment.getMacroNames());
        }
        return Collections.unmodifiableCollection(set);
    }

    public static List<ContentMacroNames> merge(List<ContentMacroNames> list, List<ContentMacroNames> knownCommentsAndMacroNames) {
        if (list == null || knownCommentsAndMacroNames == null) {
            return list;
        }
        ImmutableMap map = Maps.uniqueIndex(list, ContentMacroNames::getContentId);
        ImmutableList.Builder result = ImmutableList.builder();
        for (ContentMacroNames item : knownCommentsAndMacroNames) {
            ContentMacroNames current = (ContentMacroNames)map.get(item.getContentId());
            if (current == null) {
                result.add((Object)item);
                continue;
            }
            if (current.getMacroNames() != null) {
                result.add((Object)current);
                continue;
            }
            result.add((Object)new ContentMacroNames(current, item.getMacroNames(), ContentMacroNames.merge(current.getComments(), item.getComments())));
        }
        return result.build();
    }

    public static List<ContentMacroNames> makeCommentHierarchy(List<ContentMacroNames> flattenedContentAndComments) {
        ArrayList<ContentMacroNames> parentContents = new ArrayList<ContentMacroNames>();
        HashMap<Long, List<ContentMacroNames>> parentContentIdToComments = new HashMap<Long, List<ContentMacroNames>>();
        ContentMacroNames.splitIntoParentContentAndComments(flattenedContentAndComments, parentContents, parentContentIdToComments);
        return Collections.unmodifiableList(parentContents.stream().map(parentContent -> {
            List comments = parentContentIdToComments.getOrDefault(parentContent.getContentId(), new ArrayList());
            return new ContentMacroNames(parentContent.getContentId(), null, parentContent.getMacroNames(), comments);
        }).collect(Collectors.toList()));
    }

    private static void splitIntoParentContentAndComments(List<ContentMacroNames> flattenedContentsAndComments, List<ContentMacroNames> parentContents, Map<Long, List<ContentMacroNames>> parentContentIdToComments) {
        for (ContentMacroNames content : flattenedContentsAndComments) {
            if (content.isComment()) {
                ContentMacroNames.addToMapList(parentContentIdToComments, content.getCommentParentContentId(), content);
                continue;
            }
            parentContents.add(content);
        }
    }

    private static <K, V> void addToMapList(Map<K, List<V>> map, K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }
        list.add(value);
    }
}

