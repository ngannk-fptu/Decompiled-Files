/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.index.Terms;

public abstract class Fields
implements Iterable<String> {
    public static final Fields[] EMPTY_ARRAY = new Fields[0];

    protected Fields() {
    }

    @Override
    public abstract Iterator<String> iterator();

    public abstract Terms terms(String var1) throws IOException;

    public abstract int size();

    @Deprecated
    public long getUniqueTermCount() throws IOException {
        long numTerms = 0L;
        for (String field : this) {
            Terms terms = this.terms(field);
            if (terms == null) continue;
            long termCount = terms.size();
            if (termCount == -1L) {
                return -1L;
            }
            numTerms += termCount;
        }
        return numTerms;
    }
}

