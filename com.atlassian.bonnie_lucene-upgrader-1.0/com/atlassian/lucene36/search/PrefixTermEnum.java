/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import java.io.IOException;

public class PrefixTermEnum
extends FilteredTermEnum {
    private final Term prefix;
    private boolean endEnum = false;

    public PrefixTermEnum(IndexReader reader, Term prefix) throws IOException {
        this.prefix = prefix;
        this.setEnum(reader.terms(new Term(prefix.field(), prefix.text())));
    }

    public float difference() {
        return 1.0f;
    }

    protected boolean endEnum() {
        return this.endEnum;
    }

    protected Term getPrefixTerm() {
        return this.prefix;
    }

    protected boolean termCompare(Term term) {
        if (term.field() == this.prefix.field() && term.text().startsWith(this.prefix.text())) {
            return true;
        }
        this.endEnum = true;
        return false;
    }
}

