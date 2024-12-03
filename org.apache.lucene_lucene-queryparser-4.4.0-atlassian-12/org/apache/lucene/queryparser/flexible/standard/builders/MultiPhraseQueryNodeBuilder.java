/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.MultiPhraseQuery
 *  org.apache.lucene.search.TermQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.TermQuery;

public class MultiPhraseQueryNodeBuilder
implements StandardQueryBuilder {
    public MultiPhraseQuery build(QueryNode queryNode) throws QueryNodeException {
        MultiPhraseQueryNode phraseNode = (MultiPhraseQueryNode)queryNode;
        MultiPhraseQuery phraseQuery = new MultiPhraseQuery();
        List<QueryNode> children = phraseNode.getChildren();
        if (children != null) {
            TreeMap<Integer, LinkedList<Term>> positionTermMap = new TreeMap<Integer, LinkedList<Term>>();
            for (QueryNode child : children) {
                FieldQueryNode termNode = (FieldQueryNode)child;
                TermQuery termQuery = (TermQuery)termNode.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                LinkedList<Term> termList = (LinkedList<Term>)positionTermMap.get(termNode.getPositionIncrement());
                if (termList == null) {
                    termList = new LinkedList<Term>();
                    positionTermMap.put(termNode.getPositionIncrement(), termList);
                }
                termList.add(termQuery.getTerm());
            }
            Iterator<QueryNode> iterator = positionTermMap.keySet().iterator();
            while (iterator.hasNext()) {
                int positionIncrement = (Integer)((Object)iterator.next());
                List termList = (List)positionTermMap.get(positionIncrement);
                phraseQuery.add(termList.toArray(new Term[termList.size()]), positionIncrement);
            }
        }
        return phraseQuery;
    }
}

