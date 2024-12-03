/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermDocs;
import java.io.IOException;

public interface TermPositions
extends TermDocs {
    public int nextPosition() throws IOException;

    public int getPayloadLength();

    public byte[] getPayload(byte[] var1, int var2) throws IOException;

    public boolean isPayloadAvailable();
}

