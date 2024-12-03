/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CapitalizationFilterFactory
extends TokenFilterFactory {
    public static final String KEEP = "keep";
    public static final String KEEP_IGNORE_CASE = "keepIgnoreCase";
    public static final String OK_PREFIX = "okPrefix";
    public static final String MIN_WORD_LENGTH = "minWordLength";
    public static final String MAX_WORD_COUNT = "maxWordCount";
    public static final String MAX_TOKEN_LENGTH = "maxTokenLength";
    public static final String ONLY_FIRST_WORD = "onlyFirstWord";
    public static final String FORCE_FIRST_LETTER = "forceFirstLetter";
    CharArraySet keep;
    Collection<char[]> okPrefix = Collections.emptyList();
    final int minWordLength;
    final int maxWordCount;
    final int maxTokenLength;
    final boolean onlyFirstWord;
    final boolean forceFirstLetter;

    public CapitalizationFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        boolean ignoreCase = this.getBoolean(args, KEEP_IGNORE_CASE, false);
        Set<String> k = this.getSet(args, KEEP);
        if (k != null) {
            this.keep = new CharArraySet(this.luceneMatchVersion, 10, ignoreCase);
            this.keep.addAll(k);
        }
        if ((k = this.getSet(args, OK_PREFIX)) != null) {
            this.okPrefix = new ArrayList<char[]>();
            for (String item : k) {
                this.okPrefix.add(item.toCharArray());
            }
        }
        this.minWordLength = this.getInt(args, MIN_WORD_LENGTH, 0);
        this.maxWordCount = this.getInt(args, MAX_WORD_COUNT, Integer.MAX_VALUE);
        this.maxTokenLength = this.getInt(args, MAX_TOKEN_LENGTH, Integer.MAX_VALUE);
        this.onlyFirstWord = this.getBoolean(args, ONLY_FIRST_WORD, true);
        this.forceFirstLetter = this.getBoolean(args, FORCE_FIRST_LETTER, true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public CapitalizationFilter create(TokenStream input) {
        return new CapitalizationFilter(input, this.onlyFirstWord, this.keep, this.forceFirstLetter, this.okPrefix, this.minWordLength, this.maxWordCount, this.maxTokenLength);
    }
}

