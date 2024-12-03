/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.io.ByteStreams
 *  org.springframework.core.io.InputStreamResource
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;

public interface AttachmentDataStream
extends InputStreamSource {
    public AttachmentDataStreamType getType();

    public InputStream getInputStream() throws IOException;

    default public AttachmentDataStream limit(final RangeRequest range) {
        final AttachmentDataStream self = this;
        return new AttachmentDataStream(){

            @Override
            public AttachmentDataStreamType getType() {
                return self.getType();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                InputStream inputStream = self.getInputStream();
                ByteStreams.skipFully((InputStream)inputStream, (long)range.getOffset());
                return ByteStreams.limit((InputStream)inputStream, (long)range.getRangeLength());
            }
        };
    }

    public static AttachmentDataStream create(final AttachmentDataStreamType type, final InputStreamSource inputStreamSource) {
        return new AttachmentDataStream(){

            @Override
            public AttachmentDataStreamType getType() {
                return type;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return inputStreamSource.getInputStream();
            }
        };
    }

    @Deprecated
    public static class RandomFileWrapper
    implements AttachmentDataStream {
        private final AttachmentDataStreamType dataStreamType;
        private final File file;
        private final RangeRequest range;

        public RandomFileWrapper(AttachmentDataStreamType dataStreamType, File file, RangeRequest range) throws IOException {
            this.dataStreamType = (AttachmentDataStreamType)((Object)Preconditions.checkNotNull((Object)((Object)dataStreamType), (Object)"dataStreamType cannot be null"));
            this.file = (File)Preconditions.checkNotNull((Object)file, (Object)"file cannot be null");
            this.range = (RangeRequest)Preconditions.checkNotNull((Object)range, (Object)"Range request cannot be null");
        }

        @Override
        public AttachmentDataStreamType getType() {
            return this.dataStreamType;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            RandomAccessFile accessFile = new RandomAccessFile(this.file, "r");
            accessFile.seek(this.range.getOffset());
            return ByteStreams.limit((InputStream)Channels.newInputStream(accessFile.getChannel()), (long)this.range.getRangeLength());
        }
    }

    @Deprecated
    public static class FileWrapper
    implements AttachmentDataStream {
        private final AttachmentDataStreamType dataStreamType;
        private final File file;

        public FileWrapper(AttachmentDataStreamType dataStreamType, File file) {
            this.dataStreamType = (AttachmentDataStreamType)((Object)Preconditions.checkNotNull((Object)((Object)dataStreamType), (Object)"dataStreamType cannot be null"));
            this.file = (File)Preconditions.checkNotNull((Object)file, (Object)"file cannot be null");
        }

        @Override
        public AttachmentDataStreamType getType() {
            return this.dataStreamType;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(this.file);
        }

        public File getFile() {
            return this.file;
        }
    }

    @Deprecated
    public static class InputStreamWrapper
    implements AttachmentDataStream {
        private final AttachmentDataStreamType dataStreamType;
        private final InputStreamResource inputStreamResource;

        public InputStreamWrapper(AttachmentDataStreamType dataStreamType, InputStream inputStream) {
            this.dataStreamType = (AttachmentDataStreamType)((Object)Preconditions.checkNotNull((Object)((Object)dataStreamType), (Object)"dataStreamType cannot be null"));
            this.inputStreamResource = new InputStreamResource((InputStream)Preconditions.checkNotNull((Object)inputStream, (Object)"inputStream cannot be null"));
        }

        @Override
        public AttachmentDataStreamType getType() {
            return this.dataStreamType;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.inputStreamResource.getInputStream();
        }
    }
}

