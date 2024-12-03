/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BaseCompositeReader
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.FilterAtomicReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.IndexWriterConfig$OpenMode
 *  org.apache.lucene.index.MultiReader
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.FixedBitSet
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BaseCompositeReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FilterAtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.Version;

public class MultiPassIndexSplitter {
    public void split(Version version, IndexReader in, Directory[] outputs, boolean seq) throws IOException {
        if (outputs == null || outputs.length < 2) {
            throw new IOException("Invalid number of outputs.");
        }
        if (in == null || in.numDocs() < 2) {
            throw new IOException("Not enough documents for splitting");
        }
        int numParts = outputs.length;
        FakeDeleteIndexReader input = new FakeDeleteIndexReader(in);
        int maxDoc = input.maxDoc();
        int partLen = maxDoc / numParts;
        for (int i = 0; i < numParts; ++i) {
            input.undeleteAll();
            if (seq) {
                int j;
                int lo = partLen * i;
                int hi = lo + partLen;
                for (j = 0; j < lo; ++j) {
                    input.deleteDocument(j);
                }
                if (i < numParts - 1) {
                    for (j = hi; j < maxDoc; ++j) {
                        input.deleteDocument(j);
                    }
                }
            } else {
                for (int j = 0; j < maxDoc; ++j) {
                    if ((j + numParts - i) % numParts == 0) continue;
                    input.deleteDocument(j);
                }
            }
            IndexWriter w = new IndexWriter(outputs[i], new IndexWriterConfig(version, null).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            System.err.println("Writing part " + (i + 1) + " ...");
            List sr = input.getSequentialSubReaders();
            w.addIndexes(sr.toArray(new IndexReader[sr.size()]));
            w.close();
        }
        System.err.println("Done.");
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: MultiPassIndexSplitter -out <outputDir> -num <numParts> [-seq] <inputIndex1> [<inputIndex2 ...]");
            System.err.println("\tinputIndex\tpath to input index, multiple values are ok");
            System.err.println("\t-out ouputDir\tpath to output directory to contain partial indexes");
            System.err.println("\t-num numParts\tnumber of parts to produce");
            System.err.println("\t-seq\tsequential docid-range split (default is round-robin)");
            System.exit(-1);
        }
        ArrayList<DirectoryReader> indexes = new ArrayList<DirectoryReader>();
        String outDir = null;
        int numParts = -1;
        boolean seq = false;
        for (int i = 0; i < args.length; ++i) {
            FSDirectory dir;
            block14: {
                if (args[i].equals("-out")) {
                    outDir = args[++i];
                    continue;
                }
                if (args[i].equals("-num")) {
                    numParts = Integer.parseInt(args[++i]);
                    continue;
                }
                if (args[i].equals("-seq")) {
                    seq = true;
                    continue;
                }
                File file = new File(args[i]);
                if (!file.exists() || !file.isDirectory()) {
                    System.err.println("Invalid input path - skipping: " + file);
                    continue;
                }
                dir = FSDirectory.open((File)new File(args[i]));
                try {
                    if (!DirectoryReader.indexExists((Directory)dir)) {
                        System.err.println("Invalid input index - skipping: " + file);
                    }
                    break block14;
                }
                catch (Exception e) {
                    System.err.println("Invalid input index - skipping: " + file);
                }
                continue;
            }
            indexes.add(DirectoryReader.open((Directory)dir));
        }
        if (outDir == null) {
            throw new Exception("Required argument missing: -out outputDir");
        }
        if (numParts < 2) {
            throw new Exception("Invalid value of required argument: -num numParts");
        }
        if (indexes.size() == 0) {
            throw new Exception("No input indexes to process");
        }
        File out = new File(outDir);
        if (!out.mkdirs()) {
            throw new Exception("Can't create output directory: " + out);
        }
        Directory[] dirs = new Directory[numParts];
        for (int i = 0; i < numParts; ++i) {
            dirs[i] = FSDirectory.open((File)new File(out, "part-" + i));
        }
        MultiPassIndexSplitter splitter = new MultiPassIndexSplitter();
        Object input = indexes.size() == 1 ? (IndexReader)indexes.get(0) : new MultiReader(indexes.toArray(new IndexReader[indexes.size()]));
        splitter.split(Version.LUCENE_CURRENT, (IndexReader)input, dirs, seq);
    }

    private static final class FakeDeleteAtomicIndexReader
    extends FilterAtomicReader {
        FixedBitSet liveDocs;

        public FakeDeleteAtomicIndexReader(AtomicReader reader) {
            super(reader);
            this.undeleteAll();
        }

        public int numDocs() {
            return this.liveDocs.cardinality();
        }

        public void undeleteAll() {
            int maxDoc = this.in.maxDoc();
            this.liveDocs = new FixedBitSet(this.in.maxDoc());
            if (this.in.hasDeletions()) {
                Bits oldLiveDocs = this.in.getLiveDocs();
                assert (oldLiveDocs != null);
                for (int i = 0; i < maxDoc; ++i) {
                    if (!oldLiveDocs.get(i)) continue;
                    this.liveDocs.set(i);
                }
            } else {
                this.liveDocs.set(0, maxDoc);
            }
        }

        public void deleteDocument(int n) {
            this.liveDocs.clear(n);
        }

        public Bits getLiveDocs() {
            return this.liveDocs;
        }
    }

    private static final class FakeDeleteIndexReader
    extends BaseCompositeReader<FakeDeleteAtomicIndexReader> {
        public FakeDeleteIndexReader(IndexReader reader) {
            super((IndexReader[])FakeDeleteIndexReader.initSubReaders(reader));
        }

        private static FakeDeleteAtomicIndexReader[] initSubReaders(IndexReader reader) {
            List leaves = reader.leaves();
            FakeDeleteAtomicIndexReader[] subs = new FakeDeleteAtomicIndexReader[leaves.size()];
            int i = 0;
            for (AtomicReaderContext ctx : leaves) {
                subs[i++] = new FakeDeleteAtomicIndexReader(ctx.reader());
            }
            return subs;
        }

        public void deleteDocument(int docID) {
            int i = this.readerIndex(docID);
            ((FakeDeleteAtomicIndexReader)((Object)this.getSequentialSubReaders().get(i))).deleteDocument(docID - this.readerBase(i));
        }

        public void undeleteAll() {
            for (FakeDeleteAtomicIndexReader r : this.getSequentialSubReaders()) {
                r.undeleteAll();
            }
        }

        protected void doClose() {
        }
    }
}

