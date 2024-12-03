/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CharFilter
 */
package org.apache.lucene.analysis.pattern;

import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class PatternReplaceCharFilterFactory
extends CharFilterFactory {
    private final Pattern pattern;
    private final String replacement;
    private final int maxBlockChars;
    private final String blockDelimiters;

    public PatternReplaceCharFilterFactory(Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, "pattern");
        this.replacement = this.get(args, "replacement", "");
        this.maxBlockChars = this.getInt(args, "maxBlockChars", 10000);
        this.blockDelimiters = args.remove("blockDelimiters");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public CharFilter create(Reader input) {
        return new PatternReplaceCharFilter(this.pattern, this.replacement, this.maxBlockChars, this.blockDelimiters, input);
    }
}

