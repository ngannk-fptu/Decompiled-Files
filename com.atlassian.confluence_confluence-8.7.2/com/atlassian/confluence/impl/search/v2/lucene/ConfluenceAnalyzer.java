/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 *  org.apache.lucene.analysis.ar.ArabicAnalyzer
 *  org.apache.lucene.analysis.br.BrazilianAnalyzer
 *  org.apache.lucene.analysis.cjk.CJKAnalyzer
 *  org.apache.lucene.analysis.cz.CzechAnalyzer
 *  org.apache.lucene.analysis.da.DanishAnalyzer
 *  org.apache.lucene.analysis.de.GermanAnalyzer
 *  org.apache.lucene.analysis.el.GreekAnalyzer
 *  org.apache.lucene.analysis.es.SpanishAnalyzer
 *  org.apache.lucene.analysis.fa.PersianAnalyzer
 *  org.apache.lucene.analysis.fi.FinnishAnalyzer
 *  org.apache.lucene.analysis.fr.FrenchAnalyzer
 *  org.apache.lucene.analysis.hu.HungarianAnalyzer
 *  org.apache.lucene.analysis.it.ItalianAnalyzer
 *  org.apache.lucene.analysis.ja.JapaneseAnalyzer
 *  org.apache.lucene.analysis.ja.JapaneseTokenizer
 *  org.apache.lucene.analysis.nl.DutchAnalyzer
 *  org.apache.lucene.analysis.no.NorwegianAnalyzer
 *  org.apache.lucene.analysis.pl.PolishAnalyzer
 *  org.apache.lucene.analysis.ro.RomanianAnalyzer
 *  org.apache.lucene.analysis.ru.RussianAnalyzer
 *  org.apache.lucene.analysis.standard.StandardAnalyzer
 *  org.apache.lucene.analysis.sv.SwedishAnalyzer
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.EnglishAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.UserDictionaryFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.setup.settings.SettingsManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.ASCIILanguageAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;

public class ConfluenceAnalyzer
extends AnalyzerWrapper {
    private SettingsManager settingsManager;
    private final Map<SearchLanguage, Analyzer> analyzersMap;
    private static final Analyzer STANDARD_ANALYZER = new StandardAnalyzer(LuceneConstants.LUCENE_VERSION);

    public ConfluenceAnalyzer(SettingsManager settingsManager, UserDictionaryFactory userDictionaryFactory) {
        this.settingsManager = settingsManager;
        HashMap<SearchLanguage, Object> analyzersMap = new HashMap<SearchLanguage, Object>();
        analyzersMap.put(SearchLanguage.ARABIC, new ArabicAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.BRAZILIAN, (Object)new ASCIILanguageAnalyzer((Analyzer)new BrazilianAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.CHINESE, new StandardAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.CJK, new CJKAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.CUSTOM_JAPANESE, new JapaneseAnalyzer(LuceneConstants.LUCENE_VERSION, userDictionaryFactory != null ? userDictionaryFactory.getUserDictionary() : null, JapaneseTokenizer.DEFAULT_MODE, JapaneseAnalyzer.getDefaultStopSet(), JapaneseAnalyzer.getDefaultStopTags()));
        analyzersMap.put(SearchLanguage.CZECH, new CzechAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.DANISH, (Object)new ASCIILanguageAnalyzer((Analyzer)new DanishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.DUTCH, (Object)new ASCIILanguageAnalyzer((Analyzer)new DutchAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.ENGLISH, (Object)new ASCIILanguageAnalyzer(new EnglishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.FINNISH, (Object)new ASCIILanguageAnalyzer((Analyzer)new FinnishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.FRENCH, (Object)new ASCIILanguageAnalyzer((Analyzer)new FrenchAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.GERMAN, new GermanAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.GREEK, new GreekAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.HUNGARIAN, (Object)new ASCIILanguageAnalyzer((Analyzer)new HungarianAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.ITALIAN, (Object)new ASCIILanguageAnalyzer((Analyzer)new ItalianAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.NORWEGIAN, (Object)new ASCIILanguageAnalyzer((Analyzer)new NorwegianAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.PERSIAN, new PersianAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.POLISH, (Object)new ASCIILanguageAnalyzer((Analyzer)new PolishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.ROMANIAN, (Object)new ASCIILanguageAnalyzer((Analyzer)new RomanianAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.RUSSIAN, new RussianAnalyzer(LuceneConstants.LUCENE_VERSION));
        analyzersMap.put(SearchLanguage.SPANISH, (Object)new ASCIILanguageAnalyzer((Analyzer)new SpanishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.SWEDISH, (Object)new ASCIILanguageAnalyzer((Analyzer)new SwedishAnalyzer(LuceneConstants.LUCENE_VERSION)));
        analyzersMap.put(SearchLanguage.OTHER, STANDARD_ANALYZER);
        this.analyzersMap = Collections.unmodifiableMap(analyzersMap);
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        SearchLanguage indexingLanguage = SearchLanguage.fromString(this.settingsManager.getGlobalSettings().getIndexingLanguage());
        return this.analyzersMap.getOrDefault(indexingLanguage, STANDARD_ANALYZER);
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        return components;
    }
}

