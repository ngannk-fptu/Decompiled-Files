/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Attachment;
import java.util.Comparator;

public class AttachmentSizeComparator
implements Comparator<Attachment> {
    @Override
    public int compare(Attachment o1, Attachment o2) {
        return Long.compare(o1.getFileSize(), o2.getFileSize());
    }
}

