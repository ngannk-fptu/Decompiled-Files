/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.Bits;

public abstract class Weight {
    public abstract Explanation explain(AtomicReaderContext var1, int var2) throws IOException;

    public abstract Query getQuery();

    public abstract float getValueForNormalization() throws IOException;

    public abstract void normalize(float var1, float var2);

    public abstract Scorer scorer(AtomicReaderContext var1, boolean var2, boolean var3, Bits var4) throws IOException;

    public boolean scoresDocsOutOfOrder() {
        return false;
    }
}

