/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.util.BytesRef;

public abstract class DocsAndPositionsEnum
extends DocsEnum {
    public static final int FLAG_OFFSETS = 1;
    public static final int FLAG_PAYLOADS = 2;

    protected DocsAndPositionsEnum() {
    }

    public abstract int nextPosition() throws IOException;

    public abstract int startOffset() throws IOException;

    public abstract int endOffset() throws IOException;

    public abstract BytesRef getPayload() throws IOException;
}

