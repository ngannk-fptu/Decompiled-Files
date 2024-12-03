/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import java.io.IOException;
import java.io.Serializable;

public abstract class Filter
implements Serializable {
    public abstract DocIdSet getDocIdSet(IndexReader var1) throws IOException;
}

