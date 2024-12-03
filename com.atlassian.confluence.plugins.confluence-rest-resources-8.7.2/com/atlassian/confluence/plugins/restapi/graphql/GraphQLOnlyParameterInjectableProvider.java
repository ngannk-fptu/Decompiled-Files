/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.google.common.base.Defaults
 *  com.sun.jersey.api.core.HttpContext
 *  com.sun.jersey.api.model.Parameter
 *  com.sun.jersey.core.spi.component.ComponentContext
 *  com.sun.jersey.core.spi.component.ComponentScope
 *  com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable
 *  com.sun.jersey.spi.inject.Injectable
 *  com.sun.jersey.spi.inject.InjectableProvider
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.confluence.plugins.restapi.graphql;

import com.atlassian.confluence.plugins.restapi.graphql.ReflectionUtil;
import com.atlassian.graphql.annotations.GraphQLName;
import com.google.common.base.Defaults;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.ext.Provider;

@Provider
public class GraphQLOnlyParameterInjectableProvider
implements InjectableProvider<GraphQLName, Parameter> {
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    public Injectable getInjectable(ComponentContext ic, GraphQLName a, Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation.annotationType().getPackage().getName().equals(GraphQLName.class.getPackage().getName()) || annotation.annotationType() == DefaultValue.class) continue;
            return null;
        }
        return new GraphQLOnlyParameterInjectable(parameter.getParameterType());
    }

    private static final class GraphQLOnlyParameterInjectable
    extends AbstractHttpContextInjectable<Object> {
        private final Type parameterType;

        public GraphQLOnlyParameterInjectable(Type parameterType) {
            this.parameterType = parameterType;
        }

        public Object getValue(HttpContext context) {
            Class clazz = ReflectionUtil.getClazz(this.parameterType);
            return Collection.class.isAssignableFrom(clazz) ? GraphQLOnlyParameterInjectable.constructCollection(clazz) : Defaults.defaultValue((Class)clazz);
        }

        public static Collection constructCollection(Class clazz) {
            if (List.class.isAssignableFrom(clazz)) {
                return new ArrayList();
            }
            if (Set.class.isAssignableFrom(clazz)) {
                return new HashSet();
            }
            if (SortedSet.class.isAssignableFrom(clazz)) {
                return new TreeSet();
            }
            throw new RuntimeException("Unsupported collection type '" + clazz.getName() + "'");
        }
    }
}

