/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.document.DateTools$Resolution
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.QueryParserHelper;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.CommonQueryParserConfiguration;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.standard.config.FuzzyConfig;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.processors.StandardQueryNodeProcessorPipeline;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;

public class StandardQueryParser
extends QueryParserHelper
implements CommonQueryParserConfiguration {
    public StandardQueryParser() {
        super(new StandardQueryConfigHandler(), new StandardSyntaxParser(), new StandardQueryNodeProcessorPipeline(null), new StandardQueryTreeBuilder());
        this.setEnablePositionIncrements(true);
    }

    public StandardQueryParser(Analyzer analyzer) {
        this();
        this.setAnalyzer(analyzer);
    }

    public String toString() {
        return "<StandardQueryParser config=\"" + this.getQueryConfigHandler() + "\"/>";
    }

    public Query parse(String query, String defaultField) throws QueryNodeException {
        return (Query)super.parse(query, defaultField);
    }

    public StandardQueryConfigHandler.Operator getDefaultOperator() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
    }

    public void setDefaultOperator(StandardQueryConfigHandler.Operator operator) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR, operator);
    }

    @Override
    public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.LOWERCASE_EXPANDED_TERMS, lowercaseExpandedTerms);
    }

    @Override
    public boolean getLowercaseExpandedTerms() {
        Boolean lowercaseExpandedTerms = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOWERCASE_EXPANDED_TERMS);
        if (lowercaseExpandedTerms == null) {
            return true;
        }
        return lowercaseExpandedTerms;
    }

    @Override
    public void setAllowLeadingWildcard(boolean allowLeadingWildcard) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ALLOW_LEADING_WILDCARD, allowLeadingWildcard);
    }

    @Override
    public void setEnablePositionIncrements(boolean enabled) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS, enabled);
    }

    @Override
    public boolean getEnablePositionIncrements() {
        Boolean enablePositionsIncrements = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS);
        if (enablePositionsIncrements == null) {
            return false;
        }
        return enablePositionsIncrements;
    }

    @Override
    public void setMultiTermRewriteMethod(MultiTermQuery.RewriteMethod method) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD, method);
    }

    @Override
    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD);
    }

    public void setMultiFields(CharSequence[] fields) {
        if (fields == null) {
            fields = new CharSequence[]{};
        }
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS, fields);
    }

    public void getMultiFields(CharSequence[] fields) {
        this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS);
    }

    @Override
    public void setFuzzyPrefixLength(int fuzzyPrefixLength) {
        QueryConfigHandler config = this.getQueryConfigHandler();
        FuzzyConfig fuzzyConfig = config.get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            fuzzyConfig = new FuzzyConfig();
            config.set(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG, fuzzyConfig);
        }
        fuzzyConfig.setPrefixLength(fuzzyPrefixLength);
    }

    public void setNumericConfigMap(Map<String, NumericConfig> numericConfigMap) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP, numericConfigMap);
    }

    public Map<String, NumericConfig> getNumericConfigMap() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP);
    }

    @Override
    public void setLocale(Locale locale) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.LOCALE, locale);
    }

    @Override
    public Locale getLocale() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOCALE);
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE, timeZone);
    }

    @Override
    public TimeZone getTimeZone() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.TIMEZONE);
    }

    @Deprecated
    public void setDefaultPhraseSlop(int defaultPhraseSlop) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP, defaultPhraseSlop);
    }

    @Override
    public void setPhraseSlop(int defaultPhraseSlop) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP, defaultPhraseSlop);
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER, analyzer);
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER);
    }

    @Override
    public boolean getAllowLeadingWildcard() {
        Boolean allowLeadingWildcard = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ALLOW_LEADING_WILDCARD);
        if (allowLeadingWildcard == null) {
            return false;
        }
        return allowLeadingWildcard;
    }

    @Override
    public float getFuzzyMinSim() {
        FuzzyConfig fuzzyConfig = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            return 2.0f;
        }
        return fuzzyConfig.getMinSimilarity();
    }

    @Override
    public int getFuzzyPrefixLength() {
        FuzzyConfig fuzzyConfig = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            return 0;
        }
        return fuzzyConfig.getPrefixLength();
    }

    @Override
    public int getPhraseSlop() {
        Integer phraseSlop = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP);
        if (phraseSlop == null) {
            return 0;
        }
        return phraseSlop;
    }

    @Override
    public void setFuzzyMinSim(float fuzzyMinSim) {
        QueryConfigHandler config = this.getQueryConfigHandler();
        FuzzyConfig fuzzyConfig = config.get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG);
        if (fuzzyConfig == null) {
            fuzzyConfig = new FuzzyConfig();
            config.set(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG, fuzzyConfig);
        }
        fuzzyConfig.setMinSimilarity(fuzzyMinSim);
    }

    public void setFieldsBoost(Map<String, Float> boosts) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP, boosts);
    }

    public Map<String, Float> getFieldsBoost() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP);
    }

    @Override
    public void setDateResolution(DateTools.Resolution dateResolution) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION, dateResolution);
    }

    public DateTools.Resolution getDateResolution() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
    }

    @Deprecated
    public void setDateResolution(Map<CharSequence, DateTools.Resolution> dateRes) {
        this.setDateResolutionMap(dateRes);
    }

    public Map<CharSequence, DateTools.Resolution> getDateResolutionMap() {
        return this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP);
    }

    public void setDateResolutionMap(Map<CharSequence, DateTools.Resolution> dateRes) {
        this.getQueryConfigHandler().set(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP, dateRes);
    }
}

