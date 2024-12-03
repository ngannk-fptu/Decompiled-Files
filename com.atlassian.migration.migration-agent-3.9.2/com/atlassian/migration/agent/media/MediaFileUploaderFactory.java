/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.migration.agent.media;

import com.atlassian.migration.agent.media.MediaFileUploader;
import javax.annotation.Nonnull;

public interface MediaFileUploaderFactory {
    @Nonnull
    public MediaFileUploader create(String var1);
}

