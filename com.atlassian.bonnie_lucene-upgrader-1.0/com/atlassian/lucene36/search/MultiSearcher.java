/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.FieldDoc;
import com.atlassian.lucene36.search.FieldDocSortedHitQueue;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.HitQueue;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searchable;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.DummyConcurrentLock;
import com.atlassian.lucene36.util.ReaderUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class MultiSearcher
extends Searcher {
    private Searchable[] searchables;
    private int[] starts;
    private int maxDoc = 0;

    public MultiSearcher(Searchable ... searchables) throws IOException {
        this.searchables = searchables;
        this.starts = new int[searchables.length + 1];
        for (int i = 0; i < searchables.length; ++i) {
            this.starts[i] = this.maxDoc;
            this.maxDoc += searchables[i].maxDoc();
        }
        this.starts[searchables.length] = this.maxDoc;
    }

    public Searchable[] getSearchables() {
        return this.searchables;
    }

    protected int[] getStarts() {
        return this.starts;
    }

    @Override
    public void close() throws IOException {
        for (int i = 0; i < this.searchables.length; ++i) {
            this.searchables[i].close();
        }
    }

    @Override
    public int docFreq(Term term) throws IOException {
        int docFreq = 0;
        for (int i = 0; i < this.searchables.length; ++i) {
            docFreq += this.searchables[i].docFreq(term);
        }
        return docFreq;
    }

    @Override
    public Document doc(int n) throws CorruptIndexException, IOException {
        int i = this.subSearcher(n);
        return this.searchables[i].doc(n - this.starts[i]);
    }

    @Override
    public Document doc(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        int i = this.subSearcher(n);
        return this.searchables[i].doc(n - this.starts[i], fieldSelector);
    }

    public int subSearcher(int n) {
        return ReaderUtil.subIndex(n, this.starts);
    }

    public int subDoc(int n) {
        return n - this.starts[this.subSearcher(n)];
    }

    @Override
    public int maxDoc() throws IOException {
        return this.maxDoc;
    }

    @Override
    public TopDocs search(Weight weight, Filter filter, int nDocs) throws IOException {
        nDocs = Math.min(nDocs, this.maxDoc());
        HitQueue hq = new HitQueue(nDocs, false);
        int totalHits = 0;
        for (int i = 0; i < this.searchables.length; ++i) {
            TopDocs docs = new MultiSearcherCallableNoSort(DummyConcurrentLock.INSTANCE, this.searchables[i], weight, filter, nDocs, hq, i, this.starts).call();
            totalHits += docs.totalHits;
        }
        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size() - 1; i >= 0; --i) {
            scoreDocs[i] = (ScoreDoc)hq.pop();
        }
        float maxScore = totalHits == 0 ? Float.NEGATIVE_INFINITY : scoreDocs[0].score;
        return new TopDocs(totalHits, scoreDocs, maxScore);
    }

    @Override
    public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort) throws IOException {
        n = Math.min(n, this.maxDoc());
        FieldDocSortedHitQueue hq = new FieldDocSortedHitQueue(n);
        int totalHits = 0;
        float maxScore = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < this.searchables.length; ++i) {
            TopFieldDocs docs = new MultiSearcherCallableWithSort(DummyConcurrentLock.INSTANCE, this.searchables[i], weight, filter, n, hq, sort, i, this.starts).call();
            totalHits += docs.totalHits;
            maxScore = Math.max(maxScore, docs.getMaxScore());
        }
        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size() - 1; i >= 0; --i) {
            scoreDocs[i] = (ScoreDoc)hq.pop();
        }
        return new TopFieldDocs(totalHits, scoreDocs, hq.getFields(), maxScore);
    }

    @Override
    public void search(Weight weight, Filter filter, final Collector collector) throws IOException {
        for (int i = 0; i < this.searchables.length; ++i) {
            final int start = this.starts[i];
            Collector hc = new Collector(){

                public void setScorer(Scorer scorer) throws IOException {
                    collector.setScorer(scorer);
                }

                public void collect(int doc) throws IOException {
                    collector.collect(doc);
                }

                public void setNextReader(IndexReader reader, int docBase) throws IOException {
                    collector.setNextReader(reader, start + docBase);
                }

                public boolean acceptsDocsOutOfOrder() {
                    return collector.acceptsDocsOutOfOrder();
                }
            };
            this.searchables[i].search(weight, filter, hc);
        }
    }

    @Override
    public Query rewrite(Query original) throws IOException {
        Query[] queries = new Query[this.searchables.length];
        for (int i = 0; i < this.searchables.length; ++i) {
            queries[i] = this.searchables[i].rewrite(original);
        }
        return queries[0].combine(queries);
    }

    @Override
    public Explanation explain(Weight weight, int doc) throws IOException {
        int i = this.subSearcher(doc);
        return this.searchables[i].explain(weight, doc - this.starts[i]);
    }

    @Override
    public Weight createNormalizedWeight(Query original) throws IOException {
        Query rewrittenQuery = this.rewrite(original);
        HashSet<Term> terms = new HashSet<Term>();
        rewrittenQuery.extractTerms(terms);
        Map<Term, Integer> dfMap = this.createDocFrequencyMap(terms);
        int numDocs = this.maxDoc();
        CachedDfSource cacheSim = new CachedDfSource(dfMap, numDocs, this.getSimilarity());
        return cacheSim.createNormalizedWeight(rewrittenQuery);
    }

    Map<Term, Integer> createDocFrequencyMap(Set<Term> terms) throws IOException {
        Term[] allTermsArray = terms.toArray(new Term[terms.size()]);
        int[] aggregatedDfs = new int[allTermsArray.length];
        for (Searchable searchable : this.searchables) {
            int[] dfs = searchable.docFreqs(allTermsArray);
            for (int j = 0; j < aggregatedDfs.length; ++j) {
                int n = j;
                aggregatedDfs[n] = aggregatedDfs[n] + dfs[j];
            }
        }
        HashMap<Term, Integer> dfMap = new HashMap<Term, Integer>();
        for (int i = 0; i < allTermsArray.length; ++i) {
            dfMap.put(allTermsArray[i], aggregatedDfs[i]);
        }
        return dfMap;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class MultiSearcherCallableWithSort
    implements Callable<TopFieldDocs> {
        private final Lock lock;
        private final Searchable searchable;
        private final Weight weight;
        private final Filter filter;
        private final int nDocs;
        private final int i;
        private final FieldDocSortedHitQueue hq;
        private final int[] starts;
        private final Sort sort;

        public MultiSearcherCallableWithSort(Lock lock, Searchable searchable, Weight weight, Filter filter, int nDocs, FieldDocSortedHitQueue hq, Sort sort, int i, int[] starts) {
            this.lock = lock;
            this.searchable = searchable;
            this.weight = weight;
            this.filter = filter;
            this.nDocs = nDocs;
            this.hq = hq;
            this.i = i;
            this.starts = starts;
            this.sort = sort;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopFieldDocs call() throws IOException {
            TopFieldDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs, this.sort);
            for (int j = 0; j < docs.fields.length; ++j) {
                if (docs.fields[j].getType() != 1) continue;
                for (int j2 = 0; j2 < docs.scoreDocs.length; ++j2) {
                    FieldDoc fd = (FieldDoc)docs.scoreDocs[j2];
                    fd.fields[j] = (Integer)fd.fields[j] + this.starts[this.i];
                }
                break;
            }
            this.lock.lock();
            try {
                this.hq.setFields(docs.fields);
                Object var6_6 = null;
                this.lock.unlock();
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                this.lock.unlock();
                throw throwable;
            }
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            for (int j = 0; j < scoreDocs.length; ++j) {
                Object var8_9;
                FieldDoc fieldDoc = (FieldDoc)scoreDocs[j];
                fieldDoc.doc += this.starts[this.i];
                this.lock.lock();
                try {
                    if (fieldDoc == this.hq.insertWithOverflow(fieldDoc)) {
                        var8_9 = null;
                        this.lock.unlock();
                        break;
                    }
                    var8_9 = null;
                    this.lock.unlock();
                    continue;
                }
                catch (Throwable throwable) {
                    var8_9 = null;
                    this.lock.unlock();
                    throw throwable;
                }
            }
            return docs;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class MultiSearcherCallableNoSort
    implements Callable<TopDocs> {
        private final Lock lock;
        private final Searchable searchable;
        private final Weight weight;
        private final Filter filter;
        private final int nDocs;
        private final int i;
        private final HitQueue hq;
        private final int[] starts;

        public MultiSearcherCallableNoSort(Lock lock, Searchable searchable, Weight weight, Filter filter, int nDocs, HitQueue hq, int i, int[] starts) {
            this.lock = lock;
            this.searchable = searchable;
            this.weight = weight;
            this.filter = filter;
            this.nDocs = nDocs;
            this.hq = hq;
            this.i = i;
            this.starts = starts;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopDocs call() throws IOException {
            TopDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs);
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            for (int j = 0; j < scoreDocs.length; ++j) {
                Object var6_5;
                ScoreDoc scoreDoc = scoreDocs[j];
                scoreDoc.doc += this.starts[this.i];
                this.lock.lock();
                try {
                    if (scoreDoc == this.hq.insertWithOverflow(scoreDoc)) {
                        var6_5 = null;
                        this.lock.unlock();
                        break;
                    }
                    var6_5 = null;
                    this.lock.unlock();
                    continue;
                }
                catch (Throwable throwable) {
                    var6_5 = null;
                    this.lock.unlock();
                    throw throwable;
                }
            }
            return docs;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class CachedDfSource
    extends Searcher {
        private final Map<Term, Integer> dfMap;
        private final int maxDoc;

        public CachedDfSource(Map<Term, Integer> dfMap, int maxDoc, Similarity similarity) {
            this.dfMap = dfMap;
            this.maxDoc = maxDoc;
            this.setSimilarity(similarity);
        }

        @Override
        public int docFreq(Term term) {
            int df;
            try {
                df = this.dfMap.get(term);
            }
            catch (NullPointerException e) {
                throw new IllegalArgumentException("df for term " + term.text() + " not available");
            }
            return df;
        }

        @Override
        public int[] docFreqs(Term[] terms) {
            int[] result = new int[terms.length];
            for (int i = 0; i < terms.length; ++i) {
                result[i] = this.docFreq(terms[i]);
            }
            return result;
        }

        @Override
        public int maxDoc() {
            return this.maxDoc;
        }

        @Override
        public Query rewrite(Query query) {
            return query;
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Document doc(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Document doc(int i, FieldSelector fieldSelector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Explanation explain(Weight weight, int doc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void search(Weight weight, Filter filter, Collector results) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TopDocs search(Weight weight, Filter filter, int n) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort) {
            throw new UnsupportedOperationException();
        }
    }
}

