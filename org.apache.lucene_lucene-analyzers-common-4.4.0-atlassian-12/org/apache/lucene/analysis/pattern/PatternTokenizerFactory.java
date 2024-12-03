/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.pattern;

import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class PatternTokenizerFactory
extends TokenizerFactory {
    public static final String PATTERN = "pattern";
    public static final String GROUP = "group";
    protected final Pattern pattern;
    protected final int group;

    public PatternTokenizerFactory(Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, PATTERN);
        this.group = this.getInt(args, GROUP, -1);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public PatternTokenizer create(AttributeSource.AttributeFactory factory, Reader in) {
        return new PatternTokenizer(factory, in, this.pattern, this.group);
    }
}

