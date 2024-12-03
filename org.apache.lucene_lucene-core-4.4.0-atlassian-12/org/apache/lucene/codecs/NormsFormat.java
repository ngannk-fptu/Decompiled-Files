/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public abstract class NormsFormat {
    protected NormsFormat() {
    }

    public abstract DocValuesConsumer normsConsumer(SegmentWriteState var1) throws IOException;

    public abstract DocValuesProducer normsProducer(SegmentReadState var1) throws IOException;
}

