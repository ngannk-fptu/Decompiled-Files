/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.BlockTreeTermsReader;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.lucene40.Lucene40PostingsReader;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
public class Lucene40PostingsFormat
extends PostingsFormat {
    protected final int minBlockSize;
    protected final int maxBlockSize;
    static final String FREQ_EXTENSION = "frq";
    static final String PROX_EXTENSION = "prx";

    public Lucene40PostingsFormat() {
        this(25, 48);
    }

    private Lucene40PostingsFormat(int minBlockSize, int maxBlockSize) {
        super("Lucene40");
        this.minBlockSize = minBlockSize;
        assert (minBlockSize > 1);
        this.maxBlockSize = maxBlockSize;
    }

    @Override
    public FieldsConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldsProducer fieldsProducer(SegmentReadState state) throws IOException {
        Lucene40PostingsReader postings = new Lucene40PostingsReader(state.directory, state.fieldInfos, state.segmentInfo, state.context, state.segmentSuffix);
        boolean success = false;
        try {
            BlockTreeTermsReader ret = new BlockTreeTermsReader(state.directory, state.fieldInfos, state.segmentInfo, postings, state.context, state.segmentSuffix, state.termsIndexDivisor);
            success = true;
            BlockTreeTermsReader blockTreeTermsReader = ret;
            return blockTreeTermsReader;
        }
        finally {
            if (!success) {
                ((PostingsReaderBase)postings).close();
            }
        }
    }

    @Override
    public String toString() {
        return this.getName() + "(minBlockSize=" + this.minBlockSize + " maxBlockSize=" + this.maxBlockSize + ")";
    }
}

