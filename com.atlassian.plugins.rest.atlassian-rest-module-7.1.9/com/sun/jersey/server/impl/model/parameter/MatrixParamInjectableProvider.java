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
import java.util.List;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.PathSegment;

public final class MatrixParamInjectableProvider
extends BaseParamInjectableProvider<MatrixParam> {
    public MatrixParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, MatrixParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new MatrixParamInjectable(e, !c.isEncoded());
    }

    private static final class MatrixParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;

        MatrixParamInjectable(MultivaluedParameterExtractor extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }

        @Override
        public Object getValue(HttpContext context) {
            List<PathSegment> l = context.getUriInfo().getPathSegments(this.decode);
            PathSegment p = l.get(l.size() - 1);
            try {
                return this.extractor.extract(p.getMatrixParameters());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.MatrixParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}

