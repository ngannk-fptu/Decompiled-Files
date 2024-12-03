/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentMerger;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public final class CompoundFileWriter {
    static final int FORMAT_PRE_VERSION = 0;
    static final int FORMAT_NO_SEGMENT_PREFIX = -1;
    static final int FORMAT_CURRENT = -1;
    private Directory directory;
    private String fileName;
    private HashSet<String> ids;
    private LinkedList<FileEntry> entries;
    private boolean merged = false;
    private SegmentMerger.CheckAbort checkAbort;

    public CompoundFileWriter(Directory dir, String name) {
        this(dir, name, null);
    }

    CompoundFileWriter(Directory dir, String name, SegmentMerger.CheckAbort checkAbort) {
        if (dir == null) {
            throw new NullPointerException("directory cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.checkAbort = checkAbort;
        this.directory = dir;
        this.fileName = name;
        this.ids = new HashSet();
        this.entries = new LinkedList();
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public String getName() {
        return this.fileName;
    }

    public void addFile(String file) {
        this.addFile(file, this.directory);
    }

    public void addFile(String file, Directory dir) {
        if (this.merged) {
            throw new IllegalStateException("Can't add extensions after merge has been called");
        }
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        if (!this.ids.add(file)) {
            throw new IllegalArgumentException("File " + file + " already added");
        }
        FileEntry entry = new FileEntry();
        entry.file = file;
        entry.dir = dir;
        this.entries.add(entry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void close() throws IOException {
        if (this.merged) {
            throw new IllegalStateException("Merge already performed");
        }
        if (this.entries.isEmpty()) {
            throw new IllegalStateException("No entries to merge have been defined");
        }
        this.merged = true;
        IndexOutput os = this.directory.createOutput(this.fileName);
        IOException priorException = null;
        try {
            try {
                os.writeVInt(-1);
                os.writeVInt(this.entries.size());
                long totalSize = 0L;
                for (FileEntry fe : this.entries) {
                    fe.directoryOffset = os.getFilePointer();
                    os.writeLong(0L);
                    os.writeString(IndexFileNames.stripSegmentName(fe.file));
                    totalSize += fe.dir.fileLength(fe.file);
                }
                long finalLength = totalSize + os.getFilePointer();
                os.setLength(finalLength);
                for (FileEntry fe : this.entries) {
                    fe.dataOffset = os.getFilePointer();
                    this.copyFile(fe, os);
                }
                for (FileEntry fe : this.entries) {
                    os.seek(fe.directoryOffset);
                    os.writeLong(fe.dataOffset);
                }
                assert (finalLength == os.length());
                IndexOutput tmp = os;
                os = null;
                tmp.close();
            }
            catch (IOException e) {
                priorException = e;
                Object var13_10 = null;
                IOUtils.closeWhileHandlingException(priorException, os);
                return;
            }
            Object var13_9 = null;
        }
        catch (Throwable throwable) {
            Object var13_11 = null;
            IOUtils.closeWhileHandlingException(priorException, os);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(priorException, os);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void copyFile(FileEntry source, IndexOutput os) throws IOException {
        IndexInput is = source.dir.openInput(source.file);
        try {
            long endPtr;
            long diff;
            long startPtr = os.getFilePointer();
            long length = is.length();
            os.copyBytes(is, length);
            if (this.checkAbort != null) {
                this.checkAbort.work(length);
            }
            if ((diff = (endPtr = os.getFilePointer()) - startPtr) != length) {
                throw new IOException("Difference in the output file offsets " + diff + " does not match the original file length " + length);
            }
            Object var13_8 = null;
        }
        catch (Throwable throwable) {
            Object var13_9 = null;
            is.close();
            throw throwable;
        }
        is.close();
    }

    private static final class FileEntry {
        String file;
        long directoryOffset;
        long dataOffset;
        Directory dir;

        private FileEntry() {
        }
    }
}

