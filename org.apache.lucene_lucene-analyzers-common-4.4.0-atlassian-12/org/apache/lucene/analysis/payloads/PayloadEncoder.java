/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.payloads;

import org.apache.lucene.util.BytesRef;

public interface PayloadEncoder {
    public BytesRef encode(char[] var1);

    public BytesRef encode(char[] var1, int var2, int var3);
}

