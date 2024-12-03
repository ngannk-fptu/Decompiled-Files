/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchAllDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.BooleanQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.BoostQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.DummyQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.FieldQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.FuzzyQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.GroupQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.MatchAllDocsQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.MatchNoDocsQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.ModifierQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.MultiPhraseQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.NumericRangeQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.PhraseQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.PrefixWildcardQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.RegexpQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.SlopQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardBooleanQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.TermRangeQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.WildcardQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.PrefixWildcardQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.Query;

public class StandardQueryTreeBuilder
extends QueryTreeBuilder
implements StandardQueryBuilder {
    public StandardQueryTreeBuilder() {
        this.setBuilder(GroupQueryNode.class, (QueryBuilder)new GroupQueryNodeBuilder());
        this.setBuilder(FieldQueryNode.class, (QueryBuilder)new FieldQueryNodeBuilder());
        this.setBuilder(BooleanQueryNode.class, (QueryBuilder)new BooleanQueryNodeBuilder());
        this.setBuilder(FuzzyQueryNode.class, (QueryBuilder)new FuzzyQueryNodeBuilder());
        this.setBuilder(NumericQueryNode.class, (QueryBuilder)new DummyQueryNodeBuilder());
        this.setBuilder(NumericRangeQueryNode.class, (QueryBuilder)new NumericRangeQueryNodeBuilder());
        this.setBuilder(BoostQueryNode.class, (QueryBuilder)new BoostQueryNodeBuilder());
        this.setBuilder(ModifierQueryNode.class, (QueryBuilder)new ModifierQueryNodeBuilder());
        this.setBuilder(WildcardQueryNode.class, (QueryBuilder)new WildcardQueryNodeBuilder());
        this.setBuilder(TokenizedPhraseQueryNode.class, (QueryBuilder)new PhraseQueryNodeBuilder());
        this.setBuilder(MatchNoDocsQueryNode.class, (QueryBuilder)new MatchNoDocsQueryNodeBuilder());
        this.setBuilder(PrefixWildcardQueryNode.class, (QueryBuilder)new PrefixWildcardQueryNodeBuilder());
        this.setBuilder(TermRangeQueryNode.class, (QueryBuilder)new TermRangeQueryNodeBuilder());
        this.setBuilder(RegexpQueryNode.class, (QueryBuilder)new RegexpQueryNodeBuilder());
        this.setBuilder(SlopQueryNode.class, (QueryBuilder)new SlopQueryNodeBuilder());
        this.setBuilder(StandardBooleanQueryNode.class, (QueryBuilder)new StandardBooleanQueryNodeBuilder());
        this.setBuilder(MultiPhraseQueryNode.class, (QueryBuilder)new MultiPhraseQueryNodeBuilder());
        this.setBuilder(MatchAllDocsQueryNode.class, (QueryBuilder)new MatchAllDocsQueryNodeBuilder());
    }

    @Override
    public Query build(QueryNode queryNode) throws QueryNodeException {
        return (Query)super.build(queryNode);
    }
}

