/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.core.KeywordAnalyzer
 *  org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.ConfluenceAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.ConfluenceFilenameAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.UserDictionaryFactory;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact.ExactAnalyzer;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

public class ConfluenceLuceneAnalyzerFactory
implements LuceneAnalyzerFactory {
    private final ConfluenceAnalyzer confluenceAnalyzer;
    private final Analyzer unstemmedAnalyzer;
    private Analyzer indexingAnalyzer;
    private Analyzer searchingAnalyzer;
    private static final List<String> KEYWORD_FIELDS = Arrays.asList(SearchFieldNames.HANDLE, SearchFieldNames.CLASS_NAME, SearchFieldNames.SPACE_KEY, SearchFieldNames.URL_PATH, SearchFieldNames.TYPE, SearchFieldNames.SPACE_TYPE, SearchFieldNames.ATTACHMENT_OWNER_CONTENT_TYPE, "content-name-untokenized", SearchFieldNames.MACRO_NAME, SearchFieldNames.MACRO_STORAGE_VERSION);

    @Deprecated
    @VisibleForTesting
    public ConfluenceLuceneAnalyzerFactory(SettingsManager settingsManager, Analyzer unstemmedAnalyzer, UserDictionaryFactory userDictionaryFactory) {
        this(new ConfluenceAnalyzer(settingsManager, userDictionaryFactory), unstemmedAnalyzer);
    }

    public ConfluenceLuceneAnalyzerFactory(ConfluenceAnalyzer confluenceAnalyzer, Analyzer unstemmedAnalyzer) {
        this.confluenceAnalyzer = confluenceAnalyzer;
        this.unstemmedAnalyzer = unstemmedAnalyzer;
    }

    public Analyzer createIndexingAnalyzer() {
        if (this.indexingAnalyzer == null) {
            this.indexingAnalyzer = new PerFieldAnalyzerWrapper((Analyzer)this.confluenceAnalyzer, (Map)ImmutableMap.builder().put((Object)"filename", (Object)new ConfluenceFilenameAnalyzer(this.confluenceAnalyzer)).put((Object)SearchFieldNames.CONTENT_NAME_UNSTEMMED, (Object)this.unstemmedAnalyzer).put((Object)SearchFieldNames.PARENT_TITLE_UNSTEMMED, (Object)this.unstemmedAnalyzer).put((Object)SearchFieldNames.EXACT_TITLE, (Object)new ExactAnalyzer(LuceneConstants.LUCENE_VERSION)).put((Object)SearchFieldNames.EXACT_CONTENT_BODY, (Object)new ExactAnalyzer(LuceneConstants.LUCENE_VERSION)).build());
        }
        return this.indexingAnalyzer;
    }

    public Analyzer createAnalyzer() {
        if (this.searchingAnalyzer == null) {
            ImmutableMap.Builder fieldAnalyzers = ImmutableMap.builder();
            for (String fieldName : KEYWORD_FIELDS) {
                fieldAnalyzers.put((Object)fieldName, (Object)new KeywordAnalyzer());
            }
            fieldAnalyzers.put((Object)SearchFieldNames.EXACT_TITLE, (Object)new ExactAnalyzer(LuceneConstants.LUCENE_VERSION));
            fieldAnalyzers.put((Object)SearchFieldNames.EXACT_FILENAME, (Object)new ExactAnalyzer(LuceneConstants.LUCENE_VERSION));
            fieldAnalyzers.put((Object)SearchFieldNames.EXACT_CONTENT_BODY, (Object)new ExactAnalyzer(LuceneConstants.LUCENE_VERSION));
            this.searchingAnalyzer = new PerFieldAnalyzerWrapper((Analyzer)this.confluenceAnalyzer, (Map)fieldAnalyzers.build());
        }
        return this.searchingAnalyzer;
    }
}

