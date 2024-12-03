/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.CompoundFileWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.util.IOUtils;

public final class CompoundFileDirectory
extends Directory {
    private final Directory directory;
    private final String fileName;
    protected final int readBufferSize;
    private final Map<String, FileEntry> entries;
    private final boolean openForWrite;
    private static final Map<String, FileEntry> SENTINEL = Collections.emptyMap();
    private final CompoundFileWriter writer;
    private final Directory.IndexInputSlicer handle;
    private static final byte CODEC_MAGIC_BYTE1 = 63;
    private static final byte CODEC_MAGIC_BYTE2 = -41;
    private static final byte CODEC_MAGIC_BYTE3 = 108;
    private static final byte CODEC_MAGIC_BYTE4 = 23;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public CompoundFileDirectory(Directory directory, String fileName, IOContext context, boolean openForWrite) throws IOException {
        this.directory = directory;
        this.fileName = fileName;
        this.readBufferSize = BufferedIndexInput.bufferSize(context);
        this.isOpen = false;
        this.openForWrite = openForWrite;
        if (!openForWrite) {
            block4: {
                boolean success = false;
                this.handle = directory.createSlicer(fileName, context);
                try {
                    this.entries = CompoundFileDirectory.readEntries(this.handle, directory, fileName);
                    success = true;
                    if (success) break block4;
                }
                catch (Throwable throwable) {
                    if (success) throw throwable;
                    IOUtils.closeWhileHandlingException(this.handle);
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(this.handle);
            }
            this.isOpen = true;
            this.writer = null;
            return;
        }
        assert (!(directory instanceof CompoundFileDirectory)) : "compound file inside of compound file: " + fileName;
        this.entries = SENTINEL;
        this.isOpen = true;
        this.writer = new CompoundFileWriter(directory, fileName);
        this.handle = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Map<String, FileEntry> readEntries(Directory.IndexInputSlicer handle, Directory dir, String name) throws IOException {
        Map<String, FileEntry> map;
        IOException priorE = null;
        IndexInput stream = null;
        IndexInput entriesStream = null;
        try {
            Map<String, FileEntry> mapping;
            stream = handle.openFullSlice();
            int firstInt = stream.readVInt();
            if (firstInt == 63) {
                byte secondByte = stream.readByte();
                byte thirdByte = stream.readByte();
                byte fourthByte = stream.readByte();
                if (secondByte != -41 || thirdByte != 108 || fourthByte != 23) {
                    throw new CorruptIndexException("Illegal/impossible header for CFS file: " + secondByte + "," + thirdByte + "," + fourthByte);
                }
                CodecUtil.checkHeaderNoMagic(stream, "CompoundFileWriterData", 0, 0);
                String entriesFileName = IndexFileNames.segmentFileName(IndexFileNames.stripExtension(name), "", "cfe");
                entriesStream = dir.openInput(entriesFileName, IOContext.READONCE);
                CodecUtil.checkHeader(entriesStream, "CompoundFileWriterEntries", 0, 0);
                int numEntries = entriesStream.readVInt();
                mapping = new HashMap<String, FileEntry>(numEntries);
                for (int i = 0; i < numEntries; ++i) {
                    FileEntry fileEntry = new FileEntry();
                    String id = entriesStream.readString();
                    FileEntry previous = mapping.put(id, fileEntry);
                    if (previous != null) {
                        throw new CorruptIndexException("Duplicate cfs entry id=" + id + " in CFS: " + entriesStream);
                    }
                    fileEntry.offset = entriesStream.readLong();
                    fileEntry.length = entriesStream.readLong();
                }
            } else {
                mapping = CompoundFileDirectory.readLegacyEntries(stream, firstInt);
            }
            map = mapping;
        }
        catch (IOException ioe) {
            try {
                priorE = ioe;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException(priorE, stream, entriesStream);
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(priorE, stream, entriesStream);
        }
        IOUtils.closeWhileHandlingException(priorE, stream, entriesStream);
        return map;
        throw new AssertionError((Object)"impossible to get here");
    }

    private static Map<String, FileEntry> readLegacyEntries(IndexInput stream, int firstInt) throws CorruptIndexException, IOException {
        boolean stripSegmentName;
        int count;
        HashMap<String, FileEntry> entries = new HashMap<String, FileEntry>();
        if (firstInt < 0) {
            if (firstInt < -1) {
                throw new CorruptIndexException("Incompatible format version: " + firstInt + " expected >= " + -1 + " (resource: " + stream + ")");
            }
            count = stream.readVInt();
            stripSegmentName = false;
        } else {
            count = firstInt;
            stripSegmentName = true;
        }
        long streamLength = stream.length();
        FileEntry entry = null;
        for (int i = 0; i < count; ++i) {
            long offset = stream.readLong();
            if (offset < 0L || offset > streamLength) {
                throw new CorruptIndexException("Invalid CFS entry offset: " + offset + " (resource: " + stream + ")");
            }
            String id = stream.readString();
            if (stripSegmentName) {
                id = IndexFileNames.stripSegmentName(id);
            }
            if (entry != null) {
                entry.length = offset - entry.offset;
            }
            entry = new FileEntry();
            entry.offset = offset;
            FileEntry previous = entries.put(id, entry);
            if (previous == null) continue;
            throw new CorruptIndexException("Duplicate cfs entry id=" + id + " in CFS: " + stream);
        }
        if (entry != null) {
            entry.length = streamLength - entry.offset;
        }
        return entries;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public String getName() {
        return this.fileName;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!this.isOpen) {
            return;
        }
        this.isOpen = false;
        if (this.writer != null) {
            assert (this.openForWrite);
            this.writer.close();
        } else {
            IOUtils.close(this.handle);
        }
    }

    @Override
    public synchronized IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        assert (!this.openForWrite);
        String id = IndexFileNames.stripSegmentName(name);
        FileEntry entry = this.entries.get(id);
        if (entry == null) {
            throw new FileNotFoundException("No sub-file with id " + id + " found (fileName=" + name + " files: " + this.entries.keySet() + ")");
        }
        return this.handle.openSlice(name, entry.offset, entry.length);
    }

    @Override
    public String[] listAll() {
        String[] res;
        this.ensureOpen();
        if (this.writer != null) {
            res = this.writer.listAll();
        } else {
            res = this.entries.keySet().toArray(new String[this.entries.size()]);
            String seg = IndexFileNames.parseSegmentName(this.fileName);
            for (int i = 0; i < res.length; ++i) {
                res[i] = seg + res[i];
            }
        }
        return res;
    }

    @Override
    public boolean fileExists(String name) {
        this.ensureOpen();
        if (this.writer != null) {
            return this.writer.fileExists(name);
        }
        return this.entries.containsKey(IndexFileNames.stripSegmentName(name));
    }

    @Override
    public void deleteFile(String name) {
        throw new UnsupportedOperationException();
    }

    public void renameFile(String from, String to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long fileLength(String name) throws IOException {
        this.ensureOpen();
        if (this.writer != null) {
            return this.writer.fileLength(name);
        }
        FileEntry e = this.entries.get(IndexFileNames.stripSegmentName(name));
        if (e == null) {
            throw new FileNotFoundException(name);
        }
        return e.length;
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        return this.writer.createOutput(name, context);
    }

    @Override
    public void sync(Collection<String> names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lock makeLock(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        this.ensureOpen();
        assert (!this.openForWrite);
        String id = IndexFileNames.stripSegmentName(name);
        final FileEntry entry = this.entries.get(id);
        if (entry == null) {
            throw new FileNotFoundException("No sub-file with id " + id + " found (fileName=" + name + " files: " + this.entries.keySet() + ")");
        }
        return new Directory.IndexInputSlicer(){

            @Override
            public void close() {
            }

            @Override
            public IndexInput openSlice(String sliceDescription, long offset, long length) throws IOException {
                return CompoundFileDirectory.this.handle.openSlice(sliceDescription, entry.offset + offset, length);
            }

            @Override
            public IndexInput openFullSlice() throws IOException {
                return this.openSlice("full-slice", 0L, entry.length);
            }
        };
    }

    @Override
    public String toString() {
        return "CompoundFileDirectory(file=\"" + this.fileName + "\" in dir=" + this.directory + ")";
    }

    public static final class FileEntry {
        long offset;
        long length;
    }
}

