/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.ScoreDoc;

public class FieldDoc
extends ScoreDoc {
    public Object[] fields;

    public FieldDoc(int doc, float score) {
        super(doc, score);
    }

    public FieldDoc(int doc, float score, Object[] fields) {
        super(doc, score);
        this.fields = fields;
    }

    public FieldDoc(int doc, float score, Object[] fields, int shardIndex) {
        super(doc, score, shardIndex);
        this.fields = fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[");
        for (int i = 0; i < this.fields.length; ++i) {
            sb.append(this.fields[i]).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }
}

