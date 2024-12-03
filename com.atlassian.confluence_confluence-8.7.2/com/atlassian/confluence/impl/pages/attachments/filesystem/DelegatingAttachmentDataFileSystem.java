/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;

public interface DelegatingAttachmentDataFileSystem
extends AttachmentDataFileSystem {
    public AttachmentDataFileSystem getDelegate();
}

