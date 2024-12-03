/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.InvertedDocEndConsumer;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerThread;
import com.atlassian.lucene36.index.NormsWriterPerField;
import com.atlassian.lucene36.index.NormsWriterPerThread;
import com.atlassian.lucene36.index.SegmentNorms;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class NormsWriter
extends InvertedDocEndConsumer {
    private final byte defaultNorm = Similarity.getDefault().encodeNormValue(1.0f);
    private FieldInfos fieldInfos;

    NormsWriter() {
    }

    @Override
    public InvertedDocEndConsumerPerThread addThread(DocInverterPerThread docInverterPerThread) {
        return new NormsWriterPerThread(docInverterPerThread, this);
    }

    @Override
    public void abort() {
    }

    void files(Collection<String> files) {
    }

    @Override
    void setFieldInfos(FieldInfos fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void flush(Map<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException {
        IndexOutput normsOut;
        block21: {
            HashMap<FieldInfo, ArrayList<NormsWriterPerField>> byField = new HashMap<FieldInfo, ArrayList<NormsWriterPerField>>();
            for (Map.Entry<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> entry : threadsAndFields.entrySet()) {
                Collection<InvertedDocEndConsumerPerField> fields = entry.getValue();
                Iterator<InvertedDocEndConsumerPerField> fieldsIt = fields.iterator();
                while (fieldsIt.hasNext()) {
                    NormsWriterPerField perField = (NormsWriterPerField)fieldsIt.next();
                    if (perField.upto > 0) {
                        ArrayList<NormsWriterPerField> l = (ArrayList<NormsWriterPerField>)byField.get(perField.fieldInfo);
                        if (l == null) {
                            l = new ArrayList<NormsWriterPerField>();
                            byField.put(perField.fieldInfo, l);
                        }
                        l.add(perField);
                        continue;
                    }
                    fieldsIt.remove();
                }
            }
            String normsFileName = IndexFileNames.segmentFileName(state.segmentName, "nrm");
            normsOut = state.directory.createOutput(normsFileName);
            boolean success = false;
            try {
                normsOut.writeBytes(SegmentNorms.NORMS_HEADER, 0, SegmentNorms.NORMS_HEADER.length);
                int numField = this.fieldInfos.size();
                int normCount = 0;
                for (int fieldNumber = 0; fieldNumber < numField; ++fieldNumber) {
                    int upto;
                    FieldInfo fieldInfo = this.fieldInfos.fieldInfo(fieldNumber);
                    List toMerge = (List)byField.get(fieldInfo);
                    if (toMerge != null) {
                        int numFields = toMerge.size();
                        ++normCount;
                        NormsWriterPerField[] fields = new NormsWriterPerField[numFields];
                        int[] uptos = new int[numFields];
                        for (int j = 0; j < numFields; ++j) {
                            fields[j] = (NormsWriterPerField)toMerge.get(j);
                        }
                        int numLeft = numFields;
                        while (numLeft > 0) {
                            assert (uptos[0] < fields[0].docIDs.length) : " uptos[0]=" + uptos[0] + " len=" + fields[0].docIDs.length;
                            int minLoc = 0;
                            int minDocID = fields[0].docIDs[uptos[0]];
                            for (int j = 1; j < numLeft; ++j) {
                                int docID = fields[j].docIDs[uptos[j]];
                                if (docID >= minDocID) continue;
                                minDocID = docID;
                                minLoc = j;
                            }
                            assert (minDocID < state.numDocs);
                            while (upto < minDocID) {
                                normsOut.writeByte(this.defaultNorm);
                                ++upto;
                            }
                            normsOut.writeByte(fields[minLoc].norms[uptos[minLoc]]);
                            int n = minLoc;
                            uptos[n] = uptos[n] + 1;
                            ++upto;
                            if (uptos[minLoc] != fields[minLoc].upto) continue;
                            fields[minLoc].reset();
                            if (minLoc != numLeft - 1) {
                                fields[minLoc] = fields[numLeft - 1];
                                uptos[minLoc] = uptos[numLeft - 1];
                            }
                            --numLeft;
                        }
                        while (upto < state.numDocs) {
                            normsOut.writeByte(this.defaultNorm);
                            ++upto;
                        }
                    } else if (fieldInfo.isIndexed && !fieldInfo.omitNorms) {
                        ++normCount;
                        for (upto = 0; upto < state.numDocs; ++upto) {
                            normsOut.writeByte(this.defaultNorm);
                        }
                    }
                    assert (4L + (long)normCount * (long)state.numDocs == normsOut.getFilePointer()) : ".nrm file size mismatch: expected=" + (4L + (long)normCount * (long)state.numDocs) + " actual=" + normsOut.getFilePointer();
                }
                success = true;
                Object var22_25 = null;
                if (!success) break block21;
            }
            catch (Throwable throwable) {
                Object var22_26 = null;
                if (success) {
                    IOUtils.close(normsOut);
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(normsOut);
                throw throwable;
            }
            IOUtils.close(normsOut);
            return;
        }
        IOUtils.closeWhileHandlingException(normsOut);
    }
}

