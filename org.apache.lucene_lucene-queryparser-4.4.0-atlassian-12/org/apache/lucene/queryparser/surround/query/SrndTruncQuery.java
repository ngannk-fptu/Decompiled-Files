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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.surround.query.SimpleTerm;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.StringHelper;

public class SrndTruncQuery
extends SimpleTerm {
    private final String truncated;
    private final char unlimited;
    private final char mask;
    private String prefix;
    private BytesRef prefixRef;
    private Pattern pattern;

    public SrndTruncQuery(String truncated, char unlimited, char mask) {
        super(false);
        this.truncated = truncated;
        this.unlimited = unlimited;
        this.mask = mask;
        this.truncatedToPrefixAndPattern();
    }

    public String getTruncated() {
        return this.truncated;
    }

    @Override
    public String toStringUnquoted() {
        return this.getTruncated();
    }

    protected boolean matchingChar(char c) {
        return c != this.unlimited && c != this.mask;
    }

    protected void appendRegExpForChar(char c, StringBuilder re) {
        if (c == this.unlimited) {
            re.append(".*");
        } else if (c == this.mask) {
            re.append(".");
        } else {
            re.append(c);
        }
    }

    protected void truncatedToPrefixAndPattern() {
        int i;
        for (i = 0; i < this.truncated.length() && this.matchingChar(this.truncated.charAt(i)); ++i) {
        }
        this.prefix = this.truncated.substring(0, i);
        this.prefixRef = new BytesRef((CharSequence)this.prefix);
        StringBuilder re = new StringBuilder();
        while (i < this.truncated.length()) {
            this.appendRegExpForChar(this.truncated.charAt(i), re);
            ++i;
        }
        this.pattern = Pattern.compile(re.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitMatchingTerms(IndexReader reader, String fieldName, SimpleTerm.MatchingTermVisitor mtv) throws IOException {
        int prefixLength = this.prefix.length();
        Terms terms = MultiFields.getTerms((IndexReader)reader, (String)fieldName);
        if (terms != null) {
            Matcher matcher = this.pattern.matcher("");
            try {
                TermsEnum termsEnum = terms.iterator(null);
                TermsEnum.SeekStatus status = termsEnum.seekCeil(this.prefixRef);
                Object text = status == TermsEnum.SeekStatus.FOUND ? this.prefixRef : (status == TermsEnum.SeekStatus.NOT_FOUND ? termsEnum.term() : null);
                while (text != null && text != null && StringHelper.startsWith((BytesRef)text, (BytesRef)this.prefixRef)) {
                    String textString = text.utf8ToString();
                    matcher.reset(textString.substring(prefixLength));
                    if (matcher.matches()) {
                        mtv.visitMatchingTerm(new Term(fieldName, textString));
                    }
                    text = termsEnum.next();
                }
            }
            finally {
                matcher.reset();
            }
        }
    }
}

