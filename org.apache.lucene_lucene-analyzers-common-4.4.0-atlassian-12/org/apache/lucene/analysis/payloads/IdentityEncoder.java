/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.apache.lucene.analysis.payloads.AbstractEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.util.BytesRef;

public class IdentityEncoder
extends AbstractEncoder
implements PayloadEncoder {
    protected Charset charset = Charset.forName("UTF-8");

    public IdentityEncoder() {
    }

    public IdentityEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public BytesRef encode(char[] buffer, int offset, int length) {
        ByteBuffer bb = this.charset.encode(CharBuffer.wrap(buffer, offset, length));
        if (bb.hasArray()) {
            return new BytesRef(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
        }
        byte[] b = new byte[bb.remaining()];
        bb.get(b);
        return new BytesRef(b);
    }
}

