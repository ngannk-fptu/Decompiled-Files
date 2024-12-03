/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.EmptyDeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public interface DeclaredQuery {
    public static DeclaredQuery of(@Nullable String query) {
        return StringUtils.isEmpty((Object)query) ? EmptyDeclaredQuery.EMPTY_QUERY : new StringQuery(query);
    }

    public boolean hasNamedParameter();

    public String getQueryString();

    @Nullable
    public String getAlias();

    public boolean hasConstructorExpression();

    public boolean isDefaultProjection();

    public List<StringQuery.ParameterBinding> getParameterBindings();

    public DeclaredQuery deriveCountQuery(@Nullable String var1, @Nullable String var2);

    default public boolean usesPaging() {
        return false;
    }

    public boolean usesJdbcStyleParameters();
}

