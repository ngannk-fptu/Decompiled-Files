/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.commons.query.QueryTreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryTreeBuilderRegistry {
    private static final Logger log = LoggerFactory.getLogger(QueryTreeBuilderRegistry.class);
    private static final List BUILDERS = new ArrayList();
    private static final Set LANGUAGES;

    public static QueryTreeBuilder getQueryTreeBuilder(String language) throws InvalidQueryException {
        for (int i = 0; i < BUILDERS.size(); ++i) {
            QueryTreeBuilder builder = (QueryTreeBuilder)BUILDERS.get(i);
            if (!builder.canHandle(language)) continue;
            return builder;
        }
        throw new InvalidQueryException("Unsupported language: " + language);
    }

    public static String[] getSupportedLanguages() {
        return LANGUAGES.toArray(new String[LANGUAGES.size()]);
    }

    static {
        HashSet<String> languages = new HashSet<String>();
        try {
            for (QueryTreeBuilder qtb : ServiceLoader.load(QueryTreeBuilder.class, QueryTreeBuilderRegistry.class.getClassLoader())) {
                BUILDERS.add(qtb);
                languages.addAll(Arrays.asList(qtb.getSupportedLanguages()));
            }
        }
        catch (Error e) {
            log.warn("Unable to load providers for QueryTreeBuilder: " + e);
        }
        LANGUAGES = Collections.unmodifiableSet(languages);
    }
}

