/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexFileNameFilter;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.NoSuchDirectoryException;
import com.atlassian.lucene36.util.CollectionUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IndexFileDeleter {
    private List<String> deletable;
    private Map<String, RefCount> refCounts = new HashMap<String, RefCount>();
    private List<CommitPoint> commits = new ArrayList<CommitPoint>();
    private List<Collection<String>> lastFiles = new ArrayList<Collection<String>>();
    private List<CommitPoint> commitsToDelete = new ArrayList<CommitPoint>();
    private PrintStream infoStream;
    private Directory directory;
    private IndexDeletionPolicy policy;
    final boolean startingCommitDeleted;
    private SegmentInfos lastSegmentInfos;
    public static boolean VERBOSE_REF_COUNTS = false;
    private final IndexWriter writer;

    void setInfoStream(PrintStream infoStream) {
        this.infoStream = infoStream;
        if (infoStream != null) {
            this.message("setInfoStream deletionPolicy=" + this.policy);
        }
    }

    private void message(String message) {
        this.infoStream.println("IFD [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
    }

    private boolean locked() {
        return this.writer == null || Thread.holdsLock(this.writer);
    }

    public IndexFileDeleter(Directory directory, IndexDeletionPolicy policy, SegmentInfos segmentInfos, PrintStream infoStream, IndexWriter writer) throws CorruptIndexException, IOException {
        this.infoStream = infoStream;
        this.writer = writer;
        String currentSegmentsFile = segmentInfos.getSegmentsFileName();
        if (infoStream != null) {
            this.message("init: current segments file is \"" + currentSegmentsFile + "\"; deletionPolicy=" + policy);
        }
        this.policy = policy;
        this.directory = directory;
        long currentGen = segmentInfos.getGeneration();
        IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
        CommitPoint currentCommitPoint = null;
        String[] files = null;
        try {
            files = directory.listAll();
        }
        catch (NoSuchDirectoryException e) {
            files = new String[]{};
        }
        for (String fileName : files) {
            if (!filter.accept(null, fileName) || fileName.equals("segments.gen")) continue;
            this.getRefCount(fileName);
            if (!fileName.startsWith("segments")) continue;
            if (infoStream != null) {
                this.message("init: load commit \"" + fileName + "\"");
            }
            SegmentInfos sis = new SegmentInfos();
            try {
                sis.read(directory, fileName);
            }
            catch (FileNotFoundException e) {
                if (infoStream != null) {
                    this.message("init: hit FileNotFoundException when loading commit \"" + fileName + "\"; skipping this commit point");
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
        if (currentCommitPoint == null && currentSegmentsFile != null) {
            SegmentInfos sis = new SegmentInfos();
            try {
                sis.read(directory, currentSegmentsFile);
            }
            catch (IOException e) {
                throw new CorruptIndexException("failed to locate current segments_N file");
            }
            if (infoStream != null) {
                this.message("forced open of current segments file " + segmentInfos.getSegmentsFileName());
            }
            currentCommitPoint = new CommitPoint(this.commitsToDelete, directory, sis);
            this.commits.add(currentCommitPoint);
            this.incRef(sis, true);
        }
        CollectionUtil.mergeSort(this.commits);
        for (Map.Entry<String, RefCount> entry : this.refCounts.entrySet()) {
            String fileName;
            RefCount rc = entry.getValue();
            fileName = entry.getKey();
            if (0 != rc.count) continue;
            if (infoStream != null) {
                this.message("init: removing unreferenced file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
        if (currentSegmentsFile != null) {
            policy.onInit(this.commits);
        }
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
                if (this.infoStream != null) {
                    this.message("deleteCommits: now decRef commit \"" + commit.getSegmentsFileName() + "\"");
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
        IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
        if (segmentName != null) {
            segmentPrefix1 = segmentName + ".";
            segmentPrefix2 = segmentName + "_";
        } else {
            segmentPrefix1 = null;
            segmentPrefix2 = null;
        }
        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i];
            if (!filter.accept(null, fileName) || segmentName != null && !fileName.startsWith(segmentPrefix1) && !fileName.startsWith(segmentPrefix2) || this.refCounts.containsKey(fileName) || fileName.equals("segments.gen")) continue;
            if (this.infoStream != null) {
                this.message("refresh [prefix=" + segmentName + "]: removing newly created unreferenced file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
    }

    public void refresh() throws IOException {
        assert (this.locked());
        this.deletable = null;
        this.refresh(null);
    }

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
        if (this.infoStream != null) {
            this.message("now revisitPolicy");
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
                if (this.infoStream != null) {
                    this.message("delete pending file " + oldDeletable.get(i));
                }
                this.deleteFile(oldDeletable.get(i));
            }
        }
    }

    public void checkpoint(SegmentInfos segmentInfos, boolean isCommit) throws IOException {
        assert (this.locked());
        if (this.infoStream != null) {
            this.message("now checkpoint \"" + segmentInfos.getSegmentsFileName() + "\" [" + segmentInfos.size() + " segments " + "; isCommit = " + isCommit + "]");
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
    }

    void incRef(SegmentInfos segmentInfos, boolean isCommit) throws IOException {
        assert (this.locked());
        for (String fileName : segmentInfos.files(this.directory, isCommit)) {
            this.incRef(fileName);
        }
    }

    void incRef(Collection<String> files) throws IOException {
        assert (this.locked());
        for (String file : files) {
            this.incRef(file);
        }
    }

    void incRef(String fileName) throws IOException {
        assert (this.locked());
        RefCount rc = this.getRefCount(fileName);
        if (this.infoStream != null && VERBOSE_REF_COUNTS) {
            this.message("  IncRef \"" + fileName + "\": pre-incr count is " + rc.count);
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
        if (this.infoStream != null && VERBOSE_REF_COUNTS) {
            this.message("  DecRef \"" + fileName + "\": pre-decr count is " + rc.count);
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
            if (this.refCounts.containsKey(fileName)) continue;
            if (this.infoStream != null) {
                this.message("delete new file \"" + fileName + "\"");
            }
            this.deleteFile(fileName);
        }
    }

    void deleteFile(String fileName) throws IOException {
        block6: {
            assert (this.locked());
            try {
                if (this.infoStream != null) {
                    this.message("delete \"" + fileName + "\"");
                }
                this.directory.deleteFile(fileName);
            }
            catch (IOException e) {
                if (!this.directory.fileExists(fileName)) break block6;
                if (this.infoStream != null) {
                    this.message("unable to remove file \"" + fileName + "\": " + e.toString() + "; Will re-try later.");
                }
                if (this.deletable == null) {
                    this.deletable = new ArrayList<String>();
                }
                this.deletable.add(fileName);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class CommitPoint
    extends IndexCommit {
        Collection<String> files;
        String segmentsFileName;
        boolean deleted;
        Directory directory;
        Collection<CommitPoint> commitsToDelete;
        long version;
        long generation;
        final Map<String, String> userData;
        private final int segmentCount;

        public CommitPoint(Collection<CommitPoint> commitsToDelete, Directory directory, SegmentInfos segmentInfos) throws IOException {
            this.directory = directory;
            this.commitsToDelete = commitsToDelete;
            this.userData = segmentInfos.getUserData();
            this.segmentsFileName = segmentInfos.getSegmentsFileName();
            this.version = segmentInfos.getVersion();
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
        public Collection<String> getFileNames() throws IOException {
            return this.files;
        }

        @Override
        public Directory getDirectory() {
            return this.directory;
        }

        @Override
        public long getVersion() {
            return this.version;
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

