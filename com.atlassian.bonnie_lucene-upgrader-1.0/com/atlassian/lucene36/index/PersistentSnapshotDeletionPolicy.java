/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.Field;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.IndexWriterConfig;
import com.atlassian.lucene36.index.SnapshotDeletionPolicy;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PersistentSnapshotDeletionPolicy
extends SnapshotDeletionPolicy {
    private static final String SNAPSHOTS_ID = "$SNAPSHOTS_DOC$";
    private final IndexWriter writer;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, String> readSnapshotsInfo(Directory dir) throws IOException {
        IndexReader r = IndexReader.open(dir, true);
        HashMap<String, String> snapshots = new HashMap<String, String>();
        try {
            int numDocs = r.numDocs();
            if (numDocs == 1) {
                Document doc = r.document(r.maxDoc() - 1);
                Field sid = doc.getField(SNAPSHOTS_ID);
                if (sid == null) {
                    throw new IllegalStateException("directory is not a valid snapshots store!");
                }
                doc.removeField(SNAPSHOTS_ID);
                for (Fieldable f : doc.getFields()) {
                    snapshots.put(f.name(), f.stringValue());
                }
            } else if (numDocs != 0) {
                throw new IllegalStateException("should be at most 1 document in the snapshots directory: " + numDocs);
            }
            Object var9_8 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            r.close();
            throw throwable;
        }
        r.close();
        return snapshots;
    }

    public PersistentSnapshotDeletionPolicy(IndexDeletionPolicy primary, Directory dir, IndexWriterConfig.OpenMode mode, Version matchVersion) throws CorruptIndexException, LockObtainFailedException, IOException {
        super(primary, null);
        this.writer = new IndexWriter(dir, new IndexWriterConfig(matchVersion, null).setOpenMode(mode));
        if (mode != IndexWriterConfig.OpenMode.APPEND) {
            this.writer.commit();
        }
        try {
            for (Map.Entry<String, String> e : PersistentSnapshotDeletionPolicy.readSnapshotsInfo(dir).entrySet()) {
                this.registerSnapshotInfo(e.getKey(), e.getValue(), null);
            }
        }
        catch (RuntimeException e) {
            this.writer.close();
            throw e;
        }
        catch (IOException e) {
            this.writer.close();
            throw e;
        }
    }

    @Override
    public synchronized void onInit(List<? extends IndexCommit> commits) throws IOException {
        super.onInit(commits);
        this.persistSnapshotInfos(null, null);
    }

    @Override
    public synchronized IndexCommit snapshot(String id) throws IOException {
        this.checkSnapshotted(id);
        if (SNAPSHOTS_ID.equals(id)) {
            throw new IllegalArgumentException(id + " is reserved and cannot be used as a snapshot id");
        }
        this.persistSnapshotInfos(id, this.lastCommit.getSegmentsFileName());
        return super.snapshot(id);
    }

    @Override
    public synchronized void release(String id) throws IOException {
        super.release(id);
        this.persistSnapshotInfos(null, null);
    }

    public void close() throws CorruptIndexException, IOException {
        this.writer.close();
    }

    private void persistSnapshotInfos(String id, String segment) throws IOException {
        this.writer.deleteAll();
        Document d = new Document();
        d.add(new Field(SNAPSHOTS_ID, "", Field.Store.YES, Field.Index.NO));
        for (Map.Entry<String, String> e : super.getSnapshots().entrySet()) {
            d.add(new Field(e.getKey(), e.getValue(), Field.Store.YES, Field.Index.NO));
        }
        if (id != null) {
            d.add(new Field(id, segment, Field.Store.YES, Field.Index.NO));
        }
        this.writer.addDocument(d);
        this.writer.commit();
    }
}

