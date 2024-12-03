/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.util.Bits;

public abstract class StoredFieldsWriter
implements Closeable {
    protected StoredFieldsWriter() {
    }

    public abstract void startDocument(int var1) throws IOException;

    public void finishDocument() throws IOException {
    }

    public abstract void writeField(FieldInfo var1, IndexableField var2) throws IOException;

    public abstract void abort();

    public abstract void finish(FieldInfos var1, int var2) throws IOException;

    public int merge(MergeState mergeState) throws IOException {
        int docCount = 0;
        for (AtomicReader reader : mergeState.readers) {
            int maxDoc = reader.maxDoc();
            Bits liveDocs = reader.getLiveDocs();
            for (int i = 0; i < maxDoc; ++i) {
                if (liveDocs != null && !liveDocs.get(i)) continue;
                Document doc = reader.document(i);
                this.addDocument(doc, mergeState.fieldInfos);
                ++docCount;
                mergeState.checkAbort.work(300.0);
            }
        }
        this.finish(mergeState.fieldInfos, docCount);
        return docCount;
    }

    protected final void addDocument(Iterable<? extends IndexableField> doc, FieldInfos fieldInfos) throws IOException {
        int storedCount = 0;
        for (IndexableField indexableField : doc) {
            if (!indexableField.fieldType().stored()) continue;
            ++storedCount;
        }
        this.startDocument(storedCount);
        for (IndexableField indexableField : doc) {
            if (!indexableField.fieldType().stored()) continue;
            this.writeField(fieldInfos.fieldInfo(indexableField.name()), indexableField);
        }
        this.finishDocument();
    }

    @Override
    public abstract void close() throws IOException;
}

