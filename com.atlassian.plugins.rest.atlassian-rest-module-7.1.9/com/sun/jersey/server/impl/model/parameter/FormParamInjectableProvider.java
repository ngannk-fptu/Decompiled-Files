/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.api.ParamException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.model.parameter.BaseParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.inject.Injectable;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public final class FormParamInjectableProvider
extends BaseParamInjectableProvider<FormParam> {
    public FormParamInjectableProvider(MultivaluedParameterExtractorProvider w) {
        super(w);
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, FormParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new FormParamInjectable(e, !c.isEncoded());
    }

    private static final class FormParamInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final MultivaluedParameterExtractor extractor;

        FormParamInjectable(MultivaluedParameterExtractor extractor, boolean decode) {
            this.extractor = extractor;
        }

        @Override
        public Object getValue(HttpContext context) {
            Form form = this.getCachedForm(context);
            if (form == null) {
                form = this.getForm(context);
                this.cacheForm(context, form);
            }
            try {
                return this.extractor.extract(form);
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.FormParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }

        private void cacheForm(HttpContext context, Form form) {
            context.getProperties().put("com.sun.jersey.api.representation.form", form);
        }

        private Form getCachedForm(HttpContext context) {
            return (Form)context.getProperties().get("com.sun.jersey.api.representation.form");
        }

        private HttpRequestContext ensureValidRequest(HttpRequestContext r) throws IllegalStateException {
            if (r.getMethod().equals("GET")) {
                throw new IllegalStateException("The @FormParam is utilized when the request method is GET");
            }
            if (!MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, r.getMediaType())) {
                throw new IllegalStateException("The @FormParam is utilized when the content type of the request entity is not application/x-www-form-urlencoded");
            }
            return r;
        }

        private Form getForm(HttpContext context) {
            HttpRequestContext r = this.ensureValidRequest(context.getRequest());
            return r.getFormParameters();
        }
    }
}

