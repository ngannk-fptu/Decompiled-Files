/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import java.io.IOException;

public class SingleTermEnum
extends FilteredTermEnum {
    private Term singleTerm;
    private boolean endEnum = false;

    public SingleTermEnum(IndexReader reader, Term singleTerm) throws IOException {
        this.singleTerm = singleTerm;
        this.setEnum(reader.terms(singleTerm));
    }

    public float difference() {
        return 1.0f;
    }

    protected boolean endEnum() {
        return this.endEnum;
    }

    protected boolean termCompare(Term term) {
        if (term.equals(this.singleTerm)) {
            return true;
        }
        this.endEnum = true;
        return false;
    }
}

