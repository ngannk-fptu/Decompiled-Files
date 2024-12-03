/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import org.apache.lucene.codecs.compressing.CompressingStoredFieldsFormat;
import org.apache.lucene.codecs.compressing.CompressionMode;

public final class Lucene41StoredFieldsFormat
extends CompressingStoredFieldsFormat {
    public Lucene41StoredFieldsFormat() {
        super("Lucene41StoredFields", CompressionMode.FAST, 16384);
    }
}

