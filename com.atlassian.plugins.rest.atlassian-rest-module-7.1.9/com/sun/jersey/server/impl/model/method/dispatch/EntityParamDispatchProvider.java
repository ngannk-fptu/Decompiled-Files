/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.server.impl.model.method.dispatch.AbstractResourceMethodDispatchProvider;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityParamDispatchProvider
extends AbstractResourceMethodDispatchProvider {
    @Override
    protected InjectableValuesProvider getInjectableValuesProvider(AbstractResourceMethod abstractResourceMethod) {
        return new InjectableValuesProvider(this.processParameters(abstractResourceMethod));
    }

    private List<Injectable> processParameters(AbstractResourceMethod method) {
        Parameter parameter;
        int i;
        if (null == method.getParameters() || 0 == method.getParameters().size()) {
            return Collections.emptyList();
        }
        boolean hasEntity = false;
        ArrayList<Injectable> is = new ArrayList<Injectable>(method.getParameters().size());
        for (i = 0; i < method.getParameters().size(); ++i) {
            parameter = method.getParameters().get(i);
            if (Parameter.Source.ENTITY == parameter.getSource()) {
                hasEntity = true;
                is.add(this.processEntityParameter(parameter, method.getMethod().getParameterAnnotations()[i]));
                continue;
            }
            is.add(this.getInjectableProviderContext().getInjectable((AccessibleObject)method.getMethod(), parameter, ComponentScope.PerRequest));
        }
        if (hasEntity) {
            return is;
        }
        if (Collections.frequency(is, null) == 1) {
            i = is.lastIndexOf(null);
            parameter = method.getParameters().get(i);
            if (Parameter.Source.UNKNOWN == parameter.getSource() && !parameter.isQualified()) {
                Injectable ij = this.processEntityParameter(parameter, method.getMethod().getParameterAnnotations()[i]);
                is.set(i, ij);
            }
        }
        return is;
    }

    private Injectable processEntityParameter(Parameter parameter, Annotation[] annotations) {
        return new EntityInjectable(parameter.getParameterClass(), parameter.getParameterType(), annotations);
    }

    static final class EntityInjectable
    extends AbstractHttpContextInjectable<Object> {
        final Class<?> c;
        final Type t;
        final Annotation[] as;

        EntityInjectable(Class c, Type t, Annotation[] as) {
            this.c = c;
            this.t = t;
            this.as = as;
        }

        @Override
        public Object getValue(HttpContext context) {
            return context.getRequest().getEntity(this.c, this.t, this.as);
        }
    }
}

