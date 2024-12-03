/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.FilterAtomicReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.IndexWriterConfig$OpenMode
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.TermRangeFilter
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.FixedBitSet
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FilterAtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public class PKIndexSplitter {
    private final Filter docsInFirstIndex;
    private final Directory input;
    private final Directory dir1;
    private final Directory dir2;
    private final IndexWriterConfig config1;
    private final IndexWriterConfig config2;

    public PKIndexSplitter(Version version, Directory input, Directory dir1, Directory dir2, Filter docsInFirstIndex) {
        this(input, dir1, dir2, docsInFirstIndex, PKIndexSplitter.newDefaultConfig(version), PKIndexSplitter.newDefaultConfig(version));
    }

    private static IndexWriterConfig newDefaultConfig(Version version) {
        return new IndexWriterConfig(version, null).setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    }

    public PKIndexSplitter(Directory input, Directory dir1, Directory dir2, Filter docsInFirstIndex, IndexWriterConfig config1, IndexWriterConfig config2) {
        this.input = input;
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.docsInFirstIndex = docsInFirstIndex;
        this.config1 = config1;
        this.config2 = config2;
    }

    public PKIndexSplitter(Version version, Directory input, Directory dir1, Directory dir2, Term midTerm) {
        this(version, input, dir1, dir2, (Filter)new TermRangeFilter(midTerm.field(), null, midTerm.bytes(), true, false));
    }

    public PKIndexSplitter(Directory input, Directory dir1, Directory dir2, Term midTerm, IndexWriterConfig config1, IndexWriterConfig config2) {
        this(input, dir1, dir2, (Filter)new TermRangeFilter(midTerm.field(), null, midTerm.bytes(), true, false), config1, config2);
    }

    public void split() throws IOException {
        block5: {
            DirectoryReader reader;
            block4: {
                boolean success = false;
                reader = DirectoryReader.open((Directory)this.input);
                try {
                    this.createIndex(this.config1, this.dir1, (IndexReader)reader, this.docsInFirstIndex, false);
                    this.createIndex(this.config2, this.dir2, (IndexReader)reader, this.docsInFirstIndex, true);
                    success = true;
                    if (!success) break block4;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close((Closeable[])new Closeable[]{reader});
                    } else {
                        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{reader});
                    }
                    throw throwable;
                }
                IOUtils.close((Closeable[])new Closeable[]{reader});
                break block5;
            }
            IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{reader});
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createIndex(IndexWriterConfig config, Directory target, IndexReader reader, Filter preserveFilter, boolean negateFilter) throws IOException {
        block6: {
            IndexWriter w;
            block5: {
                boolean success = false;
                w = new IndexWriter(target, config);
                try {
                    List leaves = reader.leaves();
                    IndexReader[] subReaders = new IndexReader[leaves.size()];
                    int i = 0;
                    for (AtomicReaderContext ctx : leaves) {
                        subReaders[i++] = new DocumentFilteredAtomicIndexReader(ctx, preserveFilter, negateFilter);
                    }
                    w.addIndexes(subReaders);
                    success = true;
                    if (!success) break block5;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close((Closeable[])new Closeable[]{w});
                    } else {
                        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{w});
                    }
                    throw throwable;
                }
                IOUtils.close((Closeable[])new Closeable[]{w});
                break block6;
            }
            IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{w});
        }
    }

    private static class DocumentFilteredAtomicIndexReader
    extends FilterAtomicReader {
        final Bits liveDocs;
        final int numDocs;

        public DocumentFilteredAtomicIndexReader(AtomicReaderContext context, Filter preserveFilter, boolean negateFilter) throws IOException {
            super(context.reader());
            DocIdSetIterator it;
            int maxDoc = this.in.maxDoc();
            FixedBitSet bits = new FixedBitSet(maxDoc);
            DocIdSet docs = preserveFilter.getDocIdSet(context, null);
            if (docs != null && (it = docs.iterator()) != null) {
                bits.or(it);
            }
            if (negateFilter) {
                bits.flip(0, maxDoc);
            }
            if (this.in.hasDeletions()) {
                Bits oldLiveDocs = this.in.getLiveDocs();
                assert (oldLiveDocs != null);
                DocIdSetIterator it2 = bits.iterator();
                int i = it2.nextDoc();
                while (i < maxDoc) {
                    if (!oldLiveDocs.get(i)) {
                        bits.clear(i);
                    }
                    i = it2.nextDoc();
                }
            }
            this.liveDocs = bits;
            this.numDocs = bits.cardinality();
        }

        public int numDocs() {
            return this.numDocs;
        }

        public Bits getLiveDocs() {
            return this.liveDocs;
        }
    }
}

