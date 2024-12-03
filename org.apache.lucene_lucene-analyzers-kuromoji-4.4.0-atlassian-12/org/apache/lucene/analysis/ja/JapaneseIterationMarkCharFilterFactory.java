/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CharFilter
 *  org.apache.lucene.analysis.util.AbstractAnalysisFactory
 *  org.apache.lucene.analysis.util.CharFilterFactory
 *  org.apache.lucene.analysis.util.MultiTermAwareComponent
 */
package org.apache.lucene.analysis.ja;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.ja.JapaneseIterationMarkCharFilter;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;

public class JapaneseIterationMarkCharFilterFactory
extends CharFilterFactory
implements MultiTermAwareComponent {
    private static final String NORMALIZE_KANJI_PARAM = "normalizeKanji";
    private static final String NORMALIZE_KANA_PARAM = "normalizeKana";
    private final boolean normalizeKanji;
    private final boolean normalizeKana;

    public JapaneseIterationMarkCharFilterFactory(Map<String, String> args) {
        super(args);
        this.normalizeKanji = this.getBoolean(args, NORMALIZE_KANJI_PARAM, true);
        this.normalizeKana = this.getBoolean(args, NORMALIZE_KANA_PARAM, true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public CharFilter create(Reader input) {
        return new JapaneseIterationMarkCharFilter(input, this.normalizeKanji, this.normalizeKana);
    }

    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

