/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.wikipedia;

import java.io.Reader;
import java.util.Collections;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;
import org.apache.lucene.util.AttributeSource;

public class WikipediaTokenizerFactory
extends TokenizerFactory {
    public WikipediaTokenizerFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public WikipediaTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new WikipediaTokenizer(factory, input, 0, Collections.emptySet());
    }
}

