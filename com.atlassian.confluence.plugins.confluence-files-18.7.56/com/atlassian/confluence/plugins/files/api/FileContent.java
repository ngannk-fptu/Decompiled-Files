/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.api;

import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonProperty;

public interface FileContent {
    @Nonnull
    @JsonProperty
    public String getFileName();

    @Nonnull
    @JsonProperty
    public String getContentType();

    @JsonProperty
    public long getFileSize();

    @Nonnull
    @JsonProperty
    public String getDownloadUrl();
}

