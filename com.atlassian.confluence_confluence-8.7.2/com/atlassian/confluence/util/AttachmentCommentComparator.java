/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Attachment;
import java.text.Collator;
import java.util.Comparator;

public class AttachmentCommentComparator
implements Comparator {
    public int compare(Object o1, Object o2) {
        String firstComment = this.getComment(o1);
        String secondComment = this.getComment(o2);
        return Collator.getInstance().compare(firstComment, secondComment);
    }

    private String getComment(Object o) {
        if (o instanceof Attachment) {
            Attachment attachment = (Attachment)o;
            return attachment.getVersionComment();
        }
        throw new ClassCastException("Expecting either an Attachment or SearchResultWithExcerpt. Got: " + o.getClass());
    }
}

