/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.InvalidQueryException;
import com.atlassian.confluence.search.v2.QueryFactory;
import com.atlassian.confluence.search.v2.QueryStringParser;
import com.atlassian.confluence.search.v2.SearchQuery;

public class QuerySerializer {
    private final QueryFactory queryFactory;

    public QuerySerializer(QueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public static String queryToString(SearchQuery query) {
        StringBuilder buf = new StringBuilder();
        buf.append("(").append(query.getKey());
        for (Object o : query.getParameters()) {
            if (o instanceof String) {
                String s = (String)o;
                buf.append(" \"").append(QuerySerializer.escape(s)).append("\"");
                continue;
            }
            if (!(o instanceof SearchQuery)) continue;
            SearchQuery subQuery = (SearchQuery)o;
            buf.append(" ").append(QuerySerializer.queryToString(subQuery));
        }
        buf.append(")");
        return buf.toString();
    }

    public SearchQuery stringToQuery(String s) throws InvalidQueryException {
        QueryStringParser parser = new QueryStringParser(this.queryFactory, s);
        return parser.parse();
    }

    protected static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer newBuf = null;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\' || c == '\"') {
                if (newBuf == null) {
                    newBuf = new StringBuffer(s.length() * 2);
                    newBuf.append(s.substring(0, i));
                }
                newBuf.append('\\').append(c);
                continue;
            }
            if (newBuf == null) continue;
            newBuf.append(c);
        }
        return newBuf == null ? s : newBuf.toString();
    }
}

