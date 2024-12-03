/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.AnalysisSPILoader;

public abstract class CharFilterFactory
extends AbstractAnalysisFactory {
    private static final AnalysisSPILoader<CharFilterFactory> loader = new AnalysisSPILoader<CharFilterFactory>(CharFilterFactory.class);

    public static CharFilterFactory forName(String name, Map<String, String> args) {
        return loader.newInstance(name, args);
    }

    public static Class<? extends CharFilterFactory> lookupClass(String name) {
        return loader.lookupClass(name);
    }

    public static Set<String> availableCharFilters() {
        return loader.availableServices();
    }

    public static void reloadCharFilters(ClassLoader classloader) {
        loader.reload(classloader);
    }

    protected CharFilterFactory(Map<String, String> args) {
        super(args);
    }

    public abstract Reader create(Reader var1);
}

