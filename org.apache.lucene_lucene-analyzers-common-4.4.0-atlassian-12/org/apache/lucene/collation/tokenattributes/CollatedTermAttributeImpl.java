/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.collation.tokenattributes;

import java.text.Collator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.BytesRef;

public class CollatedTermAttributeImpl
extends CharTermAttributeImpl {
    private final Collator collator;

    public CollatedTermAttributeImpl(Collator collator) {
        this.collator = (Collator)collator.clone();
    }

    public int fillBytesRef() {
        BytesRef bytes = this.getBytesRef();
        bytes.bytes = this.collator.getCollationKey(this.toString()).toByteArray();
        bytes.offset = 0;
        bytes.length = bytes.bytes.length;
        return bytes.hashCode();
    }
}

