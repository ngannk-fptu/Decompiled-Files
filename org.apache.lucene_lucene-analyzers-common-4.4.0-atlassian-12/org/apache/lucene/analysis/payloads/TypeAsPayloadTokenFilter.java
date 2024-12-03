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
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.BytesRef;

public class TypeAsPayloadTokenFilter
extends TokenFilter {
    private final PayloadAttribute payloadAtt = (PayloadAttribute)this.addAttribute(PayloadAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public TypeAsPayloadTokenFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            String type = this.typeAtt.type();
            if (type != null && !type.equals("")) {
                this.payloadAtt.setPayload(new BytesRef(type.getBytes("UTF-8")));
            }
            return true;
        }
        return false;
    }
}

