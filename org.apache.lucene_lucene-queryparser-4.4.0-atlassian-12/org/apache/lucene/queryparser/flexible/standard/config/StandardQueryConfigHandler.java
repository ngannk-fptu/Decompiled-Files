/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.document.DateTools$Resolution
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryparser.flexible.core.config.ConfigurationKey;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.FieldBoostMapFCListener;
import org.apache.lucene.queryparser.flexible.standard.config.FieldDateResolutionFCListener;
import org.apache.lucene.queryparser.flexible.standard.config.FuzzyConfig;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.config.NumericFieldConfigListener;
import org.apache.lucene.search.MultiTermQuery;

public class StandardQueryConfigHandler
extends QueryConfigHandler {
    public StandardQueryConfigHandler() {
        this.addFieldConfigListener(new FieldBoostMapFCListener(this));
        this.addFieldConfigListener(new FieldDateResolutionFCListener(this));
        this.addFieldConfigListener(new NumericFieldConfigListener(this));
        this.set(ConfigurationKeys.ALLOW_LEADING_WILDCARD, false);
        this.set(ConfigurationKeys.ANALYZER, null);
        this.set(ConfigurationKeys.DEFAULT_OPERATOR, Operator.OR);
        this.set(ConfigurationKeys.PHRASE_SLOP, 0);
        this.set(ConfigurationKeys.LOWERCASE_EXPANDED_TERMS, true);
        this.set(ConfigurationKeys.ENABLE_POSITION_INCREMENTS, false);
        this.set(ConfigurationKeys.FIELD_BOOST_MAP, new LinkedHashMap());
        this.set(ConfigurationKeys.FUZZY_CONFIG, new FuzzyConfig());
        this.set(ConfigurationKeys.LOCALE, Locale.getDefault());
        this.set(ConfigurationKeys.MULTI_TERM_REWRITE_METHOD, MultiTermQuery.CONSTANT_SCORE_AUTO_REWRITE_DEFAULT);
        this.set(ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP, new HashMap());
    }

    public static enum Operator {
        AND,
        OR;

    }

    public static final class ConfigurationKeys {
        public static final ConfigurationKey<Boolean> ENABLE_POSITION_INCREMENTS = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Boolean> LOWERCASE_EXPANDED_TERMS = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Boolean> ALLOW_LEADING_WILDCARD = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Analyzer> ANALYZER = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Operator> DEFAULT_OPERATOR = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Integer> PHRASE_SLOP = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Locale> LOCALE = ConfigurationKey.newInstance();
        public static final ConfigurationKey<TimeZone> TIMEZONE = ConfigurationKey.newInstance();
        public static final ConfigurationKey<MultiTermQuery.RewriteMethod> MULTI_TERM_REWRITE_METHOD = ConfigurationKey.newInstance();
        public static final ConfigurationKey<CharSequence[]> MULTI_FIELDS = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Map<String, Float>> FIELD_BOOST_MAP = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Map<CharSequence, DateTools.Resolution>> FIELD_DATE_RESOLUTION_MAP = ConfigurationKey.newInstance();
        public static final ConfigurationKey<FuzzyConfig> FUZZY_CONFIG = ConfigurationKey.newInstance();
        public static final ConfigurationKey<DateTools.Resolution> DATE_RESOLUTION = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Float> BOOST = ConfigurationKey.newInstance();
        public static final ConfigurationKey<NumericConfig> NUMERIC_CONFIG = ConfigurationKey.newInstance();
        public static final ConfigurationKey<Map<String, NumericConfig>> NUMERIC_CONFIG_MAP = ConfigurationKey.newInstance();
    }
}

