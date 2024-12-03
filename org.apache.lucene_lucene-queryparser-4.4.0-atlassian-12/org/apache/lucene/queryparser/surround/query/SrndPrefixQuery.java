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
 *  org.apache.lucene.util.StringHelper
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
import org.apache.lucene.util.StringHelper;

public class SrndPrefixQuery
extends SimpleTerm {
    private final BytesRef prefixRef;
    private final String prefix;
    private final char truncator;

    public SrndPrefixQuery(String prefix, boolean quoted, char truncator) {
        super(quoted);
        this.prefix = prefix;
        this.prefixRef = new BytesRef((CharSequence)prefix);
        this.truncator = truncator;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public char getSuffixOperator() {
        return this.truncator;
    }

    public Term getLucenePrefixTerm(String fieldName) {
        return new Term(fieldName, this.getPrefix());
    }

    @Override
    public String toStringUnquoted() {
        return this.getPrefix();
    }

    @Override
    protected void suffixToString(StringBuilder r) {
        r.append(this.getSuffixOperator());
    }

    @Override
    public void visitMatchingTerms(IndexReader reader, String fieldName, SimpleTerm.MatchingTermVisitor mtv) throws IOException {
        Terms terms = MultiFields.getTerms((IndexReader)reader, (String)fieldName);
        if (terms != null) {
            TermsEnum termsEnum = terms.iterator(null);
            boolean skip = false;
            TermsEnum.SeekStatus status = termsEnum.seekCeil(new BytesRef((CharSequence)this.getPrefix()));
            if (status == TermsEnum.SeekStatus.FOUND) {
                mtv.visitMatchingTerm(this.getLucenePrefixTerm(fieldName));
            } else if (status == TermsEnum.SeekStatus.NOT_FOUND) {
                if (StringHelper.startsWith((BytesRef)termsEnum.term(), (BytesRef)this.prefixRef)) {
                    mtv.visitMatchingTerm(new Term(fieldName, termsEnum.term().utf8ToString()));
                } else {
                    skip = true;
                }
            } else {
                skip = true;
            }
            if (!skip) {
                BytesRef text;
                while ((text = termsEnum.next()) != null && StringHelper.startsWith((BytesRef)text, (BytesRef)this.prefixRef)) {
                    mtv.visitMatchingTerm(new Term(fieldName, text.utf8ToString()));
                }
            }
        }
    }
}

