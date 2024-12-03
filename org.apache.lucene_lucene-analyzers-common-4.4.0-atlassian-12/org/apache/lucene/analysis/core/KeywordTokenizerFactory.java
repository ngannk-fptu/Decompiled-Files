/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class KeywordTokenizerFactory
extends TokenizerFactory {
    public KeywordTokenizerFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public KeywordTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new KeywordTokenizer(factory, input, 256);
    }
}

