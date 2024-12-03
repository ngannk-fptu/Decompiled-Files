/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.BytesRef;

public interface TermToBytesRefAttribute
extends Attribute {
    public int fillBytesRef();

    public BytesRef getBytesRef();
}

