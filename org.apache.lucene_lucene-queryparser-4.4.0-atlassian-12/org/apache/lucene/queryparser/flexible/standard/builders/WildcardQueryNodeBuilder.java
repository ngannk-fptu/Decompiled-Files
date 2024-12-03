/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.WildcardQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.WildcardQuery;

public class WildcardQueryNodeBuilder
implements StandardQueryBuilder {
    public WildcardQuery build(QueryNode queryNode) throws QueryNodeException {
        WildcardQueryNode wildcardNode = (WildcardQueryNode)queryNode;
        WildcardQuery q = new WildcardQuery(new Term(wildcardNode.getFieldAsString(), wildcardNode.getTextAsString()));
        MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}

