/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.payloads.AbstractEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.util.BytesRef;

public class FloatEncoder
extends AbstractEncoder
implements PayloadEncoder {
    @Override
    public BytesRef encode(char[] buffer, int offset, int length) {
        float payload = Float.parseFloat(new String(buffer, offset, length));
        byte[] bytes = PayloadHelper.encodeFloat(payload);
        BytesRef result = new BytesRef(bytes);
        return result;
    }
}

