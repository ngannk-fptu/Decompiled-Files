/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public final class QueryParserUtil {
    public static Query parse(String[] queries, String[] fields, Analyzer analyzer) throws QueryNodeException {
        if (queries.length != fields.length) {
            throw new IllegalArgumentException("queries.length != fields.length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            Query q = qp.parse(queries[i], fields[i]);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, BooleanClause.Occur.SHOULD);
        }
        return bQuery;
    }

    public static Query parse(String query, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer) throws QueryNodeException {
        if (fields.length != flags.length) {
            throw new IllegalArgumentException("fields.length != flags.length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            Query q = qp.parse(query, fields[i]);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, flags[i]);
        }
        return bQuery;
    }

    public static Query parse(String[] queries, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer) throws QueryNodeException {
        if (queries.length != fields.length || queries.length != flags.length) {
            throw new IllegalArgumentException("queries, fields, and flags array have have different length");
        }
        BooleanQuery bQuery = new BooleanQuery();
        StandardQueryParser qp = new StandardQueryParser();
        qp.setAnalyzer(analyzer);
        for (int i = 0; i < fields.length; ++i) {
            Query q = qp.parse(queries[i], fields[i]);
            if (q == null || q instanceof BooleanQuery && ((BooleanQuery)q).getClauses().length <= 0) continue;
            bQuery.add(q, flags[i]);
        }
        return bQuery;
    }

    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}

