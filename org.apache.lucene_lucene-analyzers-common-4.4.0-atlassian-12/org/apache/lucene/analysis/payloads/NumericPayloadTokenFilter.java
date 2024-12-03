/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PayloadAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.BytesRef;

public class NumericPayloadTokenFilter
extends TokenFilter {
    private String typeMatch;
    private BytesRef thePayload;
    private final PayloadAttribute payloadAtt = (PayloadAttribute)this.addAttribute(PayloadAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public NumericPayloadTokenFilter(TokenStream input, float payload, String typeMatch) {
        super(input);
        if (typeMatch == null) {
            throw new IllegalArgumentException("typeMatch cannot be null");
        }
        this.thePayload = new BytesRef(PayloadHelper.encodeFloat(payload));
        this.typeMatch = typeMatch;
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.typeAtt.type().equals(this.typeMatch)) {
                this.payloadAtt.setPayload(this.thePayload);
            }
            return true;
        }
        return false;
    }
}

