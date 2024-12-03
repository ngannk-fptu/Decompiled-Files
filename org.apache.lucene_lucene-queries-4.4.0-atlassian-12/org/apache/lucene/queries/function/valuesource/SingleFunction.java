/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.IndexSearcher;

public abstract class SingleFunction
extends ValueSource {
    protected final ValueSource source;

    public SingleFunction(ValueSource source) {
        this.source = source;
    }

    protected abstract String name();

    @Override
    public String description() {
        return this.name() + '(' + this.source.description() + ')';
    }

    @Override
    public int hashCode() {
        return this.source.hashCode() + this.name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        SingleFunction other = (SingleFunction)o;
        return this.name().equals(other.name()) && this.source.equals(other.source);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
}

