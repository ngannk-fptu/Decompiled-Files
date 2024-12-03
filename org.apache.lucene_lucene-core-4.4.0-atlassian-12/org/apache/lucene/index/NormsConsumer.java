/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.InvertedDocEndConsumer;
import org.apache.lucene.index.InvertedDocEndConsumerPerField;
import org.apache.lucene.index.NormsConsumerPerField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.IOUtils;

final class NormsConsumer
extends InvertedDocEndConsumer {
    NormsConsumer() {
    }

    @Override
    void abort() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush(Map<String, InvertedDocEndConsumerPerField> fieldsToFlush, SegmentWriteState state) throws IOException {
        block11: {
            DocValuesConsumer normsConsumer;
            block10: {
                boolean success = false;
                normsConsumer = null;
                try {
                    if (state.fieldInfos.hasNorms()) {
                        NormsFormat normsFormat = state.segmentInfo.getCodec().normsFormat();
                        assert (normsFormat != null);
                        normsConsumer = normsFormat.normsConsumer(state);
                        for (FieldInfo fi : state.fieldInfos) {
                            NormsConsumerPerField toWrite = (NormsConsumerPerField)fieldsToFlush.get(fi.name);
                            if (fi.omitsNorms()) continue;
                            if (toWrite != null && !toWrite.isEmpty()) {
                                toWrite.flush(state, normsConsumer);
                                assert (fi.getNormType() == FieldInfo.DocValuesType.NUMERIC);
                                continue;
                            }
                            if (fi.isIndexed()) assert (fi.getNormType() == null) : "got " + (Object)((Object)fi.getNormType()) + "; field=" + fi.name;
                        }
                    }
                    if (!(success = true)) break block10;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(normsConsumer);
                    } else {
                        IOUtils.closeWhileHandlingException(normsConsumer);
                    }
                    throw throwable;
                }
                IOUtils.close(normsConsumer);
                break block11;
            }
            IOUtils.closeWhileHandlingException(normsConsumer);
        }
    }

    @Override
    void finishDocument() {
    }

    @Override
    void startDocument() {
    }

    @Override
    InvertedDocEndConsumerPerField addField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo) {
        return new NormsConsumerPerField(docInverterPerField, fieldInfo, this);
    }
}

