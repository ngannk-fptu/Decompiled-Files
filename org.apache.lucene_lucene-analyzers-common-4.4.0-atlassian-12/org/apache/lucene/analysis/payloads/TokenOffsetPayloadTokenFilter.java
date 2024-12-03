/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PayloadAttribute
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

public class TokenOffsetPayloadTokenFilter
extends TokenFilter {
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PayloadAttribute payAtt = (PayloadAttribute)this.addAttribute(PayloadAttribute.class);

    public TokenOffsetPayloadTokenFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            byte[] data = new byte[8];
            PayloadHelper.encodeInt(this.offsetAtt.startOffset(), data, 0);
            PayloadHelper.encodeInt(this.offsetAtt.endOffset(), data, 4);
            BytesRef payload = new BytesRef(data);
            this.payAtt.setPayload(payload);
            return true;
        }
        return false;
    }
}

