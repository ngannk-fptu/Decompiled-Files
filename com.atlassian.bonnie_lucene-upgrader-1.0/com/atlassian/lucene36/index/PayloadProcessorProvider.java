/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.store.Directory;
import java.io.IOException;

public abstract class PayloadProcessorProvider {
    public ReaderPayloadProcessor getReaderProcessor(IndexReader reader) throws IOException {
        return this.getDirProcessor(reader.directory());
    }

    @Deprecated
    public DirPayloadProcessor getDirProcessor(Directory dir) throws IOException {
        throw new UnsupportedOperationException("You must either implement getReaderProcessor() or getDirProcessor().");
    }

    public static abstract class PayloadProcessor {
        public abstract int payloadLength() throws IOException;

        public abstract byte[] processPayload(byte[] var1, int var2, int var3) throws IOException;
    }

    @Deprecated
    public static abstract class DirPayloadProcessor
    extends ReaderPayloadProcessor {
    }

    public static abstract class ReaderPayloadProcessor {
        public abstract PayloadProcessor getProcessor(Term var1) throws IOException;
    }
}

