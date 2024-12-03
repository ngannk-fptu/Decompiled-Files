/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.Set;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.BackingStorageException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.FilenameGenerator;
import org.apfloat.spi.MatrixStrategy;

public abstract class DiskDataStorage
extends DataStorage {
    private static final ReadableByteChannel ZERO_CHANNEL = new ReadableByteChannel(){

        @Override
        public int read(ByteBuffer buffer) {
            int writeLength = buffer.remaining();
            for (int i = 0; i < writeLength; ++i) {
                buffer.put((byte)0);
            }
            return writeLength;
        }

        @Override
        public void close() {
        }

        @Override
        public boolean isOpen() {
            return true;
        }
    };
    private static final long serialVersionUID = 741984828408146034L;
    private static final long TIMEOUT = 1000L;
    private static ReferenceQueue<FileStorage> referenceQueue = new ReferenceQueue();
    private static Set<FileStorageReference> references = new HashSet<FileStorageReference>();
    private static ThreadLocal<SoftReference<ByteBuffer>> threadLocal = new ThreadLocal();
    private static boolean cleanUp = false;
    private FileStorage fileStorage;

    protected DiskDataStorage() throws ApfloatRuntimeException {
        this.fileStorage = DiskDataStorage.createFileStorage();
    }

    protected DiskDataStorage(DiskDataStorage diskDataStorage, long offset, long length) {
        super(diskDataStorage, offset, length);
        this.fileStorage = diskDataStorage.fileStorage;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    protected void implCopyFrom(DataStorage dataStorage, long size) throws ApfloatRuntimeException {
        if (dataStorage == this) {
            this.setSize(size);
            return;
        }
        assert (size > 0L);
        assert (!this.isReadOnly());
        assert (!this.isSubsequenced());
        int unitSize = this.getUnitSize();
        long byteSize = size * (long)unitSize;
        assert (byteSize > 0L);
        try {
            this.fileStorage.setSize(byteSize);
            long readSize = Math.min(size, dataStorage.getSize());
            long oldSize = readSize * (long)unitSize;
            long padSize = byteSize - oldSize;
            if (dataStorage instanceof DiskDataStorage) {
                DiskDataStorage that = (DiskDataStorage)dataStorage;
                that.transferTo(this.getFileChannel().position(0L), that.getOffset() * (long)unitSize, oldSize);
            } else {
                long position = 0L;
                int bufferSize = DiskDataStorage.getBlockSize() / unitSize;
                while (readSize > 0L) {
                    int length = (int)Math.min((long)bufferSize, readSize);
                    try (ArrayAccess readArrayAccess = dataStorage.getArray(1, position, length);
                         ArrayAccess writeArrayAccess = this.getArray(2, position, length);){
                        System.arraycopy(readArrayAccess.getData(), readArrayAccess.getOffset(), writeArrayAccess.getData(), writeArrayAccess.getOffset(), length);
                    }
                    readSize -= (long)length;
                    position += (long)length;
                }
            }
            this.pad(oldSize, padSize);
        }
        catch (IOException ioe) {
            throw new BackingStorageException("Unable to copy to file \"" + this.getFilename() + '\"', ioe);
        }
    }

    @Override
    protected long implGetSize() throws ApfloatRuntimeException {
        try {
            return this.getFileChannel().size() / (long)this.getUnitSize();
        }
        catch (IOException ioe) {
            throw new BackingStorageException("Unable to access file \"" + this.getFilename() + '\"', ioe);
        }
    }

    @Override
    protected void implSetSize(long size) throws ApfloatRuntimeException {
        assert (size > 0L);
        assert (!this.isReadOnly());
        assert (!this.isSubsequenced());
        assert ((size *= (long)this.getUnitSize()) > 0L);
        try {
            long oldSize = this.getFileChannel().size();
            long padSize = size - oldSize;
            this.fileStorage.setSize(size);
            this.pad(oldSize, padSize);
        }
        catch (IOException ioe) {
            throw new BackingStorageException("Unable to access file \"" + this.getFilename() + '\"', ioe);
        }
    }

    @Override
    protected synchronized ArrayAccess implGetArray(int mode, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        int width = (int)(this.getSize() / (long)rows);
        if (columns != (columns & -columns) || rows != (rows & -rows) || startColumn + columns > width) {
            throw new ApfloatInternalException("Invalid size");
        }
        ArrayAccess arrayAccess = this.createArrayAccess(mode, startColumn, columns, rows);
        if ((mode & 1) != 0) {
            long readPosition = startColumn;
            int writePosition = 0;
            for (int i = 0; i < rows; ++i) {
                this.readToArray(readPosition, arrayAccess, writePosition, columns);
                readPosition += (long)width;
                writePosition += columns;
            }
        }
        return arrayAccess;
    }

    @Override
    protected synchronized ArrayAccess implGetTransposedArray(int mode, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        ArrayAccess arrayAccess;
        block8: {
            int width = (int)(this.getSize() / (long)rows);
            if (columns != (columns & -columns) || rows != (rows & -rows) || startColumn + columns > width) {
                throw new ApfloatInternalException("Invalid size");
            }
            int blockSize = columns * rows;
            int b = Math.min(columns, rows);
            arrayAccess = this.createTransposedArrayAccess(mode, startColumn, columns, rows);
            if ((mode & 1) == 0) break block8;
            ApfloatContext ctx = ApfloatContext.getContext();
            MatrixStrategy matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();
            if (columns < rows) {
                long readPosition = startColumn;
                for (int i = 0; i < rows; i += b) {
                    int writePosition = i;
                    for (int j = 0; j < b; ++j) {
                        this.readToArray(readPosition, arrayAccess, writePosition, b);
                        readPosition += (long)width;
                        writePosition += rows;
                    }
                    ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                    matrixStrategy.transposeSquare(subArrayAccess, b, rows);
                }
            } else {
                int i;
                for (i = 0; i < b; ++i) {
                    long readPosition = startColumn + i * width;
                    int writePosition = i * b;
                    for (int j = 0; j < columns; j += b) {
                        this.readToArray(readPosition, arrayAccess, writePosition, b);
                        readPosition += (long)b;
                        writePosition += b * b;
                    }
                }
                for (i = 0; i < blockSize; i += b * b) {
                    ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                    matrixStrategy.transposeSquare(subArrayAccess, b, b);
                }
            }
        }
        return arrayAccess;
    }

    protected synchronized void setArray(ArrayAccess arrayAccess, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        int width = (int)(this.getSize() / (long)rows);
        int readPosition = 0;
        long writePosition = startColumn;
        for (int i = 0; i < rows; ++i) {
            this.writeFromArray(arrayAccess, readPosition, writePosition, columns);
            readPosition += columns;
            writePosition += (long)width;
        }
    }

    protected synchronized void setTransposedArray(ArrayAccess arrayAccess, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        int width = (int)(this.getSize() / (long)rows);
        int blockSize = arrayAccess.getLength();
        int b = Math.min(columns, rows);
        ApfloatContext ctx = ApfloatContext.getContext();
        MatrixStrategy matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();
        if (columns < rows) {
            long writePosition = startColumn;
            for (int i = 0; i < rows; i += b) {
                int readPosition = i;
                ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                matrixStrategy.transposeSquare(subArrayAccess, b, rows);
                for (int j = 0; j < b; ++j) {
                    this.writeFromArray(arrayAccess, readPosition, writePosition, b);
                    readPosition += rows;
                    writePosition += (long)width;
                }
            }
        } else {
            int i;
            for (i = 0; i < blockSize; i += b * b) {
                ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                matrixStrategy.transposeSquare(subArrayAccess, b, b);
            }
            for (i = 0; i < b; ++i) {
                long writePosition = startColumn + i * width;
                int readPosition = i * b;
                for (int j = 0; j < columns; j += b) {
                    this.writeFromArray(arrayAccess, readPosition, writePosition, b);
                    readPosition += b * b;
                    writePosition += (long)b;
                }
            }
        }
    }

    private void readToArray(long readPosition, ArrayAccess arrayAccess, int writePosition, int length) throws ApfloatRuntimeException {
        try (ArrayAccess readArrayAccess = this.getArray(1, readPosition, length);){
            System.arraycopy(readArrayAccess.getData(), readArrayAccess.getOffset(), arrayAccess.getData(), arrayAccess.getOffset() + writePosition, length);
        }
    }

    private void writeFromArray(ArrayAccess arrayAccess, int readPosition, long writePosition, int length) throws ApfloatRuntimeException {
        try (ArrayAccess writeArrayAccess = this.getArray(2, writePosition, length);){
            System.arraycopy(arrayAccess.getData(), arrayAccess.getOffset() + readPosition, writeArrayAccess.getData(), writeArrayAccess.getOffset(), length);
        }
    }

    protected abstract ArrayAccess createArrayAccess(int var1, int var2, int var3, int var4);

    protected abstract ArrayAccess createTransposedArrayAccess(int var1, int var2, int var3, int var4);

    protected void transferFrom(ReadableByteChannel in, long position, long size) throws ApfloatRuntimeException {
        this.fileStorage.transferFrom(in, position, size);
    }

    protected void transferTo(WritableByteChannel out, long position, long size) throws ApfloatRuntimeException {
        this.fileStorage.transferTo(out, position, size);
    }

    protected static int getBlockSize() {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getBlockSize();
    }

    protected abstract int getUnitSize();

    protected final String getFilename() {
        return this.fileStorage.getFilename();
    }

    protected final FileChannel getFileChannel() {
        return this.fileStorage.getFileChannel();
    }

    static synchronized void cleanUp() throws ApfloatRuntimeException {
        for (FileStorageReference reference : references) {
            reference.dispose();
            reference.clear();
        }
        references.clear();
        cleanUp = true;
    }

    static synchronized void gc() throws ApfloatRuntimeException {
        DiskDataStorage.forceFreeFileStorage();
    }

    private void pad(long position, long size) throws IOException, ApfloatRuntimeException {
        this.transferFrom(ZERO_CHANNEL, position, size);
    }

    private static synchronized FileStorage createFileStorage() throws ApfloatInternalException {
        if (cleanUp) {
            throw new ApfloatInternalException("Shutdown has been initiated, clean-up is in progress");
        }
        DiskDataStorage.freeFileStorage();
        FileStorage fileStorage = new FileStorage();
        return fileStorage;
    }

    private static synchronized void referenceFileStorage(FileStorage fileStorage) throws ApfloatInternalException {
        if (cleanUp) {
            new FileStorageReference(fileStorage, null).dispose();
            throw new ApfloatInternalException("Shutdown has been initiated, clean-up is in progress");
        }
        FileStorageReference reference = new FileStorageReference(fileStorage, referenceQueue);
        references.add(reference);
    }

    private static synchronized void freeFileStorage() {
        FileStorageReference reference;
        while ((reference = (FileStorageReference)referenceQueue.poll()) != null) {
            reference.dispose();
            reference.clear();
            references.remove(reference);
        }
    }

    private static synchronized void forceFreeFileStorage() throws ApfloatInternalException {
        try {
            FileStorageReference reference;
            while ((reference = (FileStorageReference)referenceQueue.remove(1000L)) != null) {
                reference.dispose();
                reference.clear();
                references.remove(reference);
            }
        }
        catch (InterruptedException ie) {
            throw new ApfloatInternalException("Reference queue polling was interrupted", ie);
        }
    }

    private static ByteBuffer getDirectByteBuffer() {
        ByteBuffer buffer = null;
        int blockSize = DiskDataStorage.getBlockSize();
        SoftReference<ByteBuffer> reference = threadLocal.get();
        if (reference != null && (buffer = reference.get()) != null && buffer.capacity() != blockSize) {
            reference.clear();
            buffer = null;
        }
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(blockSize);
            reference = new SoftReference<ByteBuffer>(buffer);
            threadLocal.set(reference);
        }
        return buffer;
    }

    private static class FileStorage
    implements Serializable {
        private static final long serialVersionUID = 2062430603153403341L;
        private transient String filename;
        private transient File file;
        private transient RandomAccessFile randomAccessFile;
        private transient FileChannel fileChannel;

        public FileStorage() throws ApfloatRuntimeException {
            this.init();
        }

        private void init() throws ApfloatRuntimeException {
            ApfloatContext ctx = ApfloatContext.getContext();
            FilenameGenerator generator = ctx.getFilenameGenerator();
            this.filename = generator.generateFilename();
            this.file = new File(this.filename);
            try {
                if (!this.file.createNewFile()) {
                    throw new BackingStorageException("Failed to create new file \"" + this.filename + '\"');
                }
                this.file.deleteOnExit();
                this.randomAccessFile = new RandomAccessFile(this.file, "rw");
            }
            catch (IOException ioe) {
                throw new BackingStorageException("Unable to access file \"" + this.filename + '\"', ioe);
            }
            this.fileChannel = this.randomAccessFile.getChannel();
            DiskDataStorage.referenceFileStorage(this);
        }

        public void setSize(long size) throws IOException, ApfloatRuntimeException {
            try {
                this.getRandomAccessFile().setLength(size);
            }
            catch (IOException ioe) {
                System.gc();
                DiskDataStorage.forceFreeFileStorage();
                this.getRandomAccessFile().setLength(size);
            }
        }

        public void transferFrom(ReadableByteChannel in, long position, long size) throws ApfloatRuntimeException {
            try {
                if (in instanceof FileChannel) {
                    while (size > 0L) {
                        long count = this.getFileChannel().transferFrom(in, position, size);
                        position += count;
                        assert ((size -= count) >= 0L);
                    }
                } else {
                    ByteBuffer buffer = DiskDataStorage.getDirectByteBuffer();
                    while (size > 0L) {
                        int writeCount;
                        buffer.clear();
                        int readCount = (int)Math.min(size, (long)buffer.capacity());
                        buffer.limit(readCount);
                        buffer.flip();
                        for (readCount = in.read(buffer); readCount > 0; readCount -= writeCount) {
                            writeCount = this.getFileChannel().write(buffer, position);
                            position += (long)writeCount;
                            size -= (long)writeCount;
                        }
                        assert (readCount == 0);
                        assert (size >= 0L);
                    }
                }
            }
            catch (IOException ioe) {
                throw new BackingStorageException("Unable to write to file \"" + this.getFilename() + '\"', ioe);
            }
        }

        public void transferTo(WritableByteChannel out, long position, long size) throws ApfloatRuntimeException {
            try {
                if (out instanceof FileChannel) {
                    while (size > 0L) {
                        long count = this.getFileChannel().transferTo(position, size, out);
                        position += count;
                        assert ((size -= count) >= 0L);
                    }
                } else {
                    ByteBuffer buffer = DiskDataStorage.getDirectByteBuffer();
                    while (size > 0L) {
                        int writeCount;
                        buffer.clear();
                        int readCount = (int)Math.min(size, (long)buffer.capacity());
                        buffer.limit(readCount);
                        buffer.flip();
                        for (readCount = this.getFileChannel().read(buffer, position); readCount > 0; readCount -= writeCount) {
                            writeCount = out.write(buffer);
                            position += (long)writeCount;
                            size -= (long)writeCount;
                        }
                        assert (readCount == 0);
                        assert (size >= 0L);
                    }
                }
            }
            catch (IOException ioe) {
                throw new BackingStorageException("Unable to read from file \"" + this.getFilename() + '\"', ioe);
            }
        }

        public String getFilename() {
            return this.filename;
        }

        public File getFile() {
            return this.file;
        }

        public RandomAccessFile getRandomAccessFile() {
            return this.randomAccessFile;
        }

        public FileChannel getFileChannel() {
            return this.fileChannel;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            long size = this.getFileChannel().size();
            out.writeLong(size);
            this.transferTo(Channels.newChannel(out), 0L, size);
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            this.init();
            long size = in.readLong();
            this.setSize(size);
            this.transferFrom(Channels.newChannel(in), 0L, size);
            in.defaultReadObject();
        }
    }

    private static class FileStorageReference
    extends PhantomReference<FileStorage> {
        private File file;
        private RandomAccessFile randomAccessFile;
        private FileChannel fileChannel;

        public FileStorageReference(FileStorage fileStorage, ReferenceQueue<FileStorage> queue) {
            super(fileStorage, queue);
            this.file = fileStorage.getFile();
            this.randomAccessFile = fileStorage.getRandomAccessFile();
            this.fileChannel = fileStorage.getFileChannel();
        }

        public void dispose() {
            try {
                this.fileChannel.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                this.randomAccessFile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.file.delete();
        }
    }
}

