/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.util.BytesRef;

public abstract class AbstractEncoder
implements PayloadEncoder {
    @Override
    public BytesRef encode(char[] buffer) {
        return this.encode(buffer, 0, buffer.length);
    }
}

