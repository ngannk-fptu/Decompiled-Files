/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.queryParser;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.queryParser.ParseException;
import com.atlassian.lucene36.queryParser.QueryParser;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.MultiPhraseQuery;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.util.Version;
import java.util.ArrayList;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiFieldQueryParser
extends QueryParser {
    protected String[] fields;
    protected Map<String, Float> boosts;

    public MultiFieldQueryParser(Version matchVersion, String[] fields, Analyzer analyzer, Map<String, Float> boosts) {
        this(matchVersion, fields, analyzer);
        this.boosts = boosts;
    }

    public MultiFieldQueryParser(Version matchVersion, String[] fields, Analyzer analyzer) {
        super(matchVersion, null, analyzer);
        this.fields = fields;
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                Float boost;
                Query q = super.getFieldQuery(this.fields[i], queryText, true);
                if (q == null) continue;
                if (this.boosts != null && (boost = this.boosts.get(this.fields[i])) != null) {
                    q.setBoost(boost.floatValue());
                }
                this.applySlop(q, slop);
                clauses.add(new BooleanClause(q, BooleanClause.Occur.SHOULD));
            }
            if (clauses.size() == 0) {
                return null;
            }
            return this.getBooleanQuery(clauses, true);
        }
        Query q = super.getFieldQuery(field, queryText, true);
        this.applySlop(q, slop);
        return q;
    }

    private void applySlop(Query q, int slop) {
        if (q instanceof PhraseQuery) {
            ((PhraseQuery)q).setSlop(slop);
        } else if (q instanceof MultiPhraseQuery) {
            ((MultiPhraseQuery)q).setSlop(slop);
        }
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                Float boost;
                Query q = super.getFieldQuery(this.fields[i], queryText, quoted);
                if (q == null) continue;
                if (this.boosts != null && (boost = this.boosts.get(this.fields[i])) != null) {
                    q.setBoost(boost.floatValue());
                }
                clauses.add(new BooleanClause(q, BooleanClause.Occur.SHOULD));
            }
            if (clauses.size() == 0) {
                return null;
            }
            return this.getBooleanQuery(clauses, true);
        }
        Query q = super.getFieldQuery(field, queryText, quoted);
        return q;
    }

    @Override
    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getFuzzyQuery(this.fields[i], termStr, minSimilarity), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getFuzzyQuery(field, termStr, minSimilarity);
    }

    @Override
    protected Query getPrefixQuery(String field, String termStr) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getPrefixQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getPrefixQuery(field, termStr);
    }

    @Override
    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getWildcardQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getWildcardQuery(field, termStr);
    }

    @Override
    protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive) throws ParseException {
        if (field == null) {
            ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getRangeQuery(this.fields[i], part1, part2, inclusive), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getRangeQuery(field, part1, part2, inclusive);
    }

    public static Query parse(Version matchVersion, String[] queries, String[] fields, Analyzer analyzer) throws ParseException {
        if (queries.length != fields.length) {
            throw new IllegalArgumentException("queries.length != fields.length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        for (int i = 0; i < fields.length; ++i) {
            QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
            Query q = qp.parse(queries[i]);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, BooleanClause.Occur.SHOULD);
        }
        return bQuery;
    }

    public static Query parse(Version matchVersion, String query, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer) throws ParseException {
        if (fields.length != flags.length) {
            throw new IllegalArgumentException("fields.length != flags.length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        for (int i = 0; i < fields.length; ++i) {
            QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
            Query q = qp.parse(query);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, flags[i]);
        }
        return bQuery;
    }

    public static Query parse(Version matchVersion, String[] queries, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer) throws ParseException {
        if (queries.length != fields.length || queries.length != flags.length) {
            throw new IllegalArgumentException("queries, fields, and flags array have have different length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        for (int i = 0; i < fields.length; ++i) {
            QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
            Query q = qp.parse(queries[i]);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, flags[i]);
        }
        return bQuery;
    }
}

