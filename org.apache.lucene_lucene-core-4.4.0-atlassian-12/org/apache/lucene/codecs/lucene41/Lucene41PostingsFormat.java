/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import org.apache.lucene.codecs.BlockTreeTermsReader;
import org.apache.lucene.codecs.BlockTreeTermsWriter;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.lucene41.Lucene41PostingsReader;
import org.apache.lucene.codecs.lucene41.Lucene41PostingsWriter;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.IOUtils;

public final class Lucene41PostingsFormat
extends PostingsFormat {
    public static final String DOC_EXTENSION = "doc";
    public static final String POS_EXTENSION = "pos";
    public static final String PAY_EXTENSION = "pay";
    private final int minTermBlockSize;
    private final int maxTermBlockSize;
    public static final int BLOCK_SIZE = 128;

    public Lucene41PostingsFormat() {
        this(25, 48);
    }

    public Lucene41PostingsFormat(int minTermBlockSize, int maxTermBlockSize) {
        super("Lucene41");
        this.minTermBlockSize = minTermBlockSize;
        assert (minTermBlockSize > 1);
        this.maxTermBlockSize = maxTermBlockSize;
        assert (minTermBlockSize <= maxTermBlockSize);
    }

    @Override
    public String toString() {
        return this.getName() + "(blocksize=" + 128 + ")";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldsConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        BlockTreeTermsWriter blockTreeTermsWriter;
        block3: {
            Lucene41PostingsWriter postingsWriter = new Lucene41PostingsWriter(state);
            boolean success = false;
            try {
                BlockTreeTermsWriter ret = new BlockTreeTermsWriter(state, postingsWriter, this.minTermBlockSize, this.maxTermBlockSize);
                success = true;
                blockTreeTermsWriter = ret;
                if (success) break block3;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.closeWhileHandlingException(postingsWriter);
                }
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(postingsWriter);
        }
        return blockTreeTermsWriter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldsProducer fieldsProducer(SegmentReadState state) throws IOException {
        BlockTreeTermsReader blockTreeTermsReader;
        block3: {
            Lucene41PostingsReader postingsReader = new Lucene41PostingsReader(state.directory, state.fieldInfos, state.segmentInfo, state.context, state.segmentSuffix);
            boolean success = false;
            try {
                BlockTreeTermsReader ret = new BlockTreeTermsReader(state.directory, state.fieldInfos, state.segmentInfo, postingsReader, state.context, state.segmentSuffix, state.termsIndexDivisor);
                success = true;
                blockTreeTermsReader = ret;
                if (success) break block3;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.closeWhileHandlingException(postingsReader);
                }
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(postingsReader);
        }
        return blockTreeTermsReader;
    }
}

