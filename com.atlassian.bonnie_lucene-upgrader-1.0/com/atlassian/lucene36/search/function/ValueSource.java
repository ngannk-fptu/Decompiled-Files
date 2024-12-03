/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.function.DocValues;
import java.io.IOException;
import java.io.Serializable;

public abstract class ValueSource
implements Serializable {
    public abstract DocValues getValues(IndexReader var1) throws IOException;

    public abstract String description();

    public String toString() {
        return this.description();
    }

    public abstract boolean equals(Object var1);

    public abstract int hashCode();
}

