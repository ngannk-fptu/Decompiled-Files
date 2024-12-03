/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.RegexpQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.RegexpQuery;

public class RegexpQueryNodeBuilder
implements StandardQueryBuilder {
    public RegexpQuery build(QueryNode queryNode) throws QueryNodeException {
        RegexpQueryNode regexpNode = (RegexpQueryNode)queryNode;
        RegexpQuery q = new RegexpQuery(new Term(regexpNode.getFieldAsString(), regexpNode.textToBytesRef()));
        MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            q.setRewriteMethod(method);
        }
        return q;
    }
}

