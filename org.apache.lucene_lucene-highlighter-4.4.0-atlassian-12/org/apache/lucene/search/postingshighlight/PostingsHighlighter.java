/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.FieldInfo
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.MultiReader
 *  org.apache.lucene.index.ReaderUtil
 *  org.apache.lucene.index.StoredFieldVisitor
 *  org.apache.lucene.index.StoredFieldVisitor$Status
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.ScoreDoc
 *  org.apache.lucene.search.TopDocs
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.InPlaceMergeSorter
 *  org.apache.lucene.util.UnicodeUtil
 */
package org.apache.lucene.search.postingshighlight;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.postingshighlight.DefaultPassageFormatter;
import org.apache.lucene.search.postingshighlight.Passage;
import org.apache.lucene.search.postingshighlight.PassageFormatter;
import org.apache.lucene.search.postingshighlight.PassageScorer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.UnicodeUtil;

public class PostingsHighlighter {
    private static final IndexReader EMPTY_INDEXREADER = new MultiReader(new IndexReader[0]);
    public static final int DEFAULT_MAX_LENGTH = 10000;
    private final int maxLength;
    private PassageFormatter defaultFormatter;
    private PassageScorer defaultScorer;
    private static final DocsAndPositionsEnum EMPTY = new DocsAndPositionsEnum(){

        public int nextPosition() throws IOException {
            return 0;
        }

        public int startOffset() throws IOException {
            return Integer.MAX_VALUE;
        }

        public int endOffset() throws IOException {
            return Integer.MAX_VALUE;
        }

        public BytesRef getPayload() throws IOException {
            return null;
        }

        public int freq() throws IOException {
            return 0;
        }

        public int docID() {
            return Integer.MAX_VALUE;
        }

        public int nextDoc() throws IOException {
            return Integer.MAX_VALUE;
        }

        public int advance(int target) throws IOException {
            return Integer.MAX_VALUE;
        }

        public long cost() {
            return 0L;
        }
    };

    public PostingsHighlighter() {
        this(10000);
    }

    public PostingsHighlighter(int maxLength) {
        if (maxLength < 0 || maxLength == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxLength must be < Integer.MAX_VALUE");
        }
        this.maxLength = maxLength;
    }

    protected BreakIterator getBreakIterator(String field) {
        return BreakIterator.getSentenceInstance(Locale.ROOT);
    }

    protected PassageFormatter getFormatter(String field) {
        if (this.defaultFormatter == null) {
            this.defaultFormatter = new DefaultPassageFormatter();
        }
        return this.defaultFormatter;
    }

    protected PassageScorer getScorer(String field) {
        if (this.defaultScorer == null) {
            this.defaultScorer = new PassageScorer();
        }
        return this.defaultScorer;
    }

    public String[] highlight(String field, Query query, IndexSearcher searcher, TopDocs topDocs) throws IOException {
        return this.highlight(field, query, searcher, topDocs, 1);
    }

    public String[] highlight(String field, Query query, IndexSearcher searcher, TopDocs topDocs, int maxPassages) throws IOException {
        Map<String, String[]> res = this.highlightFields(new String[]{field}, query, searcher, topDocs, new int[]{maxPassages});
        return res.get(field);
    }

    public Map<String, String[]> highlightFields(String[] fields, Query query, IndexSearcher searcher, TopDocs topDocs) throws IOException {
        int[] maxPassages = new int[fields.length];
        Arrays.fill(maxPassages, 1);
        return this.highlightFields(fields, query, searcher, topDocs, maxPassages);
    }

    public Map<String, String[]> highlightFields(String[] fields, Query query, IndexSearcher searcher, TopDocs topDocs, int[] maxPassages) throws IOException {
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        int[] docids = new int[scoreDocs.length];
        for (int i = 0; i < docids.length; ++i) {
            docids[i] = scoreDocs[i].doc;
        }
        return this.highlightFields(fields, query, searcher, docids, maxPassages);
    }

    public Map<String, String[]> highlightFields(String[] fieldsIn, Query query, IndexSearcher searcher, int[] docidsIn, int[] maxPassagesIn) throws IOException {
        if (fieldsIn.length < 1) {
            throw new IllegalArgumentException("fieldsIn must not be empty");
        }
        if (fieldsIn.length != maxPassagesIn.length) {
            throw new IllegalArgumentException("invalid number of maxPassagesIn");
        }
        IndexReader reader = searcher.getIndexReader();
        query = PostingsHighlighter.rewrite(query);
        TreeSet<Term> queryTerms = new TreeSet<Term>();
        query.extractTerms(queryTerms);
        IndexReaderContext readerContext = reader.getContext();
        List leaves = readerContext.leaves();
        int[] docids = new int[docidsIn.length];
        System.arraycopy(docidsIn, 0, docids, 0, docidsIn.length);
        final String[] fields = new String[fieldsIn.length];
        System.arraycopy(fieldsIn, 0, fields, 0, fieldsIn.length);
        final int[] maxPassages = new int[maxPassagesIn.length];
        System.arraycopy(maxPassagesIn, 0, maxPassages, 0, maxPassagesIn.length);
        Arrays.sort(docids);
        new InPlaceMergeSorter(){

            protected void swap(int i, int j) {
                String tmp = fields[i];
                fields[i] = fields[j];
                fields[j] = tmp;
                int tmp2 = maxPassages[i];
                maxPassages[i] = maxPassages[j];
                maxPassages[j] = tmp2;
            }

            protected int compare(int i, int j) {
                return fields[i].compareTo(fields[j]);
            }
        }.sort(0, fields.length);
        String[][] contents = this.loadFieldValues(searcher, fields, docids, this.maxLength);
        HashMap<String, String[]> highlights = new HashMap<String, String[]>();
        for (int i = 0; i < fields.length; ++i) {
            String field = fields[i];
            int numPassages = maxPassages[i];
            Term floor = new Term(field, "");
            Term ceiling = new Term(field, UnicodeUtil.BIG_TERM);
            SortedSet<Term> fieldTerms = queryTerms.subSet(floor, ceiling);
            BytesRef[] terms = new BytesRef[fieldTerms.size()];
            int termUpto = 0;
            for (Term term : fieldTerms) {
                terms[termUpto++] = term.bytes();
            }
            Map<Integer, String> fieldHighlights = this.highlightField(field, contents[i], this.getBreakIterator(field), terms, docids, leaves, numPassages);
            String[] result = new String[docids.length];
            for (int j = 0; j < docidsIn.length; ++j) {
                result[j] = fieldHighlights.get(docidsIn[j]);
            }
            highlights.put(field, result);
        }
        return highlights;
    }

    protected String[][] loadFieldValues(IndexSearcher searcher, String[] fields, int[] docids, int maxLength) throws IOException {
        String[][] contents = new String[fields.length][docids.length];
        char[] valueSeparators = new char[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            valueSeparators[i] = this.getMultiValuedSeparator(fields[i]);
        }
        LimitedStoredFieldVisitor visitor = new LimitedStoredFieldVisitor(fields, valueSeparators, maxLength);
        for (int i = 0; i < docids.length; ++i) {
            searcher.doc(docids[i], (StoredFieldVisitor)visitor);
            for (int j = 0; j < fields.length; ++j) {
                contents[j][i] = visitor.getValue(j).toString();
            }
            visitor.reset();
        }
        return contents;
    }

    protected char getMultiValuedSeparator(String field) {
        return ' ';
    }

    private Map<Integer, String> highlightField(String field, String[] contents, BreakIterator bi, BytesRef[] terms, int[] docids, List<AtomicReaderContext> leaves, int maxPassages) throws IOException {
        HashMap<Integer, String> highlights = new HashMap<Integer, String>();
        DocsAndPositionsEnum[] postings = null;
        TermsEnum termsEnum = null;
        int lastLeaf = -1;
        PassageFormatter fieldFormatter = this.getFormatter(field);
        if (fieldFormatter == null) {
            throw new NullPointerException("PassageFormatter cannot be null");
        }
        for (int i = 0; i < docids.length; ++i) {
            Passage[] passages;
            String content = contents[i];
            if (content.length() == 0) continue;
            bi.setText(content);
            int doc = docids[i];
            int leaf = ReaderUtil.subIndex((int)doc, leaves);
            AtomicReaderContext subContext = leaves.get(leaf);
            AtomicReader r = subContext.reader();
            Terms t = r.terms(field);
            if (t == null) continue;
            if (leaf != lastLeaf) {
                termsEnum = t.iterator(null);
                postings = new DocsAndPositionsEnum[terms.length];
            }
            if ((passages = this.highlightDoc(field, terms, content.length(), bi, doc - subContext.docBase, termsEnum, postings, maxPassages)).length == 0) {
                passages = this.getEmptyHighlight(field, bi, maxPassages);
            }
            if (passages.length > 0) {
                highlights.put(doc, fieldFormatter.format(passages, content));
            }
            lastLeaf = leaf;
        }
        return highlights;
    }

    private Passage[] highlightDoc(String field, BytesRef[] terms, int contentLength, BreakIterator bi, int doc, TermsEnum termsEnum, DocsAndPositionsEnum[] postings, int n) throws IOException {
        OffsetsEnum off;
        PassageScorer scorer = this.getScorer(field);
        if (scorer == null) {
            throw new NullPointerException("PassageScorer cannot be null");
        }
        PriorityQueue<OffsetsEnum> pq = new PriorityQueue<OffsetsEnum>();
        float[] weights = new float[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            int pDoc;
            DocsAndPositionsEnum de = postings[i];
            if (de == EMPTY) continue;
            if (de == null) {
                postings[i] = EMPTY;
                if (!termsEnum.seekExact(terms[i], true)) continue;
                postings[i] = termsEnum.docsAndPositions(null, null, 1);
                de = postings[i];
                if (de == null) {
                    throw new IllegalArgumentException("field '" + field + "' was indexed without offsets, cannot highlight");
                }
                pDoc = de.advance(doc);
            } else {
                pDoc = de.docID();
                if (pDoc < doc) {
                    pDoc = de.advance(doc);
                }
            }
            if (doc != pDoc) continue;
            weights[i] = scorer.weight(contentLength, de.freq());
            de.nextPosition();
            pq.add(new OffsetsEnum(de, i));
        }
        pq.add(new OffsetsEnum(EMPTY, Integer.MAX_VALUE));
        PriorityQueue<Passage> passageQueue = new PriorityQueue<Passage>(n, new Comparator<Passage>(){

            @Override
            public int compare(Passage left, Passage right) {
                if (left.score < right.score) {
                    return -1;
                }
                if (left.score > right.score) {
                    return 1;
                }
                return left.startOffset - right.startOffset;
            }
        });
        Passage current = new Passage();
        while ((off = (OffsetsEnum)pq.poll()) != null) {
            int tf;
            block18: {
                DocsAndPositionsEnum dp = off.dp;
                int start = dp.startOffset();
                if (start == -1) {
                    throw new IllegalArgumentException("field '" + field + "' was indexed without offsets, cannot highlight");
                }
                int end = dp.endOffset();
                if (start >= current.endOffset) {
                    if (current.startOffset >= 0) {
                        current.score *= scorer.norm(current.startOffset);
                        if (passageQueue.size() == n && current.score < passageQueue.peek().score) {
                            current.reset();
                        } else {
                            passageQueue.offer(current);
                            if (passageQueue.size() > n) {
                                current = passageQueue.poll();
                                current.reset();
                            } else {
                                current = new Passage();
                            }
                        }
                    }
                    if (start >= contentLength) {
                        Passage[] passages = new Passage[passageQueue.size()];
                        passageQueue.toArray(passages);
                        for (Passage p : passages) {
                            p.sort();
                        }
                        Arrays.sort(passages, new Comparator<Passage>(){

                            @Override
                            public int compare(Passage left, Passage right) {
                                return left.startOffset - right.startOffset;
                            }
                        });
                        return passages;
                    }
                    current.startOffset = Math.max(bi.preceding(start + 1), 0);
                    current.endOffset = Math.min(bi.next(), contentLength);
                }
                tf = 0;
                do {
                    ++tf;
                    current.addMatch(start, end, terms[off.id]);
                    if (off.pos == dp.freq()) break block18;
                    ++off.pos;
                    dp.nextPosition();
                    start = dp.startOffset();
                    end = dp.endOffset();
                } while (start < current.endOffset);
                pq.offer(off);
            }
            current.score += weights[off.id] * scorer.tf(tf, current.endOffset - current.startOffset);
        }
        assert (false);
        return null;
    }

    protected Passage[] getEmptyHighlight(String fieldName, BreakIterator bi, int maxPassages) {
        int next;
        ArrayList<Passage> passages = new ArrayList<Passage>();
        int pos = bi.current();
        assert (pos == 0);
        while (passages.size() < maxPassages && (next = bi.next()) != -1) {
            Passage passage = new Passage();
            passage.score = Float.NaN;
            passage.startOffset = pos;
            passage.endOffset = next;
            passages.add(passage);
            pos = next;
        }
        return passages.toArray(new Passage[passages.size()]);
    }

    private static Query rewrite(Query original) throws IOException {
        Query query = original;
        Query rewrittenQuery = query.rewrite(EMPTY_INDEXREADER);
        while (rewrittenQuery != query) {
            query = rewrittenQuery;
            rewrittenQuery = query.rewrite(EMPTY_INDEXREADER);
        }
        return query;
    }

    private static class LimitedStoredFieldVisitor
    extends StoredFieldVisitor {
        private final String[] fields;
        private final char[] valueSeparators;
        private final int maxLength;
        private final StringBuilder[] builders;
        private int currentField = -1;

        public LimitedStoredFieldVisitor(String[] fields, char[] valueSeparators, int maxLength) {
            assert (fields.length == valueSeparators.length);
            this.fields = fields;
            this.valueSeparators = valueSeparators;
            this.maxLength = maxLength;
            this.builders = new StringBuilder[fields.length];
            for (int i = 0; i < this.builders.length; ++i) {
                this.builders[i] = new StringBuilder();
            }
        }

        public void stringField(FieldInfo fieldInfo, String value) throws IOException {
            assert (this.currentField >= 0);
            StringBuilder builder = this.builders[this.currentField];
            if (builder.length() > 0 && builder.length() < this.maxLength) {
                builder.append(this.valueSeparators[this.currentField]);
            }
            if (builder.length() + value.length() > this.maxLength) {
                builder.append(value, 0, this.maxLength - builder.length());
            } else {
                builder.append(value);
            }
        }

        public StoredFieldVisitor.Status needsField(FieldInfo fieldInfo) throws IOException {
            this.currentField = Arrays.binarySearch(this.fields, fieldInfo.name);
            if (this.currentField < 0) {
                return StoredFieldVisitor.Status.NO;
            }
            if (this.builders[this.currentField].length() > this.maxLength) {
                return this.fields.length == 1 ? StoredFieldVisitor.Status.STOP : StoredFieldVisitor.Status.NO;
            }
            return StoredFieldVisitor.Status.YES;
        }

        String getValue(int i) {
            return this.builders[i].toString();
        }

        void reset() {
            this.currentField = -1;
            for (int i = 0; i < this.fields.length; ++i) {
                this.builders[i].setLength(0);
            }
        }
    }

    private static class OffsetsEnum
    implements Comparable<OffsetsEnum> {
        DocsAndPositionsEnum dp;
        int pos;
        int id;

        OffsetsEnum(DocsAndPositionsEnum dp, int id) throws IOException {
            this.dp = dp;
            this.id = id;
            this.pos = 1;
        }

        @Override
        public int compareTo(OffsetsEnum other) {
            try {
                int off = this.dp.startOffset();
                int otherOff = other.dp.startOffset();
                if (off == otherOff) {
                    return this.id - other.id;
                }
                return Long.signum((long)off - (long)otherOff);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

