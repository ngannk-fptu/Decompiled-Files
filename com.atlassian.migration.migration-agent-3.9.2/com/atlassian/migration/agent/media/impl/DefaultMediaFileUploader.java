/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.WillNotClose
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.migration.agent.Tracker;
import com.atlassian.migration.agent.media.CreateFileOptions;
import com.atlassian.migration.agent.media.Entity;
import com.atlassian.migration.agent.media.Etag;
import com.atlassian.migration.agent.media.MediaApiClient;
import com.atlassian.migration.agent.media.MediaFileUploader;
import com.atlassian.migration.agent.media.MediaUploadException;
import com.atlassian.migration.agent.media.Upload;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMediaFileUploader
implements MediaFileUploader {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultMediaFileUploader.class);
    private static final int CHUNK_BATCH_SIZE = 100;
    protected static final int CHUNK_SIZE = 0x500000;
    protected static final int SINGLE_PART_UPLOAD_SIZE = 0x6400000;
    private final MediaApiClient mediaApiClient;

    DefaultMediaFileUploader(MediaApiClient mediaApiClient) {
        this.mediaApiClient = Objects.requireNonNull(mediaApiClient);
    }

    @Override
    @Nonnull
    public String upload(@WillNotClose InputStream is, String name, Tracker tracker, long fileSize) {
        if (fileSize < 0x6400000L) {
            tracker.track(fileSize);
            return this.mediaApiClient.uploadFile(is, name).getId();
        }
        return this.uploadWithFileOptions(is, name, tracker, CreateFileOptions.builder().build());
    }

    private String uploadWithFileOptions(@WillNotClose InputStream is, @Nullable String name, Tracker tracker, CreateFileOptions createFileOptions) {
        Upload upload = this.mediaApiClient.createUpload().orElseThrow(() -> new MediaUploadException("Could not acquire upload"));
        String uploadId = this.uploadChunks(is, tracker, upload);
        Entity entity = this.mediaApiClient.createFileFromUpload(uploadId, name, null, createFileOptions);
        return entity.getId();
    }

    @NotNull
    private String uploadChunks(InputStream is, Tracker tracker, Upload upload) {
        int nextChunkPartNumber;
        List<Etag> etags;
        int index = 0;
        String uploadId = upload.getId();
        byte[] buffer = new byte[0x500000];
        while (!CollectionUtils.isEmpty(etags = this.sendChunks(is, buffer, tracker, uploadId, nextChunkPartNumber = index + 1))) {
            this.mediaApiClient.updateUpload(uploadId, index, etags);
            index += etags.size();
        }
        return uploadId;
    }

    private List<Etag> sendChunks(@WillNotClose InputStream is, byte[] buffer, Tracker tracker, String uploadId, int nextChunkPartNumber) {
        ArrayList<Etag> etags = new ArrayList<Etag>();
        try {
            int bytesRead;
            while ((bytesRead = DefaultMediaFileUploader.getChunk(is, buffer)) > 0) {
                Etag etag = this.mediaApiClient.uploadChunk(ByteBuffer.wrap(buffer, 0, bytesRead), uploadId, String.valueOf(nextChunkPartNumber));
                tracker.track(etag.getLength());
                etags.add(etag);
                ++nextChunkPartNumber;
                if (etags.size() < 100) continue;
                return etags;
            }
        }
        catch (IOException e) {
            throw new MediaUploadException("Unexpected exception sending chunks to media", e);
        }
        return etags;
    }

    private static int getChunk(@WillNotClose InputStream is, byte[] buffer) throws IOException {
        int bytesRead;
        int got;
        for (bytesRead = 0; bytesRead < 0x500000; bytesRead += got) {
            got = is.read(buffer, bytesRead, 0x500000 - bytesRead);
            if (got != -1) continue;
            return bytesRead == 0 ? -1 : bytesRead;
        }
        return bytesRead;
    }
}

