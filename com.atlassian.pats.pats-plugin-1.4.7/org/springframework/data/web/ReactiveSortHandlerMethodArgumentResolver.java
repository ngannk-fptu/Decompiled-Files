/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.StringUtils
 *  org.springframework.web.reactive.BindingContext
 *  org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver
 *  org.springframework.web.server.ServerWebExchange
 */
package org.springframework.data.web;

import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolverSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

public class ReactiveSortHandlerMethodArgumentResolver
extends SortHandlerMethodArgumentResolverSupport
implements SyncHandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals((Object)parameter.getParameterType());
    }

    @Nonnull
    public Sort resolveArgumentValue(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        List directionParameter = (List)exchange.getRequest().getQueryParams().get((Object)this.getSortParameter(parameter));
        if (directionParameter == null) {
            return this.getDefaultFromAnnotationOrFallback(parameter);
        }
        if (directionParameter.size() == 1 && !StringUtils.hasText((String)((String)directionParameter.get(0)))) {
            return this.getDefaultFromAnnotationOrFallback(parameter);
        }
        return this.parseParameterIntoSort(directionParameter, this.getPropertyDelimiter());
    }
}

