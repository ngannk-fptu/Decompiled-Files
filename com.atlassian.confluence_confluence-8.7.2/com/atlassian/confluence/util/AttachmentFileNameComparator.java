/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.NaturalStringComparator;
import java.util.Comparator;
import java.util.Locale;

public class AttachmentFileNameComparator
implements Comparator<Attachment> {
    private final NaturalStringComparator comparator;

    public AttachmentFileNameComparator(Locale locale) {
        this.comparator = new NaturalStringComparator(locale);
    }

    @Override
    public int compare(Attachment first, Attachment second) {
        return this.comparator.compare(first.getFileName(), second.getFileName());
    }
}

