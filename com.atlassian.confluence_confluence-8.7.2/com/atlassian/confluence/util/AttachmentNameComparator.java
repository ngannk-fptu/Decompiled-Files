/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Attachment;
import java.text.Collator;
import java.util.Comparator;

public class AttachmentNameComparator
implements Comparator {
    public int compare(Object o1, Object o2) {
        String firstFileName = this.getFileName(o1);
        String secondFileName = this.getFileName(o2);
        return Collator.getInstance().compare(firstFileName, secondFileName);
    }

    private String getFileName(Object o) {
        if (o instanceof Attachment) {
            Attachment attachment = (Attachment)o;
            return attachment.getFileName();
        }
        throw new ClassCastException("Expecting either an Attachment or SearchResultWithExcerpt. Got: " + o.getClass());
    }
}

