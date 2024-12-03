/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.nio;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.IdentityHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.nio.CleanerUtil;
import org.apache.poi.poifs.nio.DataSource;
import org.apache.poi.util.IOUtils;

public class FileBackedDataSource
extends DataSource
implements Closeable {
    private static final Logger LOG = LogManager.getLogger(FileBackedDataSource.class);
    private final FileChannel channel;
    private Long channelSize;
    private final boolean writable;
    private final boolean closeChannelOnClose;
    private final RandomAccessFile srcFile;
    private final IdentityHashMap<ByteBuffer, ByteBuffer> buffersToClean = new IdentityHashMap();

    public FileBackedDataSource(File file) throws FileNotFoundException {
        this(FileBackedDataSource.newSrcFile(file, "r"), true);
    }

    public FileBackedDataSource(File file, boolean readOnly) throws FileNotFoundException {
        this(FileBackedDataSource.newSrcFile(file, readOnly ? "r" : "rw"), readOnly);
    }

    public FileBackedDataSource(RandomAccessFile srcFile, boolean readOnly) {
        this(srcFile, srcFile.getChannel(), readOnly, false);
    }

    public FileBackedDataSource(FileChannel channel, boolean readOnly) {
        this(channel, readOnly, true);
    }

    public FileBackedDataSource(FileChannel channel, boolean readOnly, boolean closeChannelOnClose) {
        this(null, channel, readOnly, closeChannelOnClose);
    }

    private FileBackedDataSource(RandomAccessFile srcFile, FileChannel channel, boolean readOnly, boolean closeChannelOnClose) {
        this.srcFile = srcFile;
        this.channel = channel;
        this.writable = !readOnly;
        this.closeChannelOnClose = closeChannelOnClose;
    }

    public boolean isWriteable() {
        return this.writable;
    }

    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public ByteBuffer read(int length, long position) throws IOException {
        ByteBuffer dst;
        if (position >= this.size()) {
            throw new IndexOutOfBoundsException("Position " + position + " past the end of the file");
        }
        if (this.writable) {
            dst = this.channel.map(FileChannel.MapMode.READ_WRITE, position, length);
            this.buffersToClean.put(dst, dst);
        } else {
            this.channel.position(position);
            dst = ByteBuffer.allocate(length);
            int worked = IOUtils.readFully(this.channel, dst);
            if (worked == -1) {
                throw new IndexOutOfBoundsException("Position " + position + " past the end of the file");
            }
        }
        dst.position(0);
        return dst;
    }

    @Override
    public void write(ByteBuffer src, long position) throws IOException {
        this.channel.write(src, position);
        if (this.channelSize != null && position >= this.channelSize) {
            this.channelSize = null;
        }
    }

    @Override
    public void copyTo(OutputStream stream) throws IOException {
        try (WritableByteChannel out = Channels.newChannel(stream);){
            this.channel.transferTo(0L, this.channel.size(), out);
        }
    }

    @Override
    public long size() throws IOException {
        if (this.channelSize == null) {
            this.channelSize = this.channel.size();
        }
        return this.channelSize;
    }

    public void releaseBuffer(ByteBuffer buffer) {
        ByteBuffer previous = this.buffersToClean.remove(buffer);
        if (previous != null) {
            FileBackedDataSource.unmap(previous);
        }
    }

    @Override
    public void close() throws IOException {
        this.buffersToClean.forEach((k, v) -> FileBackedDataSource.unmap(v));
        this.buffersToClean.clear();
        if (this.srcFile != null) {
            this.srcFile.close();
        } else if (this.closeChannelOnClose) {
            this.channel.close();
        }
    }

    private static RandomAccessFile newSrcFile(File file, String mode) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        return new RandomAccessFile(file, mode);
    }

    private static void unmap(ByteBuffer buffer) {
        if (buffer.getClass().getName().endsWith("HeapByteBuffer")) {
            return;
        }
        if (CleanerUtil.UNMAP_SUPPORTED) {
            try {
                CleanerUtil.getCleaner().freeBuffer(buffer);
            }
            catch (IOException e) {
                LOG.atWarn().withThrowable(e).log("Failed to unmap the buffer");
            }
        } else {
            LOG.atDebug().log(CleanerUtil.UNMAP_NOT_SUPPORTED_REASON);
        }
    }
}

