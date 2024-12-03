/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 *  org.apache.lucene.analysis.br.BrazilianAnalyzer
 *  org.apache.lucene.analysis.cjk.CJKAnalyzer
 *  org.apache.lucene.analysis.core.StopAnalyzer
 *  org.apache.lucene.analysis.cz.CzechAnalyzer
 *  org.apache.lucene.analysis.de.GermanAnalyzer
 *  org.apache.lucene.analysis.fa.PersianAnalyzer
 *  org.apache.lucene.analysis.fr.FrenchAnalyzer
 *  org.apache.lucene.analysis.ru.RussianAnalyzer
 *  org.apache.lucene.analysis.standard.StandardAnalyzer
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed.ArabicAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed.ChineseAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed.EuropeanAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed.GreekAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed.JapaneseAnalyzer;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class ConfluenceUnstemmedAnalyzer
extends AnalyzerWrapper {
    private static final Analyzer STANDARD_ANALYZER = new StandardAnalyzer(LuceneConstants.LUCENE_VERSION);
    private static final Map<SearchLanguage, Analyzer> ANALYZERS_MAP = ImmutableMap.builder().put((Object)SearchLanguage.ARABIC, (Object)new ArabicAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.BRAZILIAN, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, BrazilianAnalyzer.getDefaultStopSet())).put((Object)SearchLanguage.CHINESE, (Object)new ChineseAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.CJK, (Object)new CJKAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.CUSTOM_JAPANESE, (Object)new JapaneseAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.CZECH, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, CzechAnalyzer.getDefaultStopSet())).put((Object)SearchLanguage.ENGLISH, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, StopAnalyzer.ENGLISH_STOP_WORDS_SET)).put((Object)SearchLanguage.FRENCH, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, FrenchAnalyzer.getDefaultStopSet())).put((Object)SearchLanguage.GERMAN, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, GermanAnalyzer.getDefaultStopSet())).put((Object)SearchLanguage.GREEK, (Object)new GreekAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.PERSIAN, (Object)new PersianAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchLanguage.RUSSIAN, (Object)new EuropeanAnalyzer(LuceneConstants.LUCENE_VERSION, RussianAnalyzer.getDefaultStopSet())).put((Object)SearchLanguage.OTHER, (Object)STANDARD_ANALYZER).build();
    private SettingsManager settingsManager;

    public ConfluenceUnstemmedAnalyzer(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        SearchLanguage indexingLanguage = SearchLanguage.fromString(this.settingsManager.getGlobalSettings().getIndexingLanguage());
        return ANALYZERS_MAP.getOrDefault(indexingLanguage, STANDARD_ANALYZER);
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        return components;
    }
}

