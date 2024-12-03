/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.CollectionUtil;
import org.apache.lucene.util.InfoStream;

final class IndexFileDeleter
implements Closeable {
    private List<String> deletable;
    private Map<String, RefCount> refCounts = new HashMap<String, RefCount>();
    private List<CommitPoint> commits = new ArrayList<CommitPoint>();
    private List<Collection<String>> lastFiles = new ArrayList<Collection<String>>();
    private List<CommitPoint> commitsToDelete = new ArrayList<CommitPoint>();
    private final InfoStream infoStream;
    private Directory directory;
    private IndexDeletionPolicy policy;
    final boolean startingCommitDeleted;
    private SegmentInfos lastSegmentInfos;
    public static boolean VERBOSE_REF_COUNTS = false;
    private final IndexWriter writer;

    private boolean locked() {
        return this.writer == null || Thread.holdsLock(this.writer);
    }

    public IndexFileDeleter(Directory directory, IndexDeletionPolicy policy, SegmentInfos segmentInfos, InfoStream infoStream, IndexWriter writer, boolean initialIndexExists) throws IOException {
        this.infoStream = infoStream;
        this.writer = writer;
        String currentSegmentsFile = segmentInfos.getSegmentsFileName();
        if (infoStream.isEnabled("IFD")) {
            infoStream.message("IFD", "init: current segments file is \"" + currentSegmentsFile + "\"; deletionPolicy=" + policy);
        }
        this.policy = policy;
        this.directory = directory;
        long currentGen = segmentInfos.getGeneration();
        CommitPoint currentCommitPoint = null;
        String[] files = null;
        try {
            files = directory.listAll();
        }
        catch (NoSuchDirectoryException e) {
            files = new String[]{};
        }
        if (currentSegmentsFile != null) {
            Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
            for (String fileName : files) {
                m.reset(fileName);
                if (fileName.endsWith("write.lock") || fileName.equals("segments.gen") || !m.matches() && !fileName.startsWith("segments")) continue;
                this.getRefCount(fileName);
                if (!fileName.startsWith("segments")) continue;
                if (infoStream.isEnabled("IFD")) {
                    infoStream.message("IFD", "init: load commit \"" + fileName + "\"");
                }
                SegmentInfos sis = new SegmentInfos();
                try {
                    sis.read(directory, fileName);
                }
                catch (FileNotFoundException e) {
                    if (infoStream.isEnabled("IFD")) {
                        infoStream.message("IFD", "init: hit FileNotFoundException when loading commit \"" + fileName + "\"; skipping this commit point");
                    }
                    sis = null;
                }
                catch (IOException e) {
                    if (SegmentInfos.generationFromSegmentsFileName(fileName) <= currentGen && directory.fileLength(fileName) > 0L) {
                        throw e;
                    }
                    sis = null;
                }
                if (sis == null) continue;
                CommitPoint commitPoint = new CommitPoint(this.commitsToDelete, directory, sis);
                if (sis.getGeneration() == segmentInfos.getGeneration()) {
                    currentCommitPoint = commitPoint;
                }
                this.commits.add(commitPoint);
                this.incRef(sis, true);
                if (this.lastSegmentInfos != null && sis.getGeneration() <= this.lastSegmentInfos.getGeneration()) continue;
                this.lastSegmentInfos = sis;
            }
        }
        if (currentCommitPoint == null && currentSegmentsFile != null && initialIndexExists) {
            SegmentInfos sis = new SegmentInfos();
            try {
                sis.read(directory, currentSegmentsFile);
            }
            catch (IOException e) {
                throw new CorruptIndexException("failed to locate current segments_N file \"" + currentSegmentsFile + "\"");
            }
            if (infoStream.isEnabled("IFD")) {
                infoStream.message("IFD", "forced open of current segments file " + segmentInfos.getSegmentsFileName());
            }
            currentCommitPoint = new CommitPoint(this.commitsToDelete, directory, sis);
            this.commits.add(currentCommitPoint);
            this.incRef(sis, true);
        }
        CollectionUtil.timSort(this.commits);
        for (Map.Entry<String, RefCount> entry : this.refCounts.entrySet()) {
            RefCount rc = entry.getValue();
            String fileName = entry.getKey();
            if (0 != rc.count) continue;
            if (infoStream.isEnabled("IFD")) {
                infoStream.message("IFD", "init: removing unreferenced file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
        policy.onInit(this.commits);
        this.checkpoint(segmentInfos, false);
        this.startingCommitDeleted = currentCommitPoint == null ? false : currentCommitPoint.isDeleted();
        this.deleteCommits();
    }

    public SegmentInfos getLastSegmentInfos() {
        return this.lastSegmentInfos;
    }

    private void deleteCommits() throws IOException {
        int size = this.commitsToDelete.size();
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                CommitPoint commit = this.commitsToDelete.get(i);
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "deleteCommits: now decRef commit \"" + commit.getSegmentsFileName() + "\"");
                }
                for (String file : commit.files) {
                    this.decRef(file);
                }
            }
            this.commitsToDelete.clear();
            size = this.commits.size();
            int writeTo = 0;
            for (int readFrom = 0; readFrom < size; ++readFrom) {
                CommitPoint commit = this.commits.get(readFrom);
                if (commit.deleted) continue;
                if (writeTo != readFrom) {
                    this.commits.set(writeTo, this.commits.get(readFrom));
                }
                ++writeTo;
            }
            while (size > writeTo) {
                this.commits.remove(size - 1);
                --size;
            }
        }
    }

    public void refresh(String segmentName) throws IOException {
        String segmentPrefix2;
        String segmentPrefix1;
        assert (this.locked());
        String[] files = this.directory.listAll();
        if (segmentName != null) {
            segmentPrefix1 = segmentName + ".";
            segmentPrefix2 = segmentName + "_";
        } else {
            segmentPrefix1 = null;
            segmentPrefix2 = null;
        }
        Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i];
            m.reset(fileName);
            if (segmentName != null && !fileName.startsWith(segmentPrefix1) && !fileName.startsWith(segmentPrefix2) || fileName.endsWith("write.lock") || this.refCounts.containsKey(fileName) || fileName.equals("segments.gen") || !m.matches() && !fileName.startsWith("segments")) continue;
            if (this.infoStream.isEnabled("IFD")) {
                this.infoStream.message("IFD", "refresh [prefix=" + segmentName + "]: removing newly created unreferenced file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
    }

    public void refresh() throws IOException {
        assert (this.locked());
        this.deletable = null;
        this.refresh(null);
    }

    @Override
    public void close() throws IOException {
        assert (this.locked());
        int size = this.lastFiles.size();
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                this.decRef(this.lastFiles.get(i));
            }
            this.lastFiles.clear();
        }
        this.deletePendingFiles();
    }

    void revisitPolicy() throws IOException {
        assert (this.locked());
        if (this.infoStream.isEnabled("IFD")) {
            this.infoStream.message("IFD", "now revisitPolicy");
        }
        if (this.commits.size() > 0) {
            this.policy.onCommit(this.commits);
            this.deleteCommits();
        }
    }

    public void deletePendingFiles() throws IOException {
        assert (this.locked());
        if (this.deletable != null) {
            List<String> oldDeletable = this.deletable;
            this.deletable = null;
            int size = oldDeletable.size();
            for (int i = 0; i < size; ++i) {
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "delete pending file " + oldDeletable.get(i));
                }
                this.deleteFile(oldDeletable.get(i));
            }
        }
    }

    public void checkpoint(SegmentInfos segmentInfos, boolean isCommit) throws IOException {
        assert (this.locked());
        assert (Thread.holdsLock(this.writer));
        long t0 = 0L;
        if (this.infoStream.isEnabled("IFD")) {
            t0 = System.nanoTime();
            this.infoStream.message("IFD", "now checkpoint \"" + this.writer.segString(this.writer.toLiveInfos(segmentInfos)) + "\" [" + segmentInfos.size() + " segments ; isCommit = " + isCommit + "]");
        }
        this.deletePendingFiles();
        this.incRef(segmentInfos, isCommit);
        if (isCommit) {
            this.commits.add(new CommitPoint(this.commitsToDelete, this.directory, segmentInfos));
            this.policy.onCommit(this.commits);
            this.deleteCommits();
        } else {
            for (Collection<String> lastFile : this.lastFiles) {
                this.decRef(lastFile);
            }
            this.lastFiles.clear();
            this.lastFiles.add(segmentInfos.files(this.directory, false));
        }
        if (this.infoStream.isEnabled("IFD")) {
            long t1 = System.nanoTime();
            this.infoStream.message("IFD", (t1 - t0) / 1000000L + " msec to checkpoint");
        }
    }

    void incRef(SegmentInfos segmentInfos, boolean isCommit) throws IOException {
        assert (this.locked());
        for (String fileName : segmentInfos.files(this.directory, isCommit)) {
            this.incRef(fileName);
        }
    }

    void incRef(Collection<String> files) {
        assert (this.locked());
        for (String file : files) {
            this.incRef(file);
        }
    }

    void incRef(String fileName) {
        assert (this.locked());
        RefCount rc = this.getRefCount(fileName);
        if (this.infoStream.isEnabled("IFD") && VERBOSE_REF_COUNTS) {
            this.infoStream.message("IFD", "  IncRef \"" + fileName + "\": pre-incr count is " + rc.count);
        }
        rc.IncRef();
    }

    void decRef(Collection<String> files) throws IOException {
        assert (this.locked());
        for (String file : files) {
            this.decRef(file);
        }
    }

    void decRef(String fileName) throws IOException {
        assert (this.locked());
        RefCount rc = this.getRefCount(fileName);
        if (this.infoStream.isEnabled("IFD") && VERBOSE_REF_COUNTS) {
            this.infoStream.message("IFD", "  DecRef \"" + fileName + "\": pre-decr count is " + rc.count);
        }
        if (0 == rc.DecRef()) {
            this.deleteFile(fileName);
            this.refCounts.remove(fileName);
        }
    }

    void decRef(SegmentInfos segmentInfos) throws IOException {
        assert (this.locked());
        for (String file : segmentInfos.files(this.directory, false)) {
            this.decRef(file);
        }
    }

    public boolean exists(String fileName) {
        assert (this.locked());
        if (!this.refCounts.containsKey(fileName)) {
            return false;
        }
        return this.getRefCount((String)fileName).count > 0;
    }

    private RefCount getRefCount(String fileName) {
        RefCount rc;
        assert (this.locked());
        if (!this.refCounts.containsKey(fileName)) {
            rc = new RefCount(fileName);
            this.refCounts.put(fileName, rc);
        } else {
            rc = this.refCounts.get(fileName);
        }
        return rc;
    }

    void deleteFiles(List<String> files) throws IOException {
        assert (this.locked());
        for (String file : files) {
            this.deleteFile(file);
        }
    }

    void deleteNewFiles(Collection<String> files) throws IOException {
        assert (this.locked());
        for (String fileName : files) {
            if (this.refCounts.containsKey(fileName) && this.refCounts.get((Object)fileName).count != 0) continue;
            if (this.infoStream.isEnabled("IFD")) {
                this.infoStream.message("IFD", "delete new file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
    }

    void deleteFile(String fileName) throws IOException {
        block6: {
            assert (this.locked());
            try {
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "delete \"" + fileName + "\"");
                }
                this.directory.deleteFile(fileName);
            }
            catch (IOException e) {
                if (!this.directory.fileExists(fileName)) break block6;
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "unable to remove file \"" + fileName + "\": " + e.toString() + "; Will re-try later.");
                }
                if (this.deletable == null) {
                    this.deletable = new ArrayList<String>();
                }
                this.deletable.add(fileName);
            }
        }
    }

    private static final class CommitPoint
    extends IndexCommit {
        Collection<String> files;
        String segmentsFileName;
        boolean deleted;
        Directory directory;
        Collection<CommitPoint> commitsToDelete;
        long generation;
        final Map<String, String> userData;
        private final int segmentCount;

        public CommitPoint(Collection<CommitPoint> commitsToDelete, Directory directory, SegmentInfos segmentInfos) throws IOException {
            this.directory = directory;
            this.commitsToDelete = commitsToDelete;
            this.userData = segmentInfos.getUserData();
            this.segmentsFileName = segmentInfos.getSegmentsFileName();
            this.generation = segmentInfos.getGeneration();
            this.files = Collections.unmodifiableCollection(segmentInfos.files(directory, true));
            this.segmentCount = segmentInfos.size();
        }

        public String toString() {
            return "IndexFileDeleter.CommitPoint(" + this.segmentsFileName + ")";
        }

        @Override
        public int getSegmentCount() {
            return this.segmentCount;
        }

        @Override
        public String getSegmentsFileName() {
            return this.segmentsFileName;
        }

        @Override
        public Collection<String> getFileNames() {
            return this.files;
        }

        @Override
        public Directory getDirectory() {
            return this.directory;
        }

        @Override
        public long getGeneration() {
            return this.generation;
        }

        @Override
        public Map<String, String> getUserData() {
            return this.userData;
        }

        @Override
        public void delete() {
            if (!this.deleted) {
                this.deleted = true;
                this.commitsToDelete.add(this);
            }
        }

        @Override
        public boolean isDeleted() {
            return this.deleted;
        }
    }

    private static final class RefCount {
        final String fileName;
        boolean initDone;
        int count;

        RefCount(String fileName) {
            this.fileName = fileName;
        }

        public int IncRef() {
            if (!this.initDone) {
                this.initDone = true;
            } else assert (this.count > 0) : Thread.currentThread().getName() + ": RefCount is 0 pre-increment for file \"" + this.fileName + "\"";
            return ++this.count;
        }

        public int DecRef() {
            assert (this.count > 0) : Thread.currentThread().getName() + ": RefCount is 0 pre-decrement for file \"" + this.fileName + "\"";
            return --this.count;
        }
    }
}

