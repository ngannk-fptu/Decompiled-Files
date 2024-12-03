/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.index.attachment;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import java.util.concurrent.CompletionStage;

@FunctionalInterface
@Internal
public interface AttachmentTextExtractionService {
    public CompletionStage<AttachmentTextExtraction> submit(long var1, int var3);
}

