/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.DocFieldConsumerPerField;
import com.atlassian.lucene36.index.DocInverterPerThread;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import java.io.IOException;
import java.io.Reader;

final class DocInverterPerField
extends DocFieldConsumerPerField {
    private final DocInverterPerThread perThread;
    private final FieldInfo fieldInfo;
    final InvertedDocConsumerPerField consumer;
    final InvertedDocEndConsumerPerField endConsumer;
    final DocumentsWriter.DocState docState;
    final FieldInvertState fieldState;

    public DocInverterPerField(DocInverterPerThread perThread, FieldInfo fieldInfo) {
        this.perThread = perThread;
        this.fieldInfo = fieldInfo;
        this.docState = perThread.docState;
        this.fieldState = perThread.fieldState;
        this.consumer = perThread.consumer.addField(this, fieldInfo);
        this.endConsumer = perThread.endConsumer.addField(this, fieldInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void abort() {
        try {
            this.consumer.abort();
            Object var2_1 = null;
            this.endConsumer.abort();
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.endConsumer.abort();
            throw throwable;
        }
    }

    public void close() {
        this.consumer.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processFields(Fieldable[] fields, int count) throws IOException {
        this.fieldState.reset(this.docState.doc.getBoost());
        int maxFieldLength = this.docState.maxFieldLength;
        boolean doInvert = this.consumer.start(fields, count);
        for (int i = 0; i < count; ++i) {
            Fieldable field = fields[i];
            if (field.isIndexed() && doInvert) {
                float boost = field.getBoost();
                if (field.getOmitNorms() && boost != 1.0f) {
                    throw new UnsupportedOperationException("You cannot set an index-time boost: norms are omitted for field '" + field.name() + "'");
                }
                if (i > 0) {
                    this.fieldState.position = this.fieldState.position + (this.docState.analyzer == null ? 0 : this.docState.analyzer.getPositionIncrementGap(this.fieldInfo.name));
                }
                if (!field.isTokenized()) {
                    Object var12_17;
                    String stringValue = field.stringValue();
                    int valueLength = stringValue.length();
                    this.perThread.singleToken.reinit(stringValue, 0, valueLength);
                    this.fieldState.attributeSource = this.perThread.singleToken;
                    this.consumer.start(field);
                    boolean success = false;
                    try {
                        this.consumer.add();
                        success = true;
                        var12_17 = null;
                        if (!success) {
                            this.docState.docWriter.setAborting();
                        }
                    }
                    catch (Throwable throwable) {
                        var12_17 = null;
                        if (!success) {
                            this.docState.docWriter.setAborting();
                        }
                        throw throwable;
                    }
                    this.fieldState.offset += valueLength;
                    ++this.fieldState.length;
                    ++this.fieldState.position;
                } else {
                    Object var20_23;
                    TokenStream stream;
                    TokenStream streamValue = field.tokenStreamValue();
                    if (streamValue != null) {
                        stream = streamValue;
                    } else {
                        Reader reader;
                        Reader readerValue = field.readerValue();
                        if (readerValue != null) {
                            reader = readerValue;
                        } else {
                            String stringValue = field.stringValue();
                            if (stringValue == null) {
                                throw new IllegalArgumentException("field must have either TokenStream, String or Reader value");
                            }
                            this.perThread.stringReader.init(stringValue);
                            reader = this.perThread.stringReader;
                        }
                        stream = this.docState.analyzer.reusableTokenStream(this.fieldInfo.name, reader);
                    }
                    stream.reset();
                    int startLength = this.fieldState.length;
                    try {
                        boolean hasMoreTokens = stream.incrementToken();
                        this.fieldState.attributeSource = stream;
                        OffsetAttribute offsetAttribute = this.fieldState.attributeSource.addAttribute(OffsetAttribute.class);
                        PositionIncrementAttribute posIncrAttribute = this.fieldState.attributeSource.addAttribute(PositionIncrementAttribute.class);
                        this.consumer.start(field);
                        while (hasMoreTokens) {
                            Object var18_22;
                            int posIncr = posIncrAttribute.getPositionIncrement();
                            int position = this.fieldState.position + posIncr;
                            if (position <= 0 && position < 0) {
                                throw new IllegalArgumentException("position overflow for field '" + field.name() + "'");
                            }
                            this.fieldState.position = --position;
                            if (posIncr == 0) {
                                ++this.fieldState.numOverlap;
                            }
                            boolean success = false;
                            try {
                                this.consumer.add();
                                success = true;
                                var18_22 = null;
                                if (!success) {
                                    this.docState.docWriter.setAborting();
                                }
                            }
                            catch (Throwable throwable) {
                                var18_22 = null;
                                if (!success) {
                                    this.docState.docWriter.setAborting();
                                }
                                throw throwable;
                            }
                            ++this.fieldState.position;
                            if (++this.fieldState.length >= maxFieldLength) {
                                if (this.docState.infoStream == null) break;
                                this.docState.infoStream.println("maxFieldLength " + maxFieldLength + " reached for field " + this.fieldInfo.name + ", ignoring following tokens");
                                break;
                            }
                            hasMoreTokens = stream.incrementToken();
                        }
                        stream.end();
                        this.fieldState.offset += offsetAttribute.endOffset();
                        var20_23 = null;
                    }
                    catch (Throwable throwable) {
                        var20_23 = null;
                        stream.close();
                        throw throwable;
                    }
                    stream.close();
                    {
                    }
                }
                this.fieldState.offset = this.fieldState.offset + (this.docState.analyzer == null ? 0 : this.docState.analyzer.getOffsetGap(field));
                this.fieldState.boost *= boost;
            }
            fields[i] = null;
        }
        this.consumer.finish();
        this.endConsumer.finish();
    }
}

