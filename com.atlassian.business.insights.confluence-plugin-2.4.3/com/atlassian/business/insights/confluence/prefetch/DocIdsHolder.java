/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.prefetch;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DocIdsHolder {
    private static final String DOC_ID_TYPE_CONTENT_SEPERATOR = "-";
    private final HashMap<String, List<Long>> handles = new LinkedHashMap<String, List<Long>>();

    public void addDocId(@Nonnull String docId) {
        Objects.requireNonNull(docId, "docId must not be null");
        String[] arr = docId.split(DOC_ID_TYPE_CONTENT_SEPERATOR);
        if (arr.length != 2) {
            throw new IllegalArgumentException(String.format("Bad format. Could not add docId[%s]", docId));
        }
        String classRef = arr[0];
        String contentId = arr[1];
        if (this.handles.containsKey(classRef)) {
            this.handles.get(classRef).add(Long.valueOf(contentId));
        } else {
            this.handles.put(classRef, Lists.newArrayList((Object[])new Long[]{Long.valueOf(contentId)}));
        }
    }

    public int size() {
        return this.handles.values().stream().mapToInt(List::size).sum();
    }

    public List<String> getIds(int startOffset, int limit) {
        if (startOffset < 0 || limit <= 0) {
            throw new IllegalArgumentException(String.format("Invalid arguments passed; startOffset:%d, limit:%d", startOffset, limit));
        }
        return this.handles.entrySet().stream().flatMap(entry -> ((List)entry.getValue()).stream().map(id -> (String)entry.getKey() + DOC_ID_TYPE_CONTENT_SEPERATOR + id)).skip(startOffset).limit(limit).collect(Collectors.toList());
    }

    public List<Long> getContentIds(int startOffset, int limit) {
        if (startOffset < 0 || limit <= 0) {
            throw new IllegalArgumentException(String.format("Invalid arguments passed; startOffset:%d, limit:%d", startOffset, limit));
        }
        return this.handles.entrySet().stream().flatMap(entry -> ((List)entry.getValue()).stream()).skip(startOffset).limit(limit).collect(Collectors.toList());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DocIdsHolder idsHolder = (DocIdsHolder)o;
        return Objects.equals(this.handles, idsHolder.handles);
    }

    public int hashCode() {
        return Objects.hash(this.handles);
    }
}

