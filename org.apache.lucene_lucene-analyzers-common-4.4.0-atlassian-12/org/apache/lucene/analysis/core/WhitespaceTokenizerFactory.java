/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class WhitespaceTokenizerFactory
extends TokenizerFactory {
    public WhitespaceTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public WhitespaceTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new WhitespaceTokenizer(this.luceneMatchVersion, factory, input);
    }
}

