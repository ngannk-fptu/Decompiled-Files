/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.data.web;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableHandlerMethodArgumentResolver
extends PageableHandlerMethodArgumentResolverSupport
implements PageableArgumentResolver {
    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private SortArgumentResolver sortResolver;

    public PageableHandlerMethodArgumentResolver() {
        this((SortArgumentResolver)null);
    }

    public PageableHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
        this((SortArgumentResolver)sortResolver);
    }

    public PageableHandlerMethodArgumentResolver(@Nullable SortArgumentResolver sortResolver) {
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals((Object)parameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        String page = webRequest.getParameter(this.getParameterNameToUse(this.getPageParameterName(), methodParameter));
        String pageSize = webRequest.getParameter(this.getParameterNameToUse(this.getSizeParameterName(), methodParameter));
        Sort sort = this.sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        Pageable pageable = this.getPageable(methodParameter, page, pageSize);
        if (sort.isSorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return pageable;
    }
}

