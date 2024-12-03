/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.DisjunctionMaxQuery
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.MultiTermQuery$TopTermsScoringBooleanQueryRewrite
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;

public class FieldQuery {
    final boolean fieldMatch;
    Map<String, QueryPhraseMap> rootMaps = new HashMap<String, QueryPhraseMap>();
    Map<String, Set<String>> termSetMap = new HashMap<String, Set<String>>();
    int termOrPhraseNumber;
    private static final int MAX_MTQ_TERMS = 1024;

    FieldQuery(Query query, IndexReader reader, boolean phraseHighlight, boolean fieldMatch) throws IOException {
        this.fieldMatch = fieldMatch;
        LinkedHashSet<Query> flatQueries = new LinkedHashSet<Query>();
        this.flatten(query, reader, flatQueries);
        this.saveTerms(flatQueries, reader);
        Collection<Query> expandQueries = this.expand(flatQueries);
        for (Query flatQuery : expandQueries) {
            PhraseQuery pq;
            QueryPhraseMap rootMap = this.getRootMap(flatQuery);
            rootMap.add(flatQuery, reader);
            if (phraseHighlight || !(flatQuery instanceof PhraseQuery) || (pq = (PhraseQuery)flatQuery).getTerms().length <= 1) continue;
            for (Term term : pq.getTerms()) {
                rootMap.addTerm(term, flatQuery.getBoost());
            }
        }
    }

    FieldQuery(Query query, boolean phraseHighlight, boolean fieldMatch) throws IOException {
        this(query, null, phraseHighlight, fieldMatch);
    }

    void flatten(Query sourceQuery, IndexReader reader, Collection<Query> flatQueries) throws IOException {
        if (sourceQuery instanceof BooleanQuery) {
            BooleanQuery bq = (BooleanQuery)sourceQuery;
            for (BooleanClause clause : bq.getClauses()) {
                if (clause.isProhibited()) continue;
                this.flatten(clause.getQuery(), reader, flatQueries);
            }
        } else if (sourceQuery instanceof DisjunctionMaxQuery) {
            DisjunctionMaxQuery dmq = (DisjunctionMaxQuery)sourceQuery;
            for (Query query : dmq) {
                this.flatten(query, reader, flatQueries);
            }
        } else if (sourceQuery instanceof TermQuery) {
            if (!flatQueries.contains(sourceQuery)) {
                flatQueries.add(sourceQuery);
            }
        } else if (sourceQuery instanceof PhraseQuery) {
            if (!flatQueries.contains(sourceQuery)) {
                PhraseQuery pq = (PhraseQuery)sourceQuery;
                if (pq.getTerms().length > 1) {
                    flatQueries.add((Query)pq);
                } else if (pq.getTerms().length == 1) {
                    flatQueries.add((Query)new TermQuery(pq.getTerms()[0]));
                }
            }
        } else if (sourceQuery instanceof ConstantScoreQuery) {
            Query q = ((ConstantScoreQuery)sourceQuery).getQuery();
            if (q != null) {
                this.flatten(q, reader, flatQueries);
            }
        } else if (sourceQuery instanceof FilteredQuery) {
            Query q = ((FilteredQuery)sourceQuery).getQuery();
            if (q != null) {
                this.flatten(q, reader, flatQueries);
            }
        } else if (reader != null) {
            Query rewritten;
            Query query = sourceQuery;
            if (sourceQuery instanceof MultiTermQuery) {
                MultiTermQuery copy = (MultiTermQuery)sourceQuery.clone();
                copy.setRewriteMethod((MultiTermQuery.RewriteMethod)new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(1024));
                query = copy;
            }
            if ((rewritten = query.rewrite(reader)) != query) {
                this.flatten(rewritten, reader, flatQueries);
            }
        }
    }

    Collection<Query> expand(Collection<Query> flatQueries) {
        LinkedHashSet<Query> expandQueries = new LinkedHashSet<Query>();
        Iterator<Query> i = flatQueries.iterator();
        while (i.hasNext()) {
            Query query = i.next();
            i.remove();
            expandQueries.add(query);
            if (!(query instanceof PhraseQuery)) continue;
            for (Query qj : flatQueries) {
                if (!(qj instanceof PhraseQuery)) continue;
                this.checkOverlap(expandQueries, (PhraseQuery)query, (PhraseQuery)qj);
            }
        }
        return expandQueries;
    }

    private void checkOverlap(Collection<Query> expandQueries, PhraseQuery a, PhraseQuery b) {
        if (a.getSlop() != b.getSlop()) {
            return;
        }
        Term[] ats = a.getTerms();
        Term[] bts = b.getTerms();
        if (this.fieldMatch && !ats[0].field().equals(bts[0].field())) {
            return;
        }
        this.checkOverlap(expandQueries, ats, bts, a.getSlop(), a.getBoost());
        this.checkOverlap(expandQueries, bts, ats, b.getSlop(), b.getBoost());
    }

    private void checkOverlap(Collection<Query> expandQueries, Term[] src, Term[] dest, int slop, float boost) {
        for (int i = 1; i < src.length; ++i) {
            boolean overlap = true;
            for (int j = i; j < src.length; ++j) {
                if (j - i >= dest.length || src[j].text().equals(dest[j - i].text())) continue;
                overlap = false;
                break;
            }
            if (!overlap || src.length - i >= dest.length) continue;
            PhraseQuery pq = new PhraseQuery();
            for (Term srcTerm : src) {
                pq.add(srcTerm);
            }
            for (int k = src.length - i; k < dest.length; ++k) {
                pq.add(new Term(src[0].field(), dest[k].text()));
            }
            pq.setSlop(slop);
            pq.setBoost(boost);
            if (expandQueries.contains(pq)) continue;
            expandQueries.add((Query)pq);
        }
    }

    QueryPhraseMap getRootMap(Query query) {
        String key = this.getKey(query);
        QueryPhraseMap map = this.rootMaps.get(key);
        if (map == null) {
            map = new QueryPhraseMap(this);
            this.rootMaps.put(key, map);
        }
        return map;
    }

    private String getKey(Query query) {
        if (!this.fieldMatch) {
            return null;
        }
        if (query instanceof TermQuery) {
            return ((TermQuery)query).getTerm().field();
        }
        if (query instanceof PhraseQuery) {
            PhraseQuery pq = (PhraseQuery)query;
            Term[] terms = pq.getTerms();
            return terms[0].field();
        }
        if (query instanceof MultiTermQuery) {
            return ((MultiTermQuery)query).getField();
        }
        throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
    }

    void saveTerms(Collection<Query> flatQueries, IndexReader reader) throws IOException {
        for (Query query : flatQueries) {
            Set<String> termSet = this.getTermSet(query);
            if (query instanceof TermQuery) {
                termSet.add(((TermQuery)query).getTerm().text());
                continue;
            }
            if (query instanceof PhraseQuery) {
                for (Term term : ((PhraseQuery)query).getTerms()) {
                    termSet.add(term.text());
                }
                continue;
            }
            if (query instanceof MultiTermQuery && reader != null) {
                BooleanQuery mtqTerms = (BooleanQuery)query.rewrite(reader);
                for (BooleanClause clause : mtqTerms.getClauses()) {
                    termSet.add(((TermQuery)clause.getQuery()).getTerm().text());
                }
                continue;
            }
            throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
        }
    }

    private Set<String> getTermSet(Query query) {
        String key = this.getKey(query);
        Set<String> set = this.termSetMap.get(key);
        if (set == null) {
            set = new HashSet<String>();
            this.termSetMap.put(key, set);
        }
        return set;
    }

    Set<String> getTermSet(String field) {
        return this.termSetMap.get(this.fieldMatch ? field : null);
    }

    public QueryPhraseMap getFieldTermMap(String fieldName, String term) {
        QueryPhraseMap rootMap = this.getRootMap(fieldName);
        return rootMap == null ? null : rootMap.subMap.get(term);
    }

    public QueryPhraseMap searchPhrase(String fieldName, List<FieldTermStack.TermInfo> phraseCandidate) {
        QueryPhraseMap root = this.getRootMap(fieldName);
        if (root == null) {
            return null;
        }
        return root.searchPhrase(phraseCandidate);
    }

    private QueryPhraseMap getRootMap(String fieldName) {
        return this.rootMaps.get(this.fieldMatch ? fieldName : null);
    }

    int nextTermOrPhraseNumber() {
        return this.termOrPhraseNumber++;
    }

    public static class QueryPhraseMap {
        boolean terminal;
        int slop;
        float boost;
        int termOrPhraseNumber;
        FieldQuery fieldQuery;
        Map<String, QueryPhraseMap> subMap = new HashMap<String, QueryPhraseMap>();

        public QueryPhraseMap(FieldQuery fieldQuery) {
            this.fieldQuery = fieldQuery;
        }

        void addTerm(Term term, float boost) {
            QueryPhraseMap map = this.getOrNewMap(this.subMap, term.text());
            map.markTerminal(boost);
        }

        private QueryPhraseMap getOrNewMap(Map<String, QueryPhraseMap> subMap, String term) {
            QueryPhraseMap map = subMap.get(term);
            if (map == null) {
                map = new QueryPhraseMap(this.fieldQuery);
                subMap.put(term, map);
            }
            return map;
        }

        void add(Query query, IndexReader reader) {
            if (query instanceof TermQuery) {
                this.addTerm(((TermQuery)query).getTerm(), query.getBoost());
            } else if (query instanceof PhraseQuery) {
                PhraseQuery pq = (PhraseQuery)query;
                Term[] terms = pq.getTerms();
                Map<String, QueryPhraseMap> map = this.subMap;
                QueryPhraseMap qpm = null;
                for (Term term : terms) {
                    qpm = this.getOrNewMap(map, term.text());
                    map = qpm.subMap;
                }
                super.markTerminal(pq.getSlop(), pq.getBoost());
            } else {
                throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
            }
        }

        public QueryPhraseMap getTermMap(String term) {
            return this.subMap.get(term);
        }

        private void markTerminal(float boost) {
            this.markTerminal(0, boost);
        }

        private void markTerminal(int slop, float boost) {
            this.terminal = true;
            this.slop = slop;
            this.boost = boost;
            this.termOrPhraseNumber = this.fieldQuery.nextTermOrPhraseNumber();
        }

        public boolean isTerminal() {
            return this.terminal;
        }

        public int getSlop() {
            return this.slop;
        }

        public float getBoost() {
            return this.boost;
        }

        public int getTermOrPhraseNumber() {
            return this.termOrPhraseNumber;
        }

        public QueryPhraseMap searchPhrase(List<FieldTermStack.TermInfo> phraseCandidate) {
            QueryPhraseMap currMap = this;
            for (FieldTermStack.TermInfo ti : phraseCandidate) {
                currMap = currMap.subMap.get(ti.getText());
                if (currMap != null) continue;
                return null;
            }
            return currMap.isValidTermOrPhrase(phraseCandidate) ? currMap : null;
        }

        public boolean isValidTermOrPhrase(List<FieldTermStack.TermInfo> phraseCandidate) {
            if (!this.terminal) {
                return false;
            }
            if (phraseCandidate.size() == 1) {
                return true;
            }
            int pos = phraseCandidate.get(0).getPosition();
            for (int i = 1; i < phraseCandidate.size(); ++i) {
                int nextPos = phraseCandidate.get(i).getPosition();
                if (Math.abs(nextPos - pos - 1) > this.slop) {
                    return false;
                }
                pos = nextPos;
            }
            return true;
        }
    }
}

