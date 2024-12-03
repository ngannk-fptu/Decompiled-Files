/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.LowercaseFieldSort;

public class FilenameSort
extends AbstractSort {
    public static final String KEY = "filename";
    public static final FilenameSort DESCENDING = new FilenameSort(SearchSort.Order.DESCENDING);
    public static final FilenameSort ASCENDING;
    public static final FilenameSort DEFAULT;

    public FilenameSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new LowercaseFieldSort(SearchFieldNames.ATTACHMENT_FILE_NAME_UNTOKENIZED, this.getOrder());
    }

    static {
        DEFAULT = ASCENDING = new FilenameSort(SearchSort.Order.ASCENDING);
    }
}

