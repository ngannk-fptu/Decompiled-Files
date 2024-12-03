/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.data.web.querydsl;

import com.querydsl.core.types.Predicate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class QuerydslPredicateArgumentResolver
extends QuerydslPredicateArgumentResolverSupport
implements HandlerMethodArgumentResolver {
    public QuerydslPredicateArgumentResolver(QuerydslBindingsFactory factory, Optional<ConversionService> conversionService) {
        super(factory, conversionService.orElseGet(DefaultConversionService::getSharedInstance));
    }

    public QuerydslPredicateArgumentResolver(QuerydslBindingsFactory factory, ConversionService conversionService) {
        super(factory, conversionService);
    }

    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        MultiValueMap<String, String> queryParameters = QuerydslPredicateArgumentResolver.getQueryParameters(webRequest);
        Predicate result = this.getPredicate(parameter, queryParameters);
        return QuerydslPredicateArgumentResolver.potentiallyConvertMethodParameterValue(parameter, result);
    }

    private static MultiValueMap<String, String> getQueryParameters(NativeWebRequest webRequest) {
        Map parameterMap = webRequest.getParameterMap();
        LinkedMultiValueMap queryParameters = new LinkedMultiValueMap(parameterMap.size());
        for (Map.Entry entry : parameterMap.entrySet()) {
            queryParameters.put(entry.getKey(), Arrays.asList((Object[])entry.getValue()));
        }
        return queryParameters;
    }
}

