/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;

public class FilesizeSort
extends AbstractSort {
    public static final String KEY = "filesize";
    public static final FilesizeSort DESCENDING = new FilesizeSort(SearchSort.Order.DESCENDING);
    public static final FilesizeSort ASCENDING = new FilesizeSort(SearchSort.Order.ASCENDING);
    public static final FilesizeSort DEFAULT = DESCENDING;

    public FilesizeSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new FieldSort(SearchFieldNames.ATTACHMENT_FILE_SIZE, SearchSort.Type.LONG, this.getOrder());
    }
}

