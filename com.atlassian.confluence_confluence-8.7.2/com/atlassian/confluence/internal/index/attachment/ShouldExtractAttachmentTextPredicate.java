/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.pages.Attachment;
import java.util.function.BiPredicate;

public class ShouldExtractAttachmentTextPredicate
implements BiPredicate<AttachmentTextExtractor, Attachment> {
    @Override
    public boolean test(AttachmentTextExtractor textExtractor, Attachment attachment) {
        for (String extension : textExtractor.getFileExtensions()) {
            if (!extension.equalsIgnoreCase(attachment.getFileExtension())) continue;
            return true;
        }
        for (String mimeType : textExtractor.getMimeTypes()) {
            if (!mimeType.equalsIgnoreCase(attachment.getMediaType())) continue;
            return true;
        }
        return false;
    }
}

