/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 */
package com.atlassian.crowd.search.util;

import com.atlassian.crowd.model.DirectoryEntity;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsUtil {
    public static List<String> convertEntitiesToNames(Iterable<? extends DirectoryEntity> entities) {
        ArrayList<String> names = new ArrayList<String>();
        for (DirectoryEntity directoryEntity : entities) {
            names.add(directoryEntity.getName());
        }
        return names;
    }

    public static <T> List<T> constrainResults(List<T> results, int startIndex, int maxResults) {
        int endIndex = startIndex + maxResults;
        if (endIndex < 0 || endIndex > results.size() || maxResults == -1) {
            endIndex = results.size();
        }
        if (startIndex > endIndex) {
            startIndex = endIndex;
        }
        return results.subList(startIndex, endIndex);
    }
}

