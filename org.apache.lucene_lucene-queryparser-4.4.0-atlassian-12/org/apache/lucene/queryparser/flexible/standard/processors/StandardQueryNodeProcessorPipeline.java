/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.processors.NoChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.AllowLeadingWildcardProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.AnalyzerQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BooleanQuery2ModifierNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BooleanSingleChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BoostQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.DefaultPhraseSlopQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.FuzzyQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.LowercaseExpandedTermsQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MatchAllDocsQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MultiFieldQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MultiTermRewriteMethodProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.NumericQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.NumericRangeQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.OpenRangeQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.PhraseSlopQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.RemoveEmptyNonLeafQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.TermRangeQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.WildcardQueryNodeProcessor;

public class StandardQueryNodeProcessorPipeline
extends QueryNodeProcessorPipeline {
    public StandardQueryNodeProcessorPipeline(QueryConfigHandler queryConfig) {
        super(queryConfig);
        this.add(new WildcardQueryNodeProcessor());
        this.add(new MultiFieldQueryNodeProcessor());
        this.add(new FuzzyQueryNodeProcessor());
        this.add(new MatchAllDocsQueryNodeProcessor());
        this.add(new OpenRangeQueryNodeProcessor());
        this.add(new NumericQueryNodeProcessor());
        this.add(new NumericRangeQueryNodeProcessor());
        this.add(new LowercaseExpandedTermsQueryNodeProcessor());
        this.add(new TermRangeQueryNodeProcessor());
        this.add(new AllowLeadingWildcardProcessor());
        this.add(new AnalyzerQueryNodeProcessor());
        this.add(new PhraseSlopQueryNodeProcessor());
        this.add(new BooleanQuery2ModifierNodeProcessor());
        this.add(new NoChildOptimizationQueryNodeProcessor());
        this.add(new RemoveDeletedQueryNodesProcessor());
        this.add(new RemoveEmptyNonLeafQueryNodeProcessor());
        this.add(new BooleanSingleChildOptimizationQueryNodeProcessor());
        this.add(new DefaultPhraseSlopQueryNodeProcessor());
        this.add(new BoostQueryNodeProcessor());
        this.add(new MultiTermRewriteMethodProcessor());
    }
}

