/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.util;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.AnalysisSPILoader;
import org.apache.lucene.util.AttributeSource;

public abstract class TokenizerFactory
extends AbstractAnalysisFactory {
    private static final AnalysisSPILoader<TokenizerFactory> loader = new AnalysisSPILoader<TokenizerFactory>(TokenizerFactory.class);

    public static TokenizerFactory forName(String name, Map<String, String> args) {
        return loader.newInstance(name, args);
    }

    public static Class<? extends TokenizerFactory> lookupClass(String name) {
        return loader.lookupClass(name);
    }

    public static Set<String> availableTokenizers() {
        return loader.availableServices();
    }

    public static void reloadTokenizers(ClassLoader classloader) {
        loader.reload(classloader);
    }

    protected TokenizerFactory(Map<String, String> args) {
        super(args);
    }

    public final Tokenizer create(Reader input) {
        return this.create(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input);
    }

    public abstract Tokenizer create(AttributeSource.AttributeFactory var1, Reader var2);
}

