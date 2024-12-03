/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.modelapi.validation;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSetterMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.api.model.ResourceModelIssue;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.modelapi.validation.AbstractModelValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

public class BasicValidator
extends AbstractModelValidator {
    private static final Set<Class> ParamAnnotationSET = BasicValidator.createParamAnnotationSet();

    @Override
    public void visitAbstractResource(AbstractResource resource) {
        if (resource.isRootResource() && (null == resource.getPath() || null == resource.getPath().getValue())) {
            this.issueList.add(new ResourceModelIssue(resource, ImplMessages.ERROR_RES_URI_PATH_INVALID(resource.getResourceClass(), resource.getPath()), true));
        }
        this.checkNonPublicMethods(resource);
    }

    @Override
    public void visitAbstractResourceConstructor(AbstractResourceConstructor constructor) {
    }

    @Override
    public void visitAbstractField(AbstractField field) {
        Field f = field.getField();
        this.checkParameter(field.getParameters().get(0), f, f.toGenericString(), f.getName());
    }

    @Override
    public void visitAbstractSetterMethod(AbstractSetterMethod setterMethod) {
        Method m = setterMethod.getMethod();
        this.checkParameter(setterMethod.getParameters().get(0), m, m.toGenericString(), "1");
    }

    @Override
    public void visitAbstractResourceMethod(AbstractResourceMethod method) {
        Type type;
        this.checkParameters(method, method.getMethod());
        if ("GET".equals(method.getHttpMethod()) && !this.isRequestResponseMethod(method)) {
            if (Void.TYPE == method.getMethod().getReturnType()) {
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_RETURNS_VOID(method.getMethod()), false));
            }
            if (method.hasEntity()) {
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_CONSUMES_ENTITY(method.getMethod()), false));
            }
            for (Parameter parameter : method.getParameters()) {
                if (!parameter.isAnnotationPresent(FormParam.class)) continue;
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_CONSUMES_FORM_PARAM(method.getMethod()), true));
                break;
            }
        }
        LinkedList<String> httpAnnotList = new LinkedList<String>();
        for (Annotation a : method.getMethod().getDeclaredAnnotations()) {
            if (null != a.annotationType().getAnnotation(HttpMethod.class)) {
                httpAnnotList.add(a.toString());
                continue;
            }
            if (a.annotationType() != Path.class || method instanceof AbstractSubResourceMethod) continue;
            this.issueList.add(new ResourceModelIssue(method, ImplMessages.SUB_RES_METHOD_TREATED_AS_RES_METHOD(method.getMethod(), ((Path)a).value()), false));
        }
        if (httpAnnotList.size() > 1) {
            this.issueList.add(new ResourceModelIssue(method, ImplMessages.MULTIPLE_HTTP_METHOD_DESIGNATORS(method.getMethod(), ((Object)httpAnnotList).toString()), true));
        }
        if (!this.isConcreteType(type = method.getGenericReturnType())) {
            this.issueList.add(new ResourceModelIssue(method.getMethod(), "Return type " + type + " of method " + method.getMethod().toGenericString() + " is not resolvable to a concrete type", false));
        }
    }

    @Override
    public void visitAbstractSubResourceMethod(AbstractSubResourceMethod method) {
        this.visitAbstractResourceMethod(method);
        if (null == method.getPath() || null == method.getPath().getValue() || method.getPath().getValue().length() == 0) {
            this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_SUBRES_METHOD_URI_PATH_INVALID(method.getMethod(), method.getPath()), true));
        }
    }

    @Override
    public void visitAbstractSubResourceLocator(AbstractSubResourceLocator locator) {
        this.checkParameters(locator, locator.getMethod());
        if (Void.TYPE == locator.getMethod().getReturnType()) {
            this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_RETURNS_VOID(locator.getMethod()), true));
        }
        if (null == locator.getPath() || null == locator.getPath().getValue() || locator.getPath().getValue().length() == 0) {
            this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_URI_PATH_INVALID(locator.getMethod(), locator.getPath()), true));
        }
        for (Parameter parameter : locator.getParameters()) {
            if (Parameter.Source.ENTITY != parameter.getSource()) continue;
            this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_HAS_ENTITY_PARAM(locator.getMethod()), true));
        }
    }

    private static Set<Class> createParamAnnotationSet() {
        HashSet<Class> set = new HashSet<Class>(6);
        set.add(Context.class);
        set.add(HeaderParam.class);
        set.add(CookieParam.class);
        set.add(MatrixParam.class);
        set.add(QueryParam.class);
        set.add(PathParam.class);
        return Collections.unmodifiableSet(set);
    }

    private void checkParameter(Parameter p, Object source, String nameForLogging, String paramNameForLogging) {
        Type t;
        int annotCount = 0;
        for (Annotation a : p.getAnnotations()) {
            if (!ParamAnnotationSET.contains(a.annotationType()) || ++annotCount <= 1) continue;
            this.issueList.add(new ResourceModelIssue(source, ImplMessages.AMBIGUOUS_PARAMETER(nameForLogging, paramNameForLogging), false));
            break;
        }
        if (!this.isConcreteType(t = p.getParameterType())) {
            this.issueList.add(new ResourceModelIssue(source, "Parameter " + paramNameForLogging + " of type " + t + " from " + nameForLogging + " is not resolvable to a concrete type", false));
        }
    }

    private boolean isConcreteType(Type t) {
        if (t instanceof ParameterizedType) {
            return this.isConcreteParameterizedType((ParameterizedType)t);
        }
        return t instanceof Class;
    }

    private boolean isConcreteParameterizedType(ParameterizedType pt) {
        boolean isConcrete = true;
        for (Type t : pt.getActualTypeArguments()) {
            isConcrete &= this.isConcreteType(t);
        }
        return isConcrete;
    }

    private void checkParameters(Parameterized pl, Method m) {
        int paramCount = 0;
        for (Parameter p : pl.getParameters()) {
            this.checkParameter(p, m, m.toGenericString(), Integer.toString(++paramCount));
        }
    }

    private List<Method> getDeclaredMethods(final Class _c) {
        final ArrayList<Method> ml = new ArrayList<Method>();
        AccessController.doPrivileged(new PrivilegedAction<Object>(){
            Class c;
            {
                this.c = _c;
            }

            @Override
            public Object run() {
                while (this.c != Object.class && this.c != null) {
                    ml.addAll(Arrays.asList(this.c.getDeclaredMethods()));
                    this.c = this.c.getSuperclass();
                }
                return null;
            }
        });
        return ml;
    }

    private void checkNonPublicMethods(AbstractResource ar) {
        MethodList declaredMethods = new MethodList(this.getDeclaredMethods(ar.getResourceClass()));
        for (AnnotatedMethod m : declaredMethods.hasMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_RES_METHOD(m.getMethod().toGenericString()), false));
        }
        for (AnnotatedMethod m : declaredMethods.hasMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_SUB_RES_METHOD(m.getMethod().toGenericString()), false));
        }
        for (AnnotatedMethod m : declaredMethods.hasNotMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_SUB_RES_LOC(m.getMethod().toGenericString()), false));
        }
    }

    private boolean isRequestResponseMethod(AbstractResourceMethod method) {
        return method.getMethod().getParameterTypes().length == 2 && HttpRequestContext.class == method.getMethod().getParameterTypes()[0] && HttpResponseContext.class == method.getMethod().getParameterTypes()[1];
    }
}

