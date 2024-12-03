/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermPositions;
import java.io.IOException;

final class PhrasePositions {
    int doc;
    int position;
    int count;
    int offset;
    final int ord;
    TermPositions tp;
    PhrasePositions next;
    int rptGroup = -1;
    int rptInd;
    final Term[] terms;

    PhrasePositions(TermPositions t, int o, int ord, Term[] terms) {
        this.tp = t;
        this.offset = o;
        this.ord = ord;
        this.terms = terms;
    }

    final boolean next() throws IOException {
        if (!this.tp.next()) {
            this.tp.close();
            this.doc = Integer.MAX_VALUE;
            return false;
        }
        this.doc = this.tp.doc();
        this.position = 0;
        return true;
    }

    final boolean skipTo(int target) throws IOException {
        if (!this.tp.skipTo(target)) {
            this.tp.close();
            this.doc = Integer.MAX_VALUE;
            return false;
        }
        this.doc = this.tp.doc();
        this.position = 0;
        return true;
    }

    final void firstPosition() throws IOException {
        this.count = this.tp.freq();
        this.nextPosition();
    }

    final boolean nextPosition() throws IOException {
        if (this.count-- > 0) {
            this.position = this.tp.nextPosition() - this.offset;
            return true;
        }
        return false;
    }

    public String toString() {
        String s = "d:" + this.doc + " o:" + this.offset + " p:" + this.position + " c:" + this.count;
        if (this.rptGroup >= 0) {
            s = s + " rpt:" + this.rptGroup + ",i" + this.rptInd;
        }
        return s;
    }
}

