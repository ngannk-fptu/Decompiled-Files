/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CompoundFileReader;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldsReader;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexFormatTooNewException;
import com.atlassian.lucene36.index.IndexFormatTooOldException;
import com.atlassian.lucene36.index.IndexNotFoundException;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.store.ChecksumIndexInput;
import com.atlassian.lucene36.store.ChecksumIndexOutput;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.NoSuchDirectoryException;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SegmentInfos
implements Cloneable,
Iterable<SegmentInfo> {
    public static final int FORMAT = -1;
    public static final int FORMAT_LOCKLESS = -2;
    public static final int FORMAT_SINGLE_NORM_FILE = -3;
    public static final int FORMAT_SHARED_DOC_STORE = -4;
    public static final int FORMAT_CHECKSUM = -5;
    public static final int FORMAT_DEL_COUNT = -6;
    public static final int FORMAT_HAS_PROX = -7;
    public static final int FORMAT_USER_DATA = -8;
    public static final int FORMAT_DIAGNOSTICS = -9;
    public static final int FORMAT_HAS_VECTORS = -10;
    public static final int FORMAT_3_1 = -11;
    public static final int CURRENT_FORMAT = -11;
    public static final int FORMAT_MINIMUM = -1;
    public static final int FORMAT_MAXIMUM = -11;
    public int counter = 0;
    long version = System.currentTimeMillis();
    private long generation = 0L;
    private long lastGeneration = 0L;
    private Map<String, String> userData = Collections.emptyMap();
    private int format;
    private List<SegmentInfo> segments = new ArrayList<SegmentInfo>();
    private Set<SegmentInfo> segmentSet = new HashSet<SegmentInfo>();
    private transient List<SegmentInfo> cachedUnmodifiableList;
    private transient Set<SegmentInfo> cachedUnmodifiableSet;
    private static PrintStream infoStream = null;
    ChecksumIndexOutput pendingSegnOutput;
    private static int defaultGenLookaheadCount = 10;

    public void setFormat(int format) {
        this.format = format;
    }

    public int getFormat() {
        return this.format;
    }

    public SegmentInfo info(int i) {
        return this.segments.get(i);
    }

    public static long getLastCommitGeneration(String[] files) {
        if (files == null) {
            return -1L;
        }
        long max = -1L;
        for (int i = 0; i < files.length; ++i) {
            long gen;
            String file = files[i];
            if (!file.startsWith("segments") || file.equals("segments.gen") || (gen = SegmentInfos.generationFromSegmentsFileName(file)) <= max) continue;
            max = gen;
        }
        return max;
    }

    public static long getLastCommitGeneration(Directory directory) throws IOException {
        try {
            return SegmentInfos.getLastCommitGeneration(directory.listAll());
        }
        catch (NoSuchDirectoryException nsde) {
            return -1L;
        }
    }

    public static String getLastCommitSegmentsFileName(String[] files) throws IOException {
        return IndexFileNames.fileNameFromGeneration("segments", "", SegmentInfos.getLastCommitGeneration(files));
    }

    public static String getLastCommitSegmentsFileName(Directory directory) throws IOException {
        return IndexFileNames.fileNameFromGeneration("segments", "", SegmentInfos.getLastCommitGeneration(directory));
    }

    public String getSegmentsFileName() {
        return IndexFileNames.fileNameFromGeneration("segments", "", this.lastGeneration);
    }

    public static long generationFromSegmentsFileName(String fileName) {
        if (fileName.equals("segments")) {
            return 0L;
        }
        if (fileName.startsWith("segments")) {
            return Long.parseLong(fileName.substring(1 + "segments".length()), 36);
        }
        throw new IllegalArgumentException("fileName \"" + fileName + "\" is not a segments file");
    }

    public String getNextSegmentFileName() {
        long nextGeneration = this.generation == -1L ? 1L : this.generation + 1L;
        return IndexFileNames.fileNameFromGeneration("segments", "", nextGeneration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void read(Directory directory, String segmentFileName) throws CorruptIndexException, IOException {
        boolean success = false;
        this.clear();
        ChecksumIndexInput input = new ChecksumIndexInput(directory.openInput(segmentFileName));
        this.lastGeneration = this.generation = SegmentInfos.generationFromSegmentsFileName(segmentFileName);
        try {
            long checksumThen;
            long checksumNow;
            int format = input.readInt();
            if (format > -1) {
                throw new IndexFormatTooOldException(input, format, -1, -11);
            }
            if (format < -11) {
                throw new IndexFormatTooNewException(input, format, -1, -11);
            }
            this.version = input.readLong();
            this.counter = input.readInt();
            for (int i = input.readInt(); i > 0; --i) {
                SegmentInfo si;
                block14: {
                    si = new SegmentInfo(directory, format, input);
                    if (si.getVersion() == null) {
                        Object var11_10;
                        Directory dir = directory;
                        if (si.getDocStoreOffset() != -1) {
                            if (si.getDocStoreIsCompoundFile()) {
                                dir = new CompoundFileReader(dir, IndexFileNames.segmentFileName(si.getDocStoreSegment(), "cfx"), 1024);
                            }
                        } else if (si.getUseCompoundFile()) {
                            dir = new CompoundFileReader(dir, IndexFileNames.segmentFileName(si.name, "cfs"), 1024);
                        }
                        try {
                            String store = si.getDocStoreOffset() != -1 ? si.getDocStoreSegment() : si.name;
                            si.setVersion(FieldsReader.detectCodeVersion(dir, store));
                            var11_10 = null;
                            if (dir == directory) break block14;
                        }
                        catch (Throwable throwable) {
                            var11_10 = null;
                            if (dir == directory) throw throwable;
                            dir.close();
                            throw throwable;
                        }
                        dir.close();
                    }
                }
                this.add(si);
            }
            if (format >= 0) {
                this.version = input.getFilePointer() >= input.length() ? System.currentTimeMillis() : input.readLong();
            }
            this.userData = format <= -8 ? (format <= -9 ? input.readStringStringMap() : (0 != input.readByte() ? Collections.singletonMap("userData", input.readString()) : Collections.emptyMap())) : Collections.emptyMap();
            if (format <= -5 && (checksumNow = input.getChecksum()) != (checksumThen = input.readLong())) {
                throw new CorruptIndexException("checksum mismatch in segments file (resource: " + input + ")");
            }
            success = true;
            Object var17_14 = null;
        }
        catch (Throwable throwable) {
            Object var17_15 = null;
            input.close();
            if (success) throw throwable;
            this.clear();
            throw throwable;
        }
        input.close();
        if (success) return;
        this.clear();
    }

    public final void read(Directory directory) throws CorruptIndexException, IOException {
        this.lastGeneration = -1L;
        this.generation = -1L;
        new FindSegmentsFile(directory){

            protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException {
                SegmentInfos.this.read(this.directory, segmentFileName);
                return null;
            }
        }.run();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final void write(Directory directory) throws IOException {
        String segmentFileName = this.getNextSegmentFileName();
        this.generation = this.generation == -1L ? 1L : ++this.generation;
        ChecksumIndexOutput segnOutput = null;
        boolean success = false;
        try {
            segnOutput = new ChecksumIndexOutput(directory.createOutput(segmentFileName));
            segnOutput.writeInt(-11);
            segnOutput.writeLong(this.version);
            segnOutput.writeInt(this.counter);
            segnOutput.writeInt(this.size());
            for (SegmentInfo si : this) {
                si.write(segnOutput);
            }
            segnOutput.writeStringStringMap(this.userData);
            segnOutput.prepareCommit();
            this.pendingSegnOutput = segnOutput;
            return;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(segnOutput);
            try {
                directory.deleteFile(segmentFileName);
                throw throwable;
            }
            catch (Throwable t) {
                // empty catch block
            }
            throw throwable;
        }
    }

    public void pruneDeletedSegments() throws IOException {
        Iterator<SegmentInfo> it = this.segments.iterator();
        while (it.hasNext()) {
            SegmentInfo info = it.next();
            if (info.getDelCount() != info.docCount) continue;
            it.remove();
            this.segmentSet.remove(info);
        }
        assert (this.segmentSet.size() == this.segments.size());
    }

    public Object clone() {
        try {
            SegmentInfos sis = (SegmentInfos)super.clone();
            sis.segments = new ArrayList<SegmentInfo>(this.size());
            sis.segmentSet = new HashSet<SegmentInfo>(this.size());
            sis.cachedUnmodifiableList = null;
            sis.cachedUnmodifiableSet = null;
            for (SegmentInfo info : this) {
                sis.add((SegmentInfo)info.clone());
            }
            sis.userData = new HashMap<String, String>(this.userData);
            return sis;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("should not happen", e);
        }
    }

    public long getVersion() {
        return this.version;
    }

    public long getGeneration() {
        return this.generation;
    }

    public long getLastGeneration() {
        return this.lastGeneration;
    }

    @Deprecated
    public static long readCurrentVersion(Directory directory) throws CorruptIndexException, IOException {
        SegmentInfos sis = new SegmentInfos();
        sis.read(directory);
        return sis.version;
    }

    public static void setInfoStream(PrintStream infoStream) {
        SegmentInfos.infoStream = infoStream;
    }

    public static void setDefaultGenLookaheadCount(int count) {
        defaultGenLookaheadCount = count;
    }

    public static int getDefaultGenLookahedCount() {
        return defaultGenLookaheadCount;
    }

    public static PrintStream getInfoStream() {
        return infoStream;
    }

    private static void message(String message) {
        infoStream.println("SIS [" + Thread.currentThread().getName() + "]: " + message);
    }

    @Deprecated
    public SegmentInfos range(int first, int last) {
        SegmentInfos infos = new SegmentInfos();
        infos.addAll(this.segments.subList(first, last));
        return infos;
    }

    void updateGeneration(SegmentInfos other) {
        this.lastGeneration = other.lastGeneration;
        this.generation = other.generation;
    }

    final void rollbackCommit(Directory dir) throws IOException {
        if (this.pendingSegnOutput != null) {
            try {
                this.pendingSegnOutput.close();
            }
            catch (Throwable t) {
                // empty catch block
            }
            try {
                String segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
                dir.deleteFile(segmentFileName);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.pendingSegnOutput = null;
        }
    }

    final void prepareCommit(Directory dir) throws IOException {
        if (this.pendingSegnOutput != null) {
            throw new IllegalStateException("prepareCommit was already called");
        }
        this.write(dir);
    }

    public Collection<String> files(Directory dir, boolean includeSegmentsFile) throws IOException {
        String segmentFileName;
        HashSet<String> files = new HashSet<String>();
        if (includeSegmentsFile && (segmentFileName = this.getSegmentsFileName()) != null) {
            files.add(segmentFileName);
        }
        int size = this.size();
        for (int i = 0; i < size; ++i) {
            SegmentInfo info = this.info(i);
            if (info.dir != dir) continue;
            files.addAll(this.info(i).files());
        }
        return files;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    final void finishCommit(Directory dir) throws IOException {
        block15: {
            boolean success;
            block14: {
                if (this.pendingSegnOutput == null) {
                    throw new IllegalStateException("prepareCommit was not called");
                }
                success = false;
                try {
                    this.pendingSegnOutput.finishCommit();
                    this.pendingSegnOutput.close();
                    this.pendingSegnOutput = null;
                    success = true;
                    Object var4_3 = null;
                    if (success) break block14;
                }
                catch (Throwable throwable) {
                    Object var4_4 = null;
                    if (success) throw throwable;
                    this.rollbackCommit(dir);
                    throw throwable;
                }
                this.rollbackCommit(dir);
            }
            String fileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
            success = false;
            try {
                dir.sync(Collections.singleton(fileName));
                success = true;
                Object var6_8 = null;
                if (success) break block15;
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                if (success) throw throwable;
                try {
                    dir.deleteFile(fileName);
                    throw throwable;
                }
                catch (Throwable t) {
                    // empty catch block
                }
                throw throwable;
            }
            try {}
            catch (Throwable t) {}
            dir.deleteFile(fileName);
        }
        this.lastGeneration = this.generation;
        try {
            IndexOutput genOutput = dir.createOutput("segments.gen");
            try {
                genOutput.writeInt(-2);
                genOutput.writeLong(this.generation);
                genOutput.writeLong(this.generation);
                Object var9_14 = null;
            }
            catch (Throwable throwable) {
                Object var9_15 = null;
                genOutput.close();
                dir.sync(Collections.singleton("segments.gen"));
                throw throwable;
            }
            genOutput.close();
            dir.sync(Collections.singleton("segments.gen"));
            return;
        }
        catch (Throwable t) {
            try {
                dir.deleteFile("segments.gen");
            }
            catch (Throwable t2) {
                // empty catch block
            }
            if (!(t instanceof ThreadInterruptedException)) return;
            throw (ThreadInterruptedException)t;
        }
    }

    final void commit(Directory dir) throws IOException {
        this.prepareCommit(dir);
        this.finishCommit(dir);
    }

    public String toString(Directory directory) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.getSegmentsFileName()).append(": ");
        int count = this.size();
        for (int i = 0; i < count; ++i) {
            if (i > 0) {
                buffer.append(' ');
            }
            SegmentInfo info = this.info(i);
            buffer.append(info.toString(directory, 0));
        }
        return buffer.toString();
    }

    public Map<String, String> getUserData() {
        return this.userData;
    }

    void setUserData(Map<String, String> data) {
        this.userData = data == null ? Collections.emptyMap() : data;
    }

    void replace(SegmentInfos other) {
        this.rollbackSegmentInfos(other.asList());
        this.lastGeneration = other.lastGeneration;
    }

    public int totalDocCount() {
        int count = 0;
        for (SegmentInfo info : this) {
            count += info.docCount;
        }
        return count;
    }

    public void changed() {
        ++this.version;
    }

    void applyMergeChanges(MergePolicy.OneMerge merge, boolean dropSegment) {
        HashSet<SegmentInfo> mergedAway = new HashSet<SegmentInfo>(merge.segments);
        boolean inserted = false;
        int newSegIdx = 0;
        int cnt = this.segments.size();
        for (int segIdx = 0; segIdx < cnt; ++segIdx) {
            assert (segIdx >= newSegIdx);
            SegmentInfo info = this.segments.get(segIdx);
            if (mergedAway.contains(info)) {
                if (inserted || dropSegment) continue;
                this.segments.set(segIdx, merge.info);
                inserted = true;
                ++newSegIdx;
                continue;
            }
            this.segments.set(newSegIdx, info);
            ++newSegIdx;
        }
        if (!inserted && !dropSegment) {
            this.segments.add(0, merge.info);
        }
        this.segments.subList(newSegIdx, this.segments.size()).clear();
        if (!dropSegment) {
            this.segmentSet.add(merge.info);
        }
        this.segmentSet.removeAll(mergedAway);
        assert (this.segmentSet.size() == this.segments.size());
    }

    List<SegmentInfo> createBackupSegmentInfos(boolean cloneChildren) {
        if (cloneChildren) {
            ArrayList<SegmentInfo> list = new ArrayList<SegmentInfo>(this.size());
            for (SegmentInfo info : this) {
                list.add((SegmentInfo)info.clone());
            }
            return list;
        }
        return new ArrayList<SegmentInfo>(this.segments);
    }

    void rollbackSegmentInfos(List<SegmentInfo> infos) {
        this.clear();
        this.addAll(infos);
    }

    @Override
    public Iterator<SegmentInfo> iterator() {
        return this.asList().iterator();
    }

    public List<SegmentInfo> asList() {
        if (this.cachedUnmodifiableList == null) {
            this.cachedUnmodifiableList = Collections.unmodifiableList(this.segments);
        }
        return this.cachedUnmodifiableList;
    }

    public Set<SegmentInfo> asSet() {
        if (this.cachedUnmodifiableSet == null) {
            this.cachedUnmodifiableSet = Collections.unmodifiableSet(this.segmentSet);
        }
        return this.cachedUnmodifiableSet;
    }

    public int size() {
        return this.segments.size();
    }

    public void add(SegmentInfo si) {
        if (this.segmentSet.contains(si)) {
            throw new IllegalStateException("Cannot add the same segment two times to this SegmentInfos instance");
        }
        this.segments.add(si);
        this.segmentSet.add(si);
        assert (this.segmentSet.size() == this.segments.size());
    }

    public void addAll(Iterable<SegmentInfo> sis) {
        for (SegmentInfo si : sis) {
            this.add(si);
        }
    }

    public void clear() {
        this.segments.clear();
        this.segmentSet.clear();
    }

    public void remove(SegmentInfo si) {
        int index = this.indexOf(si);
        if (index >= 0) {
            this.remove(index);
        }
    }

    public void remove(int index) {
        this.segmentSet.remove(this.segments.remove(index));
        assert (this.segmentSet.size() == this.segments.size());
    }

    public boolean contains(SegmentInfo si) {
        return this.segmentSet.contains(si);
    }

    public int indexOf(SegmentInfo si) {
        if (this.segmentSet.contains(si)) {
            return this.segments.indexOf(si);
        }
        return -1;
    }

    public static abstract class FindSegmentsFile {
        final Directory directory;

        public FindSegmentsFile(Directory directory) {
            this.directory = directory;
        }

        public Object run() throws CorruptIndexException, IOException {
            return this.run(null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public Object run(IndexCommit commit) throws CorruptIndexException, IOException {
            if (commit != null) {
                if (this.directory != commit.getDirectory()) {
                    throw new IOException("the specified commit does not match the specified Directory");
                }
                return this.doBody(commit.getSegmentsFileName());
            }
            String segmentFileName = null;
            long lastGen = -1L;
            long gen = 0L;
            int genLookaheadCount = 0;
            IOException exc = null;
            int retryCount = 0;
            boolean useFirstMethod = true;
            while (true) {
                if (useFirstMethod) {
                    long genB;
                    long genA;
                    Object[] files;
                    block37: {
                        IndexInput genInput;
                        block35: {
                            files = null;
                            genA = -1L;
                            files = this.directory.listAll();
                            if (files != null) {
                                genA = SegmentInfos.getLastCommitGeneration((String[])files);
                            }
                            if (infoStream != null) {
                                SegmentInfos.message("directory listing genA=" + genA);
                            }
                            genB = -1L;
                            genInput = null;
                            try {
                                genInput = this.directory.openInput("segments.gen");
                            }
                            catch (FileNotFoundException e) {
                                if (infoStream != null) {
                                    SegmentInfos.message("segments.gen open: FileNotFoundException " + e);
                                }
                            }
                            catch (IOException e) {
                                if (infoStream == null) break block35;
                                SegmentInfos.message("segments.gen open: IOException " + e);
                            }
                        }
                        if (genInput != null) {
                            Object var23_20;
                            try {
                                try {
                                    int version = genInput.readInt();
                                    if (version != -2) {
                                        throw new IndexFormatTooNewException(genInput, version, -2, -2);
                                    }
                                    long gen0 = genInput.readLong();
                                    long gen1 = genInput.readLong();
                                    if (infoStream != null) {
                                        SegmentInfos.message("fallback check: " + gen0 + "; " + gen1);
                                    }
                                    if (gen0 == gen1) {
                                        genB = gen0;
                                    }
                                    var23_20 = null;
                                }
                                catch (IOException err2) {
                                    if (err2 instanceof CorruptIndexException) {
                                        throw err2;
                                    }
                                    var23_20 = null;
                                    genInput.close();
                                    break block37;
                                }
                            }
                            catch (Throwable throwable) {
                                var23_20 = null;
                                genInput.close();
                                throw throwable;
                            }
                            genInput.close();
                        }
                    }
                    if (infoStream != null) {
                        SegmentInfos.message("segments.gen check: genB=" + genB);
                    }
                    if ((gen = genA > genB ? genA : genB) == -1L) {
                        throw new IndexNotFoundException("no segments* file found in " + this.directory + ": files: " + Arrays.toString(files));
                    }
                }
                if (useFirstMethod && lastGen == gen && retryCount >= 2) {
                    useFirstMethod = false;
                }
                if (!useFirstMethod) {
                    if (genLookaheadCount >= defaultGenLookaheadCount) {
                        throw exc;
                    }
                    ++gen;
                    ++genLookaheadCount;
                    if (infoStream != null) {
                        SegmentInfos.message("look ahead increment gen to " + gen);
                    }
                } else {
                    retryCount = lastGen == gen ? ++retryCount : 0;
                }
                lastGen = gen;
                segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", gen);
                try {
                    Object v = this.doBody(segmentFileName);
                    if (infoStream != null) {
                        SegmentInfos.message("success on " + segmentFileName);
                    }
                    return v;
                }
                catch (IOException err) {
                    String prevSegmentFileName;
                    boolean prevExists;
                    if (exc == null) {
                        exc = err;
                    }
                    if (infoStream != null) {
                        SegmentInfos.message("primary Exception on '" + segmentFileName + "': " + err + "'; will retry: retryCount=" + retryCount + "; gen = " + gen);
                    }
                    if (gen <= 1L || !useFirstMethod || retryCount != 1 || !(prevExists = this.directory.fileExists(prevSegmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", gen - 1L)))) continue;
                    if (infoStream != null) {
                        SegmentInfos.message("fallback to prior segment file '" + prevSegmentFileName + "'");
                    }
                    try {
                        Object v = this.doBody(prevSegmentFileName);
                        if (infoStream != null) {
                            SegmentInfos.message("success on fallback " + prevSegmentFileName);
                        }
                        return v;
                    }
                    catch (IOException err2) {
                        if (infoStream == null) continue;
                        SegmentInfos.message("secondary Exception on '" + prevSegmentFileName + "': " + err2 + "'; will retry");
                        continue;
                    }
                }
                break;
            }
        }

        protected abstract Object doBody(String var1) throws CorruptIndexException, IOException;
    }
}

