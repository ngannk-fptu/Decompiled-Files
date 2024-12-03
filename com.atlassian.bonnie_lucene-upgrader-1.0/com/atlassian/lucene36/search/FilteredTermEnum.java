/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import java.io.IOException;

public abstract class FilteredTermEnum
extends TermEnum {
    protected Term currentTerm = null;
    protected TermEnum actualEnum = null;

    protected abstract boolean termCompare(Term var1);

    public abstract float difference();

    protected abstract boolean endEnum();

    protected void setEnum(TermEnum actualEnum) throws IOException {
        this.actualEnum = actualEnum;
        Term term = actualEnum.term();
        if (term != null && this.termCompare(term)) {
            this.currentTerm = term;
        } else {
            this.next();
        }
    }

    public int docFreq() {
        if (this.currentTerm == null) {
            return -1;
        }
        assert (this.actualEnum != null);
        return this.actualEnum.docFreq();
    }

    public boolean next() throws IOException {
        if (this.actualEnum == null) {
            return false;
        }
        this.currentTerm = null;
        while (this.currentTerm == null) {
            if (this.endEnum()) {
                return false;
            }
            if (this.actualEnum.next()) {
                Term term = this.actualEnum.term();
                if (!this.termCompare(term)) continue;
                this.currentTerm = term;
                return true;
            }
            return false;
        }
        this.currentTerm = null;
        return false;
    }

    public Term term() {
        return this.currentTerm;
    }

    public void close() throws IOException {
        if (this.actualEnum != null) {
            this.actualEnum.close();
        }
        this.currentTerm = null;
        this.actualEnum = null;
    }
}

