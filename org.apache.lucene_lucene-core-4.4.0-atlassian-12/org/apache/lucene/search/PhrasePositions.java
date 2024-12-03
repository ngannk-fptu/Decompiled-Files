/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;

final class PhrasePositions {
    int doc;
    int position;
    int count;
    int offset;
    final int ord;
    final DocsAndPositionsEnum postings;
    PhrasePositions next;
    int rptGroup = -1;
    int rptInd;
    final Term[] terms;

    PhrasePositions(DocsAndPositionsEnum postings, int o, int ord, Term[] terms) {
        this.postings = postings;
        this.offset = o;
        this.ord = ord;
        this.terms = terms;
    }

    final boolean next() throws IOException {
        this.doc = this.postings.nextDoc();
        return this.doc != Integer.MAX_VALUE;
    }

    final boolean skipTo(int target) throws IOException {
        this.doc = this.postings.advance(target);
        return this.doc != Integer.MAX_VALUE;
    }

    final void firstPosition() throws IOException {
        this.count = this.postings.freq();
        this.nextPosition();
    }

    final boolean nextPosition() throws IOException {
        if (this.count-- > 0) {
            this.position = this.postings.nextPosition() - this.offset;
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

