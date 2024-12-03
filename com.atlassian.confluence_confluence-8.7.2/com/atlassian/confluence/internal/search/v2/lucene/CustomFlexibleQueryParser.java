/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.queryparser.flexible.core.config.ConfigurationKey
 *  org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler
 *  org.apache.lucene.queryparser.flexible.core.processors.NoChildOptimizationQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorPipeline
 *  org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor
 *  org.apache.lucene.queryparser.flexible.precedence.processors.BooleanModifiersQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler
 *  org.apache.lucene.queryparser.flexible.standard.processors.AllowLeadingWildcardProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.AnalyzerQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.BooleanQuery2ModifierNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.BooleanSingleChildOptimizationQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.BoostQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.DefaultPhraseSlopQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.FuzzyQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.LowercaseExpandedTermsQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.MatchAllDocsQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.MultiFieldQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.MultiTermRewriteMethodProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.NumericQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.NumericRangeQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.OpenRangeQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.PhraseSlopQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.RemoveEmptyNonLeafQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.TermRangeQueryNodeProcessor
 *  org.apache.lucene.queryparser.flexible.standard.processors.WildcardQueryNodeProcessor
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.config.ConfigurationKey;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.processors.NoChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor;
import org.apache.lucene.queryparser.flexible.precedence.processors.BooleanModifiersQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
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

public class CustomFlexibleQueryParser
extends StandardQueryParser {
    public CustomFlexibleQueryParser(Analyzer analyzer, Analyzer quotedAnalyzer) {
        this.setQueryConfigHandler((QueryConfigHandler)new CustomQueryConfigHandler(quotedAnalyzer));
        this.setQueryNodeProcessor((QueryNodeProcessor)new CustomFlexibleQueryNodeProcessorPipeline(this.getQueryConfigHandler()));
        this.setAnalyzer(analyzer);
        this.setEnablePositionIncrements(true);
    }

    private static final class CustomFlexibleQueryNodeProcessorPipeline
    extends QueryNodeProcessorPipeline {
        private CustomFlexibleQueryNodeProcessorPipeline(QueryConfigHandler queryConfigHandler) {
            super(queryConfigHandler);
            this.add((QueryNodeProcessor)new WildcardQueryNodeProcessor());
            this.add((QueryNodeProcessor)new MultiFieldQueryNodeProcessor());
            this.add((QueryNodeProcessor)new FuzzyQueryNodeProcessor());
            this.add((QueryNodeProcessor)new MatchAllDocsQueryNodeProcessor());
            this.add((QueryNodeProcessor)new OpenRangeQueryNodeProcessor());
            this.add((QueryNodeProcessor)new NumericQueryNodeProcessor());
            this.add((QueryNodeProcessor)new NumericRangeQueryNodeProcessor());
            this.add((QueryNodeProcessor)new LowercaseExpandedTermsQueryNodeProcessor());
            this.add((QueryNodeProcessor)new TermRangeQueryNodeProcessor());
            this.add((QueryNodeProcessor)new AllowLeadingWildcardProcessor());
            this.add((QueryNodeProcessor)new AnalyzerQueryNodeProcessor());
            this.add((QueryNodeProcessor)new PhraseSlopQueryNodeProcessor());
            this.add((QueryNodeProcessor)new BooleanQuery2ModifierNodeProcessor());
            this.add((QueryNodeProcessor)new NoChildOptimizationQueryNodeProcessor());
            this.add((QueryNodeProcessor)new RemoveDeletedQueryNodesProcessor());
            this.add((QueryNodeProcessor)new RemoveEmptyNonLeafQueryNodeProcessor());
            this.add((QueryNodeProcessor)new BooleanSingleChildOptimizationQueryNodeProcessor());
            this.add((QueryNodeProcessor)new DefaultPhraseSlopQueryNodeProcessor());
            this.add((QueryNodeProcessor)new BoostQueryNodeProcessor());
            this.add((QueryNodeProcessor)new MultiTermRewriteMethodProcessor());
            this.add((QueryNodeProcessor)new BooleanModifiersQueryNodeProcessor());
        }
    }

    public static final class CustomQueryConfigHandler
    extends StandardQueryConfigHandler {
        public static final ConfigurationKey<Analyzer> UNSTEMMED_ANALYZER = ConfigurationKey.newInstance();

        private CustomQueryConfigHandler(Analyzer quotedAnalyzer) {
            this.set(UNSTEMMED_ANALYZER, quotedAnalyzer);
        }
    }
}

