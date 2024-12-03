/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.AttachmentCommentComparator;
import com.atlassian.confluence.util.AttachmentCreationDateComparator;
import com.atlassian.confluence.util.AttachmentDateComparator;
import com.atlassian.confluence.util.AttachmentNameComparator;
import com.atlassian.confluence.util.AttachmentSizeComparator;
import java.util.Comparator;

public class AttachmentComparator
implements Comparator {
    private static final Comparator FILENAME_COMPARATOR = new AttachmentNameComparator();
    private static final Comparator COMMENT_COMPARATOR = new AttachmentCommentComparator();
    private static final Comparator MODIFICATION_DATE_COMPARATOR = new AttachmentDateComparator();
    private static final Comparator CREATION_DATE_COMPARATOR = new AttachmentCreationDateComparator();
    private static final Comparator SIZE_COMPARATOR = new AttachmentSizeComparator();
    public static final String FILENAME_SORT = "name";
    public static final String COMMENT_SORT = "comment";
    public static final String MODIFICATION_DATE_SORT = "date";
    public static final String CREATION_DATE_SORT = "createddate";
    public static final String SIZE_SORT = "size";
    private Comparator comparator;
    private boolean reverse;

    public AttachmentComparator(String sortBy, boolean reverse) {
        this.comparator = this.getComparator(sortBy);
        this.reverse = reverse;
    }

    public int compare(Object o1, Object o2) {
        if (this.reverse) {
            return -1 * this.comparator.compare(o1, o2);
        }
        return this.comparator.compare(o1, o2);
    }

    private Comparator getComparator(String sortBy) {
        if (FILENAME_SORT.equals(sortBy)) {
            return FILENAME_COMPARATOR;
        }
        if (COMMENT_SORT.equals(sortBy)) {
            return COMMENT_COMPARATOR;
        }
        if (MODIFICATION_DATE_SORT.equals(sortBy)) {
            return MODIFICATION_DATE_COMPARATOR;
        }
        if (SIZE_SORT.equals(sortBy)) {
            return SIZE_COMPARATOR;
        }
        if (CREATION_DATE_SORT.equals(sortBy)) {
            return CREATION_DATE_COMPARATOR;
        }
        return FILENAME_COMPARATOR;
    }
}

