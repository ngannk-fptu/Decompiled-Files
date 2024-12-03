/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

public abstract class PostingsConsumer {
    protected PostingsConsumer() {
    }

    public abstract void startDoc(int var1, int var2) throws IOException;

    public abstract void addPosition(int var1, BytesRef var2, int var3, int var4) throws IOException;

    public abstract void finishDoc() throws IOException;

    public TermStats merge(MergeState mergeState, FieldInfo.IndexOptions indexOptions, DocsEnum postings, FixedBitSet visitedDocs) throws IOException {
        int df = 0;
        long totTF = 0L;
        if (indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            int doc;
            while ((doc = postings.nextDoc()) != Integer.MAX_VALUE) {
                visitedDocs.set(doc);
                this.startDoc(doc, -1);
                this.finishDoc();
                ++df;
            }
            totTF = -1L;
        } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) {
            int doc;
            while ((doc = postings.nextDoc()) != Integer.MAX_VALUE) {
                visitedDocs.set(doc);
                int freq = postings.freq();
                this.startDoc(doc, freq);
                this.finishDoc();
                ++df;
                totTF += (long)freq;
            }
        } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            int doc;
            DocsAndPositionsEnum postingsEnum = (DocsAndPositionsEnum)postings;
            while ((doc = postingsEnum.nextDoc()) != Integer.MAX_VALUE) {
                visitedDocs.set(doc);
                int freq = postingsEnum.freq();
                this.startDoc(doc, freq);
                totTF += (long)freq;
                for (int i = 0; i < freq; ++i) {
                    int position = postingsEnum.nextPosition();
                    BytesRef payload = postingsEnum.getPayload();
                    this.addPosition(position, payload, -1, -1);
                }
                this.finishDoc();
                ++df;
            }
        } else {
            int doc;
            assert (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            DocsAndPositionsEnum postingsEnum = (DocsAndPositionsEnum)postings;
            while ((doc = postingsEnum.nextDoc()) != Integer.MAX_VALUE) {
                visitedDocs.set(doc);
                int freq = postingsEnum.freq();
                this.startDoc(doc, freq);
                totTF += (long)freq;
                for (int i = 0; i < freq; ++i) {
                    int position = postingsEnum.nextPosition();
                    BytesRef payload = postingsEnum.getPayload();
                    this.addPosition(position, payload, postingsEnum.startOffset(), postingsEnum.endOffset());
                }
                this.finishDoc();
                ++df;
            }
        }
        return new TermStats(df, indexOptions == FieldInfo.IndexOptions.DOCS_ONLY ? -1L : totTF);
    }
}

