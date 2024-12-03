/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  graphql.schema.DataFetcher
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import graphql.schema.DataFetcher;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DenyAnonymousExtension
implements GraphQLExtensions {
    public DataFetcher getDataFetcherThunk(Type type, Member accessor, DataFetcher dataFetcher) {
        if (!(accessor instanceof Method)) {
            return dataFetcher;
        }
        Method method = (Method)accessor;
        if (method.getDeclaringClass().getAnnotation(GraphQLProvider.class) == null || method.getDeclaringClass().getAnnotation(AnonymousAllowed.class) != null || method.getAnnotation(AnonymousAllowed.class) != null) {
            return dataFetcher;
        }
        return env -> {
            if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
                throw new PermissionException("Anonymous access is not permitted");
            }
            return dataFetcher.get(env);
        };
    }
}

