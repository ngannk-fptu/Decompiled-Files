/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

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
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.lucene3x.Lucene3xCodec;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.store.ChecksumIndexOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.StringHelper;

public final class SegmentInfos
implements Cloneable,
Iterable<SegmentInfoPerCommit> {
    public static final int VERSION_40 = 0;
    public static final int FORMAT_SEGMENTS_GEN_CURRENT = -2;
    public int counter;
    public long version;
    private long generation;
    private long lastGeneration;
    public Map<String, String> userData = Collections.emptyMap();
    private List<SegmentInfoPerCommit> segments = new ArrayList<SegmentInfoPerCommit>();
    private static PrintStream infoStream = null;
    ChecksumIndexOutput pendingSegnOutput;
    private static final String SEGMENT_INFO_UPGRADE_CODEC = "SegmentInfo3xUpgrade";
    private static final int SEGMENT_INFO_UPGRADE_VERSION = 0;
    private static int defaultGenLookaheadCount = 10;

    public SegmentInfoPerCommit info(int i) {
        return this.segments.get(i);
    }

    public static long getLastCommitGeneration(String[] files) {
        if (files == null) {
            return -1L;
        }
        long max = -1L;
        for (String file : files) {
            long gen;
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

    public static String getLastCommitSegmentsFileName(String[] files) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeSegmentsGen(Directory dir, long generation) {
        try {
            IndexOutput genOutput = dir.createOutput("segments.gen", IOContext.READONCE);
            try {
                genOutput.writeInt(-2);
                genOutput.writeLong(generation);
                genOutput.writeLong(generation);
            }
            finally {
                genOutput.close();
                dir.sync(Collections.singleton("segments.gen"));
            }
        }
        catch (Throwable t) {
            try {
                dir.deleteFile("segments.gen");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public String getNextSegmentFileName() {
        long nextGeneration = this.generation == -1L ? 1L : this.generation + 1L;
        return IndexFileNames.fileNameFromGeneration("segments", "", nextGeneration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void read(Directory directory, String segmentFileName) throws IOException {
        block12: {
            ChecksumIndexInput input;
            block11: {
                boolean success = false;
                this.clear();
                this.lastGeneration = this.generation = SegmentInfos.generationFromSegmentsFileName(segmentFileName);
                input = new ChecksumIndexInput(directory.openInput(segmentFileName, IOContext.READ));
                try {
                    int format = input.readInt();
                    if (format == 1071082519) {
                        CodecUtil.checkHeaderNoMagic(input, "segments", 0, 0);
                        this.version = input.readLong();
                        this.counter = input.readInt();
                        int numSegments = input.readInt();
                        if (numSegments < 0) {
                            throw new CorruptIndexException("invalid segment count: " + numSegments + " (resource: " + input + ")");
                        }
                        for (int seg = 0; seg < numSegments; ++seg) {
                            String segName = input.readString();
                            Codec codec = Codec.forName(input.readString());
                            SegmentInfo info = codec.segmentInfoFormat().getSegmentInfoReader().read(directory, segName, IOContext.READ);
                            info.setCodec(codec);
                            long delGen = input.readLong();
                            int delCount = input.readInt();
                            if (delCount < 0 || delCount > info.getDocCount()) {
                                throw new CorruptIndexException("invalid deletion count: " + delCount + " (resource: " + input + ")");
                            }
                            this.add(new SegmentInfoPerCommit(info, delCount, delGen));
                        }
                        this.userData = input.readStringStringMap();
                    } else {
                        Lucene3xSegmentInfoReader.readLegacyInfos(this, directory, input, format);
                        Codec codec = Codec.forName("Lucene3x");
                        for (SegmentInfoPerCommit info : this) {
                            info.info.setCodec(codec);
                        }
                    }
                    long checksumNow = input.getChecksum();
                    long checksumThen = input.readLong();
                    if (checksumNow != checksumThen) {
                        throw new CorruptIndexException("checksum mismatch in segments file (resource: " + input + ")");
                    }
                    success = true;
                    if (success) break block11;
                    this.clear();
                }
                catch (Throwable throwable) {
                    if (!success) {
                        this.clear();
                        IOUtils.closeWhileHandlingException(input);
                    } else {
                        input.close();
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(input);
                break block12;
            }
            input.close();
        }
    }

    public final void read(Directory directory) throws IOException {
        this.lastGeneration = -1L;
        this.generation = -1L;
        new FindSegmentsFile(directory){

            @Override
            protected Object doBody(String segmentFileName) throws IOException {
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
    private void write(Directory directory) throws IOException {
        String segmentsFileName = this.getNextSegmentFileName();
        this.generation = this.generation == -1L ? 1L : ++this.generation;
        ChecksumIndexOutput segnOutput = null;
        boolean success = false;
        HashSet<String> upgradedSIFiles = new HashSet<String>();
        try {
            segnOutput = new ChecksumIndexOutput(directory.createOutput(segmentsFileName, IOContext.DEFAULT));
            CodecUtil.writeHeader(segnOutput, "segments", 0);
            segnOutput.writeLong(this.version);
            segnOutput.writeInt(this.counter);
            segnOutput.writeInt(this.size());
            for (SegmentInfoPerCommit siPerCommit : this) {
                SegmentInfo si = siPerCommit.info;
                segnOutput.writeString(si.name);
                segnOutput.writeString(si.getCodec().getName());
                segnOutput.writeLong(siPerCommit.getDelGen());
                segnOutput.writeInt(siPerCommit.getDelCount());
                assert (si.dir == directory);
                assert (siPerCommit.getDelCount() <= si.getDocCount());
                String version = si.getVersion();
                if (version != null && StringHelper.getVersionComparator().compare(version, "4.0") >= 0 || SegmentInfos.segmentWasUpgraded(directory, si)) continue;
                String markerFileName = IndexFileNames.segmentFileName(si.name, "upgraded", "si");
                si.addFile(markerFileName);
                String segmentFileName = SegmentInfos.write3xInfo(directory, si, IOContext.DEFAULT);
                upgradedSIFiles.add(segmentFileName);
                directory.sync(Collections.singletonList(segmentFileName));
                si.addFile(markerFileName);
                try (IndexOutput out = directory.createOutput(markerFileName, IOContext.DEFAULT);){
                    CodecUtil.writeHeader(out, SEGMENT_INFO_UPGRADE_CODEC, 0);
                }
                upgradedSIFiles.add(markerFileName);
                directory.sync(Collections.singletonList(markerFileName));
            }
            segnOutput.writeStringStringMap(this.userData);
            this.pendingSegnOutput = segnOutput;
            return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(segnOutput);
            for (String fileName : upgradedSIFiles) {
                try {
                    directory.deleteFile(fileName);
                }
                catch (Throwable throwable2) {}
            }
            try {
                directory.deleteFile(segmentsFileName);
                throw throwable;
            }
            catch (Throwable throwable3) {
                // empty catch block
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private static boolean segmentWasUpgraded(Directory directory, SegmentInfo si) {
        IndexInput in;
        block7: {
            boolean bl;
            block8: {
                String markerFileName = IndexFileNames.segmentFileName(si.name, "upgraded", "si");
                in = null;
                try {
                    in = directory.openInput(markerFileName, IOContext.READONCE);
                    if (CodecUtil.checkHeader(in, SEGMENT_INFO_UPGRADE_CODEC, 0, 0) != 0) break block7;
                    bl = true;
                    if (in == null) break block8;
                }
                catch (IOException iOException) {
                    if (in != null) {
                        IOUtils.closeWhileHandlingException(in);
                    }
                    catch (Throwable throwable) {
                        if (in != null) {
                            IOUtils.closeWhileHandlingException(in);
                        }
                        throw throwable;
                    }
                }
                IOUtils.closeWhileHandlingException(in);
            }
            return bl;
        }
        if (in != null) {
            IOUtils.closeWhileHandlingException(in);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Deprecated
    public static String write3xInfo(Directory dir, SegmentInfo si, IOContext context) throws IOException {
        String fileName = IndexFileNames.segmentFileName(si.name, "", "si");
        si.addFile(fileName);
        boolean success = false;
        IndexOutput output = dir.createOutput(fileName, context);
        try {
            assert (si.getCodec() instanceof Lucene3xCodec) : "broken test, trying to mix preflex with other codecs";
            CodecUtil.writeHeader(output, "Lucene3xSegmentInfo", 0);
            output.writeString(si.getVersion());
            output.writeInt(si.getDocCount());
            output.writeStringStringMap(si.attributes());
            output.writeByte((byte)(si.getUseCompoundFile() ? 1 : -1));
            output.writeStringStringMap(si.getDiagnostics());
            output.writeStringSet(si.files());
            output.close();
            return fileName;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(output);
            try {
                si.dir.deleteFile(fileName);
                throw throwable;
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            throw throwable;
        }
    }

    public SegmentInfos clone() {
        try {
            SegmentInfos sis = (SegmentInfos)super.clone();
            sis.segments = new ArrayList<SegmentInfoPerCommit>(this.size());
            for (SegmentInfoPerCommit info : this) {
                assert (info.info.getCodec() != null);
                sis.add(info.clone());
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

    void updateGeneration(SegmentInfos other) {
        this.lastGeneration = other.lastGeneration;
        this.generation = other.generation;
    }

    final void rollbackCommit(Directory dir) {
        if (this.pendingSegnOutput != null) {
            IOUtils.closeWhileHandlingException(this.pendingSegnOutput);
            this.pendingSegnOutput = null;
            String segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
            IOUtils.deleteFilesIgnoringExceptions(dir, segmentFileName);
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
            SegmentInfoPerCommit info = this.info(i);
            assert (info.info.dir == dir);
            if (info.info.dir != dir) continue;
            files.addAll(info.files());
        }
        return files;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void finishCommit(Directory dir) throws IOException {
        if (this.pendingSegnOutput == null) {
            throw new IllegalStateException("prepareCommit was not called");
        }
        boolean success = false;
        try {
            this.pendingSegnOutput.finishCommit();
            success = true;
        }
        finally {
            if (!success) {
                this.rollbackCommit(dir);
            } else {
                success = false;
                try {
                    this.pendingSegnOutput.close();
                    success = true;
                }
                finally {
                    if (!success) {
                        this.rollbackCommit(dir);
                    } else {
                        this.pendingSegnOutput = null;
                    }
                }
            }
        }
        String fileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
        success = false;
        try {
            dir.sync(Collections.singleton(fileName));
            success = true;
        }
        finally {
            if (!success) {
                try {
                    dir.deleteFile(fileName);
                }
                catch (Throwable throwable) {}
            }
        }
        this.lastGeneration = this.generation;
        SegmentInfos.writeSegmentsGen(dir, this.generation);
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
            SegmentInfoPerCommit info = this.info(i);
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
        for (SegmentInfoPerCommit info : this) {
            count += info.info.getDocCount();
        }
        return count;
    }

    public void changed() {
        ++this.version;
    }

    void applyMergeChanges(MergePolicy.OneMerge merge, boolean dropSegment) {
        HashSet<SegmentInfoPerCommit> mergedAway = new HashSet<SegmentInfoPerCommit>(merge.segments);
        boolean inserted = false;
        int newSegIdx = 0;
        int cnt = this.segments.size();
        for (int segIdx = 0; segIdx < cnt; ++segIdx) {
            assert (segIdx >= newSegIdx);
            SegmentInfoPerCommit info = this.segments.get(segIdx);
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
        this.segments.subList(newSegIdx, this.segments.size()).clear();
        if (!inserted && !dropSegment) {
            this.segments.add(0, merge.info);
        }
    }

    List<SegmentInfoPerCommit> createBackupSegmentInfos() {
        ArrayList<SegmentInfoPerCommit> list = new ArrayList<SegmentInfoPerCommit>(this.size());
        for (SegmentInfoPerCommit info : this) {
            assert (info.info.getCodec() != null);
            list.add(info.clone());
        }
        return list;
    }

    void rollbackSegmentInfos(List<SegmentInfoPerCommit> infos) {
        this.clear();
        this.addAll(infos);
    }

    @Override
    public Iterator<SegmentInfoPerCommit> iterator() {
        return this.asList().iterator();
    }

    public List<SegmentInfoPerCommit> asList() {
        return Collections.unmodifiableList(this.segments);
    }

    public int size() {
        return this.segments.size();
    }

    public void add(SegmentInfoPerCommit si) {
        this.segments.add(si);
    }

    public void addAll(Iterable<SegmentInfoPerCommit> sis) {
        for (SegmentInfoPerCommit si : sis) {
            this.add(si);
        }
    }

    public void clear() {
        this.segments.clear();
    }

    public void remove(SegmentInfoPerCommit si) {
        this.segments.remove(si);
    }

    void remove(int index) {
        this.segments.remove(index);
    }

    boolean contains(SegmentInfoPerCommit si) {
        return this.segments.contains(si);
    }

    int indexOf(SegmentInfoPerCommit si) {
        return this.segments.indexOf(si);
    }

    public static abstract class FindSegmentsFile {
        final Directory directory;

        public FindSegmentsFile(Directory directory) {
            this.directory = directory;
        }

        public Object run() throws IOException {
            return this.run(null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public Object run(IndexCommit commit) throws IOException {
            if (commit != null) {
                if (this.directory == commit.getDirectory()) return this.doBody(commit.getSegmentsFileName());
                throw new IOException("the specified commit does not match the specified Directory");
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
                    IndexInput genInput;
                    long genB;
                    long genA;
                    Object[] files;
                    block29: {
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
                            genInput = this.directory.openInput("segments.gen", IOContext.READONCE);
                        }
                        catch (IOException e) {
                            if (infoStream == null) break block29;
                            SegmentInfos.message("segments.gen open: IOException " + e);
                        }
                    }
                    if (genInput != null) {
                        try {
                            int version = genInput.readInt();
                            if (version != -2) throw new IndexFormatTooNewException(genInput, version, -2, -2);
                            long gen0 = genInput.readLong();
                            long gen1 = genInput.readLong();
                            if (infoStream != null) {
                                SegmentInfos.message("fallback check: " + gen0 + "; " + gen1);
                            }
                            if (gen0 == gen1) {
                                genB = gen0;
                            }
                        }
                        catch (IOException err2) {
                            if (err2 instanceof CorruptIndexException) {
                                throw err2;
                            }
                        }
                        finally {
                            genInput.close();
                        }
                    }
                    if (infoStream != null) {
                        SegmentInfos.message("segments.gen check: genB=" + genB);
                    }
                    if ((gen = Math.max(genA, genB)) == -1L) {
                        throw new IndexNotFoundException("no segments* file found in " + this.directory + ": files: " + Arrays.toString(files));
                    }
                }
                if (useFirstMethod && lastGen == gen && retryCount >= 2) {
                    useFirstMethod = false;
                }
                if (!useFirstMethod) {
                    if (genLookaheadCount >= defaultGenLookaheadCount) throw exc;
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
                    if (infoStream == null) return v;
                    SegmentInfos.message("success on " + segmentFileName);
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
                        if (infoStream == null) return v;
                        SegmentInfos.message("success on fallback " + prevSegmentFileName);
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

        protected abstract Object doBody(String var1) throws IOException;
    }
}

