/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import org.apache.lucene.codecs.compressing.CompressingTermVectorsFormat;
import org.apache.lucene.codecs.compressing.CompressionMode;

public final class Lucene42TermVectorsFormat
extends CompressingTermVectorsFormat {
    public Lucene42TermVectorsFormat() {
        super("Lucene41StoredFields", "", CompressionMode.FAST, 4096);
    }
}

