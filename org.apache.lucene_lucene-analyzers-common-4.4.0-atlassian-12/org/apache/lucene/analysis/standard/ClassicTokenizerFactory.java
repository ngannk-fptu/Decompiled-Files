/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.standard;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class ClassicTokenizerFactory
extends TokenizerFactory {
    private final int maxTokenLength;

    public ClassicTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.maxTokenLength = this.getInt(args, "maxTokenLength", 255);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public ClassicTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        ClassicTokenizer tokenizer = new ClassicTokenizer(this.luceneMatchVersion, factory, input);
        tokenizer.setMaxTokenLength(this.maxTokenLength);
        return tokenizer;
    }
}

