/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.ru;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.ru.RussianLetterTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public class RussianLetterTokenizerFactory
extends TokenizerFactory {
    public RussianLetterTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public RussianLetterTokenizer create(AttributeSource.AttributeFactory factory, Reader in) {
        return new RussianLetterTokenizer(this.luceneMatchVersion, factory, in);
    }
}

