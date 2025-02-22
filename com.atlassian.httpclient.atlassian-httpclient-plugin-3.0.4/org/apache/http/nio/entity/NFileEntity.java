/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.ContentEncoderChannel;
import org.apache.http.nio.FileContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.entity.ProducingNHttpEntity;
import org.apache.http.util.Args;

public class NFileEntity
extends AbstractHttpEntity
implements HttpAsyncContentProducer,
ProducingNHttpEntity {
    private final File file;
    private RandomAccessFile accessfile;
    private FileChannel fileChannel;
    private long idx = -1L;
    private boolean useFileChannels;

    public NFileEntity(File file, ContentType contentType, boolean useFileChannels) {
        Args.notNull(file, "File");
        this.file = file;
        this.useFileChannels = useFileChannels;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }

    public NFileEntity(File file) {
        Args.notNull(file, "File");
        this.file = file;
    }

    public NFileEntity(File file, ContentType contentType) {
        this(file, contentType, true);
    }

    @Deprecated
    public NFileEntity(File file, String contentType, boolean useFileChannels) {
        Args.notNull(file, "File");
        this.file = file;
        this.useFileChannels = useFileChannels;
        this.setContentType(contentType);
    }

    @Deprecated
    public NFileEntity(File file, String contentType) {
        this(file, contentType, true);
    }

    @Override
    public void close() throws IOException {
        if (this.accessfile != null) {
            this.accessfile.close();
        }
        this.accessfile = null;
        this.fileChannel = null;
    }

    @Override
    @Deprecated
    public void finish() throws IOException {
        this.close();
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        long transferred;
        if (this.accessfile == null) {
            this.accessfile = new RandomAccessFile(this.file, "r");
        }
        if (this.fileChannel == null) {
            this.fileChannel = this.accessfile.getChannel();
            this.idx = 0L;
        }
        if ((transferred = this.useFileChannels && encoder instanceof FileContentEncoder ? ((FileContentEncoder)encoder).transfer(this.fileChannel, this.idx, Long.MAX_VALUE) : this.fileChannel.transferTo(this.idx, Long.MAX_VALUE, new ContentEncoderChannel(encoder))) > 0L) {
            this.idx += transferred;
        }
        if (this.idx >= this.fileChannel.size()) {
            encoder.complete();
            this.close();
        }
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public InputStream getContent() throws IOException {
        return new FileInputStream(this.file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        FileInputStream inStream = new FileInputStream(this.file);
        try {
            int l;
            byte[] tmp = new byte[4096];
            while ((l = ((InputStream)inStream).read(tmp)) != -1) {
                outStream.write(tmp, 0, l);
            }
            outStream.flush();
        }
        finally {
            ((InputStream)inStream).close();
        }
    }
}

