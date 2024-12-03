/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.reactive.BindingContext
 *  org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver
 *  org.springframework.web.server.ServerWebExchange
 */
package org.springframework.data.web;

import javax.annotation.Nonnull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

public class ReactivePageableHandlerMethodArgumentResolver
extends PageableHandlerMethodArgumentResolverSupport
implements SyncHandlerMethodArgumentResolver {
    private static final ReactiveSortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new ReactiveSortHandlerMethodArgumentResolver();
    private ReactiveSortHandlerMethodArgumentResolver sortResolver;

    public ReactivePageableHandlerMethodArgumentResolver() {
        this(DEFAULT_SORT_RESOLVER);
    }

    public ReactivePageableHandlerMethodArgumentResolver(ReactiveSortHandlerMethodArgumentResolver sortResolver) {
        Assert.notNull((Object)sortResolver, (String)"ReactiveSortHandlerMethodArgumentResolver must not be null!");
        this.sortResolver = sortResolver;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals((Object)parameter.getParameterType());
    }

    @Nonnull
    public Pageable resolveArgumentValue(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        MultiValueMap queryParams = exchange.getRequest().getQueryParams();
        String page = (String)queryParams.getFirst((Object)this.getParameterNameToUse(this.getPageParameterName(), parameter));
        String pageSize = (String)queryParams.getFirst((Object)this.getParameterNameToUse(this.getSizeParameterName(), parameter));
        Sort sort = this.sortResolver.resolveArgumentValue(parameter, bindingContext, exchange);
        Pageable pageable = this.getPageable(parameter, page, pageSize);
        return sort.isSorted() ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort) : pageable;
    }
}

