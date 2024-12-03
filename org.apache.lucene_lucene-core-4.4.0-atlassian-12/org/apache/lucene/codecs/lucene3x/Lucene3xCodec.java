/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xFieldInfosFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xNormsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xPostingsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xStoredFieldsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xTermVectorsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40LiveDocsFormat;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
public class Lucene3xCodec
extends Codec {
    private final PostingsFormat postingsFormat = new Lucene3xPostingsFormat();
    private final StoredFieldsFormat fieldsFormat = new Lucene3xStoredFieldsFormat();
    private final TermVectorsFormat vectorsFormat = new Lucene3xTermVectorsFormat();
    private final FieldInfosFormat fieldInfosFormat = new Lucene3xFieldInfosFormat();
    private final SegmentInfoFormat infosFormat = new Lucene3xSegmentInfoFormat();
    private final Lucene3xNormsFormat normsFormat = new Lucene3xNormsFormat();
    static final String COMPOUND_FILE_STORE_EXTENSION = "cfx";
    private final LiveDocsFormat liveDocsFormat = new Lucene40LiveDocsFormat();
    private final DocValuesFormat docValuesFormat = new DocValuesFormat("Lucene3x"){

        @Override
        public DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
            throw new UnsupportedOperationException("this codec cannot write docvalues");
        }

        @Override
        public DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
            return null;
        }
    };

    public Lucene3xCodec() {
        super("Lucene3x");
    }

    @Override
    public PostingsFormat postingsFormat() {
        return this.postingsFormat;
    }

    @Override
    public DocValuesFormat docValuesFormat() {
        return this.docValuesFormat;
    }

    @Override
    public StoredFieldsFormat storedFieldsFormat() {
        return this.fieldsFormat;
    }

    @Override
    public TermVectorsFormat termVectorsFormat() {
        return this.vectorsFormat;
    }

    @Override
    public FieldInfosFormat fieldInfosFormat() {
        return this.fieldInfosFormat;
    }

    @Override
    public SegmentInfoFormat segmentInfoFormat() {
        return this.infosFormat;
    }

    @Override
    public NormsFormat normsFormat() {
        return this.normsFormat;
    }

    @Override
    public LiveDocsFormat liveDocsFormat() {
        return this.liveDocsFormat;
    }

    public static Set<String> getDocStoreFiles(SegmentInfo info) {
        if (Lucene3xSegmentInfoFormat.getDocStoreOffset(info) != -1) {
            String dsName = Lucene3xSegmentInfoFormat.getDocStoreSegment(info);
            HashSet<String> files = new HashSet<String>();
            if (Lucene3xSegmentInfoFormat.getDocStoreIsCompoundFile(info)) {
                files.add(IndexFileNames.segmentFileName(dsName, "", COMPOUND_FILE_STORE_EXTENSION));
            } else {
                files.add(IndexFileNames.segmentFileName(dsName, "", "fdx"));
                files.add(IndexFileNames.segmentFileName(dsName, "", "fdt"));
                files.add(IndexFileNames.segmentFileName(dsName, "", "tvx"));
                files.add(IndexFileNames.segmentFileName(dsName, "", "tvf"));
                files.add(IndexFileNames.segmentFileName(dsName, "", "tvd"));
            }
            return files;
        }
        return null;
    }
}

