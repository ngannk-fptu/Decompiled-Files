/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.Assert
 *  org.springframework.web.util.UriComponents
 */
package org.springframework.data.web;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;

class MethodParameterAwarePagedResourcesAssembler<T>
extends PagedResourcesAssembler<T> {
    private final MethodParameter parameter;

    public MethodParameterAwarePagedResourcesAssembler(MethodParameter parameter, @Nullable HateoasPageableHandlerMethodArgumentResolver resolver, @Nullable UriComponents baseUri) {
        super(resolver, baseUri);
        Assert.notNull((Object)parameter, (String)"Method parameter must not be null!");
        this.parameter = parameter;
    }

    @Override
    @Nonnull
    protected MethodParameter getMethodParameter() {
        return this.parameter;
    }
}

