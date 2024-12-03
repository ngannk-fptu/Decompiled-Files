/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.ext;

import org.apache.lucene.queryparser.classic.QueryParser;

public class ExtensionQuery {
    private final String field;
    private final String rawQueryString;
    private final QueryParser topLevelParser;

    public ExtensionQuery(QueryParser topLevelParser, String field, String rawQueryString) {
        this.field = field;
        this.rawQueryString = rawQueryString;
        this.topLevelParser = topLevelParser;
    }

    public String getField() {
        return this.field;
    }

    public String getRawQueryString() {
        return this.rawQueryString;
    }

    public QueryParser getTopLevelParser() {
        return this.topLevelParser;
    }
}

