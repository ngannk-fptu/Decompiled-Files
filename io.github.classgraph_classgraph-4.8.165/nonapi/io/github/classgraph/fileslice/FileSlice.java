/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fileslice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessByteBufferReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessFileChannelReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class FileSlice
extends Slice {
    public final File file;
    public RandomAccessFile raf;
    private final long fileLength;
    private FileChannel fileChannel;
    private ByteBuffer backingByteBuffer;
    private final boolean isTopLevelFileSlice;
    private final AtomicBoolean isClosed;

    private FileSlice(FileSlice parentSlice, long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
        super(parentSlice, offset, length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
        this.isClosed = new AtomicBoolean();
        this.file = parentSlice.file;
        this.raf = parentSlice.raf;
        this.fileChannel = parentSlice.fileChannel;
        this.fileLength = parentSlice.fileLength;
        this.isTopLevelFileSlice = false;
        if (parentSlice.backingByteBuffer != null) {
            this.backingByteBuffer = parentSlice.backingByteBuffer.duplicate();
            ((Buffer)this.backingByteBuffer).position((int)this.sliceStartPos);
            ((Buffer)this.backingByteBuffer).limit((int)(this.sliceStartPos + this.sliceLength));
        }
    }

    public FileSlice(File file, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler, LogNode log) throws IOException {
        block5: {
            super(file.length(), isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
            this.isClosed = new AtomicBoolean();
            FileUtils.checkCanReadAndIsFile(file);
            this.file = file;
            this.raf = new RandomAccessFile(file, "r");
            this.fileChannel = this.raf.getChannel();
            this.fileLength = file.length();
            this.isTopLevelFileSlice = true;
            if (nestedJarHandler.scanSpec.enableMemoryMapping) {
                try {
                    this.backingByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, this.fileLength);
                }
                catch (IOException | OutOfMemoryError e) {
                    System.gc();
                    nestedJarHandler.runFinalizationMethod();
                    try {
                        this.backingByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, this.fileLength);
                    }
                    catch (IOException | OutOfMemoryError e2) {
                        if (log == null) break block5;
                        log.log("File " + file + " cannot be memory mapped: " + e2 + " (using RandomAccessFile API instead)");
                    }
                }
            }
        }
        nestedJarHandler.markSliceAsOpen(this);
    }

    public FileSlice(File file, NestedJarHandler nestedJarHandler, LogNode log) throws IOException {
        this(file, false, 0L, nestedJarHandler, log);
    }

    @Override
    public Slice slice(long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint) {
        if (this.isDeflatedZipEntry) {
            throw new IllegalArgumentException("Cannot slice a deflated zip entry");
        }
        return new FileSlice(this, offset, length, isDeflatedZipEntry, inflatedLengthHint, this.nestedJarHandler);
    }

    @Override
    public RandomAccessReader randomAccessReader() {
        if (this.backingByteBuffer == null) {
            return new RandomAccessFileChannelReader(this.fileChannel, this.sliceStartPos, this.sliceLength);
        }
        return new RandomAccessByteBufferReader(this.backingByteBuffer, this.sliceStartPos, this.sliceLength);
    }

    @Override
    public byte[] load() throws IOException {
        byte[] content;
        if (this.isDeflatedZipEntry) {
            if (this.inflatedLengthHint > 0x7FFFFFF7L) {
                throw new IOException("Uncompressed size is larger than 2GB");
            }
            try (InputStream inputStream = this.open();){
                byte[] byArray = NestedJarHandler.readAllBytesAsArray(inputStream, this.inflatedLengthHint);
                return byArray;
            }
        }
        if (this.sliceLength > 0x7FFFFFF7L) {
            throw new IOException("File is larger than 2GB");
        }
        RandomAccessReader reader = this.randomAccessReader();
        if (reader.read(0L, content = new byte[(int)this.sliceLength], 0, content.length) < content.length) {
            throw new IOException("File is truncated");
        }
        return content;
    }

    @Override
    public ByteBuffer read() throws IOException {
        if (this.isDeflatedZipEntry) {
            if (this.inflatedLengthHint > 0x7FFFFFF7L) {
                throw new IOException("Uncompressed size is larger than 2GB");
            }
            return ByteBuffer.wrap(this.load());
        }
        if (this.backingByteBuffer == null) {
            if (this.sliceLength > 0x7FFFFFF7L) {
                throw new IOException("File is larger than 2GB");
            }
            return ByteBuffer.wrap(this.load());
        }
        return this.backingByteBuffer.duplicate();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void close() {
        if (!this.isClosed.getAndSet(true)) {
            if (this.isTopLevelFileSlice && this.backingByteBuffer != null) {
                this.nestedJarHandler.closeDirectByteBuffer(this.backingByteBuffer);
            }
            this.backingByteBuffer = null;
            this.fileChannel = null;
            try {
                this.raf.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.raf = null;
            this.nestedJarHandler.markSliceAsClosed(this);
        }
    }
}

