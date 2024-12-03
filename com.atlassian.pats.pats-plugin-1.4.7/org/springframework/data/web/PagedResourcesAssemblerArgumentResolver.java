/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.log.LogMessage
 *  org.springframework.hateoas.server.MethodLinkBuilderFactory
 *  org.springframework.hateoas.server.core.MethodParameters
 *  org.springframework.lang.Nullable
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.data.web;

import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.log.LogMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.MethodParameterAwarePagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.MethodLinkBuilderFactory;
import org.springframework.hateoas.server.core.MethodParameters;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagedResourcesAssemblerArgumentResolver
implements HandlerMethodArgumentResolver {
    private static final Log logger = LogFactory.getLog(PagedResourcesAssemblerArgumentResolver.class);
    private static final String SUPERFLOUS_QUALIFIER = "Found qualified %s parameter, but a unique unqualified %s parameter. Using that one, but you might want to check your controller method configuration!";
    private static final String PARAMETER_AMBIGUITY = "Discovered multiple parameters of type Pageable but no qualifier annotations to disambiguate!";
    private final HateoasPageableHandlerMethodArgumentResolver resolver;

    @Deprecated
    public PagedResourcesAssemblerArgumentResolver(HateoasPageableHandlerMethodArgumentResolver resolver, @Nullable MethodLinkBuilderFactory<?> linkBuilderFactory) {
        this(resolver);
    }

    public PagedResourcesAssemblerArgumentResolver(HateoasPageableHandlerMethodArgumentResolver resolver) {
        this.resolver = resolver;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return PagedResourcesAssembler.class.equals((Object)parameter.getParameterType());
    }

    @Nonnull
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        MethodParameter pageableParameter = PagedResourcesAssemblerArgumentResolver.findMatchingPageableParameter(parameter);
        if (pageableParameter != null) {
            return new MethodParameterAwarePagedResourcesAssembler(pageableParameter, this.resolver, null);
        }
        return new PagedResourcesAssembler(this.resolver, null);
    }

    @Nullable
    private static MethodParameter findMatchingPageableParameter(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalArgumentException(String.format("Could not obtain method from parameter %s!", parameter));
        }
        MethodParameters parameters = MethodParameters.of((Method)method);
        List pageableParameters = parameters.getParametersOfType(Pageable.class);
        Qualifier assemblerQualifier = (Qualifier)parameter.getParameterAnnotation(Qualifier.class);
        if (pageableParameters.isEmpty()) {
            return null;
        }
        if (pageableParameters.size() == 1) {
            MethodParameter pageableParameter = (MethodParameter)pageableParameters.get(0);
            MethodParameter matchingParameter = PagedResourcesAssemblerArgumentResolver.returnIfQualifiersMatch(pageableParameter, assemblerQualifier);
            if (matchingParameter == null) {
                logger.info((Object)LogMessage.format((String)SUPERFLOUS_QUALIFIER, (Object)PagedResourcesAssembler.class.getSimpleName(), (Object)Pageable.class.getName()));
            }
            return pageableParameter;
        }
        if (assemblerQualifier == null) {
            throw new IllegalStateException(PARAMETER_AMBIGUITY);
        }
        for (MethodParameter pageableParameter : pageableParameters) {
            MethodParameter matchingParameter = PagedResourcesAssemblerArgumentResolver.returnIfQualifiersMatch(pageableParameter, assemblerQualifier);
            if (matchingParameter == null) continue;
            return matchingParameter;
        }
        throw new IllegalStateException(PARAMETER_AMBIGUITY);
    }

    @Nullable
    private static MethodParameter returnIfQualifiersMatch(MethodParameter pageableParameter, @Nullable Qualifier assemblerQualifier) {
        if (assemblerQualifier == null) {
            return pageableParameter;
        }
        Qualifier pageableParameterQualifier = (Qualifier)pageableParameter.getParameterAnnotation(Qualifier.class);
        if (pageableParameterQualifier == null) {
            return null;
        }
        return pageableParameterQualifier.value().equals(assemblerQualifier.value()) ? pageableParameter : null;
    }
}

