/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.server.impl.model.method.dispatch.AbstractResourceMethodDispatchProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

public class FormDispatchProvider
extends AbstractResourceMethodDispatchProvider {
    public static final String FORM_PROPERTY = "com.sun.jersey.api.representation.form";
    @Context
    private MultivaluedParameterExtractorProvider mpep;

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        if ("GET".equals(abstractResourceMethod.getHttpMethod())) {
            return null;
        }
        return super.create(abstractResourceMethod, invoker);
    }

    @Override
    protected InjectableValuesProvider getInjectableValuesProvider(AbstractResourceMethod abstractResourceMethod) {
        List<Injectable> is = this.processParameters(abstractResourceMethod);
        if (is == null) {
            return null;
        }
        return new FormParameterProvider(is);
    }

    protected MultivaluedParameterExtractorProvider getMultivaluedParameterExtractorProvider() {
        return this.mpep;
    }

    private void processForm(HttpContext context) {
        Form form = (Form)context.getProperties().get(FORM_PROPERTY);
        if (form == null) {
            form = context.getRequest().getEntity(Form.class);
            context.getProperties().put(FORM_PROPERTY, form);
        }
    }

    private List<Injectable> processParameters(AbstractResourceMethod method) {
        if (method.getParameters().isEmpty()) {
            return null;
        }
        boolean hasFormParam = false;
        for (int i = 0; i < method.getParameters().size(); ++i) {
            Parameter parameter = method.getParameters().get(i);
            if (parameter.getAnnotation() == null) continue;
            hasFormParam |= parameter.getAnnotation().annotationType() == FormParam.class;
        }
        if (!hasFormParam) {
            return null;
        }
        return this.getInjectables(method);
    }

    protected List<Injectable> getInjectables(AbstractResourceMethod method) {
        ArrayList<Injectable> is = new ArrayList<Injectable>(method.getParameters().size());
        for (int i = 0; i < method.getParameters().size(); ++i) {
            Parameter p = method.getParameters().get(i);
            if (Parameter.Source.ENTITY == p.getSource()) {
                if (MultivaluedMap.class.isAssignableFrom(p.getParameterClass())) {
                    is.add(new FormEntityInjectable(p.getParameterClass(), p.getParameterType(), p.getAnnotations()));
                    continue;
                }
                is.add(null);
                continue;
            }
            Injectable injectable = this.getInjectableProviderContext().getInjectable((AccessibleObject)method.getMethod(), p, ComponentScope.PerRequest);
            is.add(injectable);
        }
        return is;
    }

    private static final class FormEntityInjectable
    extends AbstractHttpContextInjectable<Object> {
        final Class<?> c;
        final Type t;
        final Annotation[] as;

        FormEntityInjectable(Class c, Type t, Annotation[] as) {
            this.c = c;
            this.t = t;
            this.as = as;
        }

        @Override
        public Object getValue(HttpContext context) {
            return context.getProperties().get(FormDispatchProvider.FORM_PROPERTY);
        }
    }

    private final class FormParameterProvider
    extends InjectableValuesProvider {
        public FormParameterProvider(List<Injectable> is) {
            super(is);
        }

        @Override
        public Object[] getInjectableValues(HttpContext context) {
            FormDispatchProvider.this.processForm(context);
            return super.getInjectableValues(context);
        }
    }
}

