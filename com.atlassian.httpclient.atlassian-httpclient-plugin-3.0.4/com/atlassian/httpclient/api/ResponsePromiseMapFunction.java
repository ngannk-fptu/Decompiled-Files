/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Buildable;
import com.atlassian.httpclient.api.Response;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;

final class ResponsePromiseMapFunction<O>
implements Function<Response, O> {
    private final ImmutableMap<StatusRange, Function<Response, ? extends O>> functions;
    private Function<Response, ? extends O> othersFunction;

    private ResponsePromiseMapFunction(ImmutableMap<StatusRange, Function<Response, ? extends O>> functions, Function<Response, ? extends O> othersFunction) {
        this.functions = functions;
        this.othersFunction = othersFunction;
    }

    public static <T> ResponsePromiseMapFunctionBuilder<T> builder() {
        return new ResponsePromiseMapFunctionBuilder();
    }

    @Override
    public O apply(Response response) {
        int statusCode = response.getStatusCode();
        Map matchingFunctions = Maps.filterKeys(this.functions, input -> input.isIn(statusCode));
        if (matchingFunctions.isEmpty()) {
            if (this.othersFunction != null) {
                return this.othersFunction.apply(response);
            }
            throw new IllegalStateException("Could not match any function to status " + statusCode);
        }
        if (matchingFunctions.size() > 1) {
            throw new IllegalStateException("Found multiple functions for status " + statusCode);
        }
        return (O)((Function)Iterables.getLast(matchingFunctions.values())).apply(response);
    }

    static interface StatusRange {
        public boolean isIn(int var1);
    }

    public static final class ResponsePromiseMapFunctionBuilder<T>
    implements Buildable<ResponsePromiseMapFunction<T>> {
        private Map<StatusRange, Function<Response, ? extends T>> functionMap = Maps.newHashMap();
        private Function<Response, ? extends T> othersFunction;

        public void addStatusRangeFunction(StatusRange range, Function<Response, ? extends T> func) {
            this.functionMap.put(range, func);
        }

        public void setOthersFunction(Function<Response, ? extends T> othersFunction) {
            this.othersFunction = othersFunction;
        }

        @Override
        public ResponsePromiseMapFunction<T> build() {
            return new ResponsePromiseMapFunction(ImmutableMap.copyOf(this.functionMap), this.othersFunction);
        }
    }
}

