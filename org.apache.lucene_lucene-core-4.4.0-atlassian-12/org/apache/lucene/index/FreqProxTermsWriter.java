/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FreqProxTermsWriterPerField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TermsHash;
import org.apache.lucene.index.TermsHashConsumer;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CollectionUtil;
import org.apache.lucene.util.IOUtils;

final class FreqProxTermsWriter
extends TermsHashConsumer {
    BytesRef payload;

    FreqProxTermsWriter() {
    }

    @Override
    void abort() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush(Map<String, TermsHashConsumerPerField> fieldsToFlush, SegmentWriteState state) throws IOException {
        block9: {
            FieldsConsumer consumer;
            block8: {
                ArrayList<FreqProxTermsWriterPerField> allFields = new ArrayList<FreqProxTermsWriterPerField>();
                for (TermsHashConsumerPerField f : fieldsToFlush.values()) {
                    FreqProxTermsWriterPerField perField = (FreqProxTermsWriterPerField)f;
                    if (perField.termsHashPerField.bytesHash.size() <= 0) continue;
                    allFields.add(perField);
                }
                int numAllFields = allFields.size();
                CollectionUtil.introSort(allFields);
                consumer = state.segmentInfo.getCodec().postingsFormat().fieldsConsumer(state);
                boolean success = false;
                try {
                    TermsHash termsHash = null;
                    for (int fieldNumber = 0; fieldNumber < numAllFields; ++fieldNumber) {
                        FieldInfo fieldInfo = ((FreqProxTermsWriterPerField)allFields.get((int)fieldNumber)).fieldInfo;
                        FreqProxTermsWriterPerField fieldWriter = (FreqProxTermsWriterPerField)allFields.get(fieldNumber);
                        fieldWriter.flush(fieldInfo.name, consumer, state);
                        TermsHashPerField perField = fieldWriter.termsHashPerField;
                        assert (termsHash == null || termsHash == perField.termsHash);
                        termsHash = perField.termsHash;
                        int numPostings = perField.bytesHash.size();
                        perField.reset();
                        perField.shrinkHash(numPostings);
                        fieldWriter.reset();
                    }
                    if (termsHash != null) {
                        termsHash.reset();
                    }
                    if (!(success = true)) break block8;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(consumer);
                    } else {
                        IOUtils.closeWhileHandlingException(consumer);
                    }
                    throw throwable;
                }
                IOUtils.close(consumer);
                break block9;
            }
            IOUtils.closeWhileHandlingException(consumer);
        }
    }

    @Override
    public TermsHashConsumerPerField addField(TermsHashPerField termsHashPerField, FieldInfo fieldInfo) {
        return new FreqProxTermsWriterPerField(termsHashPerField, this, fieldInfo);
    }

    @Override
    void finishDocument(TermsHash termsHash) {
    }

    @Override
    void startDocument() {
    }
}

