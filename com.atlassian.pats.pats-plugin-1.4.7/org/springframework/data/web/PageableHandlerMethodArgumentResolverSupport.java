/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.web;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SpringDataAnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class PageableHandlerMethodArgumentResolverSupport {
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";
    private static final String DEFAULT_PAGE_PARAMETER = "page";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 20);
    private Pageable fallbackPageable = DEFAULT_PAGE_REQUEST;
    private String pageParameterName = "page";
    private String sizeParameterName = "size";
    private String prefix = "";
    private String qualifierDelimiter = "_";
    private int maxPageSize = 2000;
    private boolean oneIndexedParameters = false;

    public void setFallbackPageable(Pageable fallbackPageable) {
        Assert.notNull((Object)fallbackPageable, (String)"Fallback Pageable must not be null!");
        this.fallbackPageable = fallbackPageable;
    }

    public boolean isFallbackPageable(Pageable pageable) {
        return this.fallbackPageable.equals(pageable);
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    protected int getMaxPageSize() {
        return this.maxPageSize;
    }

    public void setPageParameterName(String pageParameterName) {
        Assert.hasText((String)pageParameterName, (String)"Page parameter name must not be null or empty!");
        this.pageParameterName = pageParameterName;
    }

    protected String getPageParameterName() {
        return this.pageParameterName;
    }

    public void setSizeParameterName(String sizeParameterName) {
        Assert.hasText((String)sizeParameterName, (String)"Size parameter name must not be null or empty!");
        this.sizeParameterName = sizeParameterName;
    }

    protected String getSizeParameterName() {
        return this.sizeParameterName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? DEFAULT_QUALIFIER_DELIMITER : qualifierDelimiter;
    }

    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    protected boolean isOneIndexedParameters() {
        return this.oneIndexedParameters;
    }

    protected Pageable getPageable(MethodParameter methodParameter, @Nullable String pageString, @Nullable String pageSizeString) {
        SpringDataAnnotationUtils.assertPageableUniqueness(methodParameter);
        Optional<Pageable> defaultOrFallback = this.getDefaultFromAnnotationOrFallback(methodParameter).toOptional();
        Optional<Integer> page = this.parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true);
        Optional<Integer> pageSize = this.parseAndApplyBoundaries(pageSizeString, this.maxPageSize, false);
        if (!(page.isPresent() && pageSize.isPresent() || defaultOrFallback.isPresent())) {
            return Pageable.unpaged();
        }
        int p = page.orElseGet(() -> defaultOrFallback.map(Pageable::getPageNumber).orElseThrow(IllegalStateException::new));
        int ps = pageSize.orElseGet(() -> defaultOrFallback.map(Pageable::getPageSize).orElseThrow(IllegalStateException::new));
        ps = ps < 1 ? defaultOrFallback.map(Pageable::getPageSize).orElseThrow(IllegalStateException::new) : ps;
        ps = ps > this.maxPageSize ? this.maxPageSize : ps;
        return PageRequest.of(p, ps, defaultOrFallback.map(Pageable::getSort).orElseGet(Sort::unsorted));
    }

    protected String getParameterNameToUse(String source, @Nullable MethodParameter parameter) {
        StringBuilder builder = new StringBuilder(this.prefix);
        String value = SpringDataAnnotationUtils.getQualifier(parameter);
        if (StringUtils.hasLength((String)value)) {
            builder.append(value);
            builder.append(this.qualifierDelimiter);
        }
        return builder.append(source).toString();
    }

    private Pageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {
        PageableDefault defaults = (PageableDefault)methodParameter.getParameterAnnotation(PageableDefault.class);
        if (defaults != null) {
            return PageableHandlerMethodArgumentResolverSupport.getDefaultPageRequestFrom(methodParameter, defaults);
        }
        return this.fallbackPageable;
    }

    private static Pageable getDefaultPageRequestFrom(MethodParameter parameter, PageableDefault defaults) {
        int defaultPageNumber = defaults.page();
        Integer defaultPageSize = (Integer)SpringDataAnnotationUtils.getSpecificPropertyOrDefaultFromValue(defaults, DEFAULT_SIZE_PARAMETER);
        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        }
        if (defaults.sort().length == 0) {
            return PageRequest.of(defaultPageNumber, defaultPageSize);
        }
        return PageRequest.of(defaultPageNumber, defaultPageSize, defaults.direction(), defaults.sort());
    }

    private Optional<Integer> parseAndApplyBoundaries(@Nullable String parameter, int upper, boolean shiftIndex) {
        if (!StringUtils.hasText((String)parameter)) {
            return Optional.empty();
        }
        try {
            int parsed = Integer.parseInt(parameter) - (this.oneIndexedParameters && shiftIndex ? 1 : 0);
            return Optional.of(parsed < 0 ? 0 : (parsed > upper ? upper : parsed));
        }
        catch (NumberFormatException e) {
            return Optional.of(0);
        }
    }
}

