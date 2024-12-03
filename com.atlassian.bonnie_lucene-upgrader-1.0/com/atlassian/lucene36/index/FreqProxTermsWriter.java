/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.ByteSliceReader;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FormatPostingsDocsConsumer;
import com.atlassian.lucene36.index.FormatPostingsFieldsConsumer;
import com.atlassian.lucene36.index.FormatPostingsFieldsWriter;
import com.atlassian.lucene36.index.FormatPostingsPositionsConsumer;
import com.atlassian.lucene36.index.FormatPostingsTermsConsumer;
import com.atlassian.lucene36.index.FreqProxFieldMergeState;
import com.atlassian.lucene36.index.FreqProxTermsWriterPerField;
import com.atlassian.lucene36.index.FreqProxTermsWriterPerThread;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermsHashConsumer;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerField;
import com.atlassian.lucene36.index.TermsHashPerThread;
import com.atlassian.lucene36.util.BitVector;
import com.atlassian.lucene36.util.CollectionUtil;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class FreqProxTermsWriter
extends TermsHashConsumer {
    private byte[] payloadBuffer;
    final UnicodeUtil.UTF8Result termsUTF8 = new UnicodeUtil.UTF8Result();

    FreqProxTermsWriter() {
    }

    @Override
    public TermsHashConsumerPerThread addThread(TermsHashPerThread perThread) {
        return new FreqProxTermsWriterPerThread(perThread);
    }

    private static int compareText(char[] text1, int pos1, char[] text2, int pos2) {
        char c1;
        do {
            char c2;
            if ((c1 = text1[pos1++]) == (c2 = text2[pos2++])) continue;
            if ('\uffff' == c2) {
                return 1;
            }
            if ('\uffff' == c1) {
                return -1;
            }
            return c1 - c2;
        } while ('\uffff' != c1);
        return 0;
    }

    @Override
    void abort() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException {
        ArrayList<FreqProxTermsWriterPerField> allFields = new ArrayList<FreqProxTermsWriterPerField>();
        for (Map.Entry<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> entry : threadsAndFields.entrySet()) {
            Collection<TermsHashConsumerPerField> fields = entry.getValue();
            for (TermsHashConsumerPerField termsHashConsumerPerField : fields) {
                FreqProxTermsWriterPerField perField = (FreqProxTermsWriterPerField)termsHashConsumerPerField;
                if (perField.termsHashPerField.numPostings <= 0) continue;
                allFields.add(perField);
            }
        }
        CollectionUtil.quickSort(allFields);
        int numAllFields = allFields.size();
        FormatPostingsFieldsWriter consumer = new FormatPostingsFieldsWriter(state, this.fieldInfos);
        try {
            int start = 0;
            while (start < numAllFields) {
                int i;
                int end;
                FieldInfo fieldInfo = ((FreqProxTermsWriterPerField)allFields.get((int)start)).fieldInfo;
                String string = fieldInfo.name;
                for (end = start + 1; end < numAllFields && ((FreqProxTermsWriterPerField)allFields.get((int)end)).fieldInfo.name.equals(string); ++end) {
                }
                FreqProxTermsWriterPerField[] fields = new FreqProxTermsWriterPerField[end - start];
                for (i = start; i < end; ++i) {
                    fields[i - start] = (FreqProxTermsWriterPerField)allFields.get(i);
                    if (fieldInfo.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) continue;
                    fieldInfo.storePayloads |= fields[i - start].hasPayloads;
                }
                this.appendPostings(string, state, fields, consumer);
                for (i = 0; i < fields.length; ++i) {
                    TermsHashPerField perField = fields[i].termsHashPerField;
                    int numPostings = perField.numPostings;
                    perField.reset();
                    perField.shrinkHash(numPostings);
                    fields[i].reset();
                }
                start = end;
            }
            for (Map.Entry entry : threadsAndFields.entrySet()) {
                FreqProxTermsWriterPerThread perThread = (FreqProxTermsWriterPerThread)entry.getKey();
                perThread.termsHashPerThread.reset(true);
            }
            Object var15_22 = null;
        }
        catch (Throwable throwable) {
            Object var15_23 = null;
            ((FormatPostingsFieldsConsumer)consumer).finish();
            throw throwable;
        }
        ((FormatPostingsFieldsConsumer)consumer).finish();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void appendPostings(String fieldName, SegmentWriteState state, FreqProxTermsWriterPerField[] fields, FormatPostingsFieldsConsumer consumer) throws CorruptIndexException, IOException {
        int numFields = fields.length;
        FreqProxFieldMergeState[] mergeStates = new FreqProxFieldMergeState[numFields];
        for (int i = 0; i < numFields; ++i) {
            FreqProxFieldMergeState fms = mergeStates[i] = new FreqProxFieldMergeState(fields[i]);
            assert (fms.field.fieldInfo == fields[0].fieldInfo);
            boolean result = fms.nextTerm();
            assert (result);
        }
        FormatPostingsTermsConsumer termsConsumer = consumer.addField(fields[0].fieldInfo);
        Term protoTerm = new Term(fieldName);
        FreqProxFieldMergeState[] termStates = new FreqProxFieldMergeState[numFields];
        FieldInfo.IndexOptions currentFieldIndexOptions = fields[0].fieldInfo.indexOptions;
        Map<Term, Integer> segDeletes = state.segDeletes != null && state.segDeletes.terms.size() > 0 ? state.segDeletes.terms : null;
        try {
            while (numFields > 0) {
                Object var26_30;
                Integer docIDUpto;
                termStates[0] = mergeStates[0];
                int numToMerge = 1;
                for (int i = 1; i < numFields; ++i) {
                    char[] text = mergeStates[i].text;
                    int textOffset = mergeStates[i].textOffset;
                    int cmp = FreqProxTermsWriter.compareText(text, textOffset, termStates[0].text, termStates[0].textOffset);
                    if (cmp < 0) {
                        termStates[0] = mergeStates[i];
                        numToMerge = 1;
                        continue;
                    }
                    if (cmp != 0) continue;
                    termStates[numToMerge++] = mergeStates[i];
                }
                FormatPostingsDocsConsumer docConsumer = termsConsumer.addTerm(termStates[0].text, termStates[0].textOffset);
                int delDocLimit = segDeletes != null ? ((docIDUpto = segDeletes.get(protoTerm.createTerm(termStates[0].termText()))) != null ? docIDUpto : 0) : 0;
                try {
                    while (numToMerge > 0) {
                        int i;
                        FreqProxFieldMergeState minState = termStates[0];
                        for (int i2 = 1; i2 < numToMerge; ++i2) {
                            if (termStates[i2].docID >= minState.docID) continue;
                            minState = termStates[i2];
                        }
                        int termDocFreq = minState.termFreq;
                        FormatPostingsPositionsConsumer posConsumer = docConsumer.addDoc(minState.docID, termDocFreq);
                        if (minState.docID < delDocLimit) {
                            if (state.deletedDocs == null) {
                                state.deletedDocs = new BitVector(state.numDocs);
                            }
                            state.deletedDocs.set(minState.docID);
                        }
                        ByteSliceReader prox = minState.prox;
                        if (currentFieldIndexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                            Object var24_29;
                            try {
                                int position = 0;
                                for (int j = 0; j < termDocFreq; ++j) {
                                    int payloadLength;
                                    int code = prox.readVInt();
                                    position += code >>> 1;
                                    if ((code & 1) != 0) {
                                        payloadLength = prox.readVInt();
                                        if (this.payloadBuffer == null || this.payloadBuffer.length < payloadLength) {
                                            this.payloadBuffer = new byte[payloadLength];
                                        }
                                        prox.readBytes(this.payloadBuffer, 0, payloadLength);
                                    } else {
                                        payloadLength = 0;
                                    }
                                    posConsumer.addPosition(position, this.payloadBuffer, 0, payloadLength);
                                }
                                var24_29 = null;
                            }
                            catch (Throwable throwable) {
                                var24_29 = null;
                                posConsumer.finish();
                                throw throwable;
                            }
                            posConsumer.finish();
                            {
                            }
                        }
                        if (minState.nextDoc()) continue;
                        int upto = 0;
                        for (i = 0; i < numToMerge; ++i) {
                            if (termStates[i] == minState) continue;
                            termStates[upto++] = termStates[i];
                        }
                        assert (upto == --numToMerge);
                        if (minState.nextTerm()) continue;
                        upto = 0;
                        for (i = 0; i < numFields; ++i) {
                            if (mergeStates[i] == minState) continue;
                            mergeStates[upto++] = mergeStates[i];
                        }
                        assert (upto == --numFields);
                    }
                    var26_30 = null;
                }
                catch (Throwable throwable) {
                    var26_30 = null;
                    docConsumer.finish();
                    throw throwable;
                }
                docConsumer.finish();
                {
                }
            }
            Object var28_33 = null;
        }
        catch (Throwable throwable) {
            Object var28_34 = null;
            termsConsumer.finish();
            throw throwable;
        }
        termsConsumer.finish();
    }
}

