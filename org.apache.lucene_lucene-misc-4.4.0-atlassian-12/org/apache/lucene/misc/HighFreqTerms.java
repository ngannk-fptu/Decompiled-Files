/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 */
package org.apache.lucene.misc;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.misc.TermStatsQueue;
import org.apache.lucene.misc.TotalTermFreqComparatorSortDescending;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class HighFreqTerms {
    public static final int DEFAULTnumTerms = 100;
    public static int numTerms = 100;

    public static void main(String[] args) throws Exception {
        DirectoryReader reader = null;
        FSDirectory dir = null;
        String field = null;
        boolean IncludeTermFreqs = false;
        if (args.length == 0 || args.length > 4) {
            HighFreqTerms.usage();
            System.exit(1);
        }
        if (args.length > 0) {
            dir = FSDirectory.open((File)new File(args[0]));
        }
        for (int i = 1; i < args.length; ++i) {
            if (args[i].equals("-t")) {
                IncludeTermFreqs = true;
                continue;
            }
            try {
                numTerms = Integer.parseInt(args[i]);
                continue;
            }
            catch (NumberFormatException e) {
                field = args[i];
            }
        }
        reader = DirectoryReader.open((Directory)dir);
        TermStats[] terms = HighFreqTerms.getHighFreqTerms((IndexReader)reader, numTerms, field);
        if (!IncludeTermFreqs) {
            for (int i = 0; i < terms.length; ++i) {
                System.out.printf(Locale.getDefault(), "%s:%s %,d \n", terms[i].field, terms[i].termtext.utf8ToString(), terms[i].docFreq);
            }
        } else {
            TermStats[] termsWithTF = HighFreqTerms.sortByTotalTermFreq((IndexReader)reader, terms);
            for (int i = 0; i < termsWithTF.length; ++i) {
                System.out.printf(Locale.getDefault(), "%s:%s \t totalTF = %,d \t doc freq = %,d \n", termsWithTF[i].field, termsWithTF[i].termtext.utf8ToString(), termsWithTF[i].totalTermFreq, termsWithTF[i].docFreq);
            }
        }
        reader.close();
    }

    private static void usage() {
        System.out.println("\n\njava org.apache.lucene.misc.HighFreqTerms <index dir> [-t] [number_terms] [field]\n\t -t: include totalTermFreq\n\n");
    }

    public static TermStats[] getHighFreqTerms(IndexReader reader, int numTerms, String field) throws Exception {
        Fields fields;
        TermStatsQueue tiq = null;
        if (field != null) {
            fields = MultiFields.getFields((IndexReader)reader);
            if (fields == null) {
                throw new RuntimeException("field " + field + " not found");
            }
            Terms terms = fields.terms(field);
            if (terms != null) {
                TermsEnum termsEnum = terms.iterator(null);
                tiq = new TermStatsQueue(numTerms);
                tiq.fill(field, termsEnum);
            }
        } else {
            fields = MultiFields.getFields((IndexReader)reader);
            if (fields == null) {
                throw new RuntimeException("no fields found for this index");
            }
            tiq = new TermStatsQueue(numTerms);
            for (String fieldName : fields) {
                Terms terms = fields.terms(fieldName);
                if (terms == null) continue;
                tiq.fill(fieldName, terms.iterator(null));
            }
        }
        TermStats[] result = new TermStats[tiq.size()];
        int count = tiq.size() - 1;
        while (tiq.size() != 0) {
            result[count] = (TermStats)tiq.pop();
            --count;
        }
        return result;
    }

    public static TermStats[] sortByTotalTermFreq(IndexReader reader, TermStats[] terms) throws Exception {
        TermStats[] ts = new TermStats[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            long totalTF = HighFreqTerms.getTotalTermFreq(reader, new Term(terms[i].field, terms[i].termtext));
            ts[i] = new TermStats(terms[i].field, terms[i].termtext, terms[i].docFreq, totalTF);
        }
        TotalTermFreqComparatorSortDescending c = new TotalTermFreqComparatorSortDescending();
        Arrays.sort(ts, c);
        return ts;
    }

    public static long getTotalTermFreq(IndexReader reader, Term term) throws Exception {
        long totalTF = 0L;
        for (AtomicReaderContext ctx : reader.leaves()) {
            long totTF;
            AtomicReader r = ctx.reader();
            if (!r.hasDeletions() && (totTF = r.totalTermFreq(term)) != -1L) {
                totalTF += totTF;
                continue;
            }
            DocsEnum de = r.termDocsEnum(term);
            if (de == null) continue;
            while (de.nextDoc() != Integer.MAX_VALUE) {
                totalTF += (long)de.freq();
            }
        }
        return totalTF;
    }
}

