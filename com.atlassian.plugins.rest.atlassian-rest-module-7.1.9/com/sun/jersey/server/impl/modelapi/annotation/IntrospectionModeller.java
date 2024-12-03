/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.modelapi.annotation;

import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSetterMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.api.model.PathValue;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.impl.ImplMessages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

public class IntrospectionModeller {
    private static final Logger LOGGER = Logger.getLogger(IntrospectionModeller.class.getName());
    private static final Map<Class, ParamAnnotationHelper> ANOT_HELPER_MAP = IntrospectionModeller.createParamAnotHelperMap();

    public static AbstractResource createResource(Class<?> resourceClass) {
        Class annotatedResourceClass = IntrospectionModeller.getAnnotatedResourceClass(resourceClass);
        Path rPathAnnotation = annotatedResourceClass.getAnnotation(Path.class);
        boolean isRootResourceClass = null != rPathAnnotation;
        boolean isEncodedAnotOnClass = null != annotatedResourceClass.getAnnotation(Encoded.class);
        AbstractResource resource = isRootResourceClass ? new AbstractResource(resourceClass, new PathValue(rPathAnnotation.value())) : new AbstractResource(resourceClass);
        IntrospectionModeller.workOutConstructorsList(resource, resourceClass.getConstructors(), isEncodedAnotOnClass);
        IntrospectionModeller.workOutFieldsList(resource, isEncodedAnotOnClass);
        MethodList methodList = new MethodList(resourceClass);
        IntrospectionModeller.workOutSetterMethodsList(resource, methodList, isEncodedAnotOnClass);
        Consumes classScopeConsumesAnnotation = annotatedResourceClass.getAnnotation(Consumes.class);
        Produces classScopeProducesAnnotation = annotatedResourceClass.getAnnotation(Produces.class);
        IntrospectionModeller.workOutResourceMethodsList(resource, methodList, isEncodedAnotOnClass, classScopeConsumesAnnotation, classScopeProducesAnnotation);
        IntrospectionModeller.workOutSubResourceMethodsList(resource, methodList, isEncodedAnotOnClass, classScopeConsumesAnnotation, classScopeProducesAnnotation);
        IntrospectionModeller.workOutSubResourceLocatorsList(resource, methodList, isEncodedAnotOnClass);
        IntrospectionModeller.workOutPostConstructPreDestroy(resource);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(ImplMessages.NEW_AR_CREATED_BY_INTROSPECTION_MODELER(resource.toString()));
        }
        return resource;
    }

    private static Class getAnnotatedResourceClass(Class rc) {
        if (rc.isAnnotationPresent(Path.class)) {
            return rc;
        }
        for (Class<?> i : rc.getInterfaces()) {
            if (!i.isAnnotationPresent(Path.class)) continue;
            return i;
        }
        return rc;
    }

    private static void addConsumes(AnnotatedMethod am, AbstractResourceMethod resourceMethod, Consumes consumeMimeAnnotation) {
        if (am.isAnnotationPresent(Consumes.class)) {
            consumeMimeAnnotation = am.getAnnotation(Consumes.class);
        }
        resourceMethod.setAreInputTypesDeclared(consumeMimeAnnotation != null);
        resourceMethod.getSupportedInputTypes().addAll(MediaTypes.createMediaTypes(consumeMimeAnnotation));
    }

    private static void addProduces(AnnotatedMethod am, AbstractResourceMethod resourceMethod, Produces produceMimeAnnotation) {
        if (am.isAnnotationPresent(Produces.class)) {
            produceMimeAnnotation = am.getAnnotation(Produces.class);
        }
        resourceMethod.setAreOutputTypesDeclared(produceMimeAnnotation != null);
        resourceMethod.getSupportedOutputTypes().addAll(MediaTypes.createQualitySourceMediaTypes(produceMimeAnnotation));
    }

    private static void workOutConstructorsList(AbstractResource resource, Constructor[] ctorArray, boolean isEncoded) {
        if (null != ctorArray) {
            for (Constructor ctor : ctorArray) {
                AbstractResourceConstructor aCtor = new AbstractResourceConstructor(ctor);
                IntrospectionModeller.processParameters(resource.getResourceClass(), ctor.getDeclaringClass(), (Parameterized)aCtor, ctor, isEncoded);
                resource.getConstructors().add(aCtor);
            }
        }
    }

    private static void workOutFieldsList(AbstractResource resource, boolean isEncoded) {
        Class<?> c = resource.getResourceClass();
        if (c.isInterface()) {
            return;
        }
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getDeclaredAnnotations().length <= 0) continue;
                AbstractField af = new AbstractField(f);
                Parameter p = IntrospectionModeller.createParameter(resource.getResourceClass(), f.getDeclaringClass(), isEncoded, f.getType(), f.getGenericType(), f.getAnnotations());
                if (null == p) continue;
                af.getParameters().add(p);
                resource.getFields().add(af);
            }
            c = c.getSuperclass();
        }
    }

    private static void workOutPostConstructPreDestroy(AbstractResource resource) {
        Method method;
        Class<?> postConstruct = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PostConstruct"));
        if (postConstruct == null) {
            return;
        }
        Class<?> preDestroy = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PreDestroy"));
        MethodList methodList = new MethodList(resource.getResourceClass(), true);
        HashSet<String> names = new HashSet<String>();
        for (AnnotatedMethod m : methodList.hasAnnotation(postConstruct).hasNumParams(0).hasReturnType(Void.TYPE)) {
            method = m.getMethod();
            if (!names.add(method.getName())) continue;
            AccessController.doPrivileged(ReflectionHelper.setAccessibleMethodPA(method));
            resource.getPostConstructMethods().add(0, method);
        }
        names = new HashSet();
        for (AnnotatedMethod m : methodList.hasAnnotation(preDestroy).hasNumParams(0).hasReturnType(Void.TYPE)) {
            method = m.getMethod();
            if (!names.add(method.getName())) continue;
            AccessController.doPrivileged(ReflectionHelper.setAccessibleMethodPA(method));
            resource.getPreDestroyMethods().add(method);
        }
    }

    private static void workOutSetterMethodsList(AbstractResource resource, MethodList methodList, boolean isEncoded) {
        for (AnnotatedMethod m : methodList.hasNotMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).hasNumParams(1).hasReturnType(Void.TYPE).nameStartsWith("set")) {
            AbstractSetterMethod asm = new AbstractSetterMethod(resource, m.getMethod(), m.getAnnotations());
            Parameter p = IntrospectionModeller.createParameter(resource.getResourceClass(), m.getMethod().getDeclaringClass(), isEncoded, m.getParameterTypes()[0], m.getGenericParameterTypes()[0], m.getAnnotations());
            if (null == p) continue;
            asm.getParameters().add(p);
            resource.getSetterMethods().add(asm);
        }
    }

    private static void workOutResourceMethodsList(AbstractResource resource, MethodList methodList, boolean isEncoded, Consumes classScopeConsumesAnnotation, Produces classScopeProducesAnnotation) {
        for (AnnotatedMethod m : methodList.hasMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class)) {
            ReflectionHelper.ClassTypePair ct = IntrospectionModeller.getGenericReturnType(resource.getResourceClass(), m.getMethod());
            AbstractResourceMethod resourceMethod = new AbstractResourceMethod(resource, m.getMethod(), ct.c, ct.t, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
            IntrospectionModeller.addConsumes(m, resourceMethod, classScopeConsumesAnnotation);
            IntrospectionModeller.addProduces(m, resourceMethod, classScopeProducesAnnotation);
            IntrospectionModeller.processParameters(resourceMethod.getResource().getResourceClass(), resourceMethod.getMethod().getDeclaringClass(), (Parameterized)resourceMethod, m, isEncoded);
            resource.getResourceMethods().add(resourceMethod);
        }
    }

    private static ReflectionHelper.ClassTypePair getGenericReturnType(Class concreteClass, Method m) {
        return IntrospectionModeller.getGenericType(concreteClass, m.getDeclaringClass(), m.getReturnType(), m.getGenericReturnType());
    }

    private static void workOutSubResourceMethodsList(AbstractResource resource, MethodList methodList, boolean isEncoded, Consumes classScopeConsumesAnnotation, Produces classScopeProducesAnnotation) {
        for (AnnotatedMethod m : methodList.hasMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class)) {
            ReflectionHelper.ClassTypePair ct;
            boolean emptySegmentCase;
            Path mPathAnnotation = m.getAnnotation(Path.class);
            PathValue pv = new PathValue(mPathAnnotation.value());
            boolean bl = emptySegmentCase = "/".equals(pv.getValue()) || "".equals(pv.getValue());
            if (!emptySegmentCase) {
                ct = IntrospectionModeller.getGenericReturnType(resource.getResourceClass(), m.getMethod());
                AbstractSubResourceMethod abstractSubResourceMethod = new AbstractSubResourceMethod(resource, m.getMethod(), ct.c, ct.t, pv, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
                IntrospectionModeller.addConsumes(m, abstractSubResourceMethod, classScopeConsumesAnnotation);
                IntrospectionModeller.addProduces(m, abstractSubResourceMethod, classScopeProducesAnnotation);
                IntrospectionModeller.processParameters(abstractSubResourceMethod.getResource().getResourceClass(), abstractSubResourceMethod.getMethod().getDeclaringClass(), (Parameterized)abstractSubResourceMethod, m, isEncoded);
                resource.getSubResourceMethods().add(abstractSubResourceMethod);
                continue;
            }
            ct = IntrospectionModeller.getGenericReturnType(resource.getResourceClass(), m.getMethod());
            AbstractResourceMethod abstractResourceMethod = new AbstractResourceMethod(resource, m.getMethod(), ct.c, ct.t, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
            IntrospectionModeller.addConsumes(m, abstractResourceMethod, classScopeConsumesAnnotation);
            IntrospectionModeller.addProduces(m, abstractResourceMethod, classScopeProducesAnnotation);
            IntrospectionModeller.processParameters(abstractResourceMethod.getResource().getResourceClass(), abstractResourceMethod.getMethod().getDeclaringClass(), (Parameterized)abstractResourceMethod, m, isEncoded);
            resource.getResourceMethods().add(abstractResourceMethod);
        }
    }

    private static void workOutSubResourceLocatorsList(AbstractResource resource, MethodList methodList, boolean isEncoded) {
        for (AnnotatedMethod m : methodList.hasNotMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class)) {
            Path mPathAnnotation = m.getAnnotation(Path.class);
            AbstractSubResourceLocator subResourceLocator = new AbstractSubResourceLocator(resource, m.getMethod(), new PathValue(mPathAnnotation.value()), m.getAnnotations());
            IntrospectionModeller.processParameters(subResourceLocator.getResource().getResourceClass(), subResourceLocator.getMethod().getDeclaringClass(), (Parameterized)subResourceLocator, m, isEncoded);
            resource.getSubResourceLocators().add(subResourceLocator);
        }
    }

    private static void processParameters(Class concreteClass, Class declaringClass, Parameterized parametrized, Constructor ctor, boolean isEncoded) {
        Type[] genericParameterTypes;
        Class[] parameterTypes = ctor.getParameterTypes();
        if (parameterTypes.length != (genericParameterTypes = ctor.getGenericParameterTypes()).length) {
            Type[] _genericParameterTypes = new Type[parameterTypes.length];
            _genericParameterTypes[0] = parameterTypes[0];
            System.arraycopy(genericParameterTypes, 0, _genericParameterTypes, 1, genericParameterTypes.length);
            genericParameterTypes = _genericParameterTypes;
        }
        IntrospectionModeller.processParameters(concreteClass, declaringClass, parametrized, null != ctor.getAnnotation(Encoded.class) || isEncoded, parameterTypes, genericParameterTypes, ctor.getParameterAnnotations());
    }

    private static void processParameters(Class concreteClass, Class declaringClass, Parameterized parametrized, AnnotatedMethod method, boolean isEncoded) {
        IntrospectionModeller.processParameters(concreteClass, declaringClass, parametrized, null != method.getAnnotation(Encoded.class) || isEncoded, method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations());
    }

    private static void processParameters(Class concreteClass, Class declaringClass, Parameterized parametrized, boolean isEncoded, Class[] parameterTypes, Type[] genericParameterTypes, Annotation[][] parameterAnnotations) {
        for (int i = 0; i < parameterTypes.length; ++i) {
            Parameter parameter = IntrospectionModeller.createParameter(concreteClass, declaringClass, isEncoded, parameterTypes[i], genericParameterTypes[i], parameterAnnotations[i]);
            if (null == parameter) {
                parametrized.getParameters().removeAll(parametrized.getParameters());
                break;
            }
            parametrized.getParameters().add(parameter);
        }
    }

    private static Map<Class, ParamAnnotationHelper> createParamAnotHelperMap() {
        WeakHashMap<Class<FormParam>, ParamAnnotationHelper<Context>> m = new WeakHashMap<Class<FormParam>, ParamAnnotationHelper<Context>>();
        m.put(Context.class, new ParamAnnotationHelper<Context>(){

            @Override
            public String getValueOf(Context a) {
                return null;
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.CONTEXT;
            }
        });
        m.put(HeaderParam.class, new ParamAnnotationHelper<HeaderParam>(){

            @Override
            public String getValueOf(HeaderParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.HEADER;
            }
        });
        m.put(CookieParam.class, new ParamAnnotationHelper<CookieParam>(){

            @Override
            public String getValueOf(CookieParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.COOKIE;
            }
        });
        m.put(MatrixParam.class, new ParamAnnotationHelper<MatrixParam>(){

            @Override
            public String getValueOf(MatrixParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.MATRIX;
            }
        });
        m.put(QueryParam.class, new ParamAnnotationHelper<QueryParam>(){

            @Override
            public String getValueOf(QueryParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.QUERY;
            }
        });
        m.put(PathParam.class, new ParamAnnotationHelper<PathParam>(){

            @Override
            public String getValueOf(PathParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.PATH;
            }
        });
        m.put(FormParam.class, new ParamAnnotationHelper<FormParam>(){

            @Override
            public String getValueOf(FormParam a) {
                return a.value();
            }

            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.FORM;
            }
        });
        return Collections.unmodifiableMap(m);
    }

    private static Parameter createParameter(Class concreteClass, Class declaringClass, boolean isEncoded, Class<?> paramClass, Type paramType, Annotation[] annotations) {
        if (null == annotations) {
            return null;
        }
        Annotation paramAnnotation = null;
        Parameter.Source paramSource = null;
        String paramName = null;
        boolean paramEncoded = isEncoded;
        String paramDefault = null;
        for (Annotation annotation : annotations) {
            if (ANOT_HELPER_MAP.containsKey(annotation.annotationType())) {
                ParamAnnotationHelper helper = ANOT_HELPER_MAP.get(annotation.annotationType());
                paramAnnotation = annotation;
                paramSource = helper.getSource();
                paramName = helper.getValueOf(annotation);
                continue;
            }
            if (Encoded.class == annotation.annotationType()) {
                paramEncoded = true;
                continue;
            }
            if (DefaultValue.class == annotation.annotationType()) {
                paramDefault = ((DefaultValue)annotation).value();
                continue;
            }
            if (paramAnnotation != null) continue;
            paramAnnotation = annotation;
            paramSource = Parameter.Source.UNKNOWN;
            paramName = IntrospectionModeller.getValue(annotation);
        }
        if (paramAnnotation == null) {
            paramSource = Parameter.Source.ENTITY;
        }
        ReflectionHelper.ClassTypePair ct = IntrospectionModeller.getGenericType(concreteClass, declaringClass, paramClass, paramType);
        paramType = ct.t;
        paramClass = ct.c;
        return new Parameter(annotations, paramAnnotation, paramSource, paramName, paramType, paramClass, paramEncoded, paramDefault);
    }

    private static String getValue(Annotation a) {
        try {
            Method m = a.annotationType().getMethod("value", new Class[0]);
            if (m.getReturnType() != String.class) {
                return null;
            }
            return (String)m.invoke((Object)a, new Object[0]);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static ReflectionHelper.ClassTypePair getGenericType(Class concreteClass, Class declaringClass, Class c, Type t) {
        if (t instanceof TypeVariable) {
            ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(concreteClass, declaringClass, (TypeVariable)t);
            if (ct != null) {
                return ct;
            }
        } else if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            final Type[] ptts = pt.getActualTypeArguments();
            boolean modified = false;
            for (int i = 0; i < ptts.length; ++i) {
                ReflectionHelper.ClassTypePair ct = IntrospectionModeller.getGenericType(concreteClass, declaringClass, (Class)pt.getRawType(), ptts[i]);
                if (ct.t == ptts[i]) continue;
                ptts[i] = ct.t;
                modified = true;
            }
            if (modified) {
                ParameterizedType rpt = new ParameterizedType(){

                    @Override
                    public Type[] getActualTypeArguments() {
                        return (Type[])ptts.clone();
                    }

                    @Override
                    public Type getRawType() {
                        return pt.getRawType();
                    }

                    @Override
                    public Type getOwnerType() {
                        return pt.getOwnerType();
                    }
                };
                return new ReflectionHelper.ClassTypePair((Class)pt.getRawType(), rpt);
            }
        } else if (t instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)t;
            ReflectionHelper.ClassTypePair ct = IntrospectionModeller.getGenericType(concreteClass, declaringClass, null, gat.getGenericComponentType());
            if (gat.getGenericComponentType() != ct.t) {
                try {
                    Class ac = ReflectionHelper.getArrayClass(ct.c);
                    return new ReflectionHelper.ClassTypePair(ac, ac);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return new ReflectionHelper.ClassTypePair(c, t);
    }

    private static interface ParamAnnotationHelper<T extends Annotation> {
        public String getValueOf(T var1);

        public Parameter.Source getSource();
    }
}

