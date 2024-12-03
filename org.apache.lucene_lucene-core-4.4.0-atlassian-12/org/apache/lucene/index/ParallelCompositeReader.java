/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BaseCompositeReader;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ParallelAtomicReader;

public class ParallelCompositeReader
extends BaseCompositeReader<IndexReader> {
    private final boolean closeSubReaders;
    private final Set<IndexReader> completeReaderSet = Collections.newSetFromMap(new IdentityHashMap());

    public ParallelCompositeReader(CompositeReader ... readers) throws IOException {
        this(true, readers);
    }

    public ParallelCompositeReader(boolean closeSubReaders, CompositeReader ... readers) throws IOException {
        this(closeSubReaders, readers, readers);
    }

    public ParallelCompositeReader(boolean closeSubReaders, CompositeReader[] readers, CompositeReader[] storedFieldReaders) throws IOException {
        super(ParallelCompositeReader.prepareSubReaders(readers, storedFieldReaders));
        this.closeSubReaders = closeSubReaders;
        Collections.addAll(this.completeReaderSet, readers);
        Collections.addAll(this.completeReaderSet, storedFieldReaders);
        if (!closeSubReaders) {
            for (IndexReader reader : this.completeReaderSet) {
                reader.incRef();
            }
        }
        this.completeReaderSet.addAll(this.getSequentialSubReaders());
    }

    private static IndexReader[] prepareSubReaders(CompositeReader[] readers, CompositeReader[] storedFieldsReaders) throws IOException {
        if (readers.length == 0) {
            if (storedFieldsReaders.length > 0) {
                throw new IllegalArgumentException("There must be at least one main reader if storedFieldsReaders are used.");
            }
            return new IndexReader[0];
        }
        List<? extends IndexReader> firstSubReaders = readers[0].getSequentialSubReaders();
        int maxDoc = readers[0].maxDoc();
        int noSubs = firstSubReaders.size();
        int[] childMaxDoc = new int[noSubs];
        boolean[] childAtomic = new boolean[noSubs];
        for (int i = 0; i < noSubs; ++i) {
            IndexReader r = firstSubReaders.get(i);
            childMaxDoc[i] = r.maxDoc();
            childAtomic[i] = r instanceof AtomicReader;
        }
        ParallelCompositeReader.validate(readers, maxDoc, childMaxDoc, childAtomic);
        ParallelCompositeReader.validate(storedFieldsReaders, maxDoc, childMaxDoc, childAtomic);
        IndexReader[] subReaders = new IndexReader[noSubs];
        for (int i = 0; i < subReaders.length; ++i) {
            int j;
            IndexReader[] storedSubs;
            if (firstSubReaders.get(i) instanceof AtomicReader) {
                AtomicReader[] atomicSubs = new AtomicReader[readers.length];
                for (int j2 = 0; j2 < readers.length; ++j2) {
                    atomicSubs[j2] = (AtomicReader)readers[j2].getSequentialSubReaders().get(i);
                }
                storedSubs = new AtomicReader[storedFieldsReaders.length];
                for (j = 0; j < storedFieldsReaders.length; ++j) {
                    storedSubs[j] = (AtomicReader)storedFieldsReaders[j].getSequentialSubReaders().get(i);
                }
                subReaders[i] = new ParallelAtomicReader(true, atomicSubs, (AtomicReader[])storedSubs){

                    @Override
                    protected void doClose() {
                    }
                };
                continue;
            }
            assert (firstSubReaders.get(i) instanceof CompositeReader);
            CompositeReader[] compositeSubs = new CompositeReader[readers.length];
            for (int j3 = 0; j3 < readers.length; ++j3) {
                compositeSubs[j3] = (CompositeReader)readers[j3].getSequentialSubReaders().get(i);
            }
            storedSubs = new CompositeReader[storedFieldsReaders.length];
            for (j = 0; j < storedFieldsReaders.length; ++j) {
                storedSubs[j] = (CompositeReader)storedFieldsReaders[j].getSequentialSubReaders().get(i);
            }
            subReaders[i] = new ParallelCompositeReader(true, compositeSubs, (CompositeReader[])storedSubs){

                @Override
                protected void doClose() {
                }
            };
        }
        return subReaders;
    }

    private static void validate(CompositeReader[] readers, int maxDoc, int[] childMaxDoc, boolean[] childAtomic) {
        for (int i = 0; i < readers.length; ++i) {
            CompositeReader reader = readers[i];
            List<? extends IndexReader> subs = reader.getSequentialSubReaders();
            if (reader.maxDoc() != maxDoc) {
                throw new IllegalArgumentException("All readers must have same maxDoc: " + maxDoc + "!=" + reader.maxDoc());
            }
            int noSubs = subs.size();
            if (noSubs != childMaxDoc.length) {
                throw new IllegalArgumentException("All readers must have same number of subReaders");
            }
            for (int subIDX = 0; subIDX < noSubs; ++subIDX) {
                IndexReader r = subs.get(subIDX);
                if (r.maxDoc() != childMaxDoc[subIDX]) {
                    throw new IllegalArgumentException("All readers must have same corresponding subReader maxDoc");
                }
                if (!childAtomic[subIDX] ? r instanceof CompositeReader : r instanceof AtomicReader) continue;
                throw new IllegalArgumentException("All readers must have same corresponding subReader types (atomic or composite)");
            }
        }
    }

    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (IndexReader reader : this.completeReaderSet) {
            try {
                if (this.closeSubReaders) {
                    reader.close();
                    continue;
                }
                reader.decRef();
            }
            catch (IOException e) {
                if (ioe != null) continue;
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }
}

