/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class EmptyDeclaredQuery
implements DeclaredQuery {
    static final DeclaredQuery EMPTY_QUERY = new EmptyDeclaredQuery();

    EmptyDeclaredQuery() {
    }

    @Override
    public boolean hasNamedParameter() {
        return false;
    }

    @Override
    public String getQueryString() {
        return "";
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public boolean hasConstructorExpression() {
        return false;
    }

    @Override
    public boolean isDefaultProjection() {
        return false;
    }

    @Override
    public List<StringQuery.ParameterBinding> getParameterBindings() {
        return Collections.emptyList();
    }

    @Override
    public DeclaredQuery deriveCountQuery(@Nullable String countQuery, @Nullable String countQueryProjection) {
        Assert.hasText((String)countQuery, (String)"CountQuery must not be empty!");
        return DeclaredQuery.of(countQuery);
    }

    @Override
    public boolean usesJdbcStyleParameters() {
        return false;
    }
}

