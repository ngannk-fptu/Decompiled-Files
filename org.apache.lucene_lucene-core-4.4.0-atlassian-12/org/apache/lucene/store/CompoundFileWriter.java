/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;

final class CompoundFileWriter
implements Closeable {
    static final int FORMAT_PRE_VERSION = 0;
    static final int FORMAT_NO_SEGMENT_PREFIX = -1;
    static final String DATA_CODEC = "CompoundFileWriterData";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    static final String ENTRY_CODEC = "CompoundFileWriterEntries";
    private final Directory directory;
    private final Map<String, FileEntry> entries = new HashMap<String, FileEntry>();
    private final Set<String> seenIDs = new HashSet<String>();
    private final Queue<FileEntry> pendingEntries = new LinkedList<FileEntry>();
    private boolean closed = false;
    private IndexOutput dataOut;
    private final AtomicBoolean outputTaken = new AtomicBoolean(false);
    final String entryTableName;
    final String dataFileName;

    CompoundFileWriter(Directory dir, String name) {
        if (dir == null) {
            throw new NullPointerException("directory cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.directory = dir;
        this.entryTableName = IndexFileNames.segmentFileName(IndexFileNames.stripExtension(name), "", "cfe");
        this.dataFileName = name;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private synchronized IndexOutput getOutput() throws IOException {
        if (this.dataOut != null) return this.dataOut;
        boolean success = false;
        try {
            this.dataOut = this.directory.createOutput(this.dataFileName, IOContext.DEFAULT);
            CodecUtil.writeHeader(this.dataOut, DATA_CODEC, 0);
            return this.dataOut;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.dataOut);
            throw throwable;
        }
    }

    Directory getDirectory() {
        return this.directory;
    }

    String getName() {
        return this.dataFileName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        IndexOutput entryTableOut;
        IOException priorException;
        block10: {
            if (this.closed) {
                return;
            }
            priorException = null;
            entryTableOut = null;
            try {
                if (!this.pendingEntries.isEmpty() || this.outputTaken.get()) {
                    throw new IllegalStateException("CFS has pending open files");
                }
                this.closed = true;
                this.getOutput();
                if ($assertionsDisabled || this.dataOut != null) break block10;
                throw new AssertionError();
            }
            catch (IOException e) {
                try {
                    priorException = e;
                }
                catch (Throwable throwable) {
                    IOUtils.closeWhileHandlingException(priorException, this.dataOut);
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(priorException, this.dataOut);
            }
        }
        IOUtils.closeWhileHandlingException(priorException, this.dataOut);
        try {
            entryTableOut = this.directory.createOutput(this.entryTableName, IOContext.DEFAULT);
            this.writeEntryTable(this.entries.values(), entryTableOut);
        }
        catch (IOException e) {
            try {
                priorException = e;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException(priorException, entryTableOut);
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(priorException, entryTableOut);
        }
        IOUtils.closeWhileHandlingException(priorException, entryTableOut);
    }

    private final void ensureOpen() {
        if (this.closed) {
            throw new AlreadyClosedException("CFS Directory is already closed");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final long copyFileEntry(IndexOutput dataOut, FileEntry fileEntry) throws IOException {
        long l;
        block6: {
            IndexInput is;
            block5: {
                is = fileEntry.dir.openInput(fileEntry.file, IOContext.READONCE);
                boolean success = false;
                try {
                    long startPtr = dataOut.getFilePointer();
                    long length = fileEntry.length;
                    dataOut.copyBytes(is, length);
                    long endPtr = dataOut.getFilePointer();
                    long diff = endPtr - startPtr;
                    if (diff != length) {
                        throw new IOException("Difference in the output file offsets " + diff + " does not match the original file length " + length);
                    }
                    fileEntry.offset = startPtr;
                    success = true;
                    l = length;
                    if (!success) break block5;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(is);
                        fileEntry.dir.deleteFile(fileEntry.file);
                    } else {
                        IOUtils.closeWhileHandlingException(is);
                    }
                    throw throwable;
                }
                IOUtils.close(is);
                fileEntry.dir.deleteFile(fileEntry.file);
                break block6;
            }
            IOUtils.closeWhileHandlingException(is);
        }
        return l;
    }

    protected void writeEntryTable(Collection<FileEntry> entries, IndexOutput entryOut) throws IOException {
        CodecUtil.writeHeader(entryOut, ENTRY_CODEC, 0);
        entryOut.writeVInt(entries.size());
        for (FileEntry fe : entries) {
            entryOut.writeString(IndexFileNames.stripSegmentName(fe.file));
            entryOut.writeLong(fe.offset);
            entryOut.writeLong(fe.length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        boolean success = false;
        boolean outputLocked = false;
        try {
            DirectCFSIndexOutput out;
            assert (name != null) : "name must not be null";
            if (this.entries.containsKey(name)) {
                throw new IllegalArgumentException("File " + name + " already exists");
            }
            FileEntry entry = new FileEntry();
            entry.file = name;
            this.entries.put(name, entry);
            String id = IndexFileNames.stripSegmentName(name);
            assert (!this.seenIDs.contains(id)) : "file=\"" + name + "\" maps to id=\"" + id + "\", which was already written";
            this.seenIDs.add(id);
            outputLocked = this.outputTaken.compareAndSet(false, true);
            if (outputLocked) {
                out = new DirectCFSIndexOutput(this.getOutput(), entry, false);
            } else {
                entry.dir = this.directory;
                if (this.directory.fileExists(name)) {
                    throw new IllegalArgumentException("File " + name + " already exists");
                }
                out = new DirectCFSIndexOutput(this.directory.createOutput(name, context), entry, true);
            }
            success = true;
            DirectCFSIndexOutput directCFSIndexOutput = out;
            return directCFSIndexOutput;
        }
        finally {
            if (!success) {
                this.entries.remove(name);
                if (outputLocked) {
                    assert (this.outputTaken.get());
                    this.releaseOutputLock();
                }
            }
        }
    }

    final void releaseOutputLock() {
        this.outputTaken.compareAndSet(true, false);
    }

    private final void prunePendingEntries() throws IOException {
        if (this.outputTaken.compareAndSet(false, true)) {
            try {
                while (!this.pendingEntries.isEmpty()) {
                    FileEntry entry = this.pendingEntries.poll();
                    this.copyFileEntry(this.getOutput(), entry);
                    this.entries.put(entry.file, entry);
                }
            }
            finally {
                boolean compareAndSet = this.outputTaken.compareAndSet(true, false);
                assert (compareAndSet);
            }
        }
    }

    long fileLength(String name) throws IOException {
        FileEntry fileEntry = this.entries.get(name);
        if (fileEntry == null) {
            throw new FileNotFoundException(name + " does not exist");
        }
        return fileEntry.length;
    }

    boolean fileExists(String name) {
        return this.entries.containsKey(name);
    }

    String[] listAll() {
        return this.entries.keySet().toArray(new String[0]);
    }

    private final class DirectCFSIndexOutput
    extends IndexOutput {
        private final IndexOutput delegate;
        private final long offset;
        private boolean closed;
        private FileEntry entry;
        private long writtenBytes;
        private final boolean isSeparate;

        DirectCFSIndexOutput(IndexOutput delegate, FileEntry entry, boolean isSeparate) {
            this.delegate = delegate;
            this.entry = entry;
            entry.offset = this.offset = delegate.getFilePointer();
            this.isSeparate = isSeparate;
        }

        @Override
        public void flush() throws IOException {
            this.delegate.flush();
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                this.entry.length = this.writtenBytes;
                if (this.isSeparate) {
                    this.delegate.close();
                    CompoundFileWriter.this.pendingEntries.add(this.entry);
                } else {
                    CompoundFileWriter.this.releaseOutputLock();
                }
                CompoundFileWriter.this.prunePendingEntries();
            }
        }

        @Override
        public long getFilePointer() {
            return this.delegate.getFilePointer() - this.offset;
        }

        @Override
        public void seek(long pos) throws IOException {
            assert (!this.closed);
            this.delegate.seek(this.offset + pos);
        }

        @Override
        public long length() throws IOException {
            assert (!this.closed);
            return this.delegate.length() - this.offset;
        }

        @Override
        public void writeByte(byte b) throws IOException {
            assert (!this.closed);
            ++this.writtenBytes;
            this.delegate.writeByte(b);
        }

        @Override
        public void writeBytes(byte[] b, int offset, int length) throws IOException {
            assert (!this.closed);
            this.writtenBytes += (long)length;
            this.delegate.writeBytes(b, offset, length);
        }
    }

    private static final class FileEntry {
        String file;
        long length;
        long offset;
        Directory dir;

        private FileEntry() {
        }
    }
}

