/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.TermQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;

public class PhraseQueryNodeBuilder
implements StandardQueryBuilder {
    public PhraseQuery build(QueryNode queryNode) throws QueryNodeException {
        TokenizedPhraseQueryNode phraseNode = (TokenizedPhraseQueryNode)queryNode;
        PhraseQuery phraseQuery = new PhraseQuery();
        List<QueryNode> children = phraseNode.getChildren();
        if (children != null) {
            for (QueryNode child : children) {
                TermQuery termQuery = (TermQuery)child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                FieldQueryNode termNode = (FieldQueryNode)child;
                phraseQuery.add(termQuery.getTerm(), termNode.getPositionIncrement());
            }
        }
        return phraseQuery;
    }
}

