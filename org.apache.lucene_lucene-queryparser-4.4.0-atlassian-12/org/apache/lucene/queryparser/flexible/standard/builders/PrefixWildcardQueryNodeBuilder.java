/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.PrefixQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.PrefixWildcardQueryNode;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixQuery;

public class PrefixWildcardQueryNodeBuilder
implements StandardQueryBuilder {
    public PrefixQuery build(QueryNode queryNode) throws QueryNodeException {
        PrefixWildcardQueryNode wildcardNode = (PrefixWildcardQueryNode)queryNode;
        String text = wildcardNode.getText().subSequence(0, wildcardNode.getText().length() - 1).toString();
        PrefixQuery q = new PrefixQuery(new Term(wildcardNode.getFieldAsString(), text));
        MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}

