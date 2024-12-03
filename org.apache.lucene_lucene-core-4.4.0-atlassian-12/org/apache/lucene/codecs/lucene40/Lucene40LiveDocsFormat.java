/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.lucene40.BitVector;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.MutableBits;

public class Lucene40LiveDocsFormat
extends LiveDocsFormat {
    static final String DELETES_EXTENSION = "del";

    @Override
    public MutableBits newLiveDocs(int size) throws IOException {
        BitVector bitVector = new BitVector(size);
        bitVector.invertAll();
        return bitVector;
    }

    @Override
    public MutableBits newLiveDocs(Bits existing) throws IOException {
        BitVector liveDocs = (BitVector)existing;
        return liveDocs.clone();
    }

    @Override
    public Bits readLiveDocs(Directory dir, SegmentInfoPerCommit info, IOContext context) throws IOException {
        String filename = IndexFileNames.fileNameFromGeneration(info.info.name, DELETES_EXTENSION, info.getDelGen());
        BitVector liveDocs = new BitVector(dir, filename, context);
        assert (liveDocs.count() == info.info.getDocCount() - info.getDelCount()) : "liveDocs.count()=" + liveDocs.count() + " info.docCount=" + info.info.getDocCount() + " info.getDelCount()=" + info.getDelCount();
        assert (liveDocs.length() == info.info.getDocCount());
        return liveDocs;
    }

    @Override
    public void writeLiveDocs(MutableBits bits, Directory dir, SegmentInfoPerCommit info, int newDelCount, IOContext context) throws IOException {
        String filename = IndexFileNames.fileNameFromGeneration(info.info.name, DELETES_EXTENSION, info.getNextDelGen());
        BitVector liveDocs = (BitVector)bits;
        assert (liveDocs.count() == info.info.getDocCount() - info.getDelCount() - newDelCount);
        assert (liveDocs.length() == info.info.getDocCount());
        liveDocs.write(dir, filename, context);
    }

    @Override
    public void files(SegmentInfoPerCommit info, Collection<String> files) throws IOException {
        if (info.hasDeletions()) {
            files.add(IndexFileNames.fileNameFromGeneration(info.info.name, DELETES_EXTENSION, info.getDelGen()));
        }
    }
}

