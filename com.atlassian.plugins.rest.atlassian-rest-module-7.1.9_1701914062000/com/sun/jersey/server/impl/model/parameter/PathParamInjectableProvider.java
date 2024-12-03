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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;

public final class PathParamInjectableProvider
extends BaseParamInjectableProvider<PathParam> {
    public PathParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext ic, PathParam a, Parameter c) {
        ParameterizedType pt;
        Type[] targs;
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        if (c.getParameterClass() == PathSegment.class) {
            return new PathParamPathSegmentInjectable(parameterName, !c.isEncoded());
        }
        if (c.getParameterClass() == List.class && c.getParameterType() instanceof ParameterizedType && (targs = (pt = (ParameterizedType)c.getParameterType()).getActualTypeArguments()).length == 1 && targs[0] == PathSegment.class) {
            return new PathParamListPathSegmentInjectable(parameterName, !c.isEncoded());
        }
        MultivaluedParameterExtractor e = this.getWithoutDefaultValue(c);
        if (e == null) {
            return null;
        }
        return new PathParamInjectable(e, !c.isEncoded());
    }

    private static final class PathParamListPathSegmentInjectable
    extends AbstractHttpContextInjectable<List<PathSegment>> {
        private final String name;
        private final boolean decode;

        PathParamListPathSegmentInjectable(String name, boolean decode) {
            this.name = name;
            this.decode = decode;
        }

        @Override
        public List<PathSegment> getValue(HttpContext context) {
            return context.getUriInfo().getPathSegments(this.name, this.decode);
        }
    }

    private static final class PathParamPathSegmentInjectable
    extends AbstractHttpContextInjectable<PathSegment> {
        private final String name;
        private final boolean decode;

        PathParamPathSegmentInjectable(String name, boolean decode) {
            this.name = name;
            this.decode = decode;
        }

        @Override
        public PathSegment getValue(HttpContext context) {
            List<PathSegment> ps = context.getUriInfo().getPathSegments(this.name, this.decode);
            if (ps.isEmpty()) {
                return null;
            }
            return ps.get(ps.size() - 1);
        }
    }

    private static final class PathParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;

        PathParamInjectable(MultivaluedParameterExtractor extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }

        @Override
        public Object getValue(HttpContext context) {
            try {
                return this.extractor.extract(context.getUriInfo().getPathParameters(this.decode));
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.PathParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}

