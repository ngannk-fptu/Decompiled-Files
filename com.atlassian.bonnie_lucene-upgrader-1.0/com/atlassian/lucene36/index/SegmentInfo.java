/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CompoundFileReader;
import com.atlassian.lucene36.index.IndexFileNameFilter;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.BitVector;
import com.atlassian.lucene36.util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SegmentInfo
implements Cloneable {
    static final int NO = -1;
    static final int YES = 1;
    static final int CHECK_DIR = 0;
    static final int WITHOUT_GEN = 0;
    public String name;
    public int docCount;
    public Directory dir;
    private boolean preLockless;
    private long delGen;
    private long[] normGen;
    private byte isCompoundFile;
    private boolean hasSingleNormFile;
    private volatile List<String> files;
    private volatile long sizeInBytesNoStore = -1L;
    private volatile long sizeInBytesWithStore = -1L;
    private int docStoreOffset;
    private String docStoreSegment;
    private boolean docStoreIsCompoundFile;
    private int delCount;
    private boolean hasProx;
    private boolean hasVectors;
    private Map<String, String> diagnostics;
    private String version;
    private long bufferedDeletesGen;

    public SegmentInfo(String name, int docCount, Directory dir, boolean isCompoundFile, boolean hasSingleNormFile, boolean hasProx, boolean hasVectors) {
        this.name = name;
        this.docCount = docCount;
        this.dir = dir;
        this.delGen = -1L;
        this.isCompoundFile = (byte)(isCompoundFile ? 1 : -1);
        this.preLockless = false;
        this.hasSingleNormFile = hasSingleNormFile;
        this.docStoreOffset = -1;
        this.delCount = 0;
        this.hasProx = hasProx;
        this.hasVectors = hasVectors;
        this.version = Constants.LUCENE_MAIN_VERSION;
    }

    void reset(SegmentInfo src) {
        this.clearFiles();
        this.version = src.version;
        this.name = src.name;
        this.docCount = src.docCount;
        this.dir = src.dir;
        this.preLockless = src.preLockless;
        this.delGen = src.delGen;
        this.docStoreOffset = src.docStoreOffset;
        this.docStoreIsCompoundFile = src.docStoreIsCompoundFile;
        this.hasVectors = src.hasVectors;
        this.hasProx = src.hasProx;
        if (src.normGen == null) {
            this.normGen = null;
        } else {
            this.normGen = new long[src.normGen.length];
            System.arraycopy(src.normGen, 0, this.normGen, 0, src.normGen.length);
        }
        this.isCompoundFile = src.isCompoundFile;
        this.hasSingleNormFile = src.hasSingleNormFile;
        this.delCount = src.delCount;
    }

    void setDiagnostics(Map<String, String> diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Map<String, String> getDiagnostics() {
        return this.diagnostics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SegmentInfo(Directory dir, int format, IndexInput input) throws IOException {
        this.dir = dir;
        if (format <= -11) {
            this.version = input.readString();
        }
        this.name = input.readString();
        this.docCount = input.readInt();
        if (format <= -2) {
            this.delGen = input.readLong();
            if (format <= -4) {
                this.docStoreOffset = input.readInt();
                if (this.docStoreOffset != -1) {
                    this.docStoreSegment = input.readString();
                    this.docStoreIsCompoundFile = 1 == input.readByte();
                } else {
                    this.docStoreSegment = this.name;
                    this.docStoreIsCompoundFile = false;
                }
            } else {
                this.docStoreOffset = -1;
                this.docStoreSegment = this.name;
                this.docStoreIsCompoundFile = false;
            }
            this.hasSingleNormFile = format <= -3 ? 1 == input.readByte() : false;
            int numNormGen = input.readInt();
            if (numNormGen == -1) {
                this.normGen = null;
            } else {
                this.normGen = new long[numNormGen];
                for (int j = 0; j < numNormGen; ++j) {
                    this.normGen[j] = input.readLong();
                }
            }
            this.isCompoundFile = input.readByte();
            boolean bl = this.preLockless = this.isCompoundFile == 0;
            if (format <= -6) {
                this.delCount = input.readInt();
                assert (this.delCount <= this.docCount);
            } else {
                this.delCount = -1;
            }
            this.hasProx = format <= -7 ? input.readByte() == 1 : true;
            this.diagnostics = format <= -9 ? input.readStringStringMap() : Collections.emptyMap();
            if (format <= -10) {
                this.hasVectors = input.readByte() == 1;
            } else {
                String ext;
                boolean isCompoundFile;
                String storesSegment;
                if (this.docStoreOffset != -1) {
                    storesSegment = this.docStoreSegment;
                    isCompoundFile = this.docStoreIsCompoundFile;
                    ext = "cfx";
                } else {
                    storesSegment = this.name;
                    isCompoundFile = this.getUseCompoundFile();
                    ext = "cfs";
                }
                Directory dirToTest = isCompoundFile ? new CompoundFileReader(dir, IndexFileNames.segmentFileName(storesSegment, ext)) : dir;
                try {
                    this.hasVectors = dirToTest.fileExists(IndexFileNames.segmentFileName(storesSegment, "tvx"));
                }
                finally {
                    if (isCompoundFile) {
                        dirToTest.close();
                    }
                }
            }
        } else {
            this.delGen = 0L;
            this.normGen = null;
            this.isCompoundFile = 0;
            this.preLockless = true;
            this.hasSingleNormFile = false;
            this.docStoreOffset = -1;
            this.docStoreIsCompoundFile = false;
            this.docStoreSegment = null;
            this.delCount = -1;
            this.hasProx = true;
            this.diagnostics = Collections.emptyMap();
        }
    }

    void setNumFields(int numFields) {
        if (this.normGen == null) {
            this.normGen = new long[numFields];
            if (!this.preLockless) {
                for (int i = 0; i < numFields; ++i) {
                    this.normGen[i] = -1L;
                }
            }
        }
    }

    public long sizeInBytes(boolean includeDocStores) throws IOException {
        if (includeDocStores) {
            if (this.sizeInBytesWithStore != -1L) {
                return this.sizeInBytesWithStore;
            }
            long sum = 0L;
            for (String fileName : this.files()) {
                if (this.docStoreOffset != -1 && IndexFileNames.isDocStoreFile(fileName)) continue;
                sum += this.dir.fileLength(fileName);
            }
            this.sizeInBytesWithStore = sum;
            return this.sizeInBytesWithStore;
        }
        if (this.sizeInBytesNoStore != -1L) {
            return this.sizeInBytesNoStore;
        }
        long sum = 0L;
        for (String fileName : this.files()) {
            if (IndexFileNames.isDocStoreFile(fileName)) continue;
            sum += this.dir.fileLength(fileName);
        }
        this.sizeInBytesNoStore = sum;
        return this.sizeInBytesNoStore;
    }

    public boolean getHasVectors() throws IOException {
        return this.hasVectors;
    }

    public void setHasVectors(boolean v) {
        this.hasVectors = v;
        this.clearFiles();
    }

    public boolean hasDeletions() throws IOException {
        if (this.delGen == -1L) {
            return false;
        }
        if (this.delGen >= 1L) {
            return true;
        }
        return this.dir.fileExists(this.getDelFileName());
    }

    void advanceDelGen() {
        this.delGen = this.delGen == -1L ? 1L : ++this.delGen;
        this.clearFiles();
    }

    void clearDelGen() {
        this.delGen = -1L;
        this.clearFiles();
    }

    public Object clone() {
        SegmentInfo si = new SegmentInfo(this.name, this.docCount, this.dir, false, this.hasSingleNormFile, this.hasProx, this.hasVectors);
        si.docStoreOffset = this.docStoreOffset;
        si.docStoreSegment = this.docStoreSegment;
        si.docStoreIsCompoundFile = this.docStoreIsCompoundFile;
        si.delGen = this.delGen;
        si.delCount = this.delCount;
        si.preLockless = this.preLockless;
        si.isCompoundFile = this.isCompoundFile;
        si.diagnostics = new HashMap<String, String>(this.diagnostics);
        if (this.normGen != null) {
            si.normGen = (long[])this.normGen.clone();
        }
        si.version = this.version;
        return si;
    }

    public String getDelFileName() {
        if (this.delGen == -1L) {
            return null;
        }
        return IndexFileNames.fileNameFromGeneration(this.name, "del", this.delGen);
    }

    public boolean hasSeparateNorms(int fieldNumber) throws IOException {
        if (this.normGen == null && this.preLockless || this.normGen != null && this.normGen[fieldNumber] == 0L) {
            String fileName = this.name + ".s" + fieldNumber;
            return this.dir.fileExists(fileName);
        }
        return this.normGen != null && this.normGen[fieldNumber] != -1L;
    }

    public boolean hasSeparateNorms() throws IOException {
        int i;
        if (this.normGen == null) {
            if (!this.preLockless) {
                return false;
            }
            String[] result = this.dir.listAll();
            if (result == null) {
                throw new IOException("cannot read directory " + this.dir + ": listAll() returned null");
            }
            IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
            String pattern = this.name + ".s";
            int patternLength = pattern.length();
            for (int i2 = 0; i2 < result.length; ++i2) {
                String fileName = result[i2];
                if (!filter.accept(null, fileName) || !fileName.startsWith(pattern) || !Character.isDigit(fileName.charAt(patternLength))) continue;
                return true;
            }
            return false;
        }
        for (i = 0; i < this.normGen.length; ++i) {
            if (this.normGen[i] < 1L) continue;
            return true;
        }
        for (i = 0; i < this.normGen.length; ++i) {
            if (this.normGen[i] != 0L || !this.hasSeparateNorms(i)) continue;
            return true;
        }
        return false;
    }

    void advanceNormGen(int fieldIndex) {
        if (this.normGen[fieldIndex] == -1L) {
            this.normGen[fieldIndex] = 1L;
        } else {
            int n = fieldIndex;
            this.normGen[n] = this.normGen[n] + 1L;
        }
        this.clearFiles();
    }

    public String getNormFileName(int number) throws IOException {
        long gen = this.normGen == null ? 0L : this.normGen[number];
        if (this.hasSeparateNorms(number)) {
            return IndexFileNames.fileNameFromGeneration(this.name, "s" + number, gen);
        }
        if (this.hasSingleNormFile) {
            return IndexFileNames.fileNameFromGeneration(this.name, "nrm", 0L);
        }
        return IndexFileNames.fileNameFromGeneration(this.name, "f" + number, 0L);
    }

    void setUseCompoundFile(boolean isCompoundFile) {
        this.isCompoundFile = isCompoundFile ? (byte)1 : (byte)-1;
        this.clearFiles();
    }

    public boolean getUseCompoundFile() throws IOException {
        if (this.isCompoundFile == -1) {
            return false;
        }
        if (this.isCompoundFile == 1) {
            return true;
        }
        return this.dir.fileExists(IndexFileNames.segmentFileName(this.name, "cfs"));
    }

    public int getDelCount() throws IOException {
        if (this.delCount == -1) {
            if (this.hasDeletions()) {
                String delFileName = this.getDelFileName();
                this.delCount = new BitVector(this.dir, delFileName).count();
            } else {
                this.delCount = 0;
            }
        }
        assert (this.delCount <= this.docCount);
        return this.delCount;
    }

    void setDelCount(int delCount) {
        this.delCount = delCount;
        assert (delCount <= this.docCount);
    }

    public int getDocStoreOffset() {
        return this.docStoreOffset;
    }

    public boolean getDocStoreIsCompoundFile() {
        return this.docStoreIsCompoundFile;
    }

    void setDocStoreIsCompoundFile(boolean v) {
        this.docStoreIsCompoundFile = v;
        this.clearFiles();
    }

    public String getDocStoreSegment() {
        return this.docStoreSegment;
    }

    public void setDocStoreSegment(String segment) {
        this.docStoreSegment = segment;
    }

    void setDocStoreOffset(int offset) {
        this.docStoreOffset = offset;
        this.clearFiles();
    }

    void setDocStore(int offset, String segment, boolean isCompoundFile) {
        this.docStoreOffset = offset;
        this.docStoreSegment = segment;
        this.docStoreIsCompoundFile = isCompoundFile;
        this.clearFiles();
    }

    void write(IndexOutput output) throws IOException {
        assert (this.delCount <= this.docCount) : "delCount=" + this.delCount + " docCount=" + this.docCount + " segment=" + this.name;
        output.writeString(this.version);
        output.writeString(this.name);
        output.writeInt(this.docCount);
        output.writeLong(this.delGen);
        output.writeInt(this.docStoreOffset);
        if (this.docStoreOffset != -1) {
            output.writeString(this.docStoreSegment);
            output.writeByte((byte)(this.docStoreIsCompoundFile ? 1 : 0));
        }
        output.writeByte((byte)(this.hasSingleNormFile ? 1 : 0));
        if (this.normGen == null) {
            output.writeInt(-1);
        } else {
            output.writeInt(this.normGen.length);
            for (int j = 0; j < this.normGen.length; ++j) {
                output.writeLong(this.normGen[j]);
            }
        }
        output.writeByte(this.isCompoundFile);
        output.writeInt(this.delCount);
        output.writeByte((byte)(this.hasProx ? 1 : 0));
        output.writeStringStringMap(this.diagnostics);
        output.writeByte((byte)(this.hasVectors ? 1 : 0));
    }

    void setHasProx(boolean hasProx) {
        this.hasProx = hasProx;
        this.clearFiles();
    }

    public boolean getHasProx() {
        return this.hasProx;
    }

    private void addIfExists(Set<String> files, String fileName) throws IOException {
        if (this.dir.fileExists(fileName)) {
            files.add(fileName);
        }
    }

    public List<String> files() throws IOException {
        if (this.files != null) {
            return this.files;
        }
        HashSet<String> filesSet = new HashSet<String>();
        boolean useCompoundFile = this.getUseCompoundFile();
        if (useCompoundFile) {
            filesSet.add(IndexFileNames.segmentFileName(this.name, "cfs"));
        } else {
            for (String ext : IndexFileNames.NON_STORE_INDEX_EXTENSIONS) {
                this.addIfExists(filesSet, IndexFileNames.segmentFileName(this.name, ext));
            }
        }
        if (this.docStoreOffset != -1) {
            assert (this.docStoreSegment != null);
            if (this.docStoreIsCompoundFile) {
                filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "cfx"));
            } else {
                filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "fdx"));
                filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "fdt"));
                if (this.hasVectors) {
                    filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvx"));
                    filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvd"));
                    filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvf"));
                }
            }
        } else if (!useCompoundFile) {
            filesSet.add(IndexFileNames.segmentFileName(this.name, "fdx"));
            filesSet.add(IndexFileNames.segmentFileName(this.name, "fdt"));
            if (this.hasVectors) {
                filesSet.add(IndexFileNames.segmentFileName(this.name, "tvx"));
                filesSet.add(IndexFileNames.segmentFileName(this.name, "tvd"));
                filesSet.add(IndexFileNames.segmentFileName(this.name, "tvf"));
            }
        }
        String delFileName = IndexFileNames.fileNameFromGeneration(this.name, "del", this.delGen);
        if (delFileName != null && (this.delGen >= 1L || this.dir.fileExists(delFileName))) {
            filesSet.add(delFileName);
        }
        if (this.normGen != null) {
            for (int i = 0; i < this.normGen.length; ++i) {
                String fileName;
                long gen = this.normGen[i];
                if (gen >= 1L) {
                    filesSet.add(IndexFileNames.fileNameFromGeneration(this.name, "s" + i, gen));
                    continue;
                }
                if (-1L == gen) {
                    if (this.hasSingleNormFile || useCompoundFile || !this.dir.fileExists(fileName = IndexFileNames.segmentFileName(this.name, "f" + i))) continue;
                    filesSet.add(fileName);
                    continue;
                }
                if (0L != gen) continue;
                fileName = null;
                if (useCompoundFile) {
                    fileName = IndexFileNames.segmentFileName(this.name, "s" + i);
                } else if (!this.hasSingleNormFile) {
                    fileName = IndexFileNames.segmentFileName(this.name, "f" + i);
                }
                if (fileName == null || !this.dir.fileExists(fileName)) continue;
                filesSet.add(fileName);
            }
        } else if (this.preLockless || !this.hasSingleNormFile && !useCompoundFile) {
            String prefix = useCompoundFile ? IndexFileNames.segmentFileName(this.name, "s") : IndexFileNames.segmentFileName(this.name, "f");
            int prefixLength = prefix.length();
            String[] allFiles = this.dir.listAll();
            IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
            for (int i = 0; i < allFiles.length; ++i) {
                String fileName = allFiles[i];
                if (!filter.accept(null, fileName) || fileName.length() <= prefixLength || !Character.isDigit(fileName.charAt(prefixLength)) || !fileName.startsWith(prefix)) continue;
                filesSet.add(fileName);
            }
        }
        this.files = new ArrayList<String>(filesSet);
        return this.files;
    }

    private void clearFiles() {
        this.files = null;
        this.sizeInBytesNoStore = -1L;
        this.sizeInBytesWithStore = -1L;
    }

    public String toString() {
        return this.toString(this.dir, 0);
    }

    public String toString(Directory dir, int pendingDelCount) {
        int delCount;
        char cfs;
        StringBuilder s = new StringBuilder();
        s.append(this.name).append('(').append(this.version == null ? "?" : this.version).append(')').append(':');
        try {
            cfs = this.getUseCompoundFile() ? (char)'c' : 'C';
        }
        catch (IOException ioe) {
            cfs = '?';
        }
        s.append(cfs);
        if (this.dir != dir) {
            s.append('x');
        }
        if (this.hasVectors) {
            s.append('v');
        }
        s.append(this.docCount);
        try {
            delCount = this.getDelCount();
        }
        catch (IOException ioe) {
            delCount = -1;
        }
        if (delCount != -1) {
            delCount += pendingDelCount;
        }
        if (delCount != 0) {
            s.append('/');
            if (delCount == -1) {
                s.append('?');
            } else {
                s.append(delCount);
            }
        }
        if (this.docStoreOffset != -1) {
            s.append("->").append(this.docStoreSegment);
            if (this.docStoreIsCompoundFile) {
                s.append('c');
            } else {
                s.append('C');
            }
            s.append('+').append(this.docStoreOffset);
        }
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SegmentInfo) {
            SegmentInfo other = (SegmentInfo)obj;
            return other.dir == this.dir && other.name.equals(this.name);
        }
        return false;
    }

    public int hashCode() {
        return this.dir.hashCode() + this.name.hashCode();
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    long getBufferedDeletesGen() {
        return this.bufferedDeletesGen;
    }

    void setBufferedDeletesGen(long v) {
        this.bufferedDeletesGen = v;
    }
}

