/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.ar;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public class ArabicLetterTokenizerFactory
extends TokenizerFactory {
    public ArabicLetterTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public ArabicLetterTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new ArabicLetterTokenizer(this.luceneMatchVersion, factory, input);
    }
}

