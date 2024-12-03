/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.media;

import com.atlassian.migration.agent.media.ClientId;
import com.atlassian.migration.agent.media.CreateFileOptions;
import com.atlassian.migration.agent.media.Entity;
import com.atlassian.migration.agent.media.Etag;
import com.atlassian.migration.agent.media.Upload;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MediaApiClient {
    @Nonnull
    public ClientId createClient(String var1, String var2);

    @Nonnull
    public Etag uploadChunk(ByteBuffer var1, String var2, String var3);

    @Nonnull
    public Optional<Upload> createUpload();

    public void updateUpload(String var1, int var2, List<Etag> var3);

    public Entity uploadFile(InputStream var1, String var2);

    @Nonnull
    public Entity createFileFromUpload(String var1, @Nullable String var2, @Nullable String var3, CreateFileOptions var4);

    @Nonnull
    default public Entity createFileFromUpload(String uploadId, CreateFileOptions options) {
        return this.createFileFromUpload(uploadId, null, null, options);
    }

    @Nonnull
    public Entity createFileFromChunks(List<Etag> var1, @Nullable String var2, @Nullable String var3, CreateFileOptions var4);

    @Nonnull
    default public Entity createFileFromChunks(List<Etag> etags, CreateFileOptions options) {
        return this.createFileFromChunks(etags, null, null, options);
    }
}

