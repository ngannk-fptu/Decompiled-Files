/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.pattern;

import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pattern.PatternCaptureGroupTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PatternCaptureGroupFilterFactory
extends TokenFilterFactory {
    private Pattern pattern;
    private boolean preserveOriginal = true;

    public PatternCaptureGroupFilterFactory(Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, "pattern");
        this.preserveOriginal = args.containsKey("preserve_original") ? Boolean.parseBoolean(args.get("preserve_original")) : true;
    }

    public PatternCaptureGroupTokenFilter create(TokenStream input) {
        return new PatternCaptureGroupTokenFilter(input, this.preserveOriginal, this.pattern);
    }
}

