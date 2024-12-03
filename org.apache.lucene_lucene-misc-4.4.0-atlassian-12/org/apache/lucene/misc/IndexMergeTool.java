/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.IndexWriterConfig$OpenMode
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.misc;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexMergeTool {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: IndexMergeTool <mergedIndex> <index1> <index2> [index3] ...");
            System.exit(1);
        }
        FSDirectory mergedIndex = FSDirectory.open((File)new File(args[0]));
        IndexWriter writer = new IndexWriter((Directory)mergedIndex, new IndexWriterConfig(Version.LUCENE_CURRENT, null).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
        Directory[] indexes = new Directory[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            indexes[i - 1] = FSDirectory.open((File)new File(args[i]));
        }
        System.out.println("Merging...");
        writer.addIndexes(indexes);
        System.out.println("Full merge...");
        writer.forceMerge(1);
        writer.close();
        System.out.println("Done.");
    }
}

