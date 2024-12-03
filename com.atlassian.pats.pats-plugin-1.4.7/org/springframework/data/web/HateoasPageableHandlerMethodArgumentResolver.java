/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.hateoas.TemplateVariable
 *  org.springframework.hateoas.TemplateVariable$VariableType
 *  org.springframework.hateoas.TemplateVariables
 *  org.springframework.hateoas.server.mvc.UriComponentsContributor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.data.web;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.server.mvc.UriComponentsContributor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class HateoasPageableHandlerMethodArgumentResolver
extends PageableHandlerMethodArgumentResolver
implements UriComponentsContributor {
    private static final HateoasSortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new HateoasSortHandlerMethodArgumentResolver();
    private final HateoasSortHandlerMethodArgumentResolver sortResolver;

    public HateoasPageableHandlerMethodArgumentResolver() {
        this(null);
    }

    public HateoasPageableHandlerMethodArgumentResolver(@Nullable HateoasSortHandlerMethodArgumentResolver sortResolver) {
        super(HateoasPageableHandlerMethodArgumentResolver.getDefaultedSortResolver(sortResolver));
        this.sortResolver = HateoasPageableHandlerMethodArgumentResolver.getDefaultedSortResolver(sortResolver);
    }

    public TemplateVariables getPaginationTemplateVariables(MethodParameter parameter, UriComponents template) {
        String pagePropertyName = this.getParameterNameToUse(this.getPageParameterName(), parameter);
        String sizePropertyName = this.getParameterNameToUse(this.getSizeParameterName(), parameter);
        ArrayList<TemplateVariable> names = new ArrayList<TemplateVariable>();
        MultiValueMap queryParameters = template.getQueryParams();
        boolean append = !queryParameters.isEmpty();
        for (String propertyName : Arrays.asList(pagePropertyName, sizePropertyName)) {
            if (queryParameters.containsKey((Object)propertyName)) continue;
            TemplateVariable.VariableType type = append ? TemplateVariable.VariableType.REQUEST_PARAM_CONTINUED : TemplateVariable.VariableType.REQUEST_PARAM;
            String description = String.format("pagination.%s.description", propertyName);
            names.add(new TemplateVariable(propertyName, type, description));
        }
        TemplateVariables pagingVariables = new TemplateVariables(names);
        return pagingVariables.concat(this.sortResolver.getSortTemplateVariables(parameter, template));
    }

    public void enhance(UriComponentsBuilder builder, @Nullable MethodParameter parameter, Object value) {
        Assert.notNull((Object)builder, (String)"UriComponentsBuilder must not be null!");
        if (!(value instanceof Pageable)) {
            return;
        }
        Pageable pageable = (Pageable)value;
        if (pageable.isUnpaged()) {
            return;
        }
        String pagePropertyName = this.getParameterNameToUse(this.getPageParameterName(), parameter);
        String sizePropertyName = this.getParameterNameToUse(this.getSizeParameterName(), parameter);
        int pageNumber = pageable.getPageNumber();
        builder.replaceQueryParam(pagePropertyName, new Object[]{this.isOneIndexedParameters() ? pageNumber + 1 : pageNumber});
        builder.replaceQueryParam(sizePropertyName, new Object[]{pageable.getPageSize() <= this.getMaxPageSize() ? pageable.getPageSize() : this.getMaxPageSize()});
        this.sortResolver.enhance(builder, parameter, pageable.getSort());
    }

    private static HateoasSortHandlerMethodArgumentResolver getDefaultedSortResolver(@Nullable HateoasSortHandlerMethodArgumentResolver sortResolver) {
        return sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }
}

