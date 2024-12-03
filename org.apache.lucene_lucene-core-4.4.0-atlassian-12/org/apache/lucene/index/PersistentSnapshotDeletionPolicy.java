/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;

public class PersistentSnapshotDeletionPolicy
extends SnapshotDeletionPolicy {
    public static final String SNAPSHOTS_PREFIX = "snapshots_";
    private static final int VERSION_START = 0;
    private static final int VERSION_CURRENT = 0;
    private static final String CODEC_NAME = "snapshots";
    private long nextWriteGen;
    private final Directory dir;

    public PersistentSnapshotDeletionPolicy(IndexDeletionPolicy primary, Directory dir) throws IOException {
        this(primary, dir, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

    public PersistentSnapshotDeletionPolicy(IndexDeletionPolicy primary, Directory dir, IndexWriterConfig.OpenMode mode) throws IOException {
        super(primary);
        this.dir = dir;
        if (mode == IndexWriterConfig.OpenMode.CREATE) {
            this.clearPriorSnapshots();
        }
        this.loadPriorSnapshots();
        if (mode == IndexWriterConfig.OpenMode.APPEND && this.nextWriteGen == 0L) {
            throw new IllegalStateException("no snapshots stored in this directory");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized IndexCommit snapshot() throws IOException {
        IndexCommit ic = super.snapshot();
        boolean success = false;
        try {
            this.persist();
            success = true;
        }
        finally {
            if (!success) {
                try {
                    super.release(ic);
                }
                catch (Exception exception) {}
            }
        }
        return ic;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void release(IndexCommit commit) throws IOException {
        super.release(commit);
        boolean success = false;
        try {
            this.persist();
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.incRef(commit);
                }
                catch (Exception exception) {}
            }
        }
    }

    public synchronized void release(long gen) throws IOException {
        super.releaseGen(gen);
        this.persist();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void persist() throws IOException {
        String lastSaveFile;
        String fileName;
        block11: {
            IndexOutput out;
            block10: {
                fileName = SNAPSHOTS_PREFIX + this.nextWriteGen;
                out = this.dir.createOutput(fileName, IOContext.DEFAULT);
                boolean success = false;
                try {
                    CodecUtil.writeHeader(out, CODEC_NAME, 0);
                    out.writeVInt(this.refCounts.size());
                    for (Map.Entry ent : this.refCounts.entrySet()) {
                        out.writeVLong((Long)ent.getKey());
                        out.writeVInt((Integer)ent.getValue());
                    }
                    success = true;
                    if (success) break block10;
                }
                catch (Throwable throwable) {
                    if (!success) {
                        IOUtils.closeWhileHandlingException(out);
                        try {
                            this.dir.deleteFile(fileName);
                        }
                        catch (Exception exception) {}
                    } else {
                        IOUtils.close(out);
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(out);
                try {
                    this.dir.deleteFile(fileName);
                }
                catch (Exception exception) {}
                break block11;
            }
            IOUtils.close(out);
        }
        this.dir.sync(Collections.singletonList(fileName));
        if (this.nextWriteGen > 0L && this.dir.fileExists(lastSaveFile = SNAPSHOTS_PREFIX + (this.nextWriteGen - 1L))) {
            this.dir.deleteFile(lastSaveFile);
        }
        ++this.nextWriteGen;
    }

    private synchronized void clearPriorSnapshots() throws IOException {
        for (String file : this.dir.listAll()) {
            if (!file.startsWith(SNAPSHOTS_PREFIX)) continue;
            this.dir.deleteFile(file);
        }
    }

    public String getLastSaveFile() {
        if (this.nextWriteGen == 0L) {
            return null;
        }
        return SNAPSHOTS_PREFIX + (this.nextWriteGen - 1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void loadPriorSnapshots() throws IOException {
        long genLoaded = -1L;
        IOException ioe = null;
        ArrayList<String> snapshotFiles = new ArrayList<String>();
        for (String file : this.dir.listAll()) {
            if (!file.startsWith(SNAPSHOTS_PREFIX)) continue;
            long gen = Long.parseLong(file.substring(SNAPSHOTS_PREFIX.length()));
            if (genLoaded != -1L && gen <= genLoaded) continue;
            snapshotFiles.add(file);
            HashMap<Long, Integer> m = new HashMap<Long, Integer>();
            try (IndexInput in = this.dir.openInput(file, IOContext.DEFAULT);){
                CodecUtil.checkHeader(in, CODEC_NAME, 0, 0);
                int count = in.readVInt();
                for (int i = 0; i < count; ++i) {
                    long commitGen = in.readVLong();
                    int refCount = in.readVInt();
                    m.put(commitGen, refCount);
                }
            }
            genLoaded = gen;
            this.refCounts.clear();
            this.refCounts.putAll(m);
        }
        if (genLoaded == -1L) {
            if (ioe != null) {
                throw ioe;
            }
        } else {
            if (snapshotFiles.size() > 1) {
                String curFileName = SNAPSHOTS_PREFIX + genLoaded;
                for (String file : snapshotFiles) {
                    if (curFileName.equals(file)) continue;
                    this.dir.deleteFile(file);
                }
            }
            this.nextWriteGen = 1L + genLoaded;
        }
    }
}

