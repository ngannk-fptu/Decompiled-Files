/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.queryparser.surround.query.SpanNearClauseFactory;

public interface DistanceSubQuery {
    public String distanceSubQueryNotAllowed();

    public void addSpanQueries(SpanNearClauseFactory var1) throws IOException;
}

