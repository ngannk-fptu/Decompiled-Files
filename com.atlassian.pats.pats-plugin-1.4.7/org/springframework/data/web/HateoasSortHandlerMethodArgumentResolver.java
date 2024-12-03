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
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.data.web;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.server.mvc.UriComponentsContributor;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class HateoasSortHandlerMethodArgumentResolver
extends SortHandlerMethodArgumentResolver
implements UriComponentsContributor {
    public TemplateVariables getSortTemplateVariables(MethodParameter parameter, UriComponents template) {
        boolean append;
        String sortParameter = this.getSortParameter(parameter);
        MultiValueMap queryParameters = template.getQueryParams();
        boolean bl = append = !queryParameters.isEmpty();
        if (queryParameters.containsKey((Object)sortParameter)) {
            return TemplateVariables.NONE;
        }
        String description = String.format("pagination.%s.description", sortParameter);
        TemplateVariable.VariableType type = append ? TemplateVariable.VariableType.REQUEST_PARAM_CONTINUED : TemplateVariable.VariableType.REQUEST_PARAM;
        return new TemplateVariables(new TemplateVariable[]{new TemplateVariable(sortParameter, type, description)});
    }

    public void enhance(UriComponentsBuilder builder, @Nullable MethodParameter parameter, @Nullable Object value) {
        if (!(value instanceof Sort)) {
            return;
        }
        Sort sort = (Sort)value;
        String sortParameter = this.getSortParameter(parameter);
        builder.replaceQueryParam(sortParameter, new Object[0]);
        for (String expression : this.foldIntoExpressions(sort)) {
            builder.queryParam(sortParameter, new Object[]{expression});
        }
    }
}

