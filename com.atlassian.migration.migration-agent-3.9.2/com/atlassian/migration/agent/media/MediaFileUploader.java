/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.migration.agent.media;

import com.atlassian.migration.agent.Tracker;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MediaFileUploader {
    @Nonnull
    public String upload(InputStream var1, @Nullable String var2, Tracker var3, long var4);
}

