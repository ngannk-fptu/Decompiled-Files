/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class LetterTokenizerFactory
extends TokenizerFactory {
    public LetterTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public LetterTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new LetterTokenizer(this.luceneMatchVersion, factory, input);
    }
}

