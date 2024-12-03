/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.util;

import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.AnalysisSPILoader;

public abstract class TokenFilterFactory
extends AbstractAnalysisFactory {
    private static final AnalysisSPILoader<TokenFilterFactory> loader = new AnalysisSPILoader<TokenFilterFactory>(TokenFilterFactory.class, new String[]{"TokenFilterFactory", "FilterFactory"});

    public static TokenFilterFactory forName(String name, Map<String, String> args) {
        return loader.newInstance(name, args);
    }

    public static Class<? extends TokenFilterFactory> lookupClass(String name) {
        return loader.lookupClass(name);
    }

    public static Set<String> availableTokenFilters() {
        return loader.availableServices();
    }

    public static void reloadTokenFilters(ClassLoader classloader) {
        loader.reload(classloader);
    }

    protected TokenFilterFactory(Map<String, String> args) {
        super(args);
    }

    public abstract TokenStream create(TokenStream var1);
}

