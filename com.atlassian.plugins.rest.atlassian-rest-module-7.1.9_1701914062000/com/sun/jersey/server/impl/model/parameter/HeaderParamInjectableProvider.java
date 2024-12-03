/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.api.ParamException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.model.parameter.BaseParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.inject.Injectable;
import javax.ws.rs.HeaderParam;

public final class HeaderParamInjectableProvider
extends BaseParamInjectableProvider<HeaderParam> {
    public HeaderParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, HeaderParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new HeaderParamInjectable(e);
    }

    private static final class HeaderParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private MultivaluedParameterExtractor extractor;

        HeaderParamInjectable(MultivaluedParameterExtractor extractor) {
            this.extractor = extractor;
        }

        @Override
        public Object getValue(HttpContext context) {
            try {
                return this.extractor.extract(context.getRequest().getRequestHeaders());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.HeaderParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}

