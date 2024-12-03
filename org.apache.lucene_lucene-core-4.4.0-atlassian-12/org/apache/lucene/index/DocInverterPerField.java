/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.index.DocFieldConsumerPerField;
import org.apache.lucene.index.DocInverter;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.InvertedDocConsumerPerField;
import org.apache.lucene.index.InvertedDocEndConsumerPerField;
import org.apache.lucene.util.IOUtils;

final class DocInverterPerField
extends DocFieldConsumerPerField {
    final FieldInfo fieldInfo;
    final InvertedDocConsumerPerField consumer;
    final InvertedDocEndConsumerPerField endConsumer;
    final DocumentsWriterPerThread.DocState docState;
    final FieldInvertState fieldState;

    public DocInverterPerField(DocInverter parent, FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.docState = parent.docState;
        this.fieldState = new FieldInvertState(fieldInfo.name);
        this.consumer = parent.consumer.addField(this, fieldInfo);
        this.endConsumer = parent.endConsumer.addField(this, fieldInfo);
    }

    @Override
    void abort() {
        try {
            this.consumer.abort();
        }
        finally {
            this.endConsumer.abort();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processFields(IndexableField[] fields, int count) throws IOException {
        this.fieldState.reset();
        boolean doInvert = this.consumer.start(fields, count);
        for (int i = 0; i < count; ++i) {
            block22: {
                boolean analyzed;
                IndexableField field;
                block23: {
                    TokenStream stream;
                    block21: {
                        field = fields[i];
                        IndexableFieldType fieldType = field.fieldType();
                        if (!fieldType.indexed() || !doInvert) break block22;
                        boolean bl = analyzed = fieldType.tokenized() && this.docState.analyzer != null;
                        if (fieldType.omitNorms() && field.boost() != 1.0f) {
                            throw new UnsupportedOperationException("You cannot set an index-time boost: norms are omitted for field '" + field.name() + "'");
                        }
                        boolean checkOffsets = fieldType.indexOptions() == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
                        int lastStartOffset = 0;
                        if (i > 0) {
                            this.fieldState.position = this.fieldState.position + (analyzed ? this.docState.analyzer.getPositionIncrementGap(this.fieldInfo.name) : 0);
                        }
                        stream = field.tokenStream(this.docState.analyzer);
                        stream.reset();
                        boolean success2 = false;
                        try {
                            boolean hasMoreTokens = stream.incrementToken();
                            this.fieldState.attributeSource = stream;
                            OffsetAttribute offsetAttribute = this.fieldState.attributeSource.addAttribute(OffsetAttribute.class);
                            PositionIncrementAttribute posIncrAttribute = this.fieldState.attributeSource.addAttribute(PositionIncrementAttribute.class);
                            if (hasMoreTokens) {
                                this.consumer.start(field);
                                do {
                                    int posIncr;
                                    if ((posIncr = posIncrAttribute.getPositionIncrement()) < 0) {
                                        throw new IllegalArgumentException("position increment must be >=0 (got " + posIncr + ") for field '" + field.name() + "'");
                                    }
                                    if (this.fieldState.position == 0 && posIncr == 0) {
                                        throw new IllegalArgumentException("first position increment must be > 0 (got 0) for field '" + field.name() + "'");
                                    }
                                    int position = this.fieldState.position + posIncr;
                                    if (position <= 0 && position < 0) {
                                        throw new IllegalArgumentException("position overflow for field '" + field.name() + "'");
                                    }
                                    this.fieldState.position = --position;
                                    if (posIncr == 0) {
                                        ++this.fieldState.numOverlap;
                                    }
                                    if (checkOffsets) {
                                        int startOffset = this.fieldState.offset + offsetAttribute.startOffset();
                                        int endOffset = this.fieldState.offset + offsetAttribute.endOffset();
                                        if (startOffset < 0 || endOffset < startOffset) {
                                            throw new IllegalArgumentException("startOffset must be non-negative, and endOffset must be >= startOffset, startOffset=" + startOffset + ",endOffset=" + endOffset + " for field '" + field.name() + "'");
                                        }
                                        if (startOffset < lastStartOffset) {
                                            throw new IllegalArgumentException("offsets must not go backwards startOffset=" + startOffset + " is < lastStartOffset=" + lastStartOffset + " for field '" + field.name() + "'");
                                        }
                                        lastStartOffset = startOffset;
                                    }
                                    boolean success = false;
                                    try {
                                        this.consumer.add();
                                        success = true;
                                    }
                                    finally {
                                        if (!success) {
                                            this.docState.docWriter.setAborting();
                                        }
                                    }
                                    ++this.fieldState.length;
                                    ++this.fieldState.position;
                                } while (stream.incrementToken());
                            }
                            stream.end();
                            this.fieldState.offset += offsetAttribute.endOffset();
                            success2 = true;
                            if (success2) break block21;
                        }
                        catch (Throwable throwable) {
                            if (!success2) {
                                IOUtils.closeWhileHandlingException(stream);
                            } else {
                                stream.close();
                            }
                            throw throwable;
                        }
                        IOUtils.closeWhileHandlingException(stream);
                        break block23;
                    }
                    stream.close();
                }
                this.fieldState.offset = this.fieldState.offset + (analyzed ? this.docState.analyzer.getOffsetGap(this.fieldInfo.name) : 0);
                this.fieldState.boost *= field.boost();
            }
            fields[i] = null;
        }
        this.consumer.finish();
        this.endConsumer.finish();
    }

    @Override
    FieldInfo getFieldInfo() {
        return this.fieldInfo;
    }
}

