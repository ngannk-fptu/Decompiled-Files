/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import java.util.Locale;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public interface QueryLookupStrategy {
    public RepositoryQuery resolveQuery(Method var1, RepositoryMetadata var2, ProjectionFactory var3, NamedQueries var4);

    public static enum Key {
        CREATE,
        USE_DECLARED_QUERY,
        CREATE_IF_NOT_FOUND;


        @Nullable
        public static Key create(String xml) {
            if (!StringUtils.hasText((String)xml)) {
                return null;
            }
            return Key.valueOf(xml.toUpperCase(Locale.US).replace("-", "_"));
        }
    }
}

