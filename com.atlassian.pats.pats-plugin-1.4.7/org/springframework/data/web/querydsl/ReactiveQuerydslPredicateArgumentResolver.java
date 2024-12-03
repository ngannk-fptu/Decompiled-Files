/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.reactive.BindingContext
 *  org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver
 *  org.springframework.web.server.ServerWebExchange
 */
package org.springframework.data.web.querydsl;

import com.querydsl.core.types.Predicate;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

public class ReactiveQuerydslPredicateArgumentResolver
extends QuerydslPredicateArgumentResolverSupport
implements SyncHandlerMethodArgumentResolver {
    public ReactiveQuerydslPredicateArgumentResolver(QuerydslBindingsFactory factory, ConversionService conversionService) {
        super(factory, conversionService);
    }

    @Nullable
    public Object resolveArgumentValue(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParameters = ReactiveQuerydslPredicateArgumentResolver.getQueryParameters(exchange);
        Predicate result = this.getPredicate(parameter, queryParameters);
        return ReactiveQuerydslPredicateArgumentResolver.potentiallyConvertMethodParameterValue(parameter, result);
    }

    private static MultiValueMap<String, String> getQueryParameters(ServerWebExchange exchange) {
        MultiValueMap queryParams = exchange.getRequest().getQueryParams();
        LinkedMultiValueMap parameters = new LinkedMultiValueMap(queryParams.size());
        for (Map.Entry entry : queryParams.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue());
        }
        return parameters;
    }
}

