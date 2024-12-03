/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.index.attachment;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.util.Optional;

@Internal
public interface AttachmentExtractedTextManager {
    public Optional<InputStreamSource> getContent(Attachment var1);

    public void saveContent(Attachment var1, InputStreamSource var2);

    public void removePreviousVersionContent(Attachment var1);

    public void removeContent(Attachment var1);
}

