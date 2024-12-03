/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.IndexableBinaryStringTools
 */
package org.apache.lucene.collation;

import java.io.IOException;
import java.text.Collator;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.IndexableBinaryStringTools;

@Deprecated
public final class CollationKeyFilter
extends TokenFilter {
    private final Collator collator;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public CollationKeyFilter(TokenStream input, Collator collator) {
        super(input);
        this.collator = (Collator)collator.clone();
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            byte[] collationKey = this.collator.getCollationKey(this.termAtt.toString()).toByteArray();
            int encodedLength = IndexableBinaryStringTools.getEncodedLength((byte[])collationKey, (int)0, (int)collationKey.length);
            this.termAtt.resizeBuffer(encodedLength);
            this.termAtt.setLength(encodedLength);
            IndexableBinaryStringTools.encode((byte[])collationKey, (int)0, (int)collationKey.length, (char[])this.termAtt.buffer(), (int)0, (int)encodedLength);
            return true;
        }
        return false;
    }
}

