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
import javax.ws.rs.QueryParam;

public final class QueryParamInjectableProvider
extends BaseParamInjectableProvider<QueryParam> {
    public QueryParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, QueryParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new QueryParamInjectable(e, !c.isEncoded());
    }

    private static final class QueryParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;

        QueryParamInjectable(MultivaluedParameterExtractor extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }

        @Override
        public Object getValue(HttpContext context) {
            try {
                return this.extractor.extract(context.getUriInfo().getQueryParameters(this.decode));
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.QueryParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}

