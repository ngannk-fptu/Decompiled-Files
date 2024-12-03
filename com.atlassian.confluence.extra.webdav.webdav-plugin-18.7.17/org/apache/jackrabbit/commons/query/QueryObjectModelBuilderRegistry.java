/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.commons.query.QueryObjectModelBuilder;

public class QueryObjectModelBuilderRegistry {
    private static final List<QueryObjectModelBuilder> BUILDERS = new ArrayList<QueryObjectModelBuilder>();
    private static final Set<String> LANGUAGES;

    public static QueryObjectModelBuilder getQueryObjectModelBuilder(String language) throws InvalidQueryException {
        for (QueryObjectModelBuilder builder : BUILDERS) {
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
        for (QueryObjectModelBuilder builder : ServiceLoader.load(QueryObjectModelBuilder.class, QueryObjectModelBuilder.class.getClassLoader())) {
            BUILDERS.add(builder);
            languages.addAll(Arrays.asList(builder.getSupportedLanguages()));
        }
        LANGUAGES = Collections.unmodifiableSet(languages);
    }
}

