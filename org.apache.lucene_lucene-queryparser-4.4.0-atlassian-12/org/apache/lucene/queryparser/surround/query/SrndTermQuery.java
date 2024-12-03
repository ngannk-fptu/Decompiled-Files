/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.index.TermsEnum$SeekStatus
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.surround.query.SimpleTerm;
import org.apache.lucene.util.BytesRef;

public class SrndTermQuery
extends SimpleTerm {
    private final String termText;

    public SrndTermQuery(String termText, boolean quoted) {
        super(quoted);
        this.termText = termText;
    }

    public String getTermText() {
        return this.termText;
    }

    public Term getLuceneTerm(String fieldName) {
        return new Term(fieldName, this.getTermText());
    }

    @Override
    public String toStringUnquoted() {
        return this.getTermText();
    }

    @Override
    public void visitMatchingTerms(IndexReader reader, String fieldName, SimpleTerm.MatchingTermVisitor mtv) throws IOException {
        TermsEnum termsEnum;
        TermsEnum.SeekStatus status;
        Terms terms = MultiFields.getTerms((IndexReader)reader, (String)fieldName);
        if (terms != null && (status = (termsEnum = terms.iterator(null)).seekCeil(new BytesRef((CharSequence)this.getTermText()))) == TermsEnum.SeekStatus.FOUND) {
            mtv.visitMatchingTerm(this.getLuceneTerm(fieldName));
        }
    }
}

