/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.StringHelper;

@Deprecated
class Lucene3xNormsProducer
extends DocValuesProducer {
    static final byte[] NORMS_HEADER = new byte[]{78, 82, 77, -1};
    static final String NORMS_EXTENSION = "nrm";
    static final String SEPARATE_NORMS_EXTENSION = "s";
    final Map<String, NormsDocValues> norms = new HashMap<String, NormsDocValues>();
    final Set<IndexInput> openFiles = Collections.newSetFromMap(new IdentityHashMap());
    IndexInput singleNormStream;
    final int maxdoc;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene3xNormsProducer(Directory dir, SegmentInfo info, FieldInfos fields, IOContext context) throws IOException {
        Directory separateNormsDir = info.dir;
        this.maxdoc = info.getDocCount();
        String segmentName = info.name;
        boolean success = false;
        try {
            long nextNormSeek = NORMS_HEADER.length;
            for (FieldInfo fi : fields) {
                long normSeek;
                if (!fi.hasNorms()) continue;
                String fileName = Lucene3xNormsProducer.getNormFilename(info, fi.number);
                Directory d = Lucene3xNormsProducer.hasSeparateNorms(info, fi.number) ? separateNormsDir : dir;
                boolean singleNormFile = IndexFileNames.matchesExtension(fileName, NORMS_EXTENSION);
                IndexInput normInput = null;
                if (singleNormFile) {
                    normSeek = nextNormSeek;
                    if (this.singleNormStream == null) {
                        this.singleNormStream = d.openInput(fileName, context);
                        this.openFiles.add(this.singleNormStream);
                    }
                    normInput = this.singleNormStream;
                } else {
                    normInput = d.openInput(fileName, context);
                    this.openFiles.add(normInput);
                    String version = info.getVersion();
                    boolean isUnversioned = (version == null || StringHelper.getVersionComparator().compare(version, "3.2") < 0) && normInput.length() == (long)this.maxdoc;
                    normSeek = isUnversioned ? 0L : (long)NORMS_HEADER.length;
                }
                NormsDocValues norm = new NormsDocValues(normInput, normSeek);
                this.norms.put(fi.name, norm);
                nextNormSeek += (long)this.maxdoc;
            }
            assert (this.singleNormStream == null || nextNormSeek == this.singleNormStream.length()) : this.singleNormStream != null ? "len: " + this.singleNormStream.length() + " expected: " + nextNormSeek : "null";
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.openFiles);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.openFiles);
        }
        finally {
            this.norms.clear();
            this.openFiles.clear();
        }
    }

    private static String getNormFilename(SegmentInfo info, int number) {
        if (Lucene3xNormsProducer.hasSeparateNorms(info, number)) {
            long gen = Long.parseLong(info.getAttribute(Lucene3xSegmentInfoFormat.NORMGEN_PREFIX + number));
            return IndexFileNames.fileNameFromGeneration(info.name, SEPARATE_NORMS_EXTENSION + number, gen);
        }
        return IndexFileNames.segmentFileName(info.name, "", NORMS_EXTENSION);
    }

    private static boolean hasSeparateNorms(SegmentInfo info, int number) {
        String v = info.getAttribute(Lucene3xSegmentInfoFormat.NORMGEN_PREFIX + number);
        if (v == null) {
            return false;
        }
        assert (Long.parseLong(v) != -1L);
        return true;
    }

    @Override
    public NumericDocValues getNumeric(FieldInfo field) throws IOException {
        NormsDocValues dv = this.norms.get(field.name);
        assert (dv != null);
        return dv.getInstance();
    }

    @Override
    public BinaryDocValues getBinary(FieldInfo field) throws IOException {
        throw new AssertionError();
    }

    @Override
    public SortedDocValues getSorted(FieldInfo field) throws IOException {
        throw new AssertionError();
    }

    @Override
    public SortedSetDocValues getSortedSet(FieldInfo field) throws IOException {
        throw new AssertionError();
    }

    private class NormsDocValues {
        private final IndexInput file;
        private final long offset;
        private NumericDocValues instance;

        public NormsDocValues(IndexInput normInput, long normSeek) {
            this.file = normInput;
            this.offset = normSeek;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        synchronized NumericDocValues getInstance() throws IOException {
            if (this.instance == null) {
                final byte[] bytes = new byte[Lucene3xNormsProducer.this.maxdoc];
                IndexInput indexInput = this.file;
                synchronized (indexInput) {
                    this.file.seek(this.offset);
                    this.file.readBytes(bytes, 0, bytes.length, false);
                }
                if (this.file != Lucene3xNormsProducer.this.singleNormStream) {
                    Lucene3xNormsProducer.this.openFiles.remove(this.file);
                    this.file.close();
                }
                this.instance = new NumericDocValues(){

                    @Override
                    public long get(int docID) {
                        return bytes[docID];
                    }
                };
            }
            return this.instance;
        }
    }
}

