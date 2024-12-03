/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.ReadersAndLiveDocs;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.IOUtils;

final class StandardDirectoryReader
extends DirectoryReader {
    private final IndexWriter writer;
    private final SegmentInfos segmentInfos;
    private final int termInfosIndexDivisor;
    private final boolean applyAllDeletes;

    StandardDirectoryReader(Directory directory, AtomicReader[] readers, IndexWriter writer, SegmentInfos sis, int termInfosIndexDivisor, boolean applyAllDeletes) {
        super(directory, readers);
        this.writer = writer;
        this.segmentInfos = sis;
        this.termInfosIndexDivisor = termInfosIndexDivisor;
        this.applyAllDeletes = applyAllDeletes;
    }

    static DirectoryReader open(Directory directory, IndexCommit commit, final int termInfosIndexDivisor) throws IOException {
        return (DirectoryReader)new SegmentInfos.FindSegmentsFile(directory){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            protected Object doBody(String segmentFileName) throws IOException {
                SegmentInfos sis = new SegmentInfos();
                sis.read(this.directory, segmentFileName);
                Closeable[] readers = new SegmentReader[sis.size()];
                for (int i = sis.size() - 1; i >= 0; --i) {
                    IOException prior = null;
                    boolean success = false;
                    try {
                        readers[i] = new SegmentReader(sis.info(i), termInfosIndexDivisor, IOContext.READ);
                        success = true;
                        continue;
                    }
                    catch (IOException ex) {
                        prior = ex;
                        continue;
                    }
                    finally {
                        if (!success) {
                            IOUtils.closeWhileHandlingException(prior, readers);
                        }
                    }
                }
                return new StandardDirectoryReader(this.directory, (AtomicReader[])readers, null, sis, termInfosIndexDivisor, false);
            }
        }.run(commit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static DirectoryReader open(IndexWriter writer, SegmentInfos infos, boolean applyAllDeletes) throws IOException {
        int numSegments = infos.size();
        ArrayList<SegmentReader> readers = new ArrayList<SegmentReader>();
        Directory dir = writer.getDirectory();
        SegmentInfos segmentInfos = infos.clone();
        int infosUpto = 0;
        for (int i = 0; i < numSegments; ++i) {
            IOException prior = null;
            boolean success = false;
            try {
                SegmentInfoPerCommit info = infos.info(i);
                assert (info.info.dir == dir);
                ReadersAndLiveDocs rld = writer.readerPool.get(info, true);
                try {
                    SegmentReader reader = rld.getReadOnlyClone(IOContext.READ);
                    if (reader.numDocs() > 0 || writer.getKeepFullyDeletedSegments()) {
                        readers.add(reader);
                        ++infosUpto;
                    } else {
                        reader.close();
                        segmentInfos.remove(infosUpto);
                    }
                }
                finally {
                    writer.readerPool.release(rld);
                }
                success = true;
                continue;
            }
            catch (IOException ex) {
                prior = ex;
                continue;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(prior, readers);
                }
            }
        }
        return new StandardDirectoryReader(dir, readers.toArray(new SegmentReader[readers.size()]), writer, segmentInfos, writer.getConfig().getReaderTermsIndexDivisor(), applyAllDeletes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static DirectoryReader open(Directory directory, SegmentInfos infos, List<? extends AtomicReader> oldReaders, int termInfosIndexDivisor) throws IOException {
        HashMap<String, Integer> segmentReaders = new HashMap<String, Integer>();
        if (oldReaders != null) {
            int c = oldReaders.size();
            for (int i = 0; i < c; ++i) {
                SegmentReader sr = (SegmentReader)oldReaders.get(i);
                segmentReaders.put(sr.getSegmentName(), i);
            }
        }
        AtomicReader[] newReaders = new SegmentReader[infos.size()];
        boolean[] readerShared = new boolean[infos.size()];
        for (int i = infos.size() - 1; i >= 0; --i) {
            Integer oldReaderIndex = (Integer)segmentReaders.get(infos.info((int)i).info.name);
            newReaders[i] = oldReaderIndex == null ? null : (SegmentReader)oldReaders.get(oldReaderIndex);
            boolean success = false;
            Throwable prior = null;
            try {
                if (newReaders[i] == null || infos.info((int)i).info.getUseCompoundFile() != ((SegmentReader)newReaders[i]).getSegmentInfo().info.getUseCompoundFile()) {
                    SegmentReader newReader = new SegmentReader(infos.info(i), termInfosIndexDivisor, IOContext.READ);
                    readerShared[i] = false;
                    newReaders[i] = newReader;
                } else if (((SegmentReader)newReaders[i]).getSegmentInfo().getDelGen() == infos.info(i).getDelGen()) {
                    readerShared[i] = true;
                    newReaders[i].incRef();
                } else {
                    readerShared[i] = false;
                    assert (infos.info((int)i).info.dir == ((SegmentReader)newReaders[i]).getSegmentInfo().info.dir);
                    assert (infos.info(i).hasDeletions());
                    newReaders[i] = new SegmentReader(infos.info(i), ((SegmentReader)newReaders[i]).core, IOContext.READ);
                }
                success = true;
                continue;
            }
            catch (Throwable ex) {
                prior = ex;
                return prior;
            }
            finally {
                if (!success) {
                    ++i;
                    while (i < infos.size()) {
                        block29: {
                            if (newReaders[i] != null) {
                                try {
                                    if (!readerShared[i]) {
                                        newReaders[i].close();
                                    } else {
                                        newReaders[i].decRef();
                                    }
                                }
                                catch (Throwable t) {
                                    if (prior != null) break block29;
                                    prior = t;
                                }
                            }
                        }
                        ++i;
                    }
                }
                if (prior != null) {
                    if (prior instanceof IOException) {
                        throw (IOException)prior;
                    }
                    if (prior instanceof RuntimeException) {
                        throw (RuntimeException)prior;
                    }
                    if (prior instanceof Error) {
                        throw (Error)prior;
                    }
                    throw new RuntimeException(prior);
                }
            }
        }
        return new StandardDirectoryReader(directory, newReaders, null, infos, termInfosIndexDivisor, false);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.getClass().getSimpleName());
        buffer.append('(');
        String segmentsFile = this.segmentInfos.getSegmentsFileName();
        if (segmentsFile != null) {
            buffer.append(segmentsFile).append(":").append(this.segmentInfos.getVersion());
        }
        if (this.writer != null) {
            buffer.append(":nrt");
        }
        for (AtomicReader r : this.getSequentialSubReaders()) {
            buffer.append(' ');
            buffer.append(r);
        }
        buffer.append(')');
        return buffer.toString();
    }

    @Override
    protected DirectoryReader doOpenIfChanged() throws IOException {
        return this.doOpenIfChanged((IndexCommit)null);
    }

    @Override
    protected DirectoryReader doOpenIfChanged(IndexCommit commit) throws IOException {
        this.ensureOpen();
        if (this.writer != null) {
            return this.doOpenFromWriter(commit);
        }
        return this.doOpenNoWriter(commit);
    }

    @Override
    protected DirectoryReader doOpenIfChanged(IndexWriter writer, boolean applyAllDeletes) throws IOException {
        this.ensureOpen();
        if (writer == this.writer && applyAllDeletes == this.applyAllDeletes) {
            return this.doOpenFromWriter(null);
        }
        return writer.getReader(applyAllDeletes);
    }

    private DirectoryReader doOpenFromWriter(IndexCommit commit) throws IOException {
        if (commit != null) {
            return this.doOpenFromCommit(commit);
        }
        if (this.writer.nrtIsCurrent(this.segmentInfos)) {
            return null;
        }
        DirectoryReader reader = this.writer.getReader(this.applyAllDeletes);
        if (reader.getVersion() == this.segmentInfos.getVersion()) {
            reader.decRef();
            return null;
        }
        return reader;
    }

    private DirectoryReader doOpenNoWriter(IndexCommit commit) throws IOException {
        if (commit == null) {
            if (this.isCurrent()) {
                return null;
            }
        } else {
            if (this.directory != commit.getDirectory()) {
                throw new IOException("the specified commit does not match the specified Directory");
            }
            if (this.segmentInfos != null && commit.getSegmentsFileName().equals(this.segmentInfos.getSegmentsFileName())) {
                return null;
            }
        }
        return this.doOpenFromCommit(commit);
    }

    private DirectoryReader doOpenFromCommit(IndexCommit commit) throws IOException {
        return (DirectoryReader)new SegmentInfos.FindSegmentsFile(this.directory){

            @Override
            protected Object doBody(String segmentFileName) throws IOException {
                SegmentInfos infos = new SegmentInfos();
                infos.read(this.directory, segmentFileName);
                return StandardDirectoryReader.this.doOpenIfChanged(infos);
            }
        }.run(commit);
    }

    DirectoryReader doOpenIfChanged(SegmentInfos infos) throws IOException {
        return StandardDirectoryReader.open(this.directory, infos, this.getSequentialSubReaders(), this.termInfosIndexDivisor);
    }

    @Override
    public long getVersion() {
        this.ensureOpen();
        return this.segmentInfos.getVersion();
    }

    @Override
    public boolean isCurrent() throws IOException {
        this.ensureOpen();
        if (this.writer == null || this.writer.isClosed()) {
            SegmentInfos sis = new SegmentInfos();
            sis.read(this.directory);
            return sis.getVersion() == this.segmentInfos.getVersion();
        }
        return this.writer.nrtIsCurrent(this.segmentInfos);
    }

    @Override
    protected void doClose() throws IOException {
        Throwable firstExc = null;
        for (AtomicReader r : this.getSequentialSubReaders()) {
            try {
                r.decRef();
            }
            catch (Throwable t) {
                if (firstExc != null) continue;
                firstExc = t;
            }
        }
        if (this.writer != null) {
            this.writer.deletePendingFiles();
        }
        if (firstExc != null) {
            if (firstExc instanceof IOException) {
                throw (IOException)firstExc;
            }
            if (firstExc instanceof RuntimeException) {
                throw (RuntimeException)firstExc;
            }
            if (firstExc instanceof Error) {
                throw (Error)firstExc;
            }
            throw new RuntimeException(firstExc);
        }
    }

    @Override
    public IndexCommit getIndexCommit() throws IOException {
        this.ensureOpen();
        return new ReaderCommit(this.segmentInfos, this.directory);
    }

    static final class ReaderCommit
    extends IndexCommit {
        private String segmentsFileName;
        Collection<String> files;
        Directory dir;
        long generation;
        final Map<String, String> userData;
        private final int segmentCount;

        ReaderCommit(SegmentInfos infos, Directory dir) throws IOException {
            this.segmentsFileName = infos.getSegmentsFileName();
            this.dir = dir;
            this.userData = infos.getUserData();
            this.files = Collections.unmodifiableCollection(infos.files(dir, true));
            this.generation = infos.getGeneration();
            this.segmentCount = infos.size();
        }

        public String toString() {
            return "DirectoryReader.ReaderCommit(" + this.segmentsFileName + ")";
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
            return this.dir;
        }

        @Override
        public long getGeneration() {
            return this.generation;
        }

        @Override
        public boolean isDeleted() {
            return false;
        }

        @Override
        public Map<String, String> getUserData() {
            return this.userData;
        }

        @Override
        public void delete() {
            throw new UnsupportedOperationException("This IndexCommit does not support deletions");
        }
    }
}

