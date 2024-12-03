/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  graphql.schema.DataFetcher
 *  graphql.schema.DataFetchingEnvironment
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.graphql.spi.GraphQLExtensions;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public class LimitRequestSizeExtension
implements GraphQLExtensions {
    public DataFetcher getDataFetcherThunk(Type type, Member accessor, DataFetcher dataFetcher) {
        if (!(accessor instanceof Method)) {
            return dataFetcher;
        }
        Method method = (Method)accessor;
        Long limitRequestSize = LimitRequestSizeExtension.getLimitRequestSize(method);
        if (limitRequestSize == null) {
            return dataFetcher;
        }
        return env -> {
            if (LimitRequestSizeExtension.calculateArgumentsSize(env) > limitRequestSize) {
                throw new RuntimeException("Field arguments too large. String field values for this field can be at most " + limitRequestSize + " bytes.");
            }
            return dataFetcher.get(env);
        };
    }

    private static Long getLimitRequestSize(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (!annotation.annotationType().getName().equals("com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize")) continue;
            try {
                return (Long)annotation.annotationType().getMethod("value", new Class[0]).invoke((Object)annotation, new Object[0]);
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return null;
    }

    private static long calculateArgumentsSize(DataFetchingEnvironment env) {
        int size = 0;
        for (Map.Entry entry : env.getArguments().entrySet()) {
            if (!(entry.getValue() instanceof String)) continue;
            size += ((String)entry.getValue()).length();
        }
        return size;
    }
}

