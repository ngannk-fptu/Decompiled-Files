/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.SpanFilterResult;
import java.io.IOException;

public abstract class SpanFilter
extends Filter {
    public abstract SpanFilterResult bitSpans(IndexReader var1) throws IOException;
}

