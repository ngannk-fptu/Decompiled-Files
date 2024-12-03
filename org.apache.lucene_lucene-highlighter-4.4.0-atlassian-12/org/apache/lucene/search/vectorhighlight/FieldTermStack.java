/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.UnicodeUtil
 */
package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;

public class FieldTermStack {
    private final String fieldName;
    LinkedList<TermInfo> termList = new LinkedList();

    public FieldTermStack(IndexReader reader, int docId, String fieldName, FieldQuery fieldQuery) throws IOException {
        BytesRef text;
        this.fieldName = fieldName;
        Set<String> termSet = fieldQuery.getTermSet(fieldName);
        if (termSet == null) {
            return;
        }
        Fields vectors = reader.getTermVectors(docId);
        if (vectors == null) {
            return;
        }
        Terms vector = vectors.terms(fieldName);
        if (vector == null) {
            return;
        }
        CharsRef spare = new CharsRef();
        TermsEnum termsEnum = vector.iterator(null);
        DocsAndPositionsEnum dpEnum = null;
        int numDocs = reader.maxDoc();
        while ((text = termsEnum.next()) != null) {
            UnicodeUtil.UTF8toUTF16((BytesRef)text, (CharsRef)spare);
            String term = spare.toString();
            if (!termSet.contains(term)) continue;
            if ((dpEnum = termsEnum.docsAndPositions(null, dpEnum)) == null) {
                return;
            }
            dpEnum.nextDoc();
            float weight = (float)(Math.log((double)numDocs / (double)(reader.docFreq(new Term(fieldName, text)) + 1)) + 1.0);
            int freq = dpEnum.freq();
            for (int i = 0; i < freq; ++i) {
                int pos = dpEnum.nextPosition();
                if (dpEnum.startOffset() < 0) {
                    return;
                }
                this.termList.add(new TermInfo(term, dpEnum.startOffset(), dpEnum.endOffset(), pos, weight));
            }
        }
        Collections.sort(this.termList);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public TermInfo pop() {
        return this.termList.poll();
    }

    public void push(TermInfo termInfo) {
        this.termList.push(termInfo);
    }

    public boolean isEmpty() {
        return this.termList == null || this.termList.size() == 0;
    }

    public static class TermInfo
    implements Comparable<TermInfo> {
        private final String text;
        private final int startOffset;
        private final int endOffset;
        private final int position;
        private final float weight;

        public TermInfo(String text, int startOffset, int endOffset, int position, float weight) {
            this.text = text;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.position = position;
            this.weight = weight;
        }

        public String getText() {
            return this.text;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }

        public int getPosition() {
            return this.position;
        }

        public float getWeight() {
            return this.weight;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.text).append('(').append(this.startOffset).append(',').append(this.endOffset).append(',').append(this.position).append(')');
            return sb.toString();
        }

        @Override
        public int compareTo(TermInfo o) {
            return this.position - o.position;
        }
    }
}

