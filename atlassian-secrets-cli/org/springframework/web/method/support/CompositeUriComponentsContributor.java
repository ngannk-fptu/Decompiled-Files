/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.util.UriComponentsBuilder;

public class CompositeUriComponentsContributor
implements UriComponentsContributor {
    private final List<Object> contributors = new LinkedList<Object>();
    private final ConversionService conversionService;

    public CompositeUriComponentsContributor(UriComponentsContributor ... contributors) {
        Collections.addAll(this.contributors, contributors);
        this.conversionService = new DefaultFormattingConversionService();
    }

    public CompositeUriComponentsContributor(Collection<?> contributors) {
        this(contributors, null);
    }

    public CompositeUriComponentsContributor(@Nullable Collection<?> contributors, @Nullable ConversionService cs) {
        if (contributors != null) {
            this.contributors.addAll(contributors);
        }
        this.conversionService = cs != null ? cs : new DefaultFormattingConversionService();
    }

    public boolean hasContributors() {
        return this.contributors.isEmpty();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        for (Object contributor : this.contributors) {
            if (contributor instanceof UriComponentsContributor) {
                if (!((UriComponentsContributor)contributor).supportsParameter(parameter)) continue;
                return true;
            }
            if (!(contributor instanceof HandlerMethodArgumentResolver) || !((HandlerMethodArgumentResolver)contributor).supportsParameter(parameter)) continue;
            return false;
        }
        return false;
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        for (Object contributor : this.contributors) {
            if (contributor instanceof UriComponentsContributor) {
                UriComponentsContributor ucc = (UriComponentsContributor)contributor;
                if (!ucc.supportsParameter(parameter)) continue;
                ucc.contributeMethodArgument(parameter, value, builder, uriVariables, conversionService);
                break;
            }
            if (!(contributor instanceof HandlerMethodArgumentResolver) || !((HandlerMethodArgumentResolver)contributor).supportsParameter(parameter)) continue;
            break;
        }
    }

    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables) {
        this.contributeMethodArgument(parameter, value, builder, uriVariables, this.conversionService);
    }
}

