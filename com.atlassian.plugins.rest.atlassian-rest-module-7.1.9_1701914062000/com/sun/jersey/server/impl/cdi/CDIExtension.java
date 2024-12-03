/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.event.Observes
 *  javax.enterprise.inject.spi.AfterBeanDiscovery
 *  javax.enterprise.inject.spi.AnnotatedCallable
 *  javax.enterprise.inject.spi.AnnotatedConstructor
 *  javax.enterprise.inject.spi.AnnotatedField
 *  javax.enterprise.inject.spi.AnnotatedMethod
 *  javax.enterprise.inject.spi.AnnotatedParameter
 *  javax.enterprise.inject.spi.AnnotatedType
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.BeanManager
 *  javax.enterprise.inject.spi.BeforeBeanDiscovery
 *  javax.enterprise.inject.spi.Extension
 *  javax.enterprise.inject.spi.InjectionPoint
 *  javax.enterprise.inject.spi.ProcessAnnotatedType
 *  javax.enterprise.inject.spi.ProcessInjectionTarget
 *  javax.enterprise.inject.spi.ProcessManagedBean
 *  javax.enterprise.util.AnnotationLiteral
 *  javax.inject.Inject
 *  javax.inject.Provider
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.server.impl.cdi.AbstractBean;
import com.sun.jersey.server.impl.cdi.AnnotatedCallableImpl;
import com.sun.jersey.server.impl.cdi.AnnotatedConstructorImpl;
import com.sun.jersey.server.impl.cdi.AnnotatedFieldImpl;
import com.sun.jersey.server.impl.cdi.AnnotatedMethodImpl;
import com.sun.jersey.server.impl.cdi.AnnotatedParameterImpl;
import com.sun.jersey.server.impl.cdi.AnnotatedTypeImpl;
import com.sun.jersey.server.impl.cdi.BeanGenerator;
import com.sun.jersey.server.impl.cdi.DiscoveredParameter;
import com.sun.jersey.server.impl.cdi.InitializedLater;
import com.sun.jersey.server.impl.cdi.ProviderBasedBean;
import com.sun.jersey.server.impl.cdi.SyntheticQualifier;
import com.sun.jersey.server.impl.cdi.Utils;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

public class CDIExtension
implements Extension {
    private static final Logger LOGGER = Logger.getLogger(CDIExtension.class.getName());
    private final javax.ws.rs.core.Context contextAnnotationLiteral = new ContextAnnotationLiteral();
    private final Inject injectAnnotationLiteral = new InjectAnnotationLiteral();
    private static final Set<Class<? extends Annotation>> knownParameterQualifiers = CDIExtension.initializeKnownParamQualifiers();
    private static final Map<Class<? extends Annotation>, Parameter.Source> paramQualifiersMap = CDIExtension.initializeKnownParamQualifiersMap();
    private static final Set<Class<?>> staticallyDefinedContextBeans = CDIExtension.initializeStaticallyDefinedContextBeans();
    private final Map<ClassLoader, WebApplication> webApplications = new HashMap<ClassLoader, WebApplication>();
    private volatile ResourceConfig resourceConfig;
    private volatile Map<Class<? extends Annotation>, Set<DiscoveredParameter>> discoveredParameterMap;
    private volatile Map<DiscoveredParameter, SyntheticQualifier> syntheticQualifierMap;
    private int nextSyntheticQualifierValue = 0;
    volatile List<InitializedLater> toBeInitializedLater;
    private static final String JNDI_CDIEXTENSION_NAME = "CDIExtension";
    private static final String JNDI_CDIEXTENSION_CTX = "com/sun/jersey/config";
    private static final String LOOKUP_EXTENSION_IN_BEAN_MANAGER_SYSTEM_PROPERTY = "com.sun.jersey.server.impl.cdi.lookupExtensionInBeanManager";
    public static final boolean lookupExtensionInBeanManager = CDIExtension.getLookupExtensionInBeanManager();

    private static Set<Class<? extends Annotation>> initializeKnownParamQualifiers() {
        HashSet<Class<javax.ws.rs.core.Context>> classes = new HashSet<Class<javax.ws.rs.core.Context>>();
        classes.add(CookieParam.class);
        classes.add(FormParam.class);
        classes.add(HeaderParam.class);
        classes.add(MatrixParam.class);
        classes.add(PathParam.class);
        classes.add(QueryParam.class);
        classes.add(javax.ws.rs.core.Context.class);
        return Collections.unmodifiableSet(classes);
    }

    private static Map<Class<? extends Annotation>, Parameter.Source> initializeKnownParamQualifiersMap() {
        HashMap<Class<javax.ws.rs.core.Context>, Parameter.Source> map = new HashMap<Class<javax.ws.rs.core.Context>, Parameter.Source>();
        map.put(CookieParam.class, Parameter.Source.COOKIE);
        map.put(FormParam.class, Parameter.Source.FORM);
        map.put(HeaderParam.class, Parameter.Source.HEADER);
        map.put(MatrixParam.class, Parameter.Source.MATRIX);
        map.put(PathParam.class, Parameter.Source.PATH);
        map.put(QueryParam.class, Parameter.Source.QUERY);
        map.put(javax.ws.rs.core.Context.class, Parameter.Source.CONTEXT);
        return Collections.unmodifiableMap(map);
    }

    private static Set<Class<?>> initializeStaticallyDefinedContextBeans() {
        HashSet<Class<WebApplication>> classes = new HashSet<Class<WebApplication>>();
        classes.add(Application.class);
        classes.add(HttpHeaders.class);
        classes.add(Providers.class);
        classes.add(Request.class);
        classes.add(SecurityContext.class);
        classes.add(UriInfo.class);
        classes.add(ExceptionMapperContext.class);
        classes.add(ExtendedUriInfo.class);
        classes.add(FeaturesAndProperties.class);
        classes.add(HttpContext.class);
        classes.add(HttpRequestContext.class);
        classes.add(HttpResponseContext.class);
        classes.add(MessageBodyWorkers.class);
        classes.add(ResourceContext.class);
        classes.add(WebApplication.class);
        return Collections.unmodifiableSet(classes);
    }

    private static boolean getLookupExtensionInBeanManager() {
        return Boolean.parseBoolean(System.getProperty(LOOKUP_EXTENSION_IN_BEAN_MANAGER_SYSTEM_PROPERTY, "false"));
    }

    public static CDIExtension getInitializedExtension() {
        try {
            InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                throw new RuntimeException();
            }
            return (CDIExtension)CDIExtension.lookupJerseyConfigJNDIContext(ic).lookup(JNDI_CDIEXTENSION_NAME);
        }
        catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static CDIExtension getInitializedExtensionFromBeanManager(BeanManager bm) {
        CDIExtension bean = Utils.getCdiExtensionInstance(bm);
        if (bean == null) {
            throw new RuntimeException("Initialized Extension not found.");
        }
        return bean;
    }

    private void initialize(BeanManager manager) {
        if (!lookupExtensionInBeanManager) {
            try {
                InitialContext ic = InitialContextHelper.getInitialContext();
                if (ic != null) {
                    Context jerseyConfigJNDIContext = CDIExtension.createJerseyConfigJNDIContext(ic);
                    jerseyConfigJNDIContext.rebind(JNDI_CDIEXTENSION_NAME, (Object)this);
                }
            }
            catch (NamingException ex) {
                throw new RuntimeException(ex);
            }
        }
        HashMap map2 = new HashMap();
        for (Class<? extends Annotation> qualifier : knownParameterQualifiers) {
            map2.put(qualifier, new HashSet());
        }
        this.discoveredParameterMap = Collections.unmodifiableMap(map2);
        this.syntheticQualifierMap = new HashMap<DiscoveredParameter, SyntheticQualifier>();
        this.toBeInitializedLater = new ArrayList<InitializedLater>();
    }

    private static Context diveIntoJNDIContext(Context initialContext, JNDIContextDiver diver) throws NamingException {
        Name jerseyConfigCtxName = initialContext.getNameParser("").parse(JNDI_CDIEXTENSION_CTX);
        Context currentContext = initialContext;
        for (int i = 0; i < jerseyConfigCtxName.size(); ++i) {
            currentContext = diver.stepInto(currentContext, jerseyConfigCtxName.get(i));
        }
        return currentContext;
    }

    private static Context createJerseyConfigJNDIContext(Context initialContext) throws NamingException {
        return CDIExtension.diveIntoJNDIContext(initialContext, new JNDIContextDiver(){

            @Override
            public Context stepInto(Context ctx, String name) throws NamingException {
                try {
                    return (Context)ctx.lookup(name);
                }
                catch (NamingException e) {
                    return ctx.createSubcontext(name);
                }
            }
        });
    }

    private static Context lookupJerseyConfigJNDIContext(Context initialContext) throws NamingException {
        return CDIExtension.diveIntoJNDIContext(initialContext, new JNDIContextDiver(){

            @Override
            public Context stepInto(Context ctx, String name) throws NamingException {
                return (Context)ctx.lookup(name);
            }
        });
    }

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        LOGGER.fine("Handling BeforeBeanDiscovery event");
        this.initialize(manager);
        for (Class<? extends Annotation> qualifier : knownParameterQualifiers) {
            event.addQualifier(qualifier);
        }
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> event) {
        boolean typeNeedsPatching;
        LOGGER.fine("Handling ProcessAnnotatedType event for " + event.getAnnotatedType().getJavaClass().getName());
        AnnotatedType type = event.getAnnotatedType();
        boolean classHasEncodedAnnotation = type.isAnnotationPresent(Encoded.class);
        HashSet<AnnotatedConstructor> mustPatchConstructors = new HashSet<AnnotatedConstructor>();
        HashMap<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap = new HashMap<AnnotatedParameter<? super T>, PatchInformation>();
        for (AnnotatedConstructor constructor : type.getConstructors()) {
            if (!this.processAnnotatedConstructor(constructor, classHasEncodedAnnotation, parameterToPatchInfoMap)) continue;
            mustPatchConstructors.add(constructor);
        }
        HashSet<AnnotatedField> mustPatchFields = new HashSet<AnnotatedField>();
        HashMap<AnnotatedField<T>, PatchInformation> fieldToPatchInfoMap = new HashMap<AnnotatedField<T>, PatchInformation>();
        for (AnnotatedField field : type.getFields()) {
            if (!this.processAnnotatedField(field, type.getJavaClass(), classHasEncodedAnnotation, fieldToPatchInfoMap)) continue;
            mustPatchFields.add(field);
        }
        HashSet<AnnotatedMethod> mustPatchMethods = new HashSet<AnnotatedMethod>();
        HashSet<AnnotatedMethod<T>> setterMethodsWithoutInject = new HashSet<AnnotatedMethod<T>>();
        for (AnnotatedMethod method : type.getMethods()) {
            if (!this.processAnnotatedMethod(method, type.getJavaClass(), classHasEncodedAnnotation, parameterToPatchInfoMap, setterMethodsWithoutInject)) continue;
            mustPatchMethods.add(method);
        }
        boolean bl = typeNeedsPatching = !mustPatchConstructors.isEmpty() || !mustPatchFields.isEmpty() || !mustPatchMethods.isEmpty();
        if (typeNeedsPatching) {
            HashSet<Annotation> annotations;
            AnnotatedTypeImpl<Object> newType = new AnnotatedTypeImpl<Object>(type);
            HashSet newConstructors = new HashSet();
            for (Object constructor : type.getConstructors()) {
                AnnotatedConstructorImpl newConstructor = new AnnotatedConstructorImpl(constructor, newType);
                if (mustPatchConstructors.contains(constructor)) {
                    this.patchAnnotatedCallable((AnnotatedCallable<? super T>)constructor, newConstructor, (Map<AnnotatedParameter<? super T>, PatchInformation>)parameterToPatchInfoMap);
                } else {
                    this.copyParametersOfAnnotatedCallable((AnnotatedCallable<? super T>)constructor, newConstructor);
                }
                newConstructors.add(newConstructor);
            }
            HashSet newFields = new HashSet();
            for (AnnotatedField field : type.getFields()) {
                if (mustPatchFields.contains(field)) {
                    PatchInformation patchInfo = (PatchInformation)fieldToPatchInfoMap.get(field);
                    annotations = new HashSet<Annotation>();
                    if (patchInfo.mustAddInject()) {
                        annotations.add((Annotation)this.injectAnnotationLiteral);
                    }
                    if (patchInfo.getSyntheticQualifier() != null) {
                        annotations.add(patchInfo.getSyntheticQualifier());
                        Annotation skippedQualifier = patchInfo.getParameter().getAnnotation();
                        for (Annotation annotation : field.getAnnotations()) {
                            if (annotation == skippedQualifier) continue;
                            annotations.add(annotation);
                        }
                    } else {
                        annotations.addAll(field.getAnnotations());
                    }
                    if (patchInfo.getAnnotation() != null) {
                        annotations.add(patchInfo.getAnnotation());
                    }
                    newFields.add(new AnnotatedFieldImpl(field, annotations, newType));
                    continue;
                }
                newFields.add(new AnnotatedFieldImpl(field, newType));
            }
            HashSet newMethods = new HashSet();
            for (AnnotatedMethod method : type.getMethods()) {
                AnnotatedMethodImpl newMethod;
                if (mustPatchMethods.contains(method)) {
                    if (setterMethodsWithoutInject.contains(method)) {
                        annotations = new HashSet();
                        annotations.add((Annotation)this.injectAnnotationLiteral);
                        for (Annotation annotation : method.getAnnotations()) {
                            if (knownParameterQualifiers.contains(annotation.annotationType())) continue;
                            annotations.add(annotation);
                        }
                        AnnotatedMethodImpl newMethod2 = new AnnotatedMethodImpl(method, annotations, newType);
                        this.patchAnnotatedCallable((AnnotatedCallable<? super T>)method, newMethod2, (Map<AnnotatedParameter<? super T>, PatchInformation>)parameterToPatchInfoMap);
                        newMethods.add(newMethod2);
                        continue;
                    }
                    newMethod = new AnnotatedMethodImpl(method, newType);
                    this.patchAnnotatedCallable((AnnotatedCallable<? super T>)method, newMethod, (Map<AnnotatedParameter<? super T>, PatchInformation>)parameterToPatchInfoMap);
                    newMethods.add(newMethod);
                    continue;
                }
                newMethod = new AnnotatedMethodImpl(method, newType);
                this.copyParametersOfAnnotatedCallable((AnnotatedCallable<? super T>)method, newMethod);
                newMethods.add(newMethod);
            }
            newType.setConstructors(newConstructors);
            newType.setFields(newFields);
            newType.setMethods(newMethods);
            event.setAnnotatedType(newType);
            LOGGER.fine("  replaced annotated type for " + type.getJavaClass());
        }
    }

    private <T> boolean processAnnotatedConstructor(AnnotatedConstructor<T> constructor, boolean classHasEncodedAnnotation, Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap) {
        boolean mustPatch = false;
        if (constructor.getAnnotation(Inject.class) != null) {
            boolean methodHasEncodedAnnotation = constructor.isAnnotationPresent(Encoded.class);
            for (AnnotatedParameter parameter : constructor.getParameters()) {
                for (Annotation annotation : parameter.getAnnotations()) {
                    Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                    if (discovered == null || !knownParameterQualifiers.contains(annotation.annotationType())) continue;
                    if (methodHasEncodedAnnotation || classHasEncodedAnnotation || parameter.isAnnotationPresent(DefaultValue.class)) {
                        mustPatch = true;
                    }
                    boolean encoded = parameter.isAnnotationPresent(Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                    DefaultValue defaultValue = (DefaultValue)parameter.getAnnotation(DefaultValue.class);
                    if (defaultValue != null) {
                        mustPatch = true;
                    }
                    DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation, parameter.getBaseType(), defaultValue, encoded);
                    discovered.add(jerseyParameter);
                    LOGGER.fine("  recorded " + jerseyParameter);
                    parameterToPatchInfoMap.put(parameter, new PatchInformation(jerseyParameter, this.getSyntheticQualifierFor(jerseyParameter), false));
                }
            }
        }
        return mustPatch;
    }

    private <T> boolean processAnnotatedMethod(AnnotatedMethod<? super T> method, Class<T> token, boolean classHasEncodedAnnotation, Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap, Set<AnnotatedMethod<? super T>> setterMethodsWithoutInject) {
        boolean mustPatch;
        block7: {
            block8: {
                mustPatch = false;
                if (method.getAnnotation(Inject.class) == null) break block8;
                boolean methodHasEncodedAnnotation = method.isAnnotationPresent(Encoded.class);
                for (AnnotatedParameter parameter : method.getParameters()) {
                    for (Annotation annotation : parameter.getAnnotations()) {
                        Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                        if (discovered == null || !knownParameterQualifiers.contains(annotation.annotationType())) continue;
                        if (methodHasEncodedAnnotation || classHasEncodedAnnotation || parameter.isAnnotationPresent(DefaultValue.class)) {
                            mustPatch = true;
                        }
                        boolean encoded = parameter.isAnnotationPresent(Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                        DefaultValue defaultValue = (DefaultValue)parameter.getAnnotation(DefaultValue.class);
                        if (defaultValue != null) {
                            mustPatch = true;
                        }
                        DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation, parameter.getBaseType(), defaultValue, encoded);
                        discovered.add(jerseyParameter);
                        LOGGER.fine("  recorded " + jerseyParameter);
                        parameterToPatchInfoMap.put(parameter, new PatchInformation(jerseyParameter, this.getSyntheticQualifierFor(jerseyParameter), false));
                    }
                }
                break block7;
            }
            if (!this.isSetterMethod(method)) break block7;
            boolean methodHasEncodedAnnotation = method.isAnnotationPresent(Encoded.class);
            for (Annotation annotation : method.getAnnotations()) {
                Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                if (discovered == null || !knownParameterQualifiers.contains(annotation.annotationType())) continue;
                mustPatch = true;
                setterMethodsWithoutInject.add(method);
                for (AnnotatedParameter parameter : method.getParameters()) {
                    boolean encoded = parameter.isAnnotationPresent(Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                    DefaultValue defaultValue = (DefaultValue)parameter.getAnnotation(DefaultValue.class);
                    if (defaultValue == null) {
                        defaultValue = (DefaultValue)method.getAnnotation(DefaultValue.class);
                    }
                    DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation, parameter.getBaseType(), defaultValue, encoded);
                    discovered.add(jerseyParameter);
                    LOGGER.fine("  recorded " + jerseyParameter);
                    SyntheticQualifier syntheticQualifier = this.getSyntheticQualifierFor(jerseyParameter);
                    Annotation addedAnnotation = syntheticQualifier == null ? annotation : null;
                    parameterToPatchInfoMap.put(parameter, new PatchInformation(jerseyParameter, syntheticQualifier, addedAnnotation, false));
                }
                break;
            }
        }
        return mustPatch;
    }

    private <T> boolean isSetterMethod(AnnotatedMethod<T> method) {
        List parameters;
        Method javaMethod = method.getJavaMember();
        return (javaMethod.getModifiers() & 1) != 0 && javaMethod.getReturnType() == Void.TYPE && javaMethod.getName().startsWith("set") && (parameters = method.getParameters()).size() == 1;
    }

    private <T> boolean processAnnotatedField(AnnotatedField<? super T> field, Class<T> token, boolean classHasEncodedAnnotation, Map<AnnotatedField<? super T>, PatchInformation> fieldToPatchInfoMap) {
        boolean mustPatch = false;
        for (Annotation annotation : field.getAnnotations()) {
            Set<DiscoveredParameter> discovered;
            boolean mustAddInjectAnnotation;
            if (!knownParameterQualifiers.contains(annotation.annotationType())) continue;
            boolean bl = mustAddInjectAnnotation = !field.isAnnotationPresent(Inject.class);
            if (field.isAnnotationPresent(Encoded.class) || classHasEncodedAnnotation || mustAddInjectAnnotation || field.isAnnotationPresent(DefaultValue.class)) {
                mustPatch = true;
            }
            if ((discovered = this.discoveredParameterMap.get(annotation.annotationType())) == null) continue;
            boolean encoded = field.isAnnotationPresent(Encoded.class) || classHasEncodedAnnotation;
            DefaultValue defaultValue = (DefaultValue)field.getAnnotation(DefaultValue.class);
            DiscoveredParameter parameter = new DiscoveredParameter(annotation, field.getBaseType(), defaultValue, encoded);
            discovered.add(parameter);
            LOGGER.fine("  recorded " + parameter);
            fieldToPatchInfoMap.put(field, new PatchInformation(parameter, this.getSyntheticQualifierFor(parameter), mustAddInjectAnnotation));
        }
        return mustPatch;
    }

    private <T> void patchAnnotatedCallable(AnnotatedCallable<? super T> callable, AnnotatedCallableImpl<T> newCallable, Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap) {
        ArrayList newParams = new ArrayList();
        for (AnnotatedParameter parameter : callable.getParameters()) {
            PatchInformation patchInfo = parameterToPatchInfoMap.get(parameter);
            if (patchInfo != null) {
                HashSet<Annotation> annotations = new HashSet<Annotation>();
                if (patchInfo.mustAddInject()) {
                    annotations.add((Annotation)this.injectAnnotationLiteral);
                }
                if (patchInfo.getSyntheticQualifier() != null) {
                    annotations.add(patchInfo.getSyntheticQualifier());
                    Annotation skippedQualifier = patchInfo.getParameter().getAnnotation();
                    for (Annotation annotation : parameter.getAnnotations()) {
                        if (annotation == skippedQualifier) continue;
                        annotations.add(annotation);
                    }
                } else {
                    annotations.addAll(parameter.getAnnotations());
                }
                if (patchInfo.getAnnotation() != null) {
                    annotations.add(patchInfo.getAnnotation());
                }
                newParams.add(new AnnotatedParameterImpl<T>(parameter, annotations, newCallable));
                continue;
            }
            newParams.add(new AnnotatedParameterImpl<T>(parameter, newCallable));
        }
        newCallable.setParameters(newParams);
    }

    private <T> void copyParametersOfAnnotatedCallable(AnnotatedCallable<? super T> callable, AnnotatedCallableImpl<T> newCallable) {
        ArrayList newParams = new ArrayList();
        for (AnnotatedParameter parameter : callable.getParameters()) {
            newParams.add(new AnnotatedParameterImpl<T>(parameter, newCallable));
        }
        newCallable.setParameters(newParams);
    }

    private SyntheticQualifier getSyntheticQualifierFor(DiscoveredParameter parameter) {
        SyntheticQualifier result = this.syntheticQualifierMap.get(parameter);
        if (result == null && (parameter.isEncoded() || parameter.getDefaultValue() != null)) {
            result = new SyntheticQualifierAnnotationImpl(this.nextSyntheticQualifierValue++);
            this.syntheticQualifierMap.put(parameter, result);
            LOGGER.fine("  created synthetic qualifier " + result);
        }
        return result;
    }

    private static Class getClassOfType(Type type) {
        ParameterizedType subType;
        Type t;
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)type;
            Type t2 = arrayType.getGenericComponentType();
            if (t2 instanceof Class) {
                Class c = (Class)t2;
                try {
                    Object o = Array.newInstance(c, 0);
                    return o.getClass();
                }
                catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        } else if (type instanceof ParameterizedType && (t = (subType = (ParameterizedType)type).getRawType()) instanceof Class) {
            return (Class)t;
        }
        return null;
    }

    <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> event) {
        LOGGER.fine("Handling ProcessInjectionTarget event for " + event.getAnnotatedType().getJavaClass().getName());
    }

    void processManagedBean(@Observes ProcessManagedBean<?> event) {
        LOGGER.fine("Handling ProcessManagedBean event for " + event.getBean().getBeanClass().getName());
        Bean bean = event.getBean();
        for (InjectionPoint injectionPoint : bean.getInjectionPoints()) {
            StringBuilder sb = new StringBuilder();
            sb.append("  found injection point ");
            sb.append(injectionPoint.getType());
            for (Annotation annotation : injectionPoint.getQualifiers()) {
                sb.append(" ");
                sb.append(annotation);
            }
            LOGGER.fine(sb.toString());
        }
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
        LOGGER.fine("Handling AfterBeanDiscovery event");
        this.addPredefinedContextBeans(event);
        BeanGenerator beanGenerator = new BeanGenerator("com/sun/jersey/server/impl/cdi/generated/Bean");
        for (Set<DiscoveredParameter> parameters : this.discoveredParameterMap.values()) {
            for (DiscoveredParameter parameter : parameters) {
                Annotation annotation = parameter.getAnnotation();
                Class klass = CDIExtension.getClassOfType(parameter.getType());
                if (annotation.annotationType() == javax.ws.rs.core.Context.class && staticallyDefinedContextBeans.contains(klass) && !parameter.isEncoded() && parameter.getDefaultValue() == null) continue;
                SyntheticQualifier syntheticQualifier = this.syntheticQualifierMap.get(parameter);
                Annotation theQualifier = syntheticQualifier != null ? syntheticQualifier : annotation;
                HashSet<Annotation> annotations = new HashSet<Annotation>();
                annotations.add(theQualifier);
                Parameter jerseyParameter = new Parameter(new Annotation[]{annotation}, annotation, paramQualifiersMap.get(annotation.annotationType()), parameter.getValue(), parameter.getType(), klass, parameter.isEncoded(), parameter.getDefaultValue() == null ? null : parameter.getDefaultValue().value());
                Class<?> beanClass = beanGenerator.createBeanClass();
                ParameterBean bean = new ParameterBean(beanClass, parameter.getType(), annotations, parameter, jerseyParameter);
                this.toBeInitializedLater.add(bean);
                event.addBean(bean);
                LOGGER.fine("Added bean for parameter " + parameter + " and qualifier " + theQualifier);
            }
        }
    }

    private void addPredefinedContextBeans(AfterBeanDiscovery event) {
        event.addBean(new PredefinedBean<Application>(Application.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<HttpHeaders>(HttpHeaders.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<Providers>(Providers.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<Request>(Request.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<SecurityContext>(SecurityContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<UriInfo>(UriInfo.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<ExceptionMapperContext>(ExceptionMapperContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<ExtendedUriInfo>(ExtendedUriInfo.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<FeaturesAndProperties>(FeaturesAndProperties.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<HttpContext>(HttpContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<HttpRequestContext>(HttpRequestContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<HttpResponseContext>(HttpResponseContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<MessageBodyWorkers>(MessageBodyWorkers.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new PredefinedBean<ResourceContext>(ResourceContext.class, (Annotation)this.contextAnnotationLiteral));
        event.addBean(new ProviderBasedBean<WebApplication>(WebApplication.class, new Provider<WebApplication>(){

            public WebApplication get() {
                return CDIExtension.this.lookupWebApplication();
            }
        }, (Annotation)this.contextAnnotationLiteral));
    }

    private WebApplication lookupWebApplication() {
        return this.lookupWebApplication(Thread.currentThread().getContextClassLoader());
    }

    private WebApplication lookupWebApplication(ClassLoader cl) {
        return this.webApplications.get(cl);
    }

    void setWebApplication(WebApplication wa) {
        this.webApplications.put(Thread.currentThread().getContextClassLoader(), wa);
    }

    WebApplication getWebApplication() {
        return this.lookupWebApplication();
    }

    void setResourceConfig(ResourceConfig rc) {
        this.resourceConfig = rc;
    }

    ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }

    void lateInitialize() {
        try {
            for (InitializedLater object : this.toBeInitializedLater) {
                object.later();
            }
        }
        finally {
            if (!lookupExtensionInBeanManager) {
                try {
                    InitialContext ic = InitialContextHelper.getInitialContext();
                    if (ic != null) {
                        CDIExtension.lookupJerseyConfigJNDIContext(ic).unbind(JNDI_CDIEXTENSION_NAME);
                    }
                }
                catch (NamingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    class ParameterBean<T>
    extends AbstractBean<T>
    implements InitializedLater {
        private final DiscoveredParameter discoveredParameter;
        private final Parameter parameter;
        private final Map<ClassLoader, Injectable<T>> injectables;
        private final Map<ClassLoader, Boolean> processed;

        public ParameterBean(Class<?> klass, Type type, Set<Annotation> qualifiers, DiscoveredParameter discoveredParameter, Parameter parameter) {
            super(klass, type, qualifiers);
            this.injectables = new ConcurrentHashMap<ClassLoader, Injectable<T>>();
            this.processed = new ConcurrentHashMap<ClassLoader, Boolean>();
            this.discoveredParameter = discoveredParameter;
            this.parameter = parameter;
        }

        @Override
        public void later() {
            Injectable injectable;
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (this.injectables.containsKey(contextClassLoader)) {
                return;
            }
            if (this.processed.containsKey(contextClassLoader)) {
                return;
            }
            this.processed.put(contextClassLoader, true);
            boolean registered = CDIExtension.this.lookupWebApplication(contextClassLoader).getServerInjectableProviderFactory().isParameterTypeRegistered(this.parameter);
            if (!registered) {
                Errors.error("Parameter type not registered " + this.discoveredParameter);
            }
            if ((injectable = CDIExtension.this.lookupWebApplication(contextClassLoader).getServerInjectableProviderFactory().getInjectable(this.parameter, ComponentScope.PerRequest)) == null) {
                Errors.error("No injectable for parameter " + this.discoveredParameter);
            } else {
                this.injectables.put(contextClassLoader, injectable);
            }
        }

        @Override
        public T create(CreationalContext<T> creationalContext) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (!this.injectables.containsKey(contextClassLoader)) {
                this.later();
                if (!this.injectables.containsKey(contextClassLoader)) {
                    return null;
                }
            }
            Injectable<T> injectable = this.injectables.get(contextClassLoader);
            try {
                return injectable.getValue();
            }
            catch (IllegalStateException e) {
                if (injectable instanceof AbstractHttpContextInjectable) {
                    return ((AbstractHttpContextInjectable)((Object)this.injectables)).getValue(CDIExtension.this.lookupWebApplication(contextClassLoader).getThreadLocalHttpContext());
                }
                throw e;
            }
        }
    }

    class PredefinedBean<T>
    extends AbstractBean<T> {
        private Annotation qualifier;

        public PredefinedBean(Class<T> klass, Annotation qualifier) {
            super(klass, qualifier);
            this.qualifier = qualifier;
        }

        @Override
        public T create(CreationalContext<T> creationalContext) {
            Injectable injectable = CDIExtension.this.lookupWebApplication().getServerInjectableProviderFactory().getInjectable(this.qualifier.annotationType(), null, this.qualifier, this.getBeanClass(), ComponentScope.Singleton);
            if (injectable == null) {
                Errors.error("No injectable for " + this.getBeanClass().getName());
                return null;
            }
            return injectable.getValue();
        }
    }

    private static class PatchInformation {
        private DiscoveredParameter parameter;
        private SyntheticQualifier syntheticQualifier;
        private Annotation annotation;
        private boolean mustAddInject;

        public PatchInformation(DiscoveredParameter parameter, SyntheticQualifier syntheticQualifier, boolean mustAddInject) {
            this(parameter, syntheticQualifier, null, mustAddInject);
        }

        public PatchInformation(DiscoveredParameter parameter, SyntheticQualifier syntheticQualifier, Annotation annotation, boolean mustAddInject) {
            this.parameter = parameter;
            this.syntheticQualifier = syntheticQualifier;
            this.annotation = annotation;
            this.mustAddInject = mustAddInject;
        }

        public DiscoveredParameter getParameter() {
            return this.parameter;
        }

        public SyntheticQualifier getSyntheticQualifier() {
            return this.syntheticQualifier;
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public boolean mustAddInject() {
            return this.mustAddInject;
        }
    }

    private static interface JNDIContextDiver {
        public Context stepInto(Context var1, String var2) throws NamingException;
    }

    private static class SyntheticQualifierAnnotationImpl
    extends AnnotationLiteral<SyntheticQualifier>
    implements SyntheticQualifier {
        private int value;

        public SyntheticQualifierAnnotationImpl(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }

    private static class InjectAnnotationLiteral
    extends AnnotationLiteral<Inject>
    implements Inject {
        private InjectAnnotationLiteral() {
        }
    }

    private static class ContextAnnotationLiteral
    extends AnnotationLiteral<javax.ws.rs.core.Context>
    implements javax.ws.rs.core.Context {
        private ContextAnnotationLiteral() {
        }
    }
}

