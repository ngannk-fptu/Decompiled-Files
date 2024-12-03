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
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;

public final class CookieParamInjectableProvider
extends BaseParamInjectableProvider<CookieParam> {
    public CookieParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, CookieParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        if (c.getParameterClass() == Cookie.class) {
            return new CookieTypeParamInjectable(parameterName);
        }
        MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new CookieParamInjectable(e);
    }

    private static final class CookieTypeParamInjectable
    extends AbstractHttpContextInjectable<Cookie> {
        private final String name;

        CookieTypeParamInjectable(String name) {
            this.name = name;
        }

        @Override
        public Cookie getValue(HttpContext context) {
            return context.getRequest().getCookies().get(this.name);
        }
    }

    private static final class CookieParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final MultivaluedParameterExtractor extractor;

        CookieParamInjectable(MultivaluedParameterExtractor extractor) {
            this.extractor = extractor;
        }

        @Override
        public Object getValue(HttpContext context) {
            try {
                return this.extractor.extract(context.getRequest().getCookieNameValueMap());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.CookieParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}

