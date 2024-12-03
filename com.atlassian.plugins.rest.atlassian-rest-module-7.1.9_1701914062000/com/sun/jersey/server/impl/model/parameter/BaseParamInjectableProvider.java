/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.annotation.Annotation;

public abstract class BaseParamInjectableProvider<A extends Annotation>
implements InjectableProvider<A, Parameter> {
    private final MultivaluedParameterExtractorProvider mpep;

    BaseParamInjectableProvider(MultivaluedParameterExtractorProvider mpep) {
        this.mpep = mpep;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    protected MultivaluedParameterExtractor getWithoutDefaultValue(Parameter p) {
        return this.mpep.getWithoutDefaultValue(p);
    }

    protected MultivaluedParameterExtractor get(Parameter p) {
        return this.mpep.get(p);
    }
}

