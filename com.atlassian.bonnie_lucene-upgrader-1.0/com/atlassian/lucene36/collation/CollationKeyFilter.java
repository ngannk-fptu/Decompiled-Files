/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.collation;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.util.IndexableBinaryStringTools;
import java.io.IOException;
import java.text.Collator;

public final class CollationKeyFilter
extends TokenFilter {
    private final Collator collator;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public CollationKeyFilter(TokenStream input, Collator collator) {
        super(input);
        this.collator = (Collator)collator.clone();
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            byte[] collationKey = this.collator.getCollationKey(this.termAtt.toString()).toByteArray();
            int encodedLength = IndexableBinaryStringTools.getEncodedLength(collationKey, 0, collationKey.length);
            this.termAtt.resizeBuffer(encodedLength);
            this.termAtt.setLength(encodedLength);
            IndexableBinaryStringTools.encode(collationKey, 0, collationKey.length, this.termAtt.buffer(), 0, encodedLength);
            return true;
        }
        return false;
    }
}

