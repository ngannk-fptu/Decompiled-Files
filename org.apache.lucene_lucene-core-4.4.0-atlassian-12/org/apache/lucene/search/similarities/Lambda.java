/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;

public abstract class Lambda {
    public abstract float lambda(BasicStats var1);

    public abstract Explanation explain(BasicStats var1);

    public abstract String toString();
}

