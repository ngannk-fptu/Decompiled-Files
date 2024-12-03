/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import java.util.function.BiFunction;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

@Deprecated
@Internal
public class LuceneQueryBuilders {
    public static BiFunction<String, String, Query> termQuery() {
        return (fieldName, value) -> new TermQuery(new Term(fieldName, value));
    }

    public static com.atlassian.confluence.search.v2.BooleanQueryBuilder<Query> boolQuery() {
        return new BooleanQueryBuilder();
    }

    public static class BooleanQueryBuilder
    extends com.atlassian.confluence.search.v2.BooleanQueryBuilder<Query> {
        @Override
        public Query build() {
            if (this.must.isEmpty() && this.should.isEmpty() && this.mustNot.isEmpty()) {
                throw new IllegalArgumentException("At least one must or should or not query parameter needs to be supplied.");
            }
            if (this.must.size() == 1 && this.should.isEmpty() && this.mustNot.isEmpty() && this.boost == 1.0f) {
                return (Query)this.must.iterator().next();
            }
            if (this.must.isEmpty() && this.should.size() == 1 && this.mustNot.isEmpty() && this.boost == 1.0f) {
                return (Query)this.should.iterator().next();
            }
            BooleanQuery result = new BooleanQuery();
            result.setBoost(this.boost);
            this.must.forEach(x -> result.add(x, BooleanClause.Occur.MUST));
            this.should.forEach(x -> result.add(x, BooleanClause.Occur.SHOULD));
            this.mustNot.forEach(x -> result.add(x, BooleanClause.Occur.MUST_NOT));
            return result;
        }
    }
}

