/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PayloadAttribute
 */
package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;

public final class DelimitedPayloadTokenFilter
extends TokenFilter {
    public static final char DEFAULT_DELIMITER = '|';
    private final char delimiter;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PayloadAttribute payAtt = (PayloadAttribute)this.addAttribute(PayloadAttribute.class);
    private final PayloadEncoder encoder;

    public DelimitedPayloadTokenFilter(TokenStream input, char delimiter, PayloadEncoder encoder) {
        super(input);
        this.delimiter = delimiter;
        this.encoder = encoder;
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                if (buffer[i] != this.delimiter) continue;
                this.payAtt.setPayload(this.encoder.encode(buffer, i + 1, length - (i + 1)));
                this.termAtt.setLength(i);
                return true;
            }
            this.payAtt.setPayload(null);
            return true;
        }
        return false;
    }
}

