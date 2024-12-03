/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.index.attachment;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.util.List;
import java.util.Optional;

@ExperimentalApi
public interface AttachmentTextExtractor {
    public List<String> getFileExtensions();

    public List<String> getMimeTypes();

    public Optional<InputStreamSource> extract(Attachment var1);
}

