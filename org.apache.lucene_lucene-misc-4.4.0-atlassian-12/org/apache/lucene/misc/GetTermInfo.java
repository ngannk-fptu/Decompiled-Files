/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 */
package org.apache.lucene.misc;

import java.io.File;
import java.util.Locale;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class GetTermInfo {
    public static void main(String[] args) throws Exception {
        FSDirectory dir = null;
        String inputStr = null;
        String field = null;
        if (args.length == 3) {
            dir = FSDirectory.open((File)new File(args[0]));
            field = args[1];
            inputStr = args[2];
        } else {
            GetTermInfo.usage();
            System.exit(1);
        }
        GetTermInfo.getTermInfo((Directory)dir, new Term(field, inputStr));
    }

    public static void getTermInfo(Directory dir, Term term) throws Exception {
        DirectoryReader reader = DirectoryReader.open((Directory)dir);
        long totalTF = HighFreqTerms.getTotalTermFreq((IndexReader)reader, term);
        System.out.printf(Locale.getDefault(), "%s:%s \t totalTF = %,d \t doc freq = %,d \n", term.field(), term.text(), totalTF, reader.docFreq(term));
    }

    private static void usage() {
        System.out.println("\n\nusage:\n\tjava " + GetTermInfo.class.getName() + " <index dir> field term \n\n");
    }
}

