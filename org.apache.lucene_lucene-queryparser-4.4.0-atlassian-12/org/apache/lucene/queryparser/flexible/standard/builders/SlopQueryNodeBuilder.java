/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.MultiPhraseQuery
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

public class SlopQueryNodeBuilder
implements StandardQueryBuilder {
    @Override
    public Query build(QueryNode queryNode) throws QueryNodeException {
        SlopQueryNode phraseSlopNode = (SlopQueryNode)queryNode;
        Query query = (Query)phraseSlopNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
        if (query instanceof PhraseQuery) {
            ((PhraseQuery)query).setSlop(phraseSlopNode.getValue());
        } else {
            ((MultiPhraseQuery)query).setSlop(phraseSlopNode.getValue());
        }
        return query;
    }
}

