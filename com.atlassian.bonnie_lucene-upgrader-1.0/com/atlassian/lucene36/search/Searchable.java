/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.search.Weight;
import java.io.Closeable;
import java.io.IOException;

@Deprecated
public interface Searchable
extends Closeable {
    public void search(Weight var1, Filter var2, Collector var3) throws IOException;

    public void close() throws IOException;

    public int docFreq(Term var1) throws IOException;

    public int[] docFreqs(Term[] var1) throws IOException;

    public int maxDoc() throws IOException;

    public TopDocs search(Weight var1, Filter var2, int var3) throws IOException;

    public Document doc(int var1) throws CorruptIndexException, IOException;

    public Document doc(int var1, FieldSelector var2) throws CorruptIndexException, IOException;

    public Query rewrite(Query var1) throws IOException;

    public Explanation explain(Weight var1, int var2) throws IOException;

    public TopFieldDocs search(Weight var1, Filter var2, int var3, Sort var4) throws IOException;
}

