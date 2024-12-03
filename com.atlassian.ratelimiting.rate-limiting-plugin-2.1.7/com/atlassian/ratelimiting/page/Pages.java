/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.page;

import com.atlassian.ratelimiting.page.DefaultPage;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Pages {
    private Pages() {
    }

    public static <T> Page<T> createPage(List<T> content, PageRequest pageRequest, int totalElements) {
        return new DefaultPage<T>(content, pageRequest, totalElements);
    }

    public static <T> Page<T> paginate(Stream<T> content, PageRequest pageRequest, int totalElements) {
        List collectedContent = content.limit((long)pageRequest.getOffset() + (long)pageRequest.getSize()).collect(Collectors.toList());
        List pagedContent = Pages.getPageFromList(collectedContent, pageRequest.getOffset());
        return Pages.createPage(pagedContent, pageRequest, totalElements);
    }

    private static <T> List<T> getPageFromList(List<T> cappedContent, int fromIndex) {
        try {
            return cappedContent.subList(fromIndex, cappedContent.size());
        }
        catch (IllegalArgumentException exception) {
            return Collections.emptyList();
        }
    }
}

